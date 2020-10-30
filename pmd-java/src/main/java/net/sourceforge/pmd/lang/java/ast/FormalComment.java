/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocComment;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocParserFacade;

public class FormalComment extends Comment {

    private static final Pattern JAVADOC_TAG = Pattern.compile("@([A-Za-z0-9]+)");
    private JdocComment parsed;

    public FormalComment(JavaccToken t) {
        super(t);
        assert t.kind == JavaTokenKinds.FORMAL_COMMENT;
        findJavadocs();
    }

    public JdocComment getJdocTree() {
        if (parsed == null)
            parsed = JavadocParserFacade.parseJavadoc(getFirstToken());
        return parsed;
    }

    @Override
    public String getXPathNodeName() {
        return "FormalComment";
    }

    private void findJavadocs() {
        List<JavadocElement> kids = new ArrayList<>();

        Matcher javadocTagMatcher = JAVADOC_TAG.matcher(getFilteredComment());
        while (javadocTagMatcher.find()) {
            JavadocTag tag = JavadocTag.tagFor(javadocTagMatcher.group(1));
            int tagStartIndex = javadocTagMatcher.start(1);
            if (tag != null) {
                kids.add(new JavadocElement(getFirstToken(), getBeginLine(), getBeginLine(),
                        // TODO valid?
                        tagStartIndex, tagStartIndex + tag.label.length() + 1, tag));
            }
        }

        for (int i = kids.size() - 1; i >= 0; i--) {
            addChild(kids.get(i), i);
        }
    }
}
