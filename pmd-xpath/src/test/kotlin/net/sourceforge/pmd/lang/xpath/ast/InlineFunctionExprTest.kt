package net.sourceforge.pmd.lang.xpath.ast

import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.util.*

/**
 * @author Clément Fournier
 * @since 6.7.0
 */
class InlineFunctionExprTest : FunSpec({

    testGroup("Test inline function expr") {

        "function() as xs:integer+ { 2, 3, 5, 7, 11, 13 }" should matchExpr<ASTInlineFunctionExpr> {
            it.isDefaultReturnType shouldBe false

            it.paramList shouldBe child {

            }
            it.declaredReturnType shouldBe childOpt<ASTSequenceType> {
                it.cardinality shouldBe Cardinality.ONE_OR_MORE
                it.isEmptySequence shouldBe false

                it.itemType shouldBe child<ASTAtomicOrUnionType> {

                    it.typeNameNode shouldBe child {
                        it.image shouldBe "xs:integer"
                        it.localName shouldBe "integer"
                        it.explicitNamespacePrefix shouldBe Optional.of("xs")
                        it.isUriLiteral shouldBe false

                    }
                }
            }
            it.bodyExpr shouldBe child<ASTSequenceExpr>(ignoreChildren = true) {}
        }


        "function(${'$'}a as xs:double, ${'$'}b as xs:double) as xs:double { ${'$'}a * ${'$'}b }" should matchExpr<ASTInlineFunctionExpr> {
            it.isDefaultReturnType shouldBe false

            it.paramList shouldBe child {

                child<ASTParam> {
                    it.isDefaultType shouldBe false

                    it.nameNode shouldBe child {
                        it.image shouldBe "a"
                        it.localName shouldBe "a"
                        it.explicitNamespacePrefix shouldBe Optional.empty()
                        it.isUriLiteral shouldBe false

                    }
                    it.declaredType shouldBe childOpt<ASTSequenceType> {
                        it.cardinality shouldBe Cardinality.EXACTLY_ONE
                        it.isEmptySequence shouldBe false

                        it.itemType shouldBe child<ASTAtomicOrUnionType> {

                            it.typeNameNode shouldBe child {
                                it.image shouldBe "xs:double"
                                it.localName shouldBe "double"
                                it.explicitNamespacePrefix shouldBe Optional.of("xs")
                                it.isUriLiteral shouldBe false

                            }
                        }
                    }
                }
                child<ASTParam> {
                    it.isDefaultType shouldBe false

                    it.nameNode shouldBe child {
                        it.image shouldBe "b"
                        it.localName shouldBe "b"
                        it.explicitNamespacePrefix shouldBe Optional.empty()
                        it.isUriLiteral shouldBe false

                    }
                    it.declaredType shouldBe childOpt<ASTSequenceType> {
                        it.cardinality shouldBe Cardinality.EXACTLY_ONE
                        it.isEmptySequence shouldBe false

                        it.itemType shouldBe child<ASTAtomicOrUnionType> {

                            it.typeNameNode shouldBe child {
                                it.image shouldBe "xs:double"
                                it.localName shouldBe "double"
                                it.explicitNamespacePrefix shouldBe Optional.of("xs")
                                it.isUriLiteral shouldBe false

                            }
                        }
                    }
                }
            }
            it.declaredReturnType shouldBe childOpt<ASTSequenceType> {
                it.cardinality shouldBe Cardinality.EXACTLY_ONE
                it.isEmptySequence shouldBe false

                it.itemType shouldBe child<ASTAtomicOrUnionType> {

                    it.typeNameNode shouldBe child {
                        it.image shouldBe "xs:double"
                        it.localName shouldBe "double"
                        it.explicitNamespacePrefix shouldBe Optional.of("xs")
                        it.isUriLiteral shouldBe false

                    }
                }
            }
            it.bodyExpr shouldBe child<ASTMultiplicativeExpr> {

                child<ASTVarRef> {

                    it.varNameNode shouldBe child {
                        it.image shouldBe "a"
                        it.localName shouldBe "a"
                        it.explicitNamespacePrefix shouldBe Optional.empty()
                        it.isUriLiteral shouldBe false

                    }
                }
                child<ASTMultiplicativeOperator> {
                    it.image shouldBe "*"
                }
                child<ASTVarRef> {

                    it.varNameNode shouldBe child {
                        it.image shouldBe "b"
                        it.localName shouldBe "b"
                        it.explicitNamespacePrefix shouldBe Optional.empty()
                        it.isUriLiteral shouldBe false

                    }
                }
            }
        }

        "function(${'$'}a) { ${'$'}a }" should matchExpr<ASTInlineFunctionExpr> {
            it.declaredReturnType shouldBe Optional.empty()
            it.isDefaultReturnType shouldBe true

            it.paramList shouldBe child {

                child<ASTParam> {
                    it.declaredType shouldBe Optional.empty()
                    it.isDefaultType shouldBe true

                    it.nameNode shouldBe child {
                        it.image shouldBe "a"
                        it.localName shouldBe "a"
                        it.explicitNamespacePrefix shouldBe Optional.empty()
                        it.isUriLiteral shouldBe false

                    }
                }
            }
            it.bodyExpr shouldBe child<ASTVarRef> {

                it.varNameNode shouldBe child {
                    it.image shouldBe "a"
                    it.localName shouldBe "a"
                    it.explicitNamespacePrefix shouldBe Optional.empty()
                    it.isUriLiteral shouldBe false

                }
            }
        }

        "collection()/(let ${'$'}a := . return function() { ${'$'}a })" should matchExpr<ASTPathExpr> {
            //it.pathAnchor

            child<ASTFunctionCall> {

                it.functionNameNode shouldBe child {
                    it.image shouldBe "collection"
                    it.localName shouldBe "collection"
                    it.explicitNamespacePrefix shouldBe Optional.empty()
                    it.isUriLiteral shouldBe false

                }
                it.arguments shouldBe child {}
            }
            child<ASTParenthesizedExpr> {

                it.wrappedNode shouldBe child<ASTLetExpr> {

                    val binding = child<ASTVarBinding> {
                        it.isLetStyle shouldBe true
                        it.varName shouldBe "a"

                        it.varNameNode shouldBe child {
                            it.image shouldBe "a"
                            it.localName shouldBe "a"
                            it.explicitNamespacePrefix shouldBe Optional.empty()
                            it.isUriLiteral shouldBe false

                        }
                        it.initializerExpr shouldBe child<ASTContextItemExpr> {

                        }
                    }

                    it.bindings.shouldContainAll(binding)


                    it.bodyExpr shouldBe child<ASTInlineFunctionExpr> {
                        it.declaredReturnType shouldBe Optional.empty()
                        it.isDefaultReturnType shouldBe true

                        it.paramList shouldBe child {}

                        it.bodyExpr shouldBe child<ASTVarRef> {

                            it.varNameNode shouldBe child {
                                it.image shouldBe "a"
                                it.localName shouldBe "a"
                                it.explicitNamespacePrefix shouldBe Optional.empty()
                                it.isUriLiteral shouldBe false

                            }
                        }
                    }
                }
            }
        }


    }
})