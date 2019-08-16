/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Represents a key parameterized with its options. Used to index memoization maps.
 *
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 * @since 5.8.0
 */
public final class ParameterizedMetricKey<N extends Node, R extends Number> extends DataKey<R> {

    private static final Map<ParameterizedMetricKey<?, ?>, ParameterizedMetricKey<?, ?>> POOL = new HashMap<>();

    /** The metric key. */
    public final MetricKey<N, R> key;
    /** The options of the metric. */
    public final MetricOptions options;


    /** Used internally by the pooler. */
    private ParameterizedMetricKey(MetricKey<N, R> key, MetricOptions options) {
        super("metric." + key.name());
        this.key = key;
        this.options = options;
    }


    @Override
    public String toString() {
        return "ParameterizedMetricKey{key=" + key.name() + ", options=" + options + '}';
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof ParameterizedMetricKey
            && ((ParameterizedMetricKey) o).key.equals(key)
            && ((ParameterizedMetricKey) o).options.equals(options);
    }


    @Override
    public int hashCode() {
        return 31 * key.hashCode() + options.hashCode();
    }


    /**
     * Builds a parameterized metric key.
     *
     * @param key     The key
     * @param options The options
     * @param <N>     The type of node of the metric key
     *
     * @return An instance of parameterized metric key corresponding to the parameters
     */
    @SuppressWarnings("PMD.SingletonClassReturningNewInstance")
    public static <N extends Node, R extends Number> ParameterizedMetricKey<N, R> getInstance(MetricKey<N, R> key, MetricOptions options) {
        ParameterizedMetricKey<N, R> tmp = new ParameterizedMetricKey<>(key, options);
        if (!POOL.containsKey(tmp)) {
            POOL.put(tmp, tmp);
        }

        @SuppressWarnings("unchecked")
        ParameterizedMetricKey<N, R> result = (ParameterizedMetricKey<N, R>) POOL.get(tmp);
        return result;
    }
}
