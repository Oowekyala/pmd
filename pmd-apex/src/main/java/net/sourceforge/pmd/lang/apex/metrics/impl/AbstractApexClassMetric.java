/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface.TypeKind;
import net.sourceforge.pmd.lang.metrics.Metric;

/**
 * Base class for Apex metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractApexClassMetric<R extends Number> implements Metric<ASTUserClassOrInterface<?>, R> {

    @Override
    public boolean supports(ASTUserClassOrInterface<?> node) {
        return node.getTypeKind() == TypeKind.CLASS;
    }
}
