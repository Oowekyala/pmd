/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.javadoc.ast;


import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_ATTR_START;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_ATTR_VAL;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_COMMENT_END;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_EQ;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_GT;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_IDENT;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.HTML_RCLOSE;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.INLINE_TAG_END;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.TAG_NAME;
import static net.sourceforge.pmd.lang.javadoc.ast.JavadocTokenType.WHITESPACE;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocComment;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocCommentData;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocHtml;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocHtmlComment;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocHtmlEnd;
import net.sourceforge.pmd.lang.javadoc.ast.JavadocNode.JdocMalformed;
import net.sourceforge.pmd.lang.javadoc.ast.JdocInlineTag.JdocLink;
import net.sourceforge.pmd.lang.javadoc.ast.JdocInlineTag.JdocUnknownInlineTag;

public class JavadocParser {

    private static final Map<String, Set<String>> HTML_AUTOCLOSED;
    private final JavadocLexer lexer;
    private final Deque<AbstractJavadocNode> nodes = new ArrayDeque<>();

    private JavadocToken tok;
    /** End of input. */
    private boolean isEoi;


    static {
        /*
        An li element's end tag may be omitted if the li element is immediately followed by another li element or if there is no more content in the parent element.

        A dt element's end tag may be omitted if the dt element is immediately followed by another dt element or a dd element.
        
        A dd element's end tag may be omitted if the dd element is immediately followed by another dd element or a dt element, or if there is no more content in the parent element.
        
        A p element's end tag may be omitted if the p element is immediately followed by an 
              , or if there is no more content in the parent element and the parent element is an HTML element that is not an
        a, audio, del, ins, map, noscript, or video element, or an autonomous custom element.
        
        */
        Map<String, Set<String>> tags = new HashMap<>();
        tags.put("li", setOf("li"));
        tags.put("dt", setOf("dd", "dd"));
        tags.put("dd", setOf("dd", "dt"));
        tags.put("p", setOf("address", "article", "aside", "blockquote",
                            "details", "div", "dl", "fieldset", "figcaption",
                            "figure", "footer", "form",
                            "h1", "h2", "h3", "h4", "h5", "h6",
                            "header", "hgroup", "hr", "main", "menu", "nav",
                            "ol", "p", "pre", "section", "table", "ul"));

        HTML_AUTOCLOSED = invertMap(tags);
    }


    public JavadocParser(String text) {
        lexer = new JavadocLexer(text);
    }


    public JavadocParser(String fileText, int startOffset, int maxOffset) {
        lexer = new JavadocLexer(fileText, startOffset, maxOffset);
    }

    public JdocComment parse() {
        JdocComment comment = new JdocComment();

        advance();
        if (tok == null) {
            // EOF
            return null;
        }
        comment.jjtSetFirstToken(tok);

        pushNode(comment);

        while (advance()) {
            dispatch();
        }

        comment.jjtSetLastToken(tok);
        return comment;
    }

    private void dispatch() {
        switch (tok.getKind()) {
        case COMMENT_END:
        case WHITESPACE:
        case LINE_BREAK:
            return;
        case COMMENT_DATA:
            growDataLeaf(tok, tok);
            break;
        case INLINE_TAG_START:
            inlineTag();
            break;
        case HTML_LT:
            htmlStart();
            break;
        case HTML_LCLOSE:
            htmlEnd();
            break;
        case HTML_COMMENT_START:
            linkLeaf(htmlComment());
            break;
        }
    }

    private void inlineTag() {
        JavadocToken start = tok;
        if (advance()) {
            if (tokIs(TAG_NAME)) {
                AbstractJavadocNode tag = parseInlineTagContent(tok.getImage());
                tag.jjtSetFirstToken(start);
                tag.jjtSetLastToken(tokIs(INLINE_TAG_END) ? tok : tok.getPrevious());
                linkLeaf(tag);
            } else if (!isEnd()) {
                growDataLeaf(start, tok);
            }
        }
    }

