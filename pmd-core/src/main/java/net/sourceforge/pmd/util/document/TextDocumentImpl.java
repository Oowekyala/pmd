/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.TextRegionImpl.WithLineInfo;


class TextDocumentImpl implements TextDocument {

    private static final String OUT_OF_BOUNDS_WITH_LINES =
        "Region [bpos=(%d, %d), epos = (%d, %d)] is not in range of this document";

    private static final String OUT_OF_BOUNDS_WITH_OFFSET =
        "Region [%d, +%d] is not in range of this document";

    /** The positioner has the original source file. */
    SourceCodePositioner positioner;


    TextDocumentImpl(final CharSequence source) {
        positioner = new SourceCodePositioner(source);
    }

    @Override
    public MutableTextDocument newMutableDoc(ReplaceHandler out) {
        return new MutableTextDocumentImpl(getText(), out);
    }

    @Override
    public RegionWithLines addLineInfo(TextRegion region) {
        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return createRegion(bline, bcol, eline, ecol);
    }

    @Override
    public RegionWithLines createRegion(int beginLine, int beginColumn, int endLine, int endColumn) {
        // TODO checks, positioner should return -1 if not found
        int startOffset = positioner.offsetFromLineColumn(beginLine, beginColumn);
        int endOffset = positioner.offsetFromLineColumn(endLine, endColumn);

        if (startOffset < 0 || endOffset < 0) {
            throw new IndexOutOfBoundsException(
                String.format(OUT_OF_BOUNDS_WITH_LINES,
                              beginLine, beginColumn,
                              endLine, endColumn)
            );
        }

        return new WithLineInfo(startOffset, endOffset - startOffset,
                                beginLine, beginColumn, endLine, endColumn);
    }

    @Override
    public TextRegion createRegion(int offset, int length) {
        if (offset < 0 || offset + length > positioner.getSourceCode().length()) {
            throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_WITH_OFFSET, offset, length));
        }

        return new TextRegionImpl(offset, length);
    }

    @Override
    public CharSequence getText() {
        return positioner.getSourceCode();
    }

    @Override
    public CharSequence subSequence(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
