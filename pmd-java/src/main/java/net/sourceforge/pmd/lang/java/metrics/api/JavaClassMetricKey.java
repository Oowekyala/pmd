/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric.AtfdClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric.LocClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric.NcssClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NoamMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NopaMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.TccMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.WmcMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.WocMetric;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * Keys identifying standard class metrics.
 */
public final class JavaClassMetricKey implements MetricKey<ASTAnyTypeDeclaration> {

    /**
     * Access to Foreign Data.
     *
     * @see AtfdMetric
     */
    public static final JavaClassMetricKey ATFD = new JavaClassMetricKey("ATFD", new AtfdClassMetric());

    /**
     * Weighed Method Count.
     *
     * @see WmcMetric
     */
    public static final JavaClassMetricKey WMC = new JavaClassMetricKey("WMC", new WmcMetric());

    /**
     * Non Commenting Source Statements.
     *
     * @see NcssMetric
     */
    public static final JavaClassMetricKey NCSS = new JavaClassMetricKey("NCSS", new NcssClassMetric());

    /**
     * Lines of Code.
     *
     * @see LocMetric
     */
    public static final JavaClassMetricKey LOC = new JavaClassMetricKey("LOC", new LocClassMetric());

    /**
     * Number of Public Attributes.
     *
     * @see NopaMetric
     */
    public static final JavaClassMetricKey NOPA = new JavaClassMetricKey("NOPA", new NopaMetric());

    /**
     * Number of Accessor Methods.
     *
     * @see NopaMetric
     */
    public static final JavaClassMetricKey NOAM = new JavaClassMetricKey("NOAM", new NoamMetric());

    /**
     * Weight of class.
     *
     * @see WocMetric
     */
    public static final JavaClassMetricKey WOC = new JavaClassMetricKey("WOC", new WocMetric());

    /**
     * Tight Class Cohesion.
     *
     * @see TccMetric
     */
    public static final JavaClassMetricKey TCC = new JavaClassMetricKey("TCC", new TccMetric());


    private final String name;
    private final Metric<ASTAnyTypeDeclaration> calculator;


    private JavaClassMetricKey(String name, Metric<ASTAnyTypeDeclaration> m) {
        this.name = name;
        calculator = m;
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public Metric<ASTAnyTypeDeclaration> getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(Node node) {
        return node instanceof ASTAnyTypeDeclaration && getCalculator().supports((ASTAnyTypeDeclaration) node);
    }

}
