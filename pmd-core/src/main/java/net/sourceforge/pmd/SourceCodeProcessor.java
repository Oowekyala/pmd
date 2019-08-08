/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.xpath.Initializer;

public class SourceCodeProcessor {

    private final PMDConfiguration configuration;

    public SourceCodeProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
    }


    /**
     * Processes the input stream against a rule set using the given input
     * encoding. If the LanguageVersion is <code>null</code> on the RuleContext,
     * it will be automatically determined. Any code which wishes to process
     * files for different Languages, will need to be sure to either properly
     * set the Language on the RuleContext, or set it to <code>null</code>
     * first.
     *
     * @param sourceCode The Reader to analyze.
     * @param ruleSets   The collection of rules to process against the file.
     * @param ctx        The context in which PMD is operating.
     *
     * @throws PMDException if the input encoding is unsupported, the input stream could
     *                      not be parsed, or other error is encountered.
     * @see RuleContext#setLanguageVersion(net.sourceforge.pmd.lang.LanguageVersion)
     * @see PMDConfiguration#getLanguageVersionOfFile(String)
     */
    public void processSourceCode(InputStream sourceCode, RuleSets ruleSets, RuleContext ctx) throws PMDException {
        determineLanguage(ctx);

        // make sure custom XPath functions are initialized
        Initializer.initialize();

        // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
        if (ruleSets.applies(ctx.getSourceCodeFile())) {
            // Is the cache up to date?
            if (configuration.getAnalysisCache().isUpToDate(ctx.getSourceCodeFile())) {
                for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(ctx.getSourceCodeFile())) {
                    ctx.getReport().addRuleViolation(rv);
                }
                return;
            }

            try {
                ruleSets.start(ctx);
                processSource(sourceCode, ruleSets, ctx);
            } catch (ParseException pe) {
                configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
                throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
            } catch (Exception e) {
                configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
                throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
            } finally {
                ruleSets.end(ctx);
            }
        }
    }

    private Node parse(RuleContext ctx, InputStream sourceCode, Parser parser) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            Node rootNode = parser.parse(ctx.getSourceCodeFilename(), sourceCode, configuration.getSourceEncoding());
            ctx.getReport().suppress(parser.getSuppressMap());
            return rootNode;
        }
    }

    private void symbolFacade(Node rootNode, LanguageVersionHandler languageVersionHandler) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.SYMBOL_TABLE)) {
            languageVersionHandler.getSymbolFacade(configuration.getClassLoader()).start(rootNode);
        }
    }

    private void resolveQualifiedNames(Node rootNode, LanguageVersionHandler handler) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.QUALIFIED_NAME_RESOLUTION)) {
            handler.getQualifiedNameResolutionFacade(configuration.getClassLoader()).start(rootNode);
        }
    }

    // private ParserOptions getParserOptions(final LanguageVersionHandler
    // languageVersionHandler) {
    // // TODO Handle Rules having different parser options.
    // ParserOptions parserOptions =
    // languageVersionHandler.getDefaultParserOptions();
    // parserOptions.setSuppressMarker(configuration.getSuppressMarker());
    // return parserOptions;
    // }

    private void usesDFA(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets, Language language) {
        if (ruleSets.usesDFA(language)) {
            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.DFA)) {
                VisitorStarter dataFlowFacade = languageVersion.getLanguageVersionHandler().getDataFlowFacade();
                dataFlowFacade.start(rootNode);
            }
        }
    }

    private void usesTypeResolution(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets,
                                    Language language) {

        if (ruleSets.usesTypeResolution(language)) {
            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.TYPE_RESOLUTION)) {
                languageVersion.getLanguageVersionHandler().getTypeResolutionFacade(configuration.getClassLoader())
                               .start(rootNode);
            }
        }
    }


    private void usesMultifile(Node rootNode, LanguageVersionHandler languageVersionHandler, RuleSets ruleSets,
                               Language language) {

        if (ruleSets.usesMultifile(language)) {
            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.MULTIFILE_ANALYSIS)) {
                languageVersionHandler.getMultifileFacade().start(rootNode);
            }
        }
    }


    private void processSource(InputStream sourceCode, RuleSets ruleSets, RuleContext ctx) {
        LanguageVersion languageVersion = ctx.getLanguageVersion();
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        Parser parser = PMD.parserFor(languageVersion, configuration);

        Node rootNode = parse(ctx, sourceCode, parser);
        // basically:
        // 1. make the union of all stage dependencies of each rule, by language, for the Rulesets
        // 2. order them by dependency
        // 3. run them and time them if needed

        // The problem is the first two steps need only be done once.
        // They're probably costly and if we do this here without changing anything,
        // they'll be done on each file! Btw currently the "usesDfa" and such are nested loops testing
        // all rules of all rulesets, but they're run on each file too!

        // Also, the benchmarking framework needs a small refactor. TimedOperationCategory needs to be
        // made extensible -> probably should be turned to a class with static constants + factory methods
        // and not an enum.

        // With mutable RuleSets, caching of the value can't be guaranteed to be accurate...
        // The approach I'd like to take is either
        // * to create a new RunnableRulesets class which is immutable, and performs all these preliminary
        //   computations upon construction.
        // * or to modify Ruleset and Rulesets to be immutable. This IMO is a better option because it makes
        //   these objects easier to reason about and pass around from thread to thread. It also avoid creating
        //   a new class, and breaking SourceCodeProcessor's API too much.
        //
        // The "preliminary computations" also include:
        // * removing dysfunctional rules
        // * separating rulechain rules from normal rules
        // * grouping rules by language/ file extension
        // * etc.

        resolveQualifiedNames(rootNode, languageVersionHandler);
        symbolFacade(rootNode, languageVersionHandler);
        Language language = languageVersion.getLanguage();
        usesDFA(languageVersion, rootNode, ruleSets, language);
        usesTypeResolution(languageVersion, rootNode, ruleSets, language);
        usesMultifile(rootNode, languageVersionHandler, ruleSets, language);

        List<Node> acus = Collections.singletonList(rootNode);
        ruleSets.apply(acus, ctx, language);
    }

    private void determineLanguage(RuleContext ctx) {
        // If LanguageVersion of the source file is not known, make a
        // determination
        if (ctx.getLanguageVersion() == null) {
            LanguageVersion languageVersion = configuration.getLanguageVersionOfFile(ctx.getSourceCodeFilename());
            ctx.setLanguageVersion(languageVersion);
        }
    }
}
