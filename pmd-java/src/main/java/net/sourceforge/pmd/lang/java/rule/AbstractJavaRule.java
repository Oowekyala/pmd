/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static net.sourceforge.pmd.lang.java.AbstractJavaHandler.asList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaProcessingStage;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPackage;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArgument;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardBounds;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;


/**
 * Base class for Java rules. Any rule written in Java to analyse Java source should extend from
 * this base class.
 *
 * TODO add documentation
 *
 */
public abstract class AbstractJavaRule extends AbstractRule implements JavaParserVisitor, ImmutableLanguage {

    public AbstractJavaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        // Enable Type Resolution on Java Rules by default
        super.setTypeResolution(true);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Node element : nodes) {
            /*
                It is important to note that we are assuming that all nodes here are of type Compilation Unit,
                but our caller method may be called with any type of node, and that's why we need to check the kind
                of instance of each element
            */
            for (ASTCompilationUnit acu : asList(element)) {
                visit(acu, ctx);
            }
        }
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
        ASTClassOrInterfaceDeclaration c = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (c != null) {
            return c.getImage();
        }
        return null;
    }

    public static boolean isQualifiedName(@Nullable String node) {
        return node != null && node.indexOf('.') != -1;
    }

    public static boolean importsPackage(ASTCompilationUnit node, String packageName) {
        List<ASTImportDeclaration> nodes = node.findChildrenOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration n : nodes) {
            if (n.getPackageName().startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isSuppressed(Node node) {
        return JavaRuleViolation.isSupressed(node, this);
    }


    @Override
    public final boolean dependsOn(AstProcessingStage<?> stage) {
        if (!(stage instanceof JavaProcessingStage)) {
            throw new IllegalArgumentException("Processing stage wasn't a Java one: " + stage);
        }
        return ((JavaProcessingStage) stage).ruleDependsOnThisStage(this);
    }

    // FIXME those are not in sync with JavaParserVisitorAdapter
    // See #1786



    public Object visit(ASTPackage node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    public Object visit(ASTAnnotation node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    public Object visit(ASTExpression node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }


    @Deprecated
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTShiftExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTPrimaryPrefix node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    @Deprecated
    public Object visit(ASTPrimarySuffix node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }


    @Deprecated
    public Object visit(ASTPrimaryExpression node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }


    @Deprecated
    public Object visit(ASTAllocationExpression node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTTypeArgument node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTWildcardBounds node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTArguments node, Object data) {
        return null;
    }
}
