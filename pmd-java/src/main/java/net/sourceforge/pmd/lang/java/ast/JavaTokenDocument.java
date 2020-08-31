/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.GT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.RSIGNEDSHIFT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.RUNSIGNEDSHIFT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.WHITESPACE;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.EscapeAwareReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaInputReader;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * {@link JavaccTokenDocument} for Java.
 */
final class JavaTokenDocument extends JavaccTokenDocument {

    JavaTokenDocument(TextDocument fullText) {
        super(fullText);
    }

    @Override
    public EscapeAwareReader newReader(Chars text) {
        return new JavaInputReader(text);
    }

    @Override
    protected @Nullable String describeKindImpl(int kind) {
        return JavaTokenKinds.describe(kind);
    }

    @Override
    public JavaccToken createToken(int kind, CharStream jcs, @Nullable String image) {
        switch (kind) {
        case RUNSIGNEDSHIFT:
        case RSIGNEDSHIFT:
        case GT:
            return new GTToken(
                GT,
                kind,
                ">",
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );

        case WHITESPACE:
            // We don't create a new string for the image of whitespace tokens eagerly

            // It's unlikely that anybody cares about that, and since
            // they're still 30% of all tokens this is advantageous
            return new LazyImageToken(
                kind,
                jcs.getStartOffset(),
                jcs.getEndOffset(),
                jcs.getTokenDocument()
            );

        default:
            return super.createToken(kind, jcs, image);
        }
    }

    static int getRealKind(JavaccToken token) {
        return token instanceof GTToken ? ((GTToken) token).realKind : token.kind;
    }

    private static final class LazyImageToken extends JavaccToken {

        LazyImageToken(int kind, int startInclusive, int endExclusive, JavaccTokenDocument document) {
            super(kind, null, startInclusive, endExclusive, document);
        }

        @Override
        public String getImage() {
            return document.getTextDocument().slice(getRegion()).toString();
        }
    }

    private static final class GTToken extends JavaccToken {

        final int realKind;

        GTToken(int kind, int realKind, CharSequence image, int startOffset, int endOffset, JavaccTokenDocument doc) {
            super(kind, image, startOffset, endOffset, doc);
            this.realKind = realKind;
        }

    }


}
