/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexClassMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexOperationMetric;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileVisitorTest;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirrorTest {

    private static ApexNode<Compilation> acu;
    private MetricKey<ASTUserClassOrInterface> classMetricKey = MetricKey.of(null, ASTUserClassOrInterface.class, new RandomClassMetric());
    private MetricKey<ASTMethod> opMetricKey = MetricKey.of(null, ASTMethod.class, new RandomOperationMetric());


    static {
        try {
            acu = parseAndVisitForString(
                IOUtils.toString(ApexMultifileVisitorTest.class.getResourceAsStream("MetadataDeployController.cls"),
                        StandardCharsets.UTF_8));
        } catch (IOException ioe) {
            // Should definitely not happen
        }
    }


    @Test
    public void memoizationTest() {


        List<Integer> expected = visitWith(acu);
        List<Integer> real = visitWith(acu);

        assertEquals(expected, real);
    }


    @Test
    public void forceMemoizationTest() {

        List<Integer> reference = visitWith(acu);
        List<Integer> real = visitWith(acu);

        assertEquals(reference.size(), real.size());

        // we force recomputation so each result should be different
        for (int i = 0; i < reference.size(); i++) {
            assertNotEquals(reference.get(i), real.get(i));
        }
    }


    private List<Integer> visitWith(ApexNode<Compilation> acu) {

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                result.add((int) opMetricKey.computeFor(node));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTUserClass node, Object data) {
                result.add((int) classMetricKey.computeFor(node));
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    static ApexNode<Compilation> parseAndVisitForString(String source) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(ApexLanguageModule.NAME)
                                                                        .getDefaultVersion().getLanguageVersionHandler();
        ApexNode<Compilation> acu = ApexParserTestHelpers.parse(source);
        languageVersionHandler.getSymbolFacade().start(acu);
        return acu;
    }

    private static class RandomOperationMetric extends AbstractApexOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTMethod node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private static class RandomClassMetric extends AbstractApexClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTUserClassOrInterface node, MetricOptions options) {
            return random.nextInt();
        }
    }


}
