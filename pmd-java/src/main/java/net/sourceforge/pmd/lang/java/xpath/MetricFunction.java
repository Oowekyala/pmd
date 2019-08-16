/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import java.util.List;
import java.util.stream.Collectors;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * Implements the {@code metric()} XPath function. Takes the
 * string name of a metric and the context node and returns
 * the result if the metric can be computed, otherwise returns
 * {@link Double#NaN}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@InternalApi
@Deprecated
public class MetricFunction implements Function {

    @Override
    public Object call(Context context, List args) {

        if (args.isEmpty()) {
            throw new IllegalArgumentException(badMetricKeyArgMessage());
        }

        if (!(args.get(0) instanceof String)) {
            throw new IllegalArgumentException(badMetricKeyArgMessage());
        }

        String metricKeyName = (String) args.get(0);
        Node n = (Node) context.getNodeSet().get(0);

        return getMetric(n, metricKeyName);
    }


    public static String badOperationMetricKeyMessage() {
        return "This is not the name of an operation metric";
    }


    public static String badClassMetricKeyMessage() {
        return "This is not the name of a class metric";
    }


    public static String genericBadNodeMessage() {
        return "Incorrect node type: the 'metric' function cannot be applied";
    }


    public static String badMetricKeyArgMessage() {
        return "The 'metric' function expects the name of a metric key";
    }


    public static double getMetric(Node n, String metricKeyName) {
        List<? extends MetricKey<? extends Node>> keys = LanguageRegistry.findLanguageByTerseName("java")
                                                                         .getDefaultVersion()
                                                                         .getLanguageVersionHandler()
                                                                         .getLanguageMetricsProvider()
                                                                         .getMetrics()
                                                                         .stream()
                                                                         .filter(it -> metricKeyName.equals(it.name()))
                                                                         .filter(it -> it.supports(n))
                                                                         .collect(Collectors.toList());
        if (keys.isEmpty()) {
            throw new IllegalStateException(genericBadNodeMessage());
        } else if (keys.size() > 1) {
            throw new IllegalStateException(
                "Ambiguous metric name '" + metricKeyName + "', several metrics are supported on " + n);
        } else {
            return compute(keys.get(0), n);
        }
    }

    private static <T extends Node> double compute(MetricKey<T> key, Node node) {
        return key.computeFor((T) node);
    }

    public static void registerSelfInSimpleContext() {
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null,
                                                                                      "metric",
                                                                                      new MetricFunction());
    }
}
