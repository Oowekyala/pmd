/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.pmd.properties.xml.XmlErrorReporter;
import net.sourceforge.pmd.properties.xml.XmlMapper;

public final class XmlUtils {

    private XmlUtils() {

    }

    public static List<Node> toList(NodeList lst) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < lst.getLength(); i++) {
            nodes.add(lst.item(i));
        }
        return nodes;
    }

    public static Stream<Element> getElementChildren(Element parent) {
        return toList(parent.getChildNodes()).stream()
                                             .filter(it -> it.getNodeType() == Node.ELEMENT_NODE)
                                             .map(Element.class::cast);
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, Set<String> names) {
        return getElementChildren(parent).filter(e -> names.contains(e.getTagName()));
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, String name) {
        return getElementChildren(parent).filter(e -> name.equals(e.getTagName()));
    }

    public static <T> T expectElement(XmlErrorReporter err, Element elt, XmlMapper<T> syntax) {

        if (!syntax.getReadElementNames().contains(elt.getTagName())) {
            err.warn(elt, "Wrong name, expected " + formatPossibleNames(syntax.getReadElementNames()));
        } else {
            return syntax.fromXml(elt, err);
        }

        return null;
    }

    public static Element getSingleChildIn(Element elt, XmlErrorReporter err, Set<String> names) {
        List<Element> children = getElementChildrenNamed(elt, names).collect(Collectors.toList());
        if (children.size() == 1) {
            return children.get(0);
        } else if (children.size() == 0) {
            if (names.size() > 1) {
                throw err.error(elt, XmlErrorMessages.MISSING_REQUIRED_ELEMENT_EITHER, formatPossibleNames(names));
            } else {
                throw err.error(elt, XmlErrorMessages.MISSING_REQUIRED_ELEMENT, names.iterator().next());
            }
        } else {
            for (int i = 1; i < children.size(); i++) {
                Element child = children.get(i);
                err.warn(child, XmlErrorMessages.IGNORED_DUPLICATE_CHILD_ELEMENT, child.getTagName());
            }
            return children.get(0);
        }
    }

    @Nullable
    public static String formatPossibleNames(Set<String> names) {
        if (names.isEmpty()) {
            return null;
        } else if (names.size() == 1) {
            return "'" + names.iterator().next() + "'";
        } else {
            return "one of " + names.stream().map(it -> "'" + it + "'").collect(Collectors.joining(", "));
        }
    }
}