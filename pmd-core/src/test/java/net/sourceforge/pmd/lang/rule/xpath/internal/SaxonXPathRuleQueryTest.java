/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.followPath;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.nodeB;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.root;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.tree;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.internal.AbstractXPathFunctionDef;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.DummyNodeWithListAndEnum;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

public class SaxonXPathRuleQueryTest {

    //    Unsupported: https://github.com/pmd/pmd/issues/2451
    //    @Test
    //    public void testListAttribute() {
    //        RootNode dummy = new DummyNodeWithListAndEnum();
    //
    //        assertQuery(1, "//dummyNode[@List = \"A\"]", dummy);
    //        assertQuery(1, "//dummyNode[@List = \"B\"]", dummy);
    //        assertQuery(0, "//dummyNode[@List = \"C\"]", dummy);
    //        assertQuery(1, "//dummyNode[@Enum = \"FOO\"]", dummy);
    //        assertQuery(0, "//dummyNode[@Enum = \"BAR\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = \"FOO\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = \"BAR\"]", dummy);
    //        assertQuery(1, "//dummyNode[@EnumList = (\"FOO\", \"BAR\")]", dummy);
    //        assertQuery(0, "//dummyNode[@EmptyList = (\"A\")]", dummy);
    //    }

    @Test
    public void testListProperty() {
        RootNode dummy = new DummyNodeWithListAndEnum();

        PropertyDescriptor<List<String>> prop = PropertyFactory.stringListProperty("prop")
                                                               .defaultValues("FOO", "BAR")
                                                               .desc("description").build();


        assertQuery(1, "//dummyRootNode[@Enum = $prop]", dummy, prop);
    }

