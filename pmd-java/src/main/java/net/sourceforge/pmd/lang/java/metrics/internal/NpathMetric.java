/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;


import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.NpathBaseVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * NPath complexity. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NpathMetric extends AbstractJavaOperationMetric<Long> {

    @Override
    public Long computeFor(ASTBlock node, MetricOptions options) {
        return (Long) node.jjtAccept(NpathBaseVisitor.INSTANCE, null);
    }

}
