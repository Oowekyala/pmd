/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;


abstract class AbstractMethodOrConstructorDeclaration extends AbstractJavaAccessNode implements ASTMethodOrConstructorDeclaration, LeftRecursiveNode {

    private JavaOperationSignature signature;
    private JavaOperationQualifiedName qualifiedName;


    AbstractMethodOrConstructorDeclaration(int i) {
        super(i);
    }


    AbstractMethodOrConstructorDeclaration(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public JavaOperationSignature getSignature() {
        if (signature == null) {
            signature = JavaOperationSignature.buildFor(this);
        }

        return signature;
    }

    void setQualifiedName(JavaOperationQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }


    @Override
    public JavaOperationQualifiedName getQualifiedName() {
        return qualifiedName;
    }

}
