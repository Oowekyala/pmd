/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NopaTestRule extends AbstractMetricTestRule {

    @Override
    protected MetricKey<ASTAnyTypeDeclaration> getClassKey() {
        return JavaClassMetricKey.NOPA;
    }


    @Override
    protected MetricKey<ASTBlock> getOpKey() {
        return null;
    }
}
