/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * A generic token implementation for JavaCC parsers. Will probably help
 * remove those duplicated implementations that all have the same name.
 *
 * <p>I think the only thing important here is {@link #getStartDocumentOffset()}
 * and {@link #getEndDocumentOffset()}. The begin/end line/column can very probably
 * be removed from the token instances to make them lighter, which doesn't
 * prevent them from being retrieved on-demand using the original text.
 *
 * <p>To make that easy we should probably link tokens to an underlying
 * "token document", which would be the list of all tokens in the file,
 * with the original (shared) full text. That would be available from
 * the {@link RootNode} instances. That representation could be shared
 * with CPD as well.
 *
 * TODO remove those four fields and fetch the begin/end line
 *   dynamically from the underlying text.
 *
 * @author Clément Fournier
 */
public class JavaccToken implements GenericToken, java.io.Serializable {

    /**
     * The version identifier for this Serializable class.
     * Increment only if the <i>serialized</i> form of the
     * class changes.
     */
    private static final long serialVersionUID = 4L;

    /**
     * An integer that describes the kind of this token.  This numbering
     * system is determined by JavaCCParser, and a table of these numbers is
     * stored in the file ...Constants.java.
     */
    public final int kind;
    /**
     * A reference to the next regular (non-special) token from the input
     * stream.  If this is the last token from the input stream, or if the
     * token manager has not read tokens beyond this one, this field is
     * set to null.  This is true only if this token is also a regular
     * token.  Otherwise, see below for a description of the contents of
     * this field.
     */
    public JavaccToken next;
    /**
     * This field is used to access special tokens that occur prior to this
     * token, but after the immediately preceding regular (non-special) token.
     * If there are no such special tokens, this field is set to null.
     * When there are more than one such special token, this field refers
     * to the last of these special tokens, which in turn refers to the next
     * previous special token through its specialToken field, and so on
     * until the first special token (whose specialToken field is null).
     * The next fields of special tokens refer to other special tokens that
     * immediately follow it (without an intervening regular token).  If there
     * is no such token, this field is null.
     */
    public JavaccToken specialToken;

    private final CharSequence image;
    private final int startOffset;
    private final int endOffset;
    /** The line number of the first character of this Token. */
    private final int beginLine;
    /** The column number of the first character of this Token. */
    private final int beginColumn;
    /** The line number of the last character of this Token. */
    private final int endLine;
    /** The column number of the last character of this Token. */
    private final int endColumn;

    /** {@link #undefined()} */
    private JavaccToken() {
        this(null);
    }

    public JavaccToken(String image) {
        this(-1, image, -1, -1, -1, -1, -1, -1);
    }

    /**
     * Constructs a new token for the specified Image and Kind.
     */
    public JavaccToken(int kind,
                       CharSequence image,
                       int startOffset,
                       int endOffset,
                       int beginColumn,
                       int endColumn,
                       int beginLine,
                       int endLine) {
        this.kind = kind;
        this.image = image;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.beginLine = beginLine;
        this.endLine = endLine;
    }


    @Override
    public GenericToken getNext() {
        return next;
    }

    @Override
    public GenericToken getPreviousComment() {
        return specialToken;
    }

    @Override
    public String getImage() {
        return image.toString();
    }

    // TODO move up to GenericToken, generalize the use of this class JavaccToken
    public int getStartDocumentOffset() {
        return startOffset;
    }

    public int getEndDocumentOffset() {
        return endOffset;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns the image.
     */
    @Override
    public String toString() {
        return image.toString();
    }

    public static JavaccToken undefined() {
        return new JavaccToken();
    }

}

