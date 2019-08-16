/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Lines of Code. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @see NcssMetric
 * @since June 2017
 */
public final class LocMetric {


    public static final class LocOperationMetric extends AbstractJavaOperationMetric<Integer> {

        @Override
        public Integer computeFor(ASTBlock node, MetricOptions options) {
            return 1 + node.getEndLine() - node.getBeginLine();
        }
    }

    public static final class LocClassMetric extends AbstractJavaClassMetric<Integer> {

        @Override
        public boolean supports(ASTAnyTypeDeclaration node) {
            return true;
        }


        @Override
        public Integer computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            return 1 + node.getEndLine() - node.getBeginLine();
        }


    }

}
