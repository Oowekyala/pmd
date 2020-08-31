/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class CppCharStreamTest {

    @Test
    public void testContinuationUnix() throws IOException {
        CppCharStream stream = CppCharStream.newCppCharStream(new StringReader("a\\\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testContinuationWindows() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CppCharStream stream = CppCharStream.newCppCharStream(new StringReader("a\\\r\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testBackup() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CppCharStream stream = CppCharStream.newCppCharStream(new StringReader("a\\b\\qc"));
        assertStream(stream, "a\\b\\qc");
    }

    private void assertStream(CppCharStream stream, String token) throws IOException {
        char c = stream.BeginToken();
        assertEquals(token.charAt(0), c);
        for (int i = 1; i < token.length(); i++) {
            c = stream.readChar();
            assertEquals(token + " char at " + i + ": " + token.charAt(i) + " != " + c, token.charAt(i), c);
        }
        assertEquals(token, stream.GetImage());
        assertEquals(token, new String(stream.GetSuffix(token.length())));
    }
}