    /**
     * Parse the content of an inline tag depending on its name. After
     * this exits, we'll consume tokens until the next INLINE_TAG_END,
     * or element that interrupts the tag.
     */
    private AbstractJavadocNode parseInlineTagContent(String name) {
        TagParser parser = KnownInlineTagParser.lookup(name);
        if (parser == null) {
            return new JdocUnknownInlineTag(name);
        } else {
            return parser.parse(name, this);
        }
    }

    private void htmlStart() {
        JavadocToken start = tok;
        advance();
        skipWhitespace();

        if (tokIs(HTML_IDENT)) {
            JdocHtml html = new JdocHtml(tok.getImage());
            html.jjtSetFirstToken(start);
            advance();
            htmlAttrs(html);
            maybeAutoclose(start.prev, tok.getImage());
            linkLeaf(html);
            if (tok.getKind() == HTML_RCLOSE) {
                html.setAutoclose(true);
                html.jjtSetLastToken(tok);
            } else {
                pushNode(html);
                if (tok.getKind() != HTML_GT) {
                    linkLeaf(new JdocMalformed(EnumSet.of(HTML_RCLOSE, HTML_GT), tok));
                }
            }
        } else {
            linkLeaf(new JdocMalformed(EnumSet.of(HTML_IDENT), tok));
        }
    }

    private void maybeAutoclose(JavadocToken prevEnd, String curTag) {
        AbstractJavadocNode top = nodes.peek();
        if (top instanceof JdocHtml
            && HTML_AUTOCLOSED.getOrDefault(((JdocHtml) top).getTagName(), Collections.emptySet()).contains(curTag)) {
            top.jjtSetLastToken(prevEnd);
            popNode();
        }
    }


    private AbstractJavadocNode htmlComment() {
        JdocHtmlComment comment = new JdocHtmlComment();
        comment.jjtSetFirstToken(tok);
        while (advance() && !tokIs(HTML_COMMENT_END)) {
            comment.jjtSetLastToken(tok);
        }
        return comment;
    }

    private void htmlEnd() {
        JavadocToken start = tok;
        advance();
        skipWhitespace();
        if (tokIs(HTML_IDENT)) {
            JavadocToken ident = tok;
            JdocHtmlEnd html = new JdocHtmlEnd(ident.getImage());
            html.jjtSetFirstToken(start);
            advance();
            skipWhitespace();
            if (tokIs(HTML_GT)) {
                html.jjtSetLastToken(tok);
            } else {
                JdocMalformed malformed = new JdocMalformed(EnumSet.of(HTML_GT), tok);
                html.jjtAddChild(malformed, 0);
            }
            AbstractJavadocNode top = peekNode();
            linkLeaf(html);
            if (top instanceof JdocHtml && ((JdocHtml) top).getTagName().equals(html.getTagName())) {
                AbstractJavadocNode node = popNode();
                node.jjtSetLastToken(tokIs(HTML_GT) ? tok : ident);
            }
            return;
        }
        linkLeaf(new JdocMalformed(EnumSet.of(HTML_IDENT), tok));
    }

    private void htmlAttrs(JdocHtml acc) {
        skipWhitespace();
        while (tokIs(HTML_IDENT)) {
            String name = tok.getImage();
            advance();
            skipWhitespace();
            if (tokIs(HTML_EQ)) {
                advance();

                if (tokIs(HTML_ATTR_START)) {
                    advance();
                    if (tokIs(HTML_ATTR_VAL)) {
                        acc.attributes.put(name, tok.getImage());
                    }
                    advance();
                    skipWhitespace();
                }
            } else {
                acc.attributes.put(name, JdocHtml.UNATTRIBUTED);
                advance();
                skipWhitespace();
            }
        }
    }

    private void skipWhitespace() {
        while (tok.getKind() == WHITESPACE && advance()) {
            // advance
        }
    }

