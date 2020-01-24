/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class ApexAssertionsShouldIncludeMessageRule extends AbstractApexUnitTestRule {

    private static final String ASSERT = "System.assert";
    private static final String ASSERT_EQUALS = "System.assertEquals";
    private static final String ASSERT_NOT_EQUALS = "System.assertNotEquals";

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        String methodName = node.getFullMethodName();

        if (ASSERT.equalsIgnoreCase(methodName) && node.getNumChildren() == 2) {
            addViolation(data, node);
        } else if ((ASSERT_EQUALS.equalsIgnoreCase(methodName)
                || ASSERT_NOT_EQUALS.equalsIgnoreCase(methodName))
                && node.getNumChildren() == 3) {
            addViolation(data, node);
        }
        return data;
    }
}
