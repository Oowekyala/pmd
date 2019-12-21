/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Deque;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Represents an expression, in the most general sense.
 * This corresponds to the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-Expression">Expression</a>
 * of the JLS.
 *
 * <p>From 7.0.0 on, this is an interface which all expression nodes
 * implement.
 *
 * <p>Expressions are required to be constant in some parts of the grammar
 * (in {@link ASTSwitchLabel SwitchLabel}, {@link ASTAnnotation Annotation},
 * {@link ASTDefaultValue DefaultValue}). A <i>constant expression</i> is
 * represented as a normal expression subtree, which does not feature any
 * {@link ASTMethodReference MethodReference}, {@link ASTLambdaExpression LambdaExpression}
 * or {@link ASTAssignmentExpression AssignmentExpression}.
 *
 *
 * <pre class="grammar">
 *
 * (: In increasing precedence order :)
 * Expression ::= {@link ASTLambdaExpression LambdaExpression}
 *              | {@link ASTAssignmentExpression AssignmentExpression}
 *              | {@link ASTConditionalExpression ConditionalExpression}
 *              | {@link ASTInfixExpression InfixExpression}
 *              | {@link ASTUnaryExpression PrefixExpression} | {@link ASTCastExpression CastExpression}
 *              | {@link ASTUnaryExpression PostfixExpression}
 *              | {@link ASTSwitchExpression SwitchExpression}
 *              | {@link ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public interface ASTExpression extends JavaNode, TypeNode, ASTMemberValue {

    /**
     * Always returns true. This is to allow XPath queries
     * to query like {@code /*[@Expression=true()]} to match
     * any expression, but is useless in Java code.
     */
    default boolean isExpression() {
        return true;
    }


    /**
     * Returns the number of parenthesis levels around this expression.
     * If this method returns 0, then no parentheses are present.
     *
     * <p>E.g. the expression {@code (a + b)} is parsed as an AdditiveExpression
     * whose parenthesisDepth is 1, and in {@code ((a + b))} it's 2.
     *
     * <p>This is to avoid the parentheses interfering with analysis.
     * Parentheses already influence parsing by breaking the natural
     * precedence of operators. It would mostly hide false positives
     * to make a ParenthesizedExpr node, because it would make semantically
     * equivalent nodes have a very different representation.
     *
     * <p>On the other hand, when a rule explicitly cares about parentheses,
     * then this attribute may be used to find out whether parentheses
     * were mentioned, so no information is lost.
     */
    int getParenthesisDepth();


    /**
     * Returns the subexpressions of this expression, in the order in
     * which they're evaluated. For example, for the expression:
     * <pre>{@code
     *  <ola.foo(a, b) + c>
     * }</pre>
     * the returned stream will contain the following expressions in order:
     * <pre>{@code
     *  <ola>, <a>, <b>, <ola.foo(a, b)>, <c>, <ola.foo(a, b) + c>
     * }</pre>
     *
     */
    default NodeStream<ASTExpression> getSubExpressions() {
        Deque<ASTExpression> deque = new LinkedList<>();
        TreeWalkUtils.postOrderWalk(this, n -> {
            if (n instanceof ASTExpression) {
                deque.push((ASTExpression) n);
            }
        });
        return NodeStream.fromIterable(deque::descendingIterator);
    }


    /**
     * Returns true if this expression has at least one level of parentheses.
     * The specific depth can be fetched with {@link #getParenthesisDepth()}.
     */
    default boolean isParenthesized() {
        return getParenthesisDepth() > 0;
    }


    /**
     * Returns the constant value of this expression, if this is a constant expression.
     * Otherwise, or if some references couldn't be resolved, returns null.
     * TODO implement that on other types
     */
    default @Nullable Object getConstValue() {
        return null;
    }


    /** Returns true if this expression is a compile-time constant, and is inlined. */
    default boolean isCompileTimeConstant() {
        return getConstValue() != null;
    }

}
