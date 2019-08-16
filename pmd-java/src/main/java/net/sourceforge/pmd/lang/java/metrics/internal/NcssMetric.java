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
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.NcssVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Non-commenting source statements. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @see LocMetric
 * @since June 2017
 */
public final class NcssMetric {


    /** Variants of NCSS. */
    public enum NcssOption implements MetricOption {
        /** Counts import and package statement. This makes the metric JavaNCSS compliant. */
        COUNT_IMPORTS("countImports");

        private final String vName;


        NcssOption(String valueName) {
            this.vName = valueName;
        }


        @Override
        public String valueName() {
            return vName;
        }
    }

    public static final class NcssClassMetric extends AbstractJavaClassMetric<Integer> {

        @Override
        public boolean supports(ASTAnyTypeDeclaration node) {
            return true;
        }


        @Override
        public Integer computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            MutableInt ncss = (MutableInt) node.jjtAccept(new NcssVisitor(options, node), new MutableInt(0));
            return ncss.getValue();
        }

    }

    public static final class NcssOperationMetric extends AbstractJavaOperationMetric<Integer> {

        @Override
        public Integer computeFor(ASTBlock node, MetricOptions options) {
            MutableInt ncss = (MutableInt) node.jjtAccept(new NcssVisitor(options, node), new MutableInt(0));
            return ncss.getValue();
        }

    }

}
