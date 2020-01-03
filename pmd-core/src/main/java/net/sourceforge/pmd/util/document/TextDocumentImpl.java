/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.TextRegionImpl.WithLineInfo;
import net.sourceforge.pmd.util.document.io.TextFile;
import net.sourceforge.pmd.internal.util.BaseCloseable;


final class TextDocumentImpl extends BaseCloseable implements TextDocument {

    private static final String OUT_OF_BOUNDS_WITH_OFFSET =
        "Region [%d, +%d] is not in range of this document (length %d)";

    private final TextFile backend;

    private long curStamp;

    private SourceCodePositioner positioner;
    private CharSequence text;

    private TextEditorImpl curEditor;

    TextDocumentImpl(TextFile backend) throws IOException {
        this.backend = backend;
        this.curStamp = backend.fetchStamp();
        this.text = backend.readContents().toString();
        this.positioner = null;
    }

    @Override
    public boolean isReadOnly() {
        return backend.isReadOnly();
    }

    @Override
    public TextEditor newEditor(EditorCommitHandler handler) throws IOException {
        ensureOpen();
        if (curEditor != null) {
            throw new ConcurrentModificationException("An editor is already open on this document");
        }
        return curEditor = new TextEditorImpl(this, backend, handler);
    }

    void closeEditor(CharSequence text, long stamp) {

        curEditor = null;
        this.text = text.toString();
        this.positioner = null;
        this.curStamp = stamp;

    }

    @Override
    protected void doClose() throws IOException {
        if (curEditor != null) {
            curEditor.sever();
            curEditor = null;
            throw new IllegalStateException("Unclosed editor!");
        }

        backend.close();
    }

    @Override
    public RegionWithLines addLineInfo(TextRegion region) {
        checkInRange(region.getStartOffset(), region.getLength());

        if (positioner == null) {
            // if nobody cares about lines, this is not computed
            positioner = new SourceCodePositioner(text);
        }


        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return new WithLineInfo(
            region.getStartOffset(),
            region.getLength(),
            bline, bcol,
            eline, ecol
        );
    }

    @Override
    public TextRegion createRegion(int startOffset, int length) {
        AssertionUtil.requireNonNegative("Start offset", startOffset);
        AssertionUtil.requireNonNegative("Region length", length);

        checkInRange(startOffset, length);
        return TextRegionImpl.fromOffsetLength(startOffset, length);
    }

    private void checkInRange(int startOffset, int length) {
        if (startOffset < 0 || startOffset + length > getLength()) {
            throw new IndexOutOfBoundsException(
                String.format(
                    OUT_OF_BOUNDS_WITH_OFFSET,
                    startOffset,
                    length,
                    getLength()
                )
            );
        }
    }

    @Override
    public int getLength() {
        return getText().length();
    }

    @Override
    public CharSequence getText() {
        return text;
    }

    long getCurStamp() {
        return curStamp;
    }


    @Override
    public CharSequence subSequence(TextRegion region) {
        if (region.isEmpty()) {
            return "";
        }
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
