/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;

public final class ASTCompilationUnit extends AbstractVfNode implements RootNode {

    private TextDocument textDocument;

    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    ASTCompilationUnit addTaskInfo(ParserTask languageVersion) {
        textDocument = languageVersion.getTextDocument();
        return this;
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
