/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.AtfdBaseVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;

/**
 * Access to Foreign Data. Quantifies the number of foreign fields accessed directly or via accessors.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class AtfdMetric {


    public static final class AtfdOperationMetric extends AbstractJavaOperationMetric<Integer> {


        @Override
        public Integer computeFor(ASTBlock node, MetricOptions options) {
            return ((MutableInt) node.jjtAccept(new AtfdBaseVisitor(), new MutableInt(0))).getValue();
        }

    }

    public static final class AtfdClassMetric extends AbstractJavaClassMetric<Integer> {

        @Override
        public Integer computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            // TODO maybe consider code outside methods
            return (int) JavaMetrics.get(JavaOperationMetricKey.ATFD, node, options, ResultOption.SUM);
        }


    }


}
