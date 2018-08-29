/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;


import net.sourceforge.pmd.lang.xpath.ast.NodeTest.NameTest;


/**
 * A {@linkplain NameTest name test} that matches nodes having
 * exactly a certain name.
 *
 * <pre>
 *
 * ExactNameTest :: {@linkplain ASTName Name}
 *
 * </pre>
 */
public final class ASTExactNameTest extends AbstractXPathNode implements NameTest {


    ASTExactNameTest(XPathParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the image of the name tested for.
     */
    public String getNameImage() {
        return getMatchedName().getImage();
    }


    /**
     * Returns the node representing the name tested for.
     */
    public ASTName getMatchedName() {
        return (ASTName) jjtGetChild(0);
    }


    @Override
    public <T> T jjtAccept(XPathParserVisitor<T> visitor, T data) {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=f84ce10a4a9a29e0d18b17f6a11e34f8 (do not edit this line) */
