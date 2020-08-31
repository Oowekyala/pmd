/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private Map<Integer, String> suppressMap = Collections.emptyMap();
    private final TextDocument textDocument;

    ASTApexFile(ParserTask task, AbstractApexNode<? extends Compilation> child) {
        super(child.getNode());
        this.textDocument = task.getTextDocument();
        addChild(child, 0);
        this.setRegion(TextRegion.fromOffsetLength(0, task.getTextDocument().getLength()));
    }

    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    @Override
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ApexNode<Compilation> getMainNode() {
        return (ApexNode<Compilation>) getChild(0);
    }

    @Override
    public @NonNull ASTApexFile getRoot() {
        return this;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return suppressMap;
    }

    void setNoPmdComments(Map<Integer, String> suppressMap) {
        this.suppressMap = suppressMap;
    }
}
