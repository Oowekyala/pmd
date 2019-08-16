/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of metrics.
 *
 * @author Clément Fournier
 */
public final class JavaMetrics {


    private JavaMetrics() { // Cannot be instantiated

    }


    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTBlock> key, ASTAnyTypeDeclaration node, ResultOption resultOption) {
        return get(key, node, MetricOptions.emptyOptions(), resultOption);
    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     * @param options      The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTBlock> key, ASTAnyTypeDeclaration node,
                             MetricOptions options, ResultOption resultOption) {
        return key.aggregate(findOperations(node).stream(), options, resultOption);
    }


    private static List<ASTBlock> findOperations(ASTAnyTypeDeclaration node) {

        List<ASTBlock> operations = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.jjtGetNumChildren() > 0 && decl.getLastChild() instanceof ASTMethodOrConstructorDeclaration) {
                ASTBlock block = ((ASTMethodOrConstructorDeclaration) decl.getLastChild()).getBody();
                if (block != null) {
                    operations.add(block);
                }
            }
        }
        return operations;
    }

}
