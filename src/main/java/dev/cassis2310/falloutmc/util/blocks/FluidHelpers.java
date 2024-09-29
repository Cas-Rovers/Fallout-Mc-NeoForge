package dev.cassis2310.falloutmc.util.blocks;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.stream.Stream;

public class FluidHelpers {
    /**
     * Checks if the given FluidState matches the specified Tag.
     *
     * @param state the FluidState to check
     * @param tag   the Tag to match
     * @return true if the FluidState matches the Tag, false otherwise
     */
    public static boolean isFluid(FluidState state, TagKey<Fluid> tag) {
        return state.is(tag);
    }

    /**
     * Checks if the given Fluid matches the specified Tag.
     *
     * @param fluid the Fluid to check
     * @param tag   the Tag to match
     * @return true if the Fluid matches the Tag, false otherwise
     */
    @SuppressWarnings("deprecation")
    public static boolean isFluid(Fluid fluid, TagKey<Fluid> tag) {
        return fluid.is(tag);
    }

    /**
     * Checks if the given FluidState matches the specified Fluid.
     *
     * @param fluid the FluidState to check
     * @param other the Fluid to match
     * @return true if the FluidState matches the Fluid, false otherwise
     */
    public static boolean isFluid(FluidState fluid, Fluid other) {
        return fluid.is(other);
    }

    /**
     * Returns a Stream of all Fluids in the specified Tag.
     *
     * @param tag the Tag to select from
     * @return a Stream of all Fluids in the Tag
     */
    public static Stream<Fluid> allFluids(TagKey<Fluid> tag) {
        return BuiltInRegistries.FLUID.getOrCreateTag(tag).stream().map(Holder::value);
    }
}
