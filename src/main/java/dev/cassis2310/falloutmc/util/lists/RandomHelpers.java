package dev.cassis2310.falloutmc.util.lists;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RandomHelpers {
    /**
     * Select N unique elements from a list, without having to shuffle the whole list.
     * This involves moving the selected elements to the end of the list. Note: this method will mutate the passed in list!
     * From <a href="https://stackoverflow.com/questions/4702036/take-n-random-elements-from-a-liste">Stack Overflow</a>
     *
     * @param list the list to sample from.
     * @param n    the number of elements to select.
     * @param r    a random source.
     * @param <T>  the type of the list.
     * @return a list of n unique elements selected from the original list.
     * @throws IllegalArgumentException if n is greater than the list size.
     */
    public static <T> List<T> uniqueRandomSample(List<T> list, int n, RandomSource r) {
        final int length = list.size();
        if (length < n) {
            throw new IllegalArgumentException("Cannot select n=" + n + " unique elements from a list of size " + length);
        }
        for (int i = length - 1; i >= length - n; i--) {
            Collections.swap(list, i, r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    /**
     * Returns a random element from the specified Registry and Tag.
     *
     * @param registry the Registry to select from
     * @param tag      the Tag to select from
     * @param random   the RandomSource to use for selection
     * @param <T>      the type of element in the Registry
     * @return a random element from the Registry and Tag, or an empty Optional if the Tag is empty
     */
    public static <T> Optional<T> getRandomElement(Registry<T> registry, TagKey<T> tag, RandomSource random) {
        return registry.getTag(tag).flatMap(set -> set.getRandomElement(random)).map(Holder::value);
    }
}
