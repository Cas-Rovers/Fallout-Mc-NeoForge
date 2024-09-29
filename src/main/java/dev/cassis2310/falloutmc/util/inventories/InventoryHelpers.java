package dev.cassis2310.falloutmc.util.inventories;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Iterator;
import java.util.List;

public class InventoryHelpers {
    /**
     * Returns an iterable over the ItemStacks in the given inventory.
     *
     * @param inventory The inventory to iterate over.
     * @return An Iterable of ItemStacks contained in the inventory.
     */
    public static Iterable<ItemStack> iterate(IItemHandler inventory) {
        return iterate(inventory, 0, inventory.getSlots());
    }

    /**
     * Returns an iterable over the ItemStacks in the given RecipeInput inventory.
     *
     * @param inventory The RecipeInput inventory to iterate over.
     * @return An Iterable of ItemStacks contained in the RecipeInput inventory.
     */
    public static Iterable<ItemStack> iterate(RecipeInput inventory) {
        return () -> new Iterator<>() {
            private int slot = 0;

            @Override
            public boolean hasNext() {
                return slot < inventory.size();
            }

            @Override
            public ItemStack next() {
                return inventory.getItem(slot++);
            }
        };
    }

    /**
     * Returns an iterable over the ItemStacks in the given inventory,
     * limited to a specified range of slots.
     *
     * @param inventory          The inventory to iterate over.
     * @param startSlotInclusive The starting slot (inclusive).
     * @param endSlotExclusive   The ending slot (exclusive).
     * @return An Iterable of ItemStacks within the specified slot range.
     */
    public static Iterable<ItemStack> iterate(IItemHandler inventory, int startSlotInclusive, int endSlotExclusive) {
        return () -> new Iterator<>() {
            private int slot = startSlotInclusive;

            @Override
            public boolean hasNext() {
                return slot < endSlotExclusive;
            }

            @Override
            public ItemStack next() {
                return inventory.getStackInSlot(slot++);
            }

            @Override
            public void remove() {
                removeStack(inventory, slot - 1); // Remove the previous slot = previous call to next()
            }
        };
    }

    /**
     * Removes and returns the ItemStack from the specified slot in the inventory.
     *
     * @param inventory The inventory from which to remove the ItemStack.
     * @param slot      The slot index from which to remove the ItemStack.
     * @return The removed ItemStack.
     */
    public static ItemStack removeStack(IItemHandler inventory, int slot) {
        return inventory.extractItem(slot, Integer.MAX_VALUE, false);
    }

    /**
     * Attempts to insert a stack across all slots of an item handler
     *
     * @param stack The stack to be inserted
     * @return The remainder after the stack is inserted, if any
     */
    public static ItemStack insertAllSlots(IItemHandler inventory, ItemStack stack) {
        return insertSlots(inventory, stack, 0, inventory.getSlots());
    }

    /**
     * Attempts to insert an ItemStack into a range of slots in the given inventory.
     * The method tries each slot sequentially until the ItemStack is either fully inserted
     * or no more slots are available in the specified range.
     *
     * @param inventory          The inventory into which the ItemStack should be inserted.
     * @param stack              The ItemStack to insert.
     * @param slotStartInclusive The starting slot index (inclusive).
     * @param slotEndExclusive   The ending slot index (exclusive).
     * @return The remaining ItemStack that couldn't be inserted,
     * or {@code ItemStack.EMPTY} if fully inserted.
     */
    public static ItemStack insertSlots(IItemHandler inventory, ItemStack stack, int slotStartInclusive, int slotEndExclusive) {
        for (int slot = slotStartInclusive; slot < slotEndExclusive; slot++) {
            stack = inventory.insertItem(slot, stack, false);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    /**
     * Checks if every slot in the provided inventory is empty.
     *
     * @param inventory An iterable of ItemStacks representing the inventory.
     * @return {@code true} if every slot in the provided inventory is empty, otherwise {@code false}.
     */
    public static boolean isEmpty(Iterable<ItemStack> inventory) {
        for (ItemStack stack : inventory)
            if (!stack.isEmpty())
                return false;
        return true;
    }

    /**
     * Copies the contents of an inventory {@code inventory} into an immutable list builder.
     *
     * @param builder   the builder to copy into.
     * @param inventory the inventory to copy from.
     */
    public static void copyTo(ImmutableList.Builder<ItemStack> builder, IItemHandler inventory) {
        copyTo(builder, iterate(inventory));
    }

    /**
     * Copies the contents of an inventory {@code inventory} into an immutable list builder.
     *
     * @param builder the builder to copy into.
     * @param stacks  the stacks to copy from.
     */
    public static void copyTo(ImmutableList.Builder<ItemStack> builder, Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks)
            builder.add(stack.copy());
    }

    /**
     * Copies the contents of the inventory {@code inventory} into a list, clears the inventory, and returns the list.
     *
     * @param inventory the inventory to copy from.
     * @return the copied list.
     * @see #copyTo
     */
    public static List<ItemStack> copyAndClear(IItemHandlerModifiable inventory) {
        final ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            builder.add(inventory.getStackInSlot(slot).copy());
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return builder.build();
    }

    /**
     * Copies the contents of the contents list {@code list} into the inventory {@code inventory}. This will copy the minimum of
     * the slot count of the inventory, and the content of the list. If the list is empty, nothing will be copied.
     *
     * @param list      the list of items to copy from
     * @param inventory the inventory to copy items into
     */
    public static void copyFrom(List<ItemStack> list, IItemHandlerModifiable inventory) {
        for (int i = 0; i < Math.min(list.size(), inventory.getSlots()); i++)
            inventory.setStackInSlot(1, list.get(i).copy());
    }

}
