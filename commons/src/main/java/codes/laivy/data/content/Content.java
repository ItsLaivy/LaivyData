package codes.laivy.data.content;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;

public interface Content<T> extends Iterable<T> {

    boolean add(@NotNull T object);

    boolean remove(@NotNull T object);

    boolean contains(@NotNull T object);

    boolean containsAll(@NotNull Collection<T> collection);

    @Range(from = 0, to = Integer.MAX_VALUE)
    int size();

    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }

    @Unmodifiable
    @NotNull Collection<T> toCollection();

    @NotNull Stream<T> stream();

    class ListProvider<T> implements Content<T> {

        protected final @NotNull List<T> list;

        public ListProvider(@NotNull List<T> list) {
            this.list = list;
        }

        @Override
        public boolean add(@NotNull T object) {
            return list.add(object);
        }

        @Override
        public boolean remove(@NotNull T object) {
            return list.remove(object);
        }

        @Override
        public boolean contains(@NotNull T object) {
            return list.contains(object);
        }

        @Override
        public boolean containsAll(@NotNull Collection<T> collection) {
            return new HashSet<>(list).containsAll(collection);
        }

        @Override
        public @Range(from = 0, to = Integer.MAX_VALUE) int size() {
            return list.size();
        }

        @Override
        public void clear() {
            for (T element : new ArrayList<>(list)) {
                remove(element);
            }
        }

        @Override
        public @Unmodifiable @NotNull Collection<T> toCollection() {
            return Collections.unmodifiableList(list);
        }

        @Override
        public @NotNull Stream<T> stream() {
            return list.stream();
        }

        @Override
        public @NotNull Iterator<T> iterator() {
            return list.iterator();
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (!(o instanceof ListProvider<?>)) return false;
            return Objects.equals(list, ((ListProvider<?>) o).list);
        }

        @Override
        public int hashCode() {
            return Objects.hash(list);
        }

        @Override
        public @NotNull String toString() {
            return list.toString();
        }
    }
    class SetProvider<T> implements Content<T> {

        protected final @NotNull Set<T> set;

        public SetProvider(@NotNull Set<T> set) {
            this.set = set;
        }

        @Override
        public boolean add(@NotNull T object) {
            return set.add(object);
        }

        @Override
        public boolean remove(@NotNull T object) {
            return set.remove(object);
        }

        @Override
        public boolean contains(@NotNull T object) {
            return set.contains(object);
        }

        @Override
        public boolean containsAll(@NotNull Collection<T> collection) {
            return set.containsAll(collection);
        }

        @Override
        public @Range(from = 0, to = Integer.MAX_VALUE) int size() {
            return set.size();
        }

        @Override
        public synchronized void clear() {
            for (T element : new HashSet<>(set)) {
                remove(element);
            }
        }

        @Override
        public @Unmodifiable @NotNull Collection<T> toCollection() {
            return Collections.unmodifiableSet(set);
        }

        @Override
        public @NotNull Stream<T> stream() {
            return set.stream();
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return set.iterator();
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (!(o instanceof SetProvider<?>)) return false;
            return Objects.equals(set, ((SetProvider<?>) o).set);
        }

        @Override
        public int hashCode() {
            return Objects.hash(set);
        }

        @Override
        public @NotNull String toString() {
            return set.toString();
        }
    }

}
