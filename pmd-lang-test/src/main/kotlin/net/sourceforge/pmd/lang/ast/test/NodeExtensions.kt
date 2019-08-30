/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import io.kotlintest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.ast.AbstractNode
import net.sourceforge.pmd.lang.ast.GenericToken
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.TextAvailableNode
import java.util.*


/** Extension methods to make the Node API more Kotlin-like */

// kotlin converts getters of java types into property accessors
// but it doesn't recognise jjtGet* methods as getters

val Node.numChildren: Int
    get() = this.jjtGetNumChildren()

val Node.childIndex: Int
    get() = this.jjtGetChildIndex()

val Node.parent: Node?
    get() = this.jjtGetParent()

val Node.containingFile: Node
    get() = generateSequence(this) { it.parent }.last()


val Node.firstToken: GenericToken
    get() = (this as AbstractNode).jjtGetFirstToken()

val Node.lastToken: GenericToken
    get() = (this as AbstractNode).jjtGetLastToken()


fun Node.getChild(i: Int): Node = jjtGetChild(i)

fun Node.safeGetChild(i: Int): Node? = when {
    i < numChildren -> jjtGetChild(i)
    else -> null
}

val Node.textRange: TextRange
    get() = TextRange(beginPosition, endPosition)

val Node.beginPosition: TextPosition
    get() = TextPosition(beginLine, beginColumn)

val Node.endPosition: TextPosition
    get() = TextPosition(endLine, endColumn)


fun Node.assertTextRangeIsOk() {

    // they're defined
    assert(beginLine >= 0) { "Begin line is not set" }
    assert(endLine >= 0) { "End line is not set" }
    assert(beginColumn >= 0) { "Begin column is not set" }
    assert(endColumn >= 0) { "End column is not set" }

    val textRange = textRange
    // they're in the right order
    textRange.assertOrdered()

    val parent = parent ?: return

    assert(textRange in parent.textRange) {
        "The text range $textRange is not a subrange of that of the parent (${parent.textRange})"
    }

    if (this is TextAvailableNode && parent is TextAvailableNode) {
        parent.text.shouldContain(this.text)
    }
}


data class TextPosition(val line: Int, val column: Int) : Comparable<TextPosition> {

    override operator fun compareTo(other: TextPosition): Int = Comparator.compare(this, other)

    companion object {
        val Comparator: Comparator<TextPosition> =
                java.util.Comparator.comparingInt<TextPosition> { o -> o.line }
                        .thenComparingInt { o -> o.column }

    }
}

data class TextRange(val beginPos: TextPosition, val endPos: TextPosition) {

    fun assertOrdered() {
        assert(beginPos <= endPos) {
            "The begin position $beginPos should be lower than the end position $endPos"
        }
    }

    operator fun contains(position: TextPosition): Boolean = position in beginPos..endPos

    /** Result makes no sense if either of those text bounds is not ordered. */
    operator fun contains(other: TextRange): Boolean = other.beginPos in this && other.endPos in this

}