    private boolean tokIs(JavadocTokenType ttype) {
        return tok != null && tok.getKind() == ttype;
    }

    private boolean isEnd() {
        return isEoi;
    }

    private void pushNode(AbstractJavadocNode node) {
        nodes.push(node);
    }

    private AbstractJavadocNode popNode() {
        AbstractJavadocNode top = nodes.pop();
        top.jjtClose();
        return top;
    }

    private AbstractJavadocNode peekNode() {
        return nodes.peek();
    }

    private void linkLeaf(AbstractJavadocNode node) {
        if (node == null) {
            return;
        }
        AbstractJavadocNode top = this.nodes.peek();
        Objects.requireNonNull(top).jjtAddChild(node, top.jjtGetNumChildren());
        top.jjtSetLastToken(node.jjtGetLastToken());
    }

    private void growDataLeaf(JavadocToken first, JavadocToken last) {
        AbstractJavadocNode top = this.nodes.getFirst();
        JavadocNode lastNode = top.jjtGetNumChildren() > 0 ? top.jjtGetChild(top.jjtGetNumChildren() - 1) : null;
        if (lastNode instanceof JdocCommentData) {
            ((JdocCommentData) lastNode).jjtSetLastToken(last);
        } else {
            linkLeaf(new JdocCommentData(first, last));
        }
    }

    /**
     * Returns false if end of input is reached (in which case tok remains the last non-null token).
     */
    private boolean advance() {
        if (isEoi) {
            return false;
        }
        JavadocToken t = lexer.getNextToken();
        if (t == null) {
            isEoi = true;
            return false;
        }
        tok = t;
        return true;
    }

    private void consumeUntil(Predicate<JavadocToken> stopCondition, Predicate<JavadocToken> filter, Consumer<JavadocToken> action) {
        while (!stopCondition.test(tok) && !isEoi) {
            if (filter.test(tok)) {
                action.accept(tok);
            }
            advance();
        }
    }

    private static Map<String, Set<String>> invertMap(Map<String, Set<String>> map) {
        Map<String, Set<String>> tags = new HashMap<>();
        for (Entry<String, Set<String>> entry : map.entrySet()) {
            for (String val : entry.getValue()) {
                tags.computeIfAbsent(val, k -> new HashSet<>()).add(entry.getKey());
            }
        }
        return tags;
    }

    private static Set<String> setOf(String strings) {
        return Collections.singleton(strings);
    }

    private static Set<String> setOf(String... strings) {
        HashSet<String> hashSet = new HashSet<>(strings.length);
        Collections.addAll(hashSet, strings);
        return Collections.unmodifiableSet(hashSet);
    }

    enum KnownInlineTagParser implements TagParser {
        LINK("@link") {
            @Override
            public AbstractJavadocNode parse(String name, JavadocParser parser) {
                parser.advance();
                StringBuilder builder = new StringBuilder();
                parser.consumeUntil(it -> INLINE_TAG_ENDERS.contains(it.getKind()),
                                    it -> it.getKind().isSignificant(),
                                    tok -> builder.append(tok.getImage()));

                return new JdocLink(name, builder.toString());
            }
        },
        LINKPLAIN("@linkplain") {
            @Override
            public AbstractJavadocNode parse(String name, JavadocParser parser) {
                return LINK.parse(name, parser);
            }
        };
        private static final EnumSet<JavadocTokenType> INLINE_TAG_ENDERS = EnumSet.of(INLINE_TAG_END, TAG_NAME);
        private static final Map<String, KnownInlineTagParser> LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(KnownInlineTagParser::getName, p -> p));
        private final String name;

        KnownInlineTagParser(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Nullable
        static KnownInlineTagParser lookup(String name) {
            return LOOKUP.get(name);
        }
    }

    interface TagParser {

        String getName();


        AbstractJavadocNode parse(String name, JavadocParser parser);
    }


}
