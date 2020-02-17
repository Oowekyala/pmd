/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath.ast;

/**
 * Iteration axis.
 *
 * <pre>
 *
 * (: Not a node :)
 * Axis ::= "self"
 *        | "child"
 *        | "attribute"
 *        | "descendant"
 *        | "descendant-or-self"
 *        | "ancestor"
 *        | "ancestor-or-self"
 *        | "following"
 *        | "following-sibling"
 *        | "namespace"
 *        | "parent"
 *        | "preceding"
 *        | "preceding-sibling"
 *
 * </pre>
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public enum Axis {
    SELF("self"),
    CHILD("child"),
    ATTRIBUTE("attribute"),
    DESCENDANT("descendant"),
    DESCENDANT_OR_SELF("descendant-or-self"),
    ANCESTOR("ancestor"),
    ANCESTOR_OR_SELF("ancestor-or-self"),
    FOLLOWING("following"),
    FOLLOWING_SIBLING("following-sibling"),
    NAMESPACE("namespace"),
    PARENT("parent"),
    PRECEDING("preceding"),
    PRECEDING_SIBLING("preceding-sibling");


    private final String name;


    Axis(String name) {
        this.name = name;
    }


    /**
     * Returns the textual name of the axis.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the opposite axis.
     */
    public Axis opposite() {
        return null; // FIXME
    }

}
