/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public class NPathTestRule extends AbstractMetricTestRule {

    @Override
    protected MetricKey<ASTAnyTypeDeclaration> getClassKey() {
        return null;
    }


    @Override
    protected MetricKey<ASTBlock> getOpKey() {
        return JavaOperationMetricKey.NPATH;
    }
}
