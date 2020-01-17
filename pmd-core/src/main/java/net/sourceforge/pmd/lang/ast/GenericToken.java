/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Represents a language-independent token such as constants, values language reserved keywords, or comments.
 */
public interface GenericToken<T extends GenericToken<T>> {

    /**
     * Obtain the next generic token according to the input stream which generated the instance of this token.
     *
     * @return the next generic token if it exists; null if it does not exist
     */
    T getNext();


    /**
     * Obtain a comment-type token which, according to the input stream which generated the instance of this token,
     * precedes this instance token and succeeds the previous generic token (if there is any).
     *
     * @return the comment-type token if it exists; null if it does not exist
     */
    T getPreviousComment();


    /**
     * Returns the token's text.
     */
    String getImage();


    // TODO these default implementations are here for compatibility because
    //  the functionality is only used in pmd-java for now, though it could
    //  be ported. I prefer doing this as changing all the GenericToken in
    //  pmd-java to JavaccToken


    /** Inclusive start offset in the source file text. */
    default int getStartInDocument() {
        return -1;
    }


    /** Exclusive end offset in the source file text. */
    default int getEndInDocument() {
        return -1;
    }


    /**
     * Gets the line where the token's region begins
     *
     * @return a non-negative integer containing the begin line
     */
    int getBeginLine();


    /**
     * Gets the line where the token's region ends
     *
     * @return a non-negative integer containing the end line
     */
    int getEndLine();


    /**
     * Gets the column offset from the start of the begin line where the token's region begins
     *
     * @return a non-negative integer containing the begin column
     */
    int getBeginColumn();


    /**
     * Gets the column offset from the start of the end line where the token's region ends
     *
     * @return a non-negative integer containing the begin column
     */
    int getEndColumn();


    /**
     * Returns true if this token represents the end-of-file (EOF).
     * For such a token {@link #getNext()} should return null and
     * {@link #getImage()} should be empty.
     */
    default boolean isEof() {
        // FIXME this implementation is for compatibility with duplicated
        //  JavaCC Tokens. When they're all replaced with JavaccToken, we
        //  can remove it.
        return getImage().isEmpty();
    }


    /**
     * Returns true if this token is implicit, ie was inserted artificially
     * and has a zero-length image.
     */
    default boolean isImplicit() {
        return false;
    }

}
