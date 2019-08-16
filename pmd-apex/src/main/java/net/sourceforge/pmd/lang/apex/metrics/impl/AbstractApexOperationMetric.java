/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;

/**
 * Base class for Apex operation metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractApexOperationMetric<R extends Number> extends AbstractMetric<ASTMethod, R> {

    /**
     * Checks if the metric can be computed on the node. For now, we filter out {@literal <clinit>, <init> and clone},
     * which are present in all apex class nodes even if they're not implemented, which may yield unexpected results.
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed
     */
    @Override
    public boolean supports(ASTMethod node) {
        return !node.isSynthetic()
            && !node.getFirstChildOfType(ASTModifierNode.class).isAbstract();
    }
}
