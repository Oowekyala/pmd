/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.DummyAstStages;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.util.document.TextDocument;

public class StageDependencyTest {

    private final LanguageVersion version = LanguageRegistry.findLanguageByTerseName("dummy").getVersion("1.0");

    private DummyNode process(String source, RuleSets ruleSets) {
        return process(source, ruleSets, new RulesetStageDependencyHelper(new PMDConfiguration()));
    }

    private DummyNode process(String source, RuleSets ruleSets, RulesetStageDependencyHelper helper) {

        // TODO Handle Rules having different parser options.

        Parser parser = version.getLanguageVersionHandler().getParser();
        TextDocument doc = TextDocument.readOnlyString(source, version);

        ParserTask task = new ParserTask(doc, SemanticErrorReporter.noop());

        RootNode rootNode = parser.parse(task);

        helper.runLanguageSpecificStages(ruleSets, version, rootNode);

        return (DummyNode) rootNode;
    }


    @Test
    public void testSimpleDependency() {

        DummyNode root = process("foo bar", withRules(new PredicateTestRule(DummyAstStages.FOO)));

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testNoDependency() {

        DummyNode root = process("foo bar", withRules(new PredicateTestRule()));

        Assert.assertFalse(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testDependencyUnion() {

        DummyNode root =
            process("foo bar",
                    withRules(
                        new PredicateTestRule(DummyAstStages.FOO),
                        new PredicateTestRule(DummyAstStages.BAR)
                    )
            );

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertTrue(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testTransitiveDependency() {

        DummyNode root = process("foo bar", withRules(new PredicateTestRule(DummyAstStages.RUNS_FOO)));

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
        Assert.assertTrue(DummyAstStages.RUNS_FOO.hasProcessed(root));
    }

    @Test
    public void testNoRecomputation() {

        PMDConfiguration configuration = new PMDConfiguration();
        RulesetStageDependencyHelper helper = new RulesetStageDependencyHelper(configuration);

        RuleSets ruleSets = withRules(new PredicateTestRule(DummyAstStages.RUNS_FOO));

        List<AstProcessingStage<?>> stages1 = helper.testOnlyGetDependencies(ruleSets, version);

        process("foo bar", ruleSets);

        List<AstProcessingStage<?>> stages2 = helper.testOnlyGetDependencies(ruleSets, version);

        Assert.assertSame(stages1, stages2);
    }

    @Test
    public void testDependencyOrdering() {

        PMDConfiguration configuration = new PMDConfiguration();
        RulesetStageDependencyHelper helper = new RulesetStageDependencyHelper(configuration);

        RuleSets ruleSets = withRules(
            new PredicateTestRule(DummyAstStages.FOO),
            new PredicateTestRule(DummyAstStages.BAR)
        );

        RuleSets ruleSets2 = withRules(
            new PredicateTestRule(DummyAstStages.BAR),
            new PredicateTestRule(DummyAstStages.FOO)
        );

        List<AstProcessingStage<?>> stages1 = helper.testOnlyGetDependencies(ruleSets, version);
        List<AstProcessingStage<?>> stages2 = helper.testOnlyGetDependencies(ruleSets2, version);

        Assert.assertNotSame(stages1, stages2);
        Assert.assertEquals(stages1, stages2);
    }


    private static RuleSets withRules(Rule r, Rule... rs) {
        List<RuleSet> rsets = new ArrayList<>();
        rsets.add(new RuleSetFactory().createSingleRuleRuleSet(r));
        for (Rule rule : rs) {
            rsets.add(new RuleSetFactory().createSingleRuleRuleSet(rule));
        }

        return new RuleSets(rsets);
    }

    private static class PredicateTestRule extends AbstractRule {

        private final List<DummyAstStages> dependencies;

        PredicateTestRule(DummyAstStages... dependencies) {
            this.dependencies = Arrays.asList(dependencies);
        }

        @Override
        public Language getLanguage() {
            return LanguageRegistry.findLanguageByTerseName("dummy");
        }

        @Override
        public boolean dependsOn(AstProcessingStage<?> stage) {
            return dependencies.contains(stage);
        }

        @Override
        public void apply(Node target, RuleContext ctx) {

        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    }


}
