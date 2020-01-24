/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.errorprone;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class EmptyForeachStmtRule extends AbstractVmRule {

    @Override
    public Object visit(final ASTForeachStatement node, final Object data) {
        final ASTBlock block = node.getFirstChildOfType(ASTBlock.class);
        if (block.getNumChildren() == 0) {
            reportViolation(data, node);
        } else if (block.getNumChildren() == 1 && block.getChild(0) instanceof ASTText
                && StringUtils.isBlank(((AbstractVmNode) block.getChild(0)).getFirstToken().toString())) {
            reportViolation(data, node);
        }
        return super.visit(node, data);
    }

}
