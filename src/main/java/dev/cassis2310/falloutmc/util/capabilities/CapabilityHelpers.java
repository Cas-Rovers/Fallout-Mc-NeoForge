package dev.cassis2310.falloutmc.util.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityHelpers {

    /**
     * Retrieves the capability for a block at a given position in a level.
     * This is useful for interacting with blocks that have capabilities such as inventories or fluid tanks.
     *
     * @param capability The capability to retrieve.
     * @param level      The level (world) where the block is located.
     * @param pos        The position of the block.
     * @param <T>        The type of the capability.
     * @param <C>        The context for the capability (if any).
     * @return The capability, or null if not available.
     */
    @Nullable
    public static <T, C> T getCapability(BlockCapability<T, @Nullable C> capability, Level level, BlockPos pos) {
        return level.getCapability(capability, pos, null);
    }

    /**
     * Retrieves the capability for a block entity.
     * This method is particularly useful when working with custom block entities that expose capabilities.
     *
     * @param capability The capability to retrieve.
     * @param entity     The block entity.
     * @param <T>        The type of the capability.
     * @param <C>        The context for the capability (if any).
     * @return The capability, or null if not available.
     */
    @Nullable
    public static <T, C> T getCapability(BlockCapability<T, @Nullable C> capability, BlockEntity entity) {
        return getCapability(capability, entity, null);
    }

    /**
     * Gets a capability from a block entity with an additional context.
     *
     * @param capability The capability to get
     * @param entity     The block entity
     * @param context    The context
     * @param <T>        The type of the capability
     * @param <C>        The type of the context
     * @return The capability with the context
     */
    @Nullable
    @SuppressWarnings("DataFlowIssue")
    // BlockEntity.level is in practice never null, and the @Nullable C is not picked up correctly w.r.t getCapability()
    public static <T, C> T getCapability(BlockCapability<T, @Nullable C> capability, BlockEntity entity, C context) {
        return entity.getLevel().getCapability(capability, entity.getBlockPos(), entity.getBlockState(), entity, context);
    }

    /**
     * Determines if an ItemStack might have a capability, either by already possessing it or by simulating a stack size of 1.
     * This is useful for containers and inventories that need to ensure an item can perform certain actions, like heating.
     *
     * @param stack      The ItemStack to check.
     * @param capability The capability to check for.
     * @param <T>        The type of the capability.
     * @return True if the stack might have the capability, otherwise false.
     */
    public static <T> boolean mightHaveCapability(ItemStack stack, ItemCapability<T, Void> capability) {
        return stack.copyWithCount(1).getCapability(capability) != null;
    }
}
