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
public class ApexClassMetricKey<R extends Number> implements MetricKey<ASTUserClassOrInterface<?>, R> {

    public static final MetricKey<ASTUserClassOrInterface<?>, Integer> WMC = new ApexClassMetricKey<>("WMC", new WmcMetric());


    private final String name;
    private final Metric<ASTUserClassOrInterface<?>, R> calculator;

    private ApexClassMetricKey(String name, Metric<ASTUserClassOrInterface<?>, R> m) {
        this.name = name;
        calculator = m;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Metric<ASTUserClassOrInterface<?>, R> getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(Node node) {
        return node instanceof ASTUserClassOrInterface<?>
            && getCalculator().supports((ASTUserClassOrInterface<?>) node);
    }

}
