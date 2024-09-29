package dev.cassis2310.falloutmc.util.lists;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ImmutableListHelpers {
    /**
     * Given a list containing {@code [a0, ... aN]} and an element {@code aN+1}, returns a new, immutable list containing {@code [a0, ... aN, aN+1]},
     * in the most efficient manner that we can manage (a single data copy). This takes advantage that {@link ImmutableList}, along with its
     * builder, will not create copies if the builder is sized perfectly.
     *
     * @param list    the list to append to.
     * @param element the element to append.
     * @param <T>     the type of the list.
     * @return a new immutable list containing the original elements and the appended element.
     */
    public static <T> List<T> immutableAdd(List<T> list, T element) {
        return ImmutableList.<T>builderWithExpectedSize(list.size() + 1).addAll(list).add(element).build();
    }

    /**
     * Appends all elements from one list to another immutable list.
     *
     * @param list   the list to append to.
     * @param others the list of elements to append.
     * @param <T>    the type of the list.
     * @return a new immutable list containing the original elements and the appended elements.
     */
    public static <T> List<T> immutableAddAll(List<T> list, List<T> others) {
        return ImmutableList.<T>builderWithExpectedSize(list.size() + others.size())
                .addAll(list)
                .addAll(others)
                .build();
    }

    /**
     * Given a list containing {@code [a0, ... aN]} and an element {@code ai}, returns a new, immutable list containing {@code [a0, ... ai-1
     * , ai+1, ... aN]} in the most efficient manner (a single data copy).
     *
     * @param list    the list to remove from.
     * @param element the element to remove.
     * @param <T>     the type of the list.
     * @return a new immutable list containing the original elements except the removed element.
     * @throws IndexOutOfBoundsException if.
     */
    public static <T> List<T> immutableRemove(List<T> list, T element) {
        final ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(list.size() - 1);
        for (final T t : list)
            if (t != element)
                builder.add(t);
        return builder.build();
    }

    /**
     * Given a list containing {@code [a0, ... ai, ... aN}, an element {@code bi}, and an index {@code i}, returns a new, immutable list
     * containing {@code [a0, ... bi, ... aN]} in the most efficient manner (a single data copy). The new list will contain the same
     * references as the original list – they're assumed to be immutable!
     *
     * @param list    the list to swap in.
     * @param element the new element to swap in.
     * @param index   the index of the element to swap out.
     * @param <T>     the type of the list.
     * @return a new immutable list containing the original elements except the swapped element.
     */
    public static <T> List<T> immutableSwap(List<T> list, T element, int index) {
        final ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(list.size());
        for (int i = 0; i < list.size(); i++)
            builder.add(i == index ? element : list.get(i));
        return builder.build();
    }

    /**
     * Creates a new immutable list containing {@code n} new, separate instances of {@code T} produced by the given {@code factory}. This is unlike
     * {@link Collections#nCopies(int, Object)} in that it produces separate instance, and consumes memory proportional to O(n). However, in
     * the event the underlying elements are interior mutable, this creates a safe to modify a list.
     *
     * @param n       the number of copies to create.
     * @param factory a supplier of the element to copy.
     * @param <T>     the type of the list.
     * @return a list of n copies of the element.
     * @see Collections#nCopies(int, Object)
     */
    public static <T> List<T> ImmutableCopies(int n, Supplier<T> factory) {
        final ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(n);
        for (int i = 0; i < n; i++)
            builder.add(factory.get());
        return builder.build();
    }
}
