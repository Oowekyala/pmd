/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.io.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Parser for the VisualForce language.
 */
public final class VfParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    @Override
    protected JavaccTokenDocument newDocumentImpl(TextDocument fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return VfTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserTask task) throws ParseException {
        return new VfParserImpl(cs).CompilationUnit().addTaskInfo(task);
    }

}
