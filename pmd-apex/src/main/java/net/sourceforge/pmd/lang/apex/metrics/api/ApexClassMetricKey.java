/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.api;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.impl.WmcMetric;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public class ApexClassMetricKey implements MetricKey<ASTUserClassOrInterface> {

    public static final MetricKey<ASTUserClassOrInterface> WMC = new ApexClassMetricKey("WMC", new WmcMetric());


    private final String name;
    private final Metric<? super ASTUserClassOrInterface> calculator;

    private ApexClassMetricKey(String name, Metric<? super ASTUserClassOrInterface> m) {
        this.name = name;
        calculator = m;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Metric<? super ASTUserClassOrInterface> getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(Node node) {
        return node instanceof ASTUserClassOrInterface<?>
            && getCalculator().supports((ASTUserClassOrInterface<?>) node);
    }

}
