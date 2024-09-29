package dev.cassis2310.falloutmc.util.enums;

import dev.cassis2310.falloutmc.FalloutMc;
import dev.cassis2310.falloutmc.util.exceptions.ExceptionHandlingHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A utility class providing helper methods for working with enums.
 */
public class EnumHelpers {

    /**
     * Creates a map from an enum class to values provided by a value mapper.
     * This method ensures consistent iteration order in the map.
     *
     * @param <E>         the enum type
     * @param <V>         the value type
     * @param enumClass   the enum class to map
     * @param valueMapper a function that maps each enum constant to a value
     * @return a map from enum constants to values
     */
    public static <E extends Enum<E>, V> Map<E, V> mapOf(Class<E> enumClass, Function<E, V> valueMapper) {
        return mapOf(enumClass, key -> true, valueMapper);
    }

    /**
     * Creates a map from an enum class to values provided by a value mapper,
     * filtering out enum constants that do not match a predicate.
     * This method ensures consistent iteration order in the map.
     *
     * @param <E>          the enum type
     * @param <V>          the value type
     * @param enumClass    the enum class to map
     * @param valueMapper  a function that maps each enum constant to a value
     * @param keyPredicate a predicate that filters enum constants
     * @return a map from enum constants to values
     */
    public static <E extends Enum<E>, V> Map<E, V> mapOf(Class<E> enumClass, Predicate<E> keyPredicate, Function<E, V> valueMapper) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(keyPredicate)
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                valueMapper,
                                (v, v2) -> ExceptionHandlingHelpers.throwAsUnchecked(new AssertionError("Merging elements not allowed!")),
                                () -> new EnumMap<>(enumClass)));
    }

    /**
     * Translates an enum into a localized text component.
     * This method is useful for displaying enum names in the user's language.
     *
     * @param anEnum The enum to translate.
     * @return A localized text component representing the enum.
     */
    public static MutableComponent translateEnum(Enum<?> anEnum) {
        return Component.translatable(getEnumTranslationKey(anEnum));
    }

    /**
     * Translates an enum into a localized text component using a custom name.
     * This allows for custom enum translations, which may differ from the enum's class name.
     *
     * @param anEnum   The enum to translate.
     * @param enumName The custom name to use in the translation key.
     * @return A localized text component representing the enum.
     */
    public static MutableComponent translateEnum(Enum<?> anEnum, String enumName) {
        return Component.translatable(getEnumTranslationKey(anEnum, enumName));
    }

    /**
     * Returns the translation key for an enum, using the enum's class name as the base.
     * This is typically used for creating localized strings for enum values.
     *
     * @param anEnum The enum to get the translation key for.
     * @return The translation key for the enum.
     */
    public static String getEnumTranslationKey(Enum<?> anEnum) {
        return getEnumTranslationKey(anEnum, anEnum.getDeclaringClass().getSimpleName());
    }

    /**
     * Returns the translation key for an enum, using a custom name instead of the enum's class name.
     * This method allows for more flexibility in creating translation keys for enums.
     *
     * @param anEnum   The enum to get the translation key for.
     * @param enumName The custom name to use in the translation key.
     * @return The translation key for the enum.
     */
    public static String getEnumTranslationKey(Enum<?> anEnum, String enumName) {
        return String.join(".", FalloutMc.MOD_ID, "enum", enumName, anEnum.name()).toLowerCase(Locale.ROOT);
    }
}
