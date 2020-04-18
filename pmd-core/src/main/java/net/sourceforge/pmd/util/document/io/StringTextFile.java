/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.document.Chars;

/**
 * Read-only view on a string.
 */
class StringTextFile implements TextFile {

    private final Chars buffer;
    private final String name;
    private final LanguageVersion lv;

    StringTextFile(CharSequence source, @NonNull String name, LanguageVersion lv) {
        this.lv = lv;
        AssertionUtil.requireParamNotNull("source text", source);
        AssertionUtil.requireParamNotNull("file name", name);

        this.buffer = Chars.wrap(source, true);
        this.name = name;
    }

    @Override
    public @NonNull String getDisplayName() {
        return name;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void writeContents(Chars charSequence) {
        throw new ReadOnlyFileException();
    }

    @Override
    public Chars readContents() {
        return buffer;
    }

    @Override
    public long fetchStamp() {
        return hashCode();
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String toString() {
        return "ReadOnlyString[" + StringUtil.truncate(buffer.toString(), 40, "...") + "]";
    }

}
