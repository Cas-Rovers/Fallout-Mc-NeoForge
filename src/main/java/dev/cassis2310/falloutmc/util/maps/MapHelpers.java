package dev.cassis2310.falloutmc.util.maps;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import net.minecraft.util.RandomSource;

import java.util.Map;
import java.util.function.Function;

public class MapHelpers {
    /**
     * Transforms a map's values using a provided function and returns a new immutable map.
     * This method is useful for converting or transforming data stored in maps.
     *
     * @param map  The original map to transform.
     * @param func The function to apply to each value.
     * @param <K>  The key type.
     * @param <V1> The original value type.
     * @param <V2> The new value type.
     * @return A new map with transformed values.
     */
    public static <K, V1, V2> Map<K, V2> mapValue(Map<K, V1> map, Function<V1, V2> func) {
        final ImmutableMap.Builder<K, V2> builder = ImmutableMap.builderWithExpectedSize(map.size());
        for (Map.Entry<K, V1> entry : map.entrySet()) {
            builder.put(entry.getKey(), func.apply(entry.getValue()));
        }
        return builder.build();
    }

    /**
     * Returns a random value from a map, using the provided random source.
     * This method is useful for randomly selecting a value from a collection of mapped data.
     *
     * @param map    The map to select from.
     * @param random The random source to use.
     * @param <K>    The key type.
     * @param <V>    The value type.
     * @return A random value from the map.
     */
    public static <K, V> V getRandomValue(Map<K, V> map, RandomSource random) {
        return Iterators.get(map.values().iterator(), random.nextInt(map.size()));
    }
}
