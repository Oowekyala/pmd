/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    private final LanguageLevelChecker<?> checker;

    public JavaParser(LanguageLevelChecker<?> checker) {
        this.checker = checker;
    }


    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return JavaTokenDocument.INSTANCE;
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserTask task) throws ParseException {
        JavaParserImpl parser = new JavaParserImpl(cs);
        parser.setSuppressMarker(task.getCommentMarker());
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        ASTCompilationUnit acu = parser.CompilationUnit();
        acu.setAstInfo(new AstInfo<>(task, acu, parser.getSuppressMap()));
        checker.check(acu);
        return acu;
    }
}
