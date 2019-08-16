/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric.AtfdOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric.LocOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric.NcssOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NpathMetric;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * Keys identifying standard operation metrics.
 */
public class JavaOperationMetricKey implements MetricKey<ASTBlock> {

    /**
     * Access to Foreign Data.
     *
     * @see AtfdMetric
     */
    public static final MetricKey<ASTBlock> ATFD = new JavaOperationMetricKey("ATFD", new AtfdOperationMetric());

    /**
     * Cyclomatic complexity.
     *
     * @see CycloMetric
     */
    public static final MetricKey<ASTBlock> CYCLO = new JavaOperationMetricKey("CYCLO", new CycloMetric());

    /**
     * Non Commenting Source Statements.
     *
     * @see NcssMetric
     */
    public static final MetricKey<ASTBlock> NCSS = new JavaOperationMetricKey("NCSS", new NcssOperationMetric());

    /**
     * Lines of Code.
     *
     * @see LocMetric
     */
    public static final MetricKey<ASTBlock> LOC = new JavaOperationMetricKey("LOC", new LocOperationMetric());


    /**
     * N-path complexity.
     *
     * @see NpathMetric
     */
    public static final MetricKey<ASTBlock> NPATH = new JavaOperationMetricKey("NPATH", new NpathMetric());


    private final String name;
    private final Metric<ASTBlock> calculator;


    private JavaOperationMetricKey(String name, Metric<ASTBlock> m) {
        this.name = name;
        calculator = m;
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public Metric<ASTBlock> getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(Node node) {
        return node instanceof ASTBlock && calculator.supports((ASTBlock) node);
    }


    public static List<MetricKey<ASTBlock>> values() {
        return Arrays.asList(
            JavaOperationMetricKey.ATFD,
            JavaOperationMetricKey.CYCLO,
            JavaOperationMetricKey.NCSS,
            JavaOperationMetricKey.LOC,
            JavaOperationMetricKey.NPATH
        );
    }
}
