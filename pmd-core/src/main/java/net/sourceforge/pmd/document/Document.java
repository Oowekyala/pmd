/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.pmd.document.TextRegion.RegionWithLines;

/**
 * Represents a text document. A document provides methods to identify
 * regions of text and to convert between lines and columns.
 *
 * <p>The default document implementations do *not* normalise line endings.
 */
public interface Document {

    /**
     * Create a new region based on line coordinates.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionWithLines createRegion(final int beginLine, final int beginColumn, final int endLine, final int endColumn);


    /**
     * Create a new region based on offset coordinates.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    TextRegion createRegion(final int offset, final int length);


    /**
     * Add line information to the given region. Only the start and end
     * offsets are considered, if the region is already a {@link RegionWithLines},
     * that information is discarded.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionWithLines addLineInfo(TextRegion region);


    /** Returns the text of this document. */
    CharSequence getText();


    /** Returns a region of the {@link #getText() text} as a character sequence. */
    CharSequence subSequence(TextRegion region);


    /** Returns a mutable document that uses the given replace handler to carry out updates. */
    MutableDocument newMutableDoc(ReplaceHandler out);


    static Document forFile(final Path file, final Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(requireNonNull(file));
        String text = new String(bytes, requireNonNull(charset));
        return forCode(text);
    }


    static Document forCode(final CharSequence source) {
        return new DocumentImpl(source);
    }

}
