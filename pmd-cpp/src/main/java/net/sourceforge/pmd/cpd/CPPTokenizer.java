/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.MalformedSourceException;
import net.sourceforge.pmd.lang.cpp.ast.CppTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * The C++ tokenizer.
 */
public class CPPTokenizer extends JavaCCTokenizer {

    private boolean skipBlocks;
    private Pattern skipBlocksStart;
    private Pattern skipBlocksEnd;

    public CPPTokenizer() {
        setProperties(new Properties()); // set the defaults
    }

    // override to make it visible in tests
    @Override
    protected TokenManager<JavaccToken> getLexerForSource(SourceCode sourceCode) throws IOException {
        return super.getLexerForSource(sourceCode);
    }

    /**
     * Sets the possible options for the C++ tokenizer.
     *
     * @param properties
     *            the properties
     * @see #OPTION_SKIP_BLOCKS
     * @see #OPTION_SKIP_BLOCKS_PATTERN
     */
    public void setProperties(Properties properties) {
        skipBlocks = Boolean.parseBoolean(properties.getProperty(OPTION_SKIP_BLOCKS, Boolean.TRUE.toString()));
        if (skipBlocks) {
            String skipBlocksPattern = properties.getProperty(OPTION_SKIP_BLOCKS_PATTERN, DEFAULT_SKIP_BLOCKS_PATTERN);
            String[] split = skipBlocksPattern.split("\\|", 2);
            skipBlocksStart = CppBlockSkipper.compileSkipMarker(split[0]);
            if (split.length == 1) {
                skipBlocksEnd = skipBlocksStart;
            } else {
                skipBlocksEnd = CppBlockSkipper.compileSkipMarker(split[1]);
            }
        }
    }


    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return new TokenDocumentBehavior(CppTokenKinds.TOKEN_NAMES) {

            @Override
            protected TextDocument translate(TextDocument text) throws MalformedSourceException {
                if (skipBlocks) {
                    text = new CppBlockSkipper(text, skipBlocksStart, skipBlocksEnd).translateDocument();
                }
                return new CppEscapeTranslator(text).translateDocument();
            }
        };
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return CppTokenKinds.newTokenManager(sourceCode);
    }
}
