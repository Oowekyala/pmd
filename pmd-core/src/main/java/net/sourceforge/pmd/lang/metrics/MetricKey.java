/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Key identifying a metric. Such keys <i>must</i> implement the hashCode method. Enums are well fitted to serve as
 * metric keys.
 *
 * @param <N> Type of nodes the metric can be computed on
 * @author Clément Fournier
 * @since 5.8.0
 */
public interface MetricKey<N extends Node, R extends Number> {

    /**
     * Returns the name of the metric.
     *
     * @return The name of the metric
     */
    String name();


    /**
     * Returns the object used to calculate the metric.
     *
     * @return The calculator
     */
    Metric<N, R> getCalculator();


    /**
     * Returns true if the metric held by this key can be computed on this node.
     *
     * @param node The node to test
     *
     * @return Whether or not the metric can be computed on this node
     */
    boolean supports(Node node);


    /**
     * Computes this metric on an AST node, with the given {@link MetricOptions}.
     *
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    default R computeFor(N node, MetricOptions options) {
        if (!this.supports(node)) {
            return 0;
        }
        ParameterizedMetricKey<N, R> paramKey = ParameterizedMetricKey.getInstance(this, options);
        return node.getData().computeIfAbsent(paramKey, () -> this.getCalculator().computeFor(node, options));
    }


    /**
     * Computes this metric on an AST node, with the default options.
     *
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    default R computeFor(N node) {
        return computeFor(node, MetricOptions.emptyOptions());
    }


    /**
     * Computes an aggregate result using a ResultOption.
     *
     * @param options The options of the metric
     * @param option  The type of result to compute
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    default double aggregate(List<N> nodes, ResultOption option, MetricOptions options) {
        DoubleStream doubleStream = nodes.stream()
                                         .filter(this::supports)
                                         .mapToDouble(op -> this.computeFor(op, options).doubleValue())
                                         .filter(it -> !Double.isNaN(it));

        switch (option) {
        case SUM:
            return doubleStream.sum();
        case HIGHEST:
            return doubleStream.max().orElse(Double.NaN);
        case AVERAGE:
            return doubleStream.average().orElse(Double.NaN);
        default:
            return Double.NaN;
        }
    }


    /**
     * Creates a new metric key from its metric and name.
     *
     * @param name   The name of the metric
     * @param metric The metric to use
     * @param <T>    Type of node the metric can be computed on
     *
     * @return The metric key
     */
    static <T extends Node, R extends Number> MetricKey<T, R> of(final String name, Class<T> nodeClass, final Metric<T, R> metric) {
        return new MetricKey<T, R>() {
            @Override
            public String name() {
                return name;
            }


            @Override
            public Metric<T, R> getCalculator() {
                return metric;
            }


            @Override
            public boolean supports(Node node) {
                return nodeClass.isInstance(node) && metric.supports(nodeClass.cast(node));
            }


            @Override
            public boolean equals(Object obj) {
                return obj != null && getClass() == obj.getClass()
                    && Objects.equals(name(), ((MetricKey) obj).name())
                    && Objects.equals(getCalculator(), ((MetricKey) obj).getCalculator());
            }


            @Override
            public int hashCode() {
                return (metric != null ? metric.hashCode() * 31 : 0) + (name != null ? name.hashCode() : 0);
            }
        };
    }
}
