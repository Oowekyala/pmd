package net.sourceforge.pmd.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A type-safe, generic data holder.
 */
public final class DataMap {

    private static final int DEFAULT_CAP = 3;
    private final Map<DataKey<?>, Object> map;

    public DataMap() {
        this(false);
    }

    public DataMap(boolean concurrent) {
        this(concurrent, DEFAULT_CAP);
    }

    public DataMap(boolean concurrent, int cap) {
        this.map = concurrent ? new ConcurrentHashMap<>(cap) : new IdentityHashMap<>(cap);
    }

    public <T> @Nullable T put(DataKey<T> key, T data) {
        @SuppressWarnings("unchecked")
        T put = (T) map.put(key, data);
        return put;
    }


    public <T> @Nullable T get(DataKey<T> key) {
        @SuppressWarnings("unchecked")
        T put = (T) map.get(key);
        return put;
    }

    public <T> @Nullable T computeIfAbsent(DataKey<T> key, Supplier<T> callable) {
        @SuppressWarnings("unchecked")
        T res = (T) map.computeIfAbsent(key, k -> callable.get());
        return res;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    /**
     * Type safe key for a {@link DataMap}. Keys are compared using
     * reference identity.
     *
     * <p>Data keys should be kept confined to the scope they're expected
     * to be used in, ideally not public.
     *
     * @param <T> Type of data addressed by the key
     */
    public static final class DataKey<T> {

        public final String name;

        public DataKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


}
