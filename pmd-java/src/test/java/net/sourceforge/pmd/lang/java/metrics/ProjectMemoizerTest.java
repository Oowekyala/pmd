/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * @author Clément Fournier
 */
public class ProjectMemoizerTest {

    private MetricKey<ASTAnyTypeDeclaration, Integer> classMetricKey = MetricKey.of(null, new RandomClassMetric());
    private MetricKey<ASTMethodOrConstructorDeclaration, Integer> opMetricKey = MetricKey.of(null, new RandomOperationMetric());


    @Test
    public void memoizationTest() {
        ASTCompilationUnit acu = ParserTstUtil.parseJavaDefaultVersion(MetricsVisitorTestData.class);

        List<Integer> expected = visitWith(acu);
        List<Integer> real = visitWith(acu);

        assertEquals(expected, real);
    }


    @Test
    public void forceMemoizationTest() {

        ASTCompilationUnit acu = ParserTstUtil.parseJavaDefaultVersion(MetricsVisitorTestData.class);

        List<Integer> reference = visitWith(acu);
        List<Integer> real = visitWith(acu);

        assertEquals(reference.size(), real.size());

        // we force recomputation so each result should be different
        for (int i = 0; i < reference.size(); i++) {
            assertNotEquals(reference.get(i), real.get(i));
        }
    }


    private List<Integer> visitWith(ASTCompilationUnit acu) {

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                result.add(opMetricKey.computeFor(node));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                result.add((int) JavaMetricsComputer.getInstance().computeForType(classMetricKey, node,
                                                                                  MetricOptions.emptyOptions()));
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    private class RandomOperationMetric extends AbstractMetric<ASTMethodOrConstructorDeclaration, Integer> {

        private Random random = new Random();

        @Override
        public boolean supports(ASTMethodOrConstructorDeclaration node) {
            return true;
        }

        @Override
        public Integer computeFor(ASTMethodOrConstructorDeclaration node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractJavaClassMetric<Integer> {

        private Random random = new Random();


        @Override
        public Integer computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            return random.nextInt();
        }
    }

}
