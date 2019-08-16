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
    public Map<MetricKey<?, ?>, Number> computeAllMetricsFor(Node node) {
        Map<MetricKey<?, ?>, Number> results = new HashMap<>();
        for (MetricKey<? extends Node, ? extends Number> metric : getMetrics()) {
            Number n = computeCapture(metric, node);
            if (n != null) results.put(metric, n);
        }

        return results;
    }

    private static <N extends Node, R extends Number> Number computeCapture(MetricKey<N, R> key, Node n) {
        if (key.supports(n)) {
            return key.computeFor((N) n);
        } else {
            return null;
        }
    }

}
