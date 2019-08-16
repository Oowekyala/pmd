/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ParameterizedMetricKey;

/**
 * @author Clément Fournier
 */
public class ParameterizedMetricKeyTest {

    private static final MetricOptions DUMMY_VERSION_1 = MetricOptions.ofOptions(Options.DUMMY1, Options.DUMMY2);
    private static final MetricOptions DUMMY_VERSION_2 = MetricOptions.ofOptions(Options.DUMMY2);

    @Test
    public void testIdentity() {
        for (MetricKey<ASTAnyTypeDeclaration> key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            assertEquals(key1, key2);
            assertSame(key1, key2);
        }

        for (MetricKey<ASTBlock> key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            assertEquals(key1, key2);
            assertSame(key1, key2);
        }
    }


    @Test
    public void testVersioning() {
        for (MetricKey<ASTAnyTypeDeclaration> key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_2);
            assertNotEquals(key1, key2);
            assertNotSame(key1, key2);
        }

        for (MetricKey<ASTBlock> key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_2);
            assertNotEquals(key1, key2);
            assertNotSame(key1, key2);
        }
    }


    @Test
    public void testToString() {
        for (MetricKey<ASTAnyTypeDeclaration> key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, DUMMY_VERSION_1);
            assertTrue(key1.toString().contains(key1.key.name()));
            assertTrue(key1.toString().contains(key1.options.toString()));
        }
    }


    @Test
    public void testAdHocMetricKey() {

        MetricKey<ASTAnyTypeDeclaration> adHocKey = MetricKey.of("metric", ASTAnyTypeDeclaration.class, null);


        ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(adHocKey, DUMMY_VERSION_1);
        ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(adHocKey, DUMMY_VERSION_1);

        assertNotNull(key1);
        assertNotNull(key2);
        assertSame(key1, key2);
        assertEquals(key1, key2);
        assertTrue(key1.toString().contains(key1.key.name()));
        assertTrue(key1.toString().contains(key1.options.toString()));

    }


    private enum Options implements MetricOption {
        DUMMY1,
        DUMMY2;


        @Override
        public String valueName() {
            return null;
        }
    }


}
