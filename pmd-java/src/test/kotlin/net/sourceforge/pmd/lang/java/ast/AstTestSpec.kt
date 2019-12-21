/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.*
import io.kotlintest.specs.IntelliMarker
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.*
import io.kotlintest.should as kotlintestShould

/**
 * Base class for grammar tests that use the DSL. Tests are layered into
 * containers that make it easier to browse in the IDE. Layout is group name,
 * then java version, then test case. Test cases are "should" assertions matching
 * a string against a matcher defined in [ParserTestCtx], e.g. [ParserTestCtx.matchExpr].
 *
 * @author Clément Fournier
 */
abstract class AstTestSpec<T : AstTestSpec<T>>(body: T.() -> Unit) : AbstractSpec(), IntelliMarker {

    init {
        (this as T).body()
    }

    fun test(name: String, test: TestContext.() -> Unit) =
            addTestCase(name, test, defaultTestCaseConfig, TestType.Test)

    /**
     * Defines a group of tests that should be named similarly,
     * with separate tests for separate versions.
     *
     * Calls to "should" in the block are intercepted to create
     * a new test, with the given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    fun parserTestGroup(name: String,
                        spec: GroupTestCtx.() -> Unit) =
            addTestCase(name, { GroupTestCtx(this).spec() }, defaultTestCaseConfig, TestType.Container)

    /**
     * Defines a group of tests that should be named similarly.
     * Calls to "should" in the block are intercepted to create
     * a new test, with the given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param javaVersion Language versions to use when parsing
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    fun parserTest(name: String,
                   javaVersion: JavaVersion = JavaVersion.Latest,
                   spec: GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTest(name, listOf(javaVersion), spec)

    /**
     * Defines a group of tests that should be named similarly,
     * executed on several java versions. Calls to "should" in
     * the block are intercepted to create a new test, with the
     * given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param javaVersions Language versions for which to generate tests
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     */
    fun parserTest(name: String,
                   javaVersions: List<JavaVersion>,
                   spec: GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTestGroup(name) {
                onVersions(javaVersions) {
                    spec()
                }
            }

    private fun containedParserTestImpl(
            context: TestContext,
            name: String,
            javaVersion: JavaVersion,
            assertions: ParserTestCtx.() -> Unit) {

        context.registerTestCase(
                name = name,
                spec = this,
                test = { ParserTestCtx(javaVersion).assertions() },
                config = defaultTestCaseConfig,
                type = TestType.Test
        )
    }

    inner class GroupTestCtx(private val context: TestContext) {

        fun onVersions(javaVersions: List<JavaVersion>, spec: VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                context.registerTestCase(
                        name = "Java ${javaVersion.pmdName}",
                        spec = this@AstTestSpec,
                        test = { VersionedTestCtx(this, javaVersion).spec() },
                        config = defaultTestCaseConfig,
                        type = TestType.Container
                )
            }
        }

        inner class VersionedTestCtx(private val context: TestContext, javaVersion: JavaVersion) : ParserTestCtx(javaVersion) {

            infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(context, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }

            infix fun String.should(matcher: Matcher<String>) {
                containedParserTestImpl(context, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }

            infix fun String.shouldNot(matcher: Matcher<String>) =
                    should(matcher.invert())

            fun inContext(nodeParsingCtx: Companion.NodeParsingCtx<*>, assertions: ImplicitNodeParsingCtx.() -> Unit) {
                ImplicitNodeParsingCtx(nodeParsingCtx).assertions()
            }

            inner class ImplicitNodeParsingCtx(private val nodeParsingCtx: Companion.NodeParsingCtx<*>) {

                /**
                 * A matcher that succeeds if the string parses correctly.
                 */
                fun parse(): Matcher<String> = this@VersionedTestCtx.parseIn(nodeParsingCtx)

                fun parseAs(matcher: ValuedNodeSpec<Node, Any>): Assertions<String> = { str ->
                    val node = nodeParsingCtx.parseNode(str, this@VersionedTestCtx)
                    val idx = node.jjtGetChildIndex()
                    node.parent kotlintestShould matchNode<Node> {
                        if (idx > 0) {
                            unspecifiedChildren(idx)
                        }
                        matcher()
                        val left = it.numChildren - 1 - idx
                        if (left > 0) {
                            unspecifiedChildren(left)
                        }
                    }
                }
            }
        }
    }
}
