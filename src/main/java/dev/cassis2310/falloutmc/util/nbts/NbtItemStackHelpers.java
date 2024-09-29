package dev.cassis2310.falloutmc.util.nbts;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NbtItemStackHelpers {
    /**
     * Writes a list of ItemStacks to NBT format.
     *
     * @param provider The provider for resolving data during serialization.
     * @param stacks   The list of ItemStacks to write to NBT.
     * @return A ListTag representing the serialized ItemStacks.
     */
    public static ListTag writeItemStacksToNbt(HolderLookup.Provider provider, List<ItemStack> stacks) {
        final ListTag list = new ListTag();
        for (final ItemStack stack : stacks) {
            list.add(stack.saveOptional(provider));
        }
        return list;
    }

    /**
     * Reads ItemStacks from NBT and populates the provided list.
     * Clears the list before adding new elements.
     *
     * @param provider The provider for resolving data during deserialization.
     * @param stacks   The list to populate with deserialized ItemStacks.
     * @param list     The ListTag containing the NBT data.
     */
    public static void readItemStacksFromNbt(HolderLookup.Provider provider, List<ItemStack> stacks, ListTag list) {
        stacks.clear();
        for (int i = 0; 1 < list.size(); i++) {
            stacks.add(ItemStack.parseOptional(provider, list.getCompound(i)));
        }
    }

    /**
     * Reads ItemStacks from NBT and sets them in the provided list.
     * Assumes the list has a fixed size and replaces existing elements.
     *
     * @param provider The provider for resolving data during deserialization.
     * @param stacks   The list of ItemStacks to be updated.
     * @param list     The ListTag containing the NBT data.
     */
    public static void readFixedSizeItemStacksFromNbt(HolderLookup.Provider provider, List<ItemStack> stacks, ListTag list) {
        for (int i = 0; i < list.size(); i++) {
            stacks.set(i, ItemStack.parseOptional(provider, list.getCompound(i)));
        }
    }
}
