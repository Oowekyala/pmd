/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

public class RuleContextTest {

    public static Report getReport(Consumer<FileAnalysisListener> sideEffects) throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try {
            sideEffects.accept(listener);
        } finally {
            listener.close();
        }
        return listener.getResult();
    }

    public static Report getReport(Rule rule, BiConsumer<Rule, RuleContext> sideEffects) throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try {
            sideEffects.accept(rule, RuleContext.create(listener, rule));
        } finally {
            listener.close();
        }
        return listener.getResult();
    }

    public static Report getReportForRuleApply(Rule rule, Node node) throws Exception {
        ReportBuilderListener listener = new ReportBuilderListener();
        try {
            rule.apply(node, RuleContext.create(listener, rule));
        } finally {
            listener.close();
        }
        return listener.getResult();
    }

    @Test
    public void testMessage() throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> ctx.addViolationWithMessage(DummyTreeUtil.tree(DummyTreeUtil::root), "message with \"'{'\""));

        Assert.assertEquals("message with \"{\"", report.getViolations().get(0).getDescription());
    }

    @Test
    public void testMessageArgs() throws Exception {
        Report report = getReport(new FooRule(), (r, ctx) -> ctx.addViolationWithMessage(DummyTreeUtil.tree(DummyTreeUtil::root), "message with 1 argument: \"{0}\"", "testarg1"));

        Assert.assertEquals("message with 1 argument: \"testarg1\"", report.getViolations().get(0).getDescription());
    }
}
