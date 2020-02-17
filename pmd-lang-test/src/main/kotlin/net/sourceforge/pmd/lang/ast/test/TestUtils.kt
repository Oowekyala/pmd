/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import io.kotlintest.Matcher
import io.kotlintest.equalityMatcher
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible
import kotlin.streams.toList

/**
 * Extension to add the name of a property to error messages.
 *
 * @see [shouldBe].
 */
infix fun <N, V : N> KCallable<N>.shouldEqual(expected: V?) =
        assertWrapper(this, expected) { n, v ->
            // using shouldBe would perform numeric conversion
            // eg (3.0 shouldBe 3L) passes, even though (3.0 != 3L)
            // equalityMatcher doesn't do this conversion
            n.should(equalityMatcher(v) as Matcher<N>)
        }

private fun <N, V> assertWrapper(callable: KCallable<N>, right: V, asserter: (N, V) -> Unit) {

    fun formatName() = "::" + callable.name.removePrefix("get").decapitalize()

    val value: N = try {
        callable.isAccessible = true
        callable.call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't fetch value for property ${formatName()}", e)
    }

    try {
        asserter(value, right)
    } catch (e: AssertionError) {

        if (e.message?.contains("expected:") == true) {
            // the exception has no path, let's add one
            throw AssertionError(e.message!!.replace("expected:", "expected property ${formatName()} to be"))
        }

        throw e
    }
}

/**
 * Extension to add the name of the property to error messages.
 * Use with double colon syntax, eg `it::isIntegerLiteral shouldBe true`.
 * For properties synthesized from Java getters starting with "get", you
 * have to use the name of the getter instead of that of the generated
 * property (with the get prefix).
 *
 * If this conflicts with [io.kotlintest.shouldBe], use the equivalent [shouldEqual]
 *
 */
infix fun <N, V : N> KCallable<N>.shouldBe(expected: V?) = this.shouldEqual(expected)

infix fun <T> KCallable<T>.shouldMatch(expected: T.() -> Unit) = assertWrapper(this, expected) { n, v -> n should v }

infix fun <T, U : T> Optional<T>.shouldBePresent(any: U) {
    ::isPresent shouldBe true
    ::get shouldBe any
}

fun Optional<*>.shouldBeEmpty() {
    ::isPresent shouldBe false
}

fun KCallable<Optional<*>>.shouldBeEmpty() = this shouldMatch {
    ::isPresent shouldBe false
}

infix fun <T, U : T> KCallable<Optional<T>>.shouldBePresent(any: U) = this shouldMatch {
    ::isPresent shouldBe true
    ::get shouldBe any
}


inline  fun <reified T> Any?.shouldBeA(f: (T) -> Unit = {}): T {
    if (this is T) {
        f(this)
        return this
    } else throw AssertionError("Expected an instance of ${T::class.java}, got $this")
}

fun Stream<*>.shouldHaveSize(i: Int) {
    toList() should haveSize(i)
}
