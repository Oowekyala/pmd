/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import net.sourceforge.pmd.cpd.Tokenizer.CpdProperties;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class SwiftTokenizerTest extends CpdTextComparisonTest {

    public SwiftTokenizerTest() {
        super(".swift");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/swift/cpd/testdata";
    }

    @NotNull
    @Override
    public Tokenizer newTokenizer(@NotNull CpdProperties properties) {
        return new SwiftTokenizer();
    }


    @Test
    public void testSwift42() {
        doTest("Swift4.2");
    }

    @Test
    public void testSwift50() {
        doTest("Swift5.0");
    }

    @Test
    public void testSwift51() {
        doTest("Swift5.1");
    }

    @Test
    public void testSwift52() {
        doTest("Swift5.2");
    }

    @Test
    public void testStackoverflowOnLongLiteral() {
        doTest("Issue628");
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }
}
