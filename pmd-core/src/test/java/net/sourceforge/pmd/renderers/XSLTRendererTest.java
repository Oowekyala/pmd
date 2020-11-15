/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XSLTRendererTest {

    @Test
    public void testDefaultStylesheet() throws Exception {
        XSLTRenderer renderer = new XSLTRenderer();
        Report report = new Report();
        DummyNode node = new DummyRoot().withFileName("file");
        node.setCoords(1, 1, 1, 2);
        RuleViolation rv = new ParametricRuleViolation(new FooRule(), node, "violation message");
        report.addRuleViolation(rv);
        String result = ReportTest.render(renderer, report);
        Assert.assertTrue(result.contains("violation message"));
    }
}
