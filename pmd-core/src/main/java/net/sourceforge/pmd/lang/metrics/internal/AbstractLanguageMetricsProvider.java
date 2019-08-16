/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics.internal;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * Base implementation for {@link LanguageMetricsProvider}.
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
public abstract class AbstractLanguageMetricsProvider implements LanguageMetricsProvider {


    protected AbstractLanguageMetricsProvider() {
    }


    @Override
    public Map<MetricKey<?>, Double> computeAllMetricsFor(Node node) {
        Map<MetricKey<?>, Double> results = new HashMap<>();
        for (MetricKey<? extends Node> metric : getMetrics()) {
            double n = computeCapture(metric, node);
            if (!Double.isNaN(n)) {
                results.put(metric, n);
            }
        }

        return results;
    }

    private static <N extends Node> double computeCapture(MetricKey<N> key, Node n) {
        if (key.supports(n)) {
            return key.computeFor((N) n);
        } else {
            return Double.NaN;
        }
    }

}