    @Test
    public void ruleChainVisits() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Image='baz']/foo | //bar[@Public = 'true'] | //dummyNode[@Public = false()] | //dummyNode");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        Assert.assertTrue(ruleChainVisits.contains("bar"));

        assertEquals(3, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[(string(data(@Image))) eq \"baz\"])/child::element(foo)", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("self::node()[(boolean(data(@Public))) eq false()]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(1));
        assertExpression("self::node()", query.getExpressionsForLocalNameOrDefault("dummyNode").get(2));
        assertExpression("self::node()[(string(data(@Public))) eq \"true\"]", query.getExpressionsForLocalNameOrDefault("bar").get(0));
        assertExpression("(((docOrder((((/)/descendant::element(dummyNode))[(string(data(@Image))) eq \"baz\"])/child::element(foo))) | (((/)/descendant::element(bar))[(string(data(@Public))) eq \"true\"])) | (((/)/descendant::element(dummyNode))[(boolean(data(@Public))) eq false()])) | ((/)/descendant::element(dummyNode))", query.getFallbackExpr());
    }

    @Test
    public void ruleChainVisitsMultipleFilters() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[@Test1 = false()][@Test2 = true()]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[(boolean(data(@Test1))) eq false()])[(boolean(data(@Test2))) eq true()]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("(((/)/descendant::element(dummyNode))[(boolean(data(@Test1))) eq false()])[(boolean(data(@Test2))) eq true()]", query.getFallbackExpr());
    }

    @Test
    public void ruleChainVisitsCustomFunctions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[pmd-dummy:imageIs(@Image)]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("self::node()[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertUntyped(data(@Image))))]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("((/)/descendant::element(Q{}dummyNode))[Q{http://pmd.sourceforge.net/pmd-dummy}imageIs(exactly-one(convertUntyped(data(@Image))))]", query.getFallbackExpr());
    }

    /**
     * If a query contains another unbounded path expression other than the first one, it must be
     * excluded from rule chain execution. Saxon itself optimizes this quite good already.
     */
    @Test
    public void ruleChainVisitsUnboundedPathExpressions() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode[//ClassOrInterfaceType]");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (/)/descendant::element(Q{}ClassOrInterfaceType) return (((/)/descendant::element(Q{}dummyNode))[exists($Q{http://saxon.sf.net/generated-variable}v0)])", query.getFallbackExpr());

        // second sample, more complex
        query = createQuery("//dummyNode[ancestor::ClassOrInterfaceDeclaration[//ClassOrInterfaceType]]");
        ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (/)/descendant::element(Q{}ClassOrInterfaceType) return (((/)/descendant::element(Q{}dummyNode))[exists(ancestor::element(Q{}ClassOrInterfaceDeclaration)[exists($Q{http://saxon.sf.net/generated-variable}v0)])])", query.getFallbackExpr());

        // third example, with boolean expr
        query = createQuery("//dummyNode[//ClassOrInterfaceType or //OtherNode]");
        ruleChainVisits = query.getRuleChainVisits();
        assertEquals(0, ruleChainVisits.size());
        assertEquals(1, query.nodeNameToXPaths.size());
        assertExpression("let $Q{http://saxon.sf.net/generated-variable}v0 := (exists((/)/descendant::element(Q{}ClassOrInterfaceType))) or (exists((/)/descendant::element(Q{}OtherNode))) return (((/)/descendant::element(Q{}dummyNode))[$Q{http://saxon.sf.net/generated-variable}v0])", query.getFallbackExpr());
    }

    @Test
    public void ruleChainVisitsNested() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(((self::node()/child::element(foo))/child::element())/child::element(bar))[(string(data(@Test))) eq \"false\"]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder(((docOrder((((/)/descendant::element(dummyNode))/child::element(foo))/child::element()))/child::element(bar))[(string(data(@Test))) eq \"false\"])", query.getFallbackExpr());
    }

    @Test
    public void ruleChainVisitsNested2() {
        SaxonXPathRuleQuery query = createQuery("//dummyNode/foo[@Baz = 'a']/*/bar[@Test = 'false']");
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("((((self::node()/child::element(foo))[(string(data(@Baz))) eq \"a\"])/child::element())/child::element(bar))[(string(data(@Test))) eq \"false\"]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder(((docOrder(((((/)/descendant::element(dummyNode))/child::element(foo))[(string(data(@Baz))) eq \"a\"])/child::element()))/child::element(bar))[(string(data(@Test))) eq \"false\"])", query.getFallbackExpr());
    }

    @Test
    public void unionBeforeSlash() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode | //dummyNodeB)/dummyNode[@Image = '10']");

        DummyRoot tree = tree(() -> root(
            node(
                node()
            ),
            nodeB(
                node()
            )
        ));

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "10"), results.get(0));
        });

        assertExpression("docOrder((((/)/descendant::(element(dummyNode) | element(dummyNodeB)))/child::element(dummyNode))[(string(data(@Image))) eq \"10\"])", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
    }

    @Test
    public void unionBeforeSlashWithFilter() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode[@Image='0'] | //dummyNodeB[@Image='1'])/dummyNode[@Image = '10']");

        DummyRoot tree = tree(() -> root(
            node(
                node()
            ),
            nodeB(
                node()
            )
        ));

        assertEquals(0, query.getRuleChainVisits().size());
        assertExpression("docOrder((((((/)/descendant::element(dummyNode))[(string(data(@Image))) eq \"0\"]) | (((/)/descendant::element(dummyNodeB))[(string(data(@Image))) eq \"1\"]))/child::element(dummyNode))[(string(data(@Image))) eq \"10\"])", query.getFallbackExpr());

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "10"), results.get(0));
        });
    }

    @Test
    public void unionBeforeSlashDeeper() {
        SaxonXPathRuleQuery query = createQuery("(//dummyNode | //dummyNodeB)/dummyNode/dummyNode");

        DummyRoot tree = tree(() -> root(
            node(
                node(
                    node()
                )
            ),
            nodeB(
                node()
            )
        ));

        assertEquals(0, query.getRuleChainVisits().size());
        assertExpression("docOrder((((/)/descendant::(element(dummyNode) | element(dummyNodeB)))/child::element(dummyNode))/child::element(dummyNode))", query.getFallbackExpr());

        tree.descendantsOrSelf().forEach(n -> {
            List<Node> results = query.evaluate(n);
            assertEquals(1, results.size());
            assertEquals(followPath(tree, "000"), results.get(0));
        });
    }

    @Test
    public void ruleChainVisitWithVariable() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("self::node()[matches(convertUntyped(data(@SimpleName)), \"a\", \"\")]", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("((/)/descendant::element(Q{}dummyNode))[matches(convertUntyped(data(@SimpleName)), \"a\", \"\")]", query.getFallbackExpr());
    }

    @Test
    public void ruleChainVisitWithVariable2() {
        PropertyDescriptor<String> testClassPattern = PropertyFactory.stringProperty("testClassPattern").desc("test").defaultValue("a").build();
        SaxonXPathRuleQuery query = createQuery("//dummyNode[matches(@SimpleName, $testClassPattern)]/foo", testClassPattern);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        assertEquals(1, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        assertEquals(2, query.nodeNameToXPaths.size());
        assertExpression("(self::node()[matches(convertUntyped(data(@SimpleName)), \"a\", \"\")])/child::element(Q{}foo)", query.getExpressionsForLocalNameOrDefault("dummyNode").get(0));
        assertExpression("docOrder((((/)/descendant::element(Q{}dummyNode))[matches(convertUntyped(data(@SimpleName)), \"a\", \"\")])/child::element(Q{}foo))", query.getFallbackExpr());
    }

    private static void assertExpression(String expected, Expression actual) {
        assertEquals(normalizeExprDump(expected),
                     normalizeExprDump(actual.toString()));
    }

    private static String normalizeExprDump(String dump) {
        return dump.replaceAll("Q\\{[^}]*+}", "") // remove namespaces
                   // generated variable ids
                   .replaceAll("\\$qq:qq-?\\d+", "\\$qq:qq000")
                   .replaceAll("\\$zz:zz-?\\d+", "\\$zz:zz000");
    }

    private static void assertQuery(int resultSize, String xpath, Node node, PropertyDescriptor<?>... descriptors) {
        SaxonXPathRuleQuery query = createQuery(xpath, descriptors);
        List<Node> result = query.evaluate(node);
        assertEquals(resultSize, result.size());
    }

    private static SaxonXPathRuleQuery createQuery(String xpath, PropertyDescriptor<?>... descriptors) {
        Map<PropertyDescriptor<?>, Object> props = new HashMap<>();
        if (descriptors != null) {
            for (PropertyDescriptor<?> prop : descriptors) {
                props.put(prop, prop.defaultValue());
            }
        }

        return new SaxonXPathRuleQuery(
            xpath,
            XPathVersion.XPATH_2_0,
            props,
            XPathHandler.getHandlerForFunctionDefs(imageIsFunction()),
            DeprecatedAttrLogger.noop()
        );
    }

    @NonNull
    private static AbstractXPathFunctionDef imageIsFunction() {
        return new AbstractXPathFunctionDef("imageIs", "dummy") {
            @Override
            public SequenceType[] getArgumentTypes() {
                return new SequenceType[] {SequenceType.SINGLE_STRING};
            }

            @Override
            public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
                return SequenceType.SINGLE_BOOLEAN;
            }

            @Override
            public ExtensionFunctionCall makeCallExpression() {
                return new ExtensionFunctionCall() {
                    @Override
                    public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                        Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                        return BooleanValue.get(arguments[0].head().getStringValue().equals(contextNode.getImage()));
                    }
                };
            }
        };
    }
}