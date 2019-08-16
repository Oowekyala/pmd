/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;


/**
 * Base class for class metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaClassMetric extends AbstractMetric<ASTAnyTypeDeclaration> {


    /**
     * Returns true if the metric can be computed on this type declaration. By default, annotation and interface
     * declarations are filtered out.
     *
     * @param node The type declaration
     *
     * @return True if the metric can be computed on this type declaration
     */
    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() != TypeKind.ANNOTATION && node.getTypeKind() != TypeKind.INTERFACE;
    }


    /**
     * Counts the operations matching the signature mask in this class.
     *
     * @param classNode The class on which to count
     * @param mask      The mask
     *
     * @return The number of operations matching the signature mask
     */
    protected int countMatchingOpSigs(ASTAnyTypeDeclaration classNode, JavaOperationSigMask mask) {
        int count = 0;
        List<ASTMethodOrConstructorDeclaration> decls = getMethodsAndConstructors(classNode);

        for (ASTMethodOrConstructorDeclaration decl : decls) {
            if (mask.covers(decl.getSignature())) {
                count++;
            }
        }

        return count;
    }


    /**
     * Counts the fields matching the signature mask in this class.
     *
     * @param classNode The class on which to count
     * @param mask      The mask
     *
     * @return The number of fields matching the signature mask
     */
    protected int countMatchingFieldSigs(ASTAnyTypeDeclaration classNode, JavaFieldSigMask mask) {
        int count = 0;
        List<ASTFieldDeclaration> decls = getFields(classNode);

        for (ASTFieldDeclaration decl : decls) {
            if (mask.covers(decl.getSignature())) {
                count++;
            }
        }

        return count;
    }


    /**
     * Gets a list of all methods and constructors declared in the class.
     *
     * @param node The class
     *
     * @return The list of all methods and constructors
     */
    protected List<ASTMethodOrConstructorDeclaration> getMethodsAndConstructors(ASTAnyTypeDeclaration node) {
        return getDeclarationsOfType(node, ASTMethodOrConstructorDeclaration.class);
    }


    /**
     * Gets a list of all fields declared in the class.
     *
     * @param node The class
     *
     * @return The list of all fields
     */
    protected List<ASTFieldDeclaration> getFields(ASTAnyTypeDeclaration node) {
        return getDeclarationsOfType(node, ASTFieldDeclaration.class);
    }


    private <T extends Node> List<T> getDeclarationsOfType(ASTAnyTypeDeclaration node, Class<T> tClass) {

        List<T> result = new ArrayList<>();
        List<ASTAnyTypeBodyDeclaration> decls = node.getDeclarations();

        for (ASTAnyTypeBodyDeclaration decl : decls) {
            if (decl.jjtGetNumChildren() > 0 && tClass.isInstance(decl.jjtGetChild(0))) {
                result.add(tClass.cast(decl.jjtGetChild(0)));
            }
        }

        return result;
    }


}
