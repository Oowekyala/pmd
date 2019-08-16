/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Language-specific provider for metrics. Knows about all the metrics
 * defined for a language. Can be used e.g. to build GUI applications
 * like the designer, in a language independent way. Accessible through
 * {@link LanguageVersionHandler#getLanguageMetricsProvider()}.
 *
 * Note: this is experimental, ie unstable until 7.0.0, after which it will probably
 * be promoted to a real API.
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
@Experimental
public interface LanguageMetricsProvider {

    /**
     * Returns a list of all supported type metric keys
     * for the language.
     */
    List<? extends MetricKey<? extends Node, ? extends Number>> getMetrics();


    /**
     * Computes all metrics available on the given node.
     * The returned results may contain Double.NaN as a value.
     *
     * @param node Node to inspect
     *
     * @return A map of metric key to their result, possibly empty, but with no null value
     */
    Map<MetricKey<?, ?>, Number> computeAllMetricsFor(Node node);
}
