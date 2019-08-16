/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;


/**
 * Base class for operation metrics. Can be applied on method and constructor declarations, and
 * lambda expressions.
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaOperationMetric extends AbstractMetric<ASTBlock> {

    /**
     * Returns true if the metric can be computed on this operation. By default, abstract operations are filtered out.
     *
     * @param node The operation
     *
     * @return True if the metric can be computed on this operation
     */
    @Override
    public boolean supports(ASTBlock node) {
        return true;
    }


}
