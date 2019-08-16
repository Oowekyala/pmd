/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;

/**
 * User-bound façade of the Apex metrics framework.
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated Use directly the methods provided by {@link MetricKey}
 *
 */
@Deprecated
public final class ApexMetrics {


    private ApexMetrics() { // Cannot be instantiated

    }


    /**
     * Computes the standard value of the metric identified by its code on a class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTUserClassOrInterface<?>, ?> key, ASTUserClass node) {
        return key.computeFor(node).doubleValue();
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting metric options with the {@code
     * options} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTUserClassOrInterface<?>, ?> key, ASTUserClass node, MetricOptions options) {
        return key.computeFor(node, options).doubleValue();
    }


    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethod, ?> key, ASTMethod node) {
        return key.computeFor(node).doubleValue();
    }


    /**
     * Computes a metric identified by its key on a operation AST node, possibly selecting metric options with the
     * {@code options} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethod, ?> key, ASTMethod node, MetricOptions options) {
        return key.computeFor(node, options).doubleValue();
    }


    private static List<ASTMethod> findOperations(ASTUserClassOrInterface<?> node) {
        List<ASTMethod> candidates = node.findChildrenOfType(ASTMethod.class);
        List<ASTMethod> result = new ArrayList<>(candidates);
        for (ASTMethod method : candidates) {
            if (method.isSynthetic()) {
                result.remove(method);
            }
        }
        return result;
    }

    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     * {@code null}
     */
    public static double get(MetricKey<ASTMethod, ?> key, ASTUserClassOrInterface<?> node, ResultOption resultOption) {
        return get(key, node, MetricOptions.emptyOptions(), resultOption);
    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param options      The options of the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     * {@code null}
     */
    public static double get(MetricKey<ASTMethod, ?> key, ASTUserClassOrInterface<?> node, MetricOptions options,
                             ResultOption resultOption) {
        return key.aggregate(findOperations(node), resultOption, options);
    }


}
