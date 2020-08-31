/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.EscapeAwareReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.JavaEscapeReader;
import net.sourceforge.pmd.lang.jsp.ast.JspTokenKinds;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

public class JSPTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return JspTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected JavaccTokenDocument newTokenDoc(TextDocument textDoc) {
        return new JavaccTokenDocument(textDoc) {
            @Override
            public EscapeAwareReader newReader(Chars text) {
                return new JavaEscapeReader(text);
            }
        };
    }

}