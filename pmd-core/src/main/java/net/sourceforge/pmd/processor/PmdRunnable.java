/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.Collections;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.RulesetStageDependencyHelper;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * A processing task for a single file.
 */
abstract class PmdRunnable implements Runnable {

    private final TextFile textFile;
    private final GlobalAnalysisListener ruleContext;

    private final PMDConfiguration configuration;

    private final RulesetStageDependencyHelper dependencyHelper;

    PmdRunnable(TextFile textFile,
                GlobalAnalysisListener ruleContext,
                PMDConfiguration configuration) {
        this.textFile = textFile;
        this.ruleContext = ruleContext;
        this.configuration = configuration;
        this.dependencyHelper = new RulesetStageDependencyHelper(configuration);
    }

    /**
     * This is only called within the run method (when we are on the actual carrier thread).
     * That way an implementation that uses a ThreadLocal will see the
     * correct thread.
     */
    protected abstract RuleSets getRulesets();

    @Override
    public void run() throws FileAnalysisException {
        TimeTracker.initThread();

        RuleSets ruleSets = getRulesets();

        try (FileAnalysisListener listener = ruleContext.startFileAnalysis(textFile)) {
            final RuleContext ruleCtx = RuleContext.create(listener);
            LanguageVersion langVersion = textFile.getLanguageVersion(configuration.getLanguageVersionDiscoverer());

            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(textFile)) {
                try (TextDocument textDocument = TextDocument.create(textFile, langVersion)) {

                    if (configuration.getAnalysisCache().isUpToDate(textDocument)) {
                        reportCachedRuleViolations(ruleCtx, textDocument);
                    } else {
                        try {
                            processSource(ruleCtx, textDocument, ruleSets);
                        } catch (Exception e) {
                            configuration.getAnalysisCache().analysisFailed(textDocument);

                            // The listener handles logging if needed,
                            // it may also rethrow the error, as a FileAnalysisException (which we let through below)
                            ruleCtx.reportError(new Report.ProcessingError(e, textFile.getDisplayName()));
                        }
                    }
                }
            }
        } catch (FileAnalysisException e) {
            throw e; // bubble managed exceptions, they were already reported
        } catch (Exception e) {
            throw FileAnalysisException.wrap(textFile.getDisplayName(), "Exception while closing listener", e);
        }

        TimeTracker.finishThread();
    }

    private void processSource(RuleContext ruleCtx, TextDocument textDoc, RuleSets ruleSets) throws IOException, FileAnalysisException {

        try {
            ruleSets.start(ruleCtx);
            processSource(textDoc, ruleSets, ruleCtx);
        } finally {
            ruleSets.end(ruleCtx);
        }

    }


    private void reportCachedRuleViolations(final RuleContext ctx, TextDocument file) {
        for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(file)) {
            ctx.addViolationNoSuppress(rv);
        }
    }

    private RootNode parse(Parser parser, ParserTask task) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(TextDocument textDocument,
                               RuleSets ruleSets,
                               RuleContext ctx) throws FileAnalysisException {

        ParserTask task = new ParserTask(
            textDocument,
            SemanticErrorReporter.noop(), // TODO
            configuration.getSuppressMarker()
        );

        Parser parser = textDocument.getLanguageVersion().getLanguageVersionHandler().getParser();

        RootNode rootNode = parse(parser, task);

        dependencyHelper.runLanguageSpecificStages(ruleSets, textDocument.getLanguageVersion(), rootNode);

        ruleSets.apply(Collections.singletonList(rootNode), ctx);
    }

}
