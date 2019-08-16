/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.api;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public class ApexOperationMetricKey<R extends Number> implements MetricKey<ASTMethod, R> {

    public static final MetricKey<ASTMethod, Integer> CYCLO = new ApexOperationMetricKey<>("CYCLO", new CycloMetric());


    private final String name;
    private final Metric<ASTMethod, R> calculator;


    private ApexOperationMetricKey(String name, Metric<ASTMethod, R> m) {
        this.name = name;
        calculator = m;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Metric<ASTMethod, R> getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(Node node) {
        return node instanceof ASTMethod && getCalculator().supports((ASTMethod) node);
    }
}
