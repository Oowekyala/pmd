/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.fortran.cpd;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 * @author rpelisse
 *
 */
public class FortranTokenizerTest extends CpdTextComparisonTest {

    public FortranTokenizerTest() {
        super("fortran", ".for");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/fortran/cpd/testdata";
    }

    @Test
    public void testSample() {
        doTest("sample");
    }
}
