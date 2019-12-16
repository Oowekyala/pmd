/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.AncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.ChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredAncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredDescendantStream;

public final class StreamImpl {

    @SuppressWarnings("rawtypes")
    private static final NodeStream EMPTY = new IteratorBasedNStream() {
        @Override
        public Iterator iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public List toList() {
            return Collections.emptyList();
        }

        @Override
        public List toList(Function mapper) {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "EmptyStream";
        }
    };

    private StreamImpl() {
        // utility class
    }

    public static <T extends Node> NodeStream<T> singleton(@NonNull T node) {
        return new SingletonNodeStream<>(node);
    }

    public static <T extends Node> NodeStream<T> fromIterable(Iterable<T> iterable) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.filterNotNull(iterable.iterator());
            }

            @Override
            public Spliterator<T> spliterator() {
                Spliterator<T> spliter = iterable.spliterator();
                return Spliterators.spliterator(iterator(), spliter.estimateSize(),
                                                (spliter.characteristics() | Spliterator.NONNULL)
                                                    & ~Spliterator.SIZED
                                                    & ~Spliterator.SUBSIZED);
            }
        };
    }

    public static <T extends Node> NodeStream<T> union(Iterable<? extends @Nullable NodeStream<? extends T>> streams) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.flatMap(streams.iterator(), NodeStream::iterator);
            }
        };
    }


    @SuppressWarnings("unchecked")
    public static <T extends Node> NodeStream<T> empty() {
        return EMPTY;
    }

    public static <R extends Node> NodeStream<R> children(@NonNull Node node, Class<R> target) {
        return node.jjtGetNumChildren() == 0 ? empty() : new FilteredChildrenStream<>(node, Filtermap.isInstance(target));
    }

    public static NodeStream<Node> children(@NonNull Node node) {
        return node.jjtGetNumChildren() == 0 ? empty() : new ChildrenStream(node);
    }

    public static NodeStream<Node> descendants(@NonNull Node node) {
        return node.jjtGetNumChildren() == 0 ? empty() : new DescendantStream(node);
    }

    public static <R extends Node> NodeStream<R> descendants(@NonNull Node node, Class<R> rClass) {
        return node.jjtGetNumChildren() == 0 ? empty() : new FilteredDescendantStream<>(node, Filtermap.isInstance(rClass));
    }

    public static NodeStream<Node> descendantsOrSelf(@NonNull Node node) {
        return node.jjtGetNumChildren() == 0 ? empty() : new DescendantOrSelfStream(node);
    }

    public static NodeStream<Node> followingSiblings(@NonNull Node node) {
        Node parent = node.jjtGetParent();
        if (parent == null || parent.jjtGetNumChildren() == 1) {
            return NodeStream.empty();
        }
        return sliceChildren(parent, Filtermap.NODE_IDENTITY,
                             node.jjtGetChildIndex() + 1,
                             parent.jjtGetNumChildren() - node.jjtGetChildIndex() - 1
        );
    }

    public static NodeStream<Node> precedingSiblings(@NonNull Node node) {
        Node parent = node.jjtGetParent();
        if (parent == null || parent.jjtGetNumChildren() == 1) {
            return NodeStream.empty();
        }
        return sliceChildren(parent, Filtermap.NODE_IDENTITY, 0, node.jjtGetChildIndex());
    }

    static <T extends Node> NodeStream<T> sliceChildren(Node parent, Filtermap<Node, T> filtermap, int from, int length) {
        // these assertions are just for tests
        assert parent != null;
        assert from >= 0 && from <= parent.jjtGetNumChildren() : "from should be a valid index";
        assert length >= 0 : "length should not be negative";
        assert from + length >= 0 && from + length <= parent.jjtGetNumChildren() : "from+length should be a valid index";

        if (length == 0) {
            return empty();
        } else if (filtermap == Filtermap.NODE_IDENTITY) {
            @SuppressWarnings("unchecked")
            NodeStream<T> res = length == 1 ? (NodeStream<T>) singleton(parent.jjtGetChild(from))
                                           : (NodeStream<T>) new ChildrenStream(parent, from, length);
            return res;
        } else {
            return new FilteredChildrenStream<>(parent, filtermap, from, length);
        }
    }


    public static NodeStream<Node> ancestorsOrSelf(@Nullable Node node) {
        return ancestorsOrSelf(node, Filtermap.NODE_IDENTITY);
    }

    static <T extends Node> NodeStream<T> ancestorsOrSelf(@Nullable Node node, Filtermap<Node, T> target) {
        if (node == null) {
            return empty();
        } else if (node.jjtGetParent() == null) {
            T apply = target.apply(node);
            return apply != null ? singleton(apply) : empty();
        }
        return target == Filtermap.NODE_IDENTITY ? (NodeStream<T>) new AncestorOrSelfStream(node)
                                                 : new FilteredAncestorOrSelfStream<>(node, target);
    }

    public static NodeStream<Node> ancestors(@NonNull Node node) {
        return ancestorsOrSelf(node.jjtGetParent());
    }

    static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Filtermap<Node, R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), target);
    }

    public static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Class<R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), Filtermap.isInstance(target));
    }


}
