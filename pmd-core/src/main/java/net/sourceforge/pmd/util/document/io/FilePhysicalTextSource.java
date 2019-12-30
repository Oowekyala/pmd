/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.pmd.internal.util.AssertionUtil;

class FilePhysicalTextSource implements PhysicalTextSource {

    private final Path path;
    private final Charset charset;

    FilePhysicalTextSource(Path path, Charset charset) throws IOException {
        AssertionUtil.requireParamNotNull(path, "path");
        AssertionUtil.requireParamNotNull(charset, "charset");

        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a regular file: " + path);
        }

        this.path = path;
        this.charset = charset;
    }


    @Override
    public boolean isReadOnly() {
        return !Files.isWritable(path);
    }

    @Override
    public void writeContents(CharSequence charSequence) throws IOException {
        byte[] bytes = charSequence.toString().getBytes(charset);
        Files.write(path, bytes);
    }

    @Override
    public CharSequence readContents() throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, charset);
    }

    @Override
    public long fetchStamp() throws IOException {
        return Files.getLastModifiedTime(path).hashCode();
    }

    @Override
    public String toString() {
        return "PhysicalFileDoc{charset=" + charset + ", path=" + path + '}';
    }
}
