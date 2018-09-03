package net.sourceforge.pmd.lang.xpath.ast

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.xpath.ast.ASTPathExpr.PathAnchor.ROOT

/**
 * @author Clément Fournier
 * @since 6.7.0
 */
class PathExprTest : FunSpec({


    parserTest("Test path anchor") {

        "/A/B" should matchExpr<ASTPathExpr> {
            it.pathAnchor shouldBe ROOT

            val fstStep = child<ASTAxisStep> {

                it.axis shouldBe Axis.CHILD

                child<ASTExactNameTest> {
                    it.nameImage shouldBe "A"

                    it.nameNode shouldBe child { it.image shouldBe "A" }
                }

            }


            val sndStep = child<ASTAxisStep> {

                it.axis shouldBe Axis.CHILD


                child<ASTExactNameTest> {
                    it.nameImage shouldBe "B"

                    child<ASTName> { it.image shouldBe "B" }

                }
            }

            it.iterator().asSequence().toList().shouldContainExactly(listOf(fstStep, sndStep))
        }
    }

    parserTest("Test attribute shorthand") {

        "/@A" should matchExpr<ASTPathExpr> {
            it.pathAnchor shouldBe ROOT

            child<ASTAxisStep> {

                it.axis shouldBe Axis.ATTRIBUTE
                it.isAbbrevAttributeAxis shouldBe true

                child<ASTExactNameTest> {
                    it.nameImage shouldBe "A"

                    child<ASTName> { it.image shouldBe "A" }
                }

            }
        }
    }


    parserTest("Test parent shorthand") {

        "/.." should matchExpr<ASTPathExpr> {
            it.pathAnchor shouldBe ROOT

            child<ASTAxisStep> {

                it.axis shouldBe Axis.PARENT
                it.isAbbrevParentNodeTest shouldBe true

                child<ASTAnyKindTest> {

                }

            }
        }
    }


    parserTest("Test double slash shorthand") {

        "/A//B" should matchExpr<ASTPathExpr> {
            it.pathAnchor shouldBe ROOT


            val fstStep = child<ASTAxisStep> {

                it.axis shouldBe Axis.CHILD

                child<ASTExactNameTest> {
                    it.nameImage shouldBe "A"

                    child<ASTName> { it.image shouldBe "A" }
                }
            }


            // synthesized descendant-or-self::node()
            val sndStep = child<ASTAxisStep> {
                it.isAbbrevDescendantOrSelf shouldBe true

                it.axis shouldBe Axis.DESCENDANT_OR_SELF

                child<ASTAnyKindTest> {}
            }


            val thrdStep = child<ASTAxisStep> {

                it.axis shouldBe Axis.CHILD

                child<ASTExactNameTest> {
                    it.nameImage shouldBe "B"

                    child<ASTName> { it.image shouldBe "B" }
                }
            }


            it.asSequence().toList().shouldContainExactly(listOf(fstStep, sndStep, thrdStep))
        }
    }

    parserTest("Test context item expr") {

        "./Foo" should matchExpr<ASTPathExpr> {

            child<ASTContextItemExpr> { }

            child<ASTAxisStep> {
                child<ASTExactNameTest> {
                    it.nameNode shouldBe child { it.localName shouldBe "Foo" }
                }
            }

        }


    }

})