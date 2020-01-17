/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.apache.commons.io.input.CharSequenceReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.vf.VfTokenManager;
import net.sourceforge.pmd.util.IOUtil;

/**
 * @author sergey.gorbaty
 *
 */
public class VfTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        CharSequenceReader input = new CharSequenceReader(sourceCode.getCodeBuffer());
        return new VfTokenManager(IOUtil.skipBOM(input));
    }
}
