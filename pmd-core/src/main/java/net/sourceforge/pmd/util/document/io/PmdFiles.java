/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Utilities to create and manipulate {@link TextFile} instances.
 */
public final class PmdFiles {

    private PmdFiles() {
        // utility class
    }


    /**
     * Returns an instance of this interface reading and writing to a file.
     * The returned instance may be read-only. If the file is not a regular
     * file (eg, a directory), or does not exist, then {@link TextFile#readContents()}
     * will throw.
     *
     * @param path    Path to the file
     * @param charset Encoding to use
     *
     * @throws NullPointerException if the path or the charset is null
     */
    public static TextFile forPath(final Path path, final Charset charset) {
        return new NioTextFile(path, charset);
    }

    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     *
     * @throws NullPointerException If the source text is null
     */
    public static TextFile readOnlyString(String source) {
        return readOnlyString(source, "n/a", null);
    }

    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     * @param name   File name to use
     *
     * @throws NullPointerException If the source text or the name is null
     */
    public static TextFile readOnlyString(String source, String name, LanguageVersion lv) {
        return new StringTextFile(source, name, lv);
    }

    /**
     * Wraps the given {@link SourceCode} (provided for compatibility).
     */
    public static TextFile cpdCompat(SourceCode sourceCode) {
        return new StringTextFile(sourceCode.getCodeBuffer(), sourceCode.getFileName(), null);
    }

    /**
     * Wraps the given {@link DataSource} (provided for compatibility).
     * Note that data sources are only usable once (even {@link DataSource#forString(String, String)}),
     * so calling {@link TextFile#readContents()} twice will throw the second time.
     */
    @Deprecated
    public static TextFile dataSourceCompat(DataSource ds, PMDConfiguration config) {
        return new TextFile() {
            @Override
            public String getPathId() {
                return ds.getNiceFileName(false, null);
            }

            @Override
            public @NonNull String getDisplayName() {
                return ds.getNiceFileName(config.isReportShortNames(), config.getInputPaths());
            }

            @Override
            public boolean isReadOnly() {
                return true;
            }

            @Override
            public void writeContents(TextFileContent content) throws IOException {
                throw new ReadOnlyFileException();
            }

            @Override
            public TextFileContent readContents() throws IOException {
                try (InputStream is = ds.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(is, config.getSourceEncoding()))) {
                    String contents = IOUtils.toString(reader);
                    return TextFileContent.normalizeToFileContent(contents);
                }
            }

            @Override
            public void close() throws IOException {
                ds.close();
            }
        };
    }
}
