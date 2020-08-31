/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dart.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.cpd.token.AntlrTokenFilter;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.dart.antlr4.Dart2Lexer;

/**
 * The Dart Tokenizer
 */
public class DartTokenizer extends AntlrTokenizer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new Dart2Lexer(charStream);
    }

    @Override
    protected AntlrTokenFilter getTokenFilter(final AntlrTokenManager tokenManager) {
        return new DartTokenFilter(tokenManager);
    }

    /**
     * The {@link DartTokenFilter} extends the {@link AntlrTokenFilter} to discard
     * Dart-specific tokens.
     * <p>
     * By default, it discards package and import statements, and
     * enables comment-based CPD suppression.
     * </p>
     */
    private static class DartTokenFilter extends AntlrTokenFilter {
        private boolean discardingLibraryAndImport = false;
        private boolean discardingNL = false;
        private boolean discardingSemicolon = false;

        /* default */ DartTokenFilter(final AntlrTokenManager tokenManager) {
            super(tokenManager);
        }

        @Override
        protected void analyzeToken(final AntlrToken currentToken) {
            skipLibraryAndImport(currentToken);
            skipNewLines(currentToken);
            skipSemicolons(currentToken);
        }

        private void skipLibraryAndImport(final AntlrToken currentToken) {
            final int type = currentToken.getKind();
            if (type == Dart2Lexer.LIBRARY || type == Dart2Lexer.IMPORT) {
                discardingLibraryAndImport = true;
            } else if (discardingLibraryAndImport && (type == Dart2Lexer.SEMICOLON || type == Dart2Lexer.NEWLINE)) {
                discardingLibraryAndImport = false;
            }
        }

        private void skipNewLines(final AntlrToken currentToken) {
            discardingNL = currentToken.getKind() == Dart2Lexer.NEWLINE;
        }

        private void skipSemicolons(final AntlrToken currentToken) {
            discardingSemicolon = currentToken.getKind() == Dart2Lexer.SEMICOLON;
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingLibraryAndImport || discardingNL || discardingSemicolon;
        }
    }
}
