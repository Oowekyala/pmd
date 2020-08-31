/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.ast.test.shouldBe

class ScalaTreeTests : FunSpec({


    test("Test line/column numbers") {

        """
class Foo {
 val I = "" 
}  
      """.trim().parseScala() should matchNode<ASTSource> {

            child<ASTDefnClass> {
                it.assertBounds(bline = 1, bcol = 1, eline = 3, ecol = 2)
                it::isImplicit shouldBe false

                child<ASTTypeName> {
                    it.assertBounds(bline = 1, bcol = 7, eline = 1, ecol = 10)
                    it::isImplicit shouldBe false
                }

                child<ASTCtorPrimary> {
                    it.assertBounds(bline = 1, bcol = 11, eline = 1, ecol = 11) // node has zero length
                    it::isImplicit shouldBe true

                    child<ASTNameAnonymous> {
                        it.assertBounds(bline = 1, bcol = 11, eline = 1, ecol = 11) // node has zero length
                        it::isImplicit shouldBe true
                    }
                }

                child<ASTTemplate> {
                    it.assertBounds(bline = 1, bcol = 11, eline = 3, ecol = 2)
                    it::isImplicit shouldBe false

                    child<ASTSelf> {
                        it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 2) // node has zero length
                        it::isImplicit shouldBe true

                        child<ASTNameAnonymous> {
                            it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 2) // node has zero length
                            it::isImplicit shouldBe true
                        }
                    }

                    child<ASTDefnVal> {
                        it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 12)
                        it::isImplicit shouldBe false

                        child<ASTPatVar> {
                            it.assertBounds(bline = 2, bcol = 6, eline = 2, ecol = 7)
                            it::isImplicit shouldBe false

                            child<ASTTermName> {
                                it.assertBounds(bline = 2, bcol = 6, eline = 2, ecol = 7)
                                it::isImplicit shouldBe false
                            }
                        }

                        child<ASTLitString> {
                            it.assertBounds(bline = 2, bcol = 10, eline = 2, ecol = 12)
                        }
                    }
                }
            }
        }
    }
})

fun String.parseScala(): ASTSource = ScalaParsingHelper.DEFAULT.parse(this)

fun Node.assertBounds(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    reportLocation.apply {
        this::getBeginLine shouldBe bline
        this::getBeginColumn shouldBe bcol
        this::getEndLine shouldBe eline
        this::getEndColumn shouldBe ecol
    }
}
