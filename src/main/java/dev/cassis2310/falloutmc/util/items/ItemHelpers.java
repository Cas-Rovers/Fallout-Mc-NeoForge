package dev.cassis2310.falloutmc.util.items;

import dev.cassis2310.falloutmc.util.inventories.InventoryHelpers;
import dev.cassis2310.falloutmc.util.lists.RandomHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ItemHelpers {
    /**
     * Damages {@code stack} by one point, when held by {@code entity} in {@code slot}
     *
     * @param stack  The item stack to damage.
     * @param entity The living entity holding the item.
     * @param slot   The equipment slot where the item is located.
     */
    public static void damageItem(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
        stack.hurtAndBreak(1, entity, slot);
    }

    /**
     * Damages {@code stack} by {@code amount}, when held by {@code entity} in {@code hand}
     *
     * @param stack  The item stack to damage.
     * @param amount The amount of damage to apply.
     * @param entity The living entity holding the item.
     * @param hand   The hand in which the item is held.
     */
    public static void damageItem(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
        stack.hurtAndBreak(amount, entity, LivingEntity.getSlotForHand(hand));
    }

    /**
     * Damages {@code stack} by one point, when held by {@code entity} in {@code hand}
     *
     * @param stack  The item stack to damage.
     * @param entity The living entity holding the item.
     * @param hand   The hand in which the item is held.
     */
    public static void damageItem(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(hand));
    }

    /**
     * Damages {@code stack} without an entity present.
     *
     * @param stack The item stack to damage.
     * @param level The level in which the item exists.
     */
    public static void damageItem(ItemStack stack, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(1, serverLevel, null, item -> {
            });
        }
    }

    /**
     * Consumes items from a stack in increments of the stack's maximum size,
     * passing each consumed increment to a provided consumer.
     *
     * @param stack      The item stack to consume from.
     * @param totalCount The total number of items to consume.
     * @param consumer   The consumer to process each consumed increment.
     */
    public static void consumeInStackSizeIncrements(ItemStack stack, int totalCount, Consumer<ItemStack> consumer) {
        while (totalCount > 0) {
            final ItemStack splitStack = stack.copy();
            final int splitCount = Math.min(splitStack.getMaxStackSize(), totalCount);
            splitStack.setCount(splitCount);
            totalCount -= splitCount;
            consumer.accept(splitStack);
        }
    }

    /**
     * Gathers and consumes items within a bounding box, storing them in an inventory.
     *
     * @param level            The level in which the items reside.
     * @param bounds           The bounding box within which to gather items.
     * @param inventory        The inventory to store gathered items.
     * @param minSlotInclusive The starting slot (inclusive) for storing items in the inventory.
     * @param maxSlotExclusive The ending slot (exclusive) for storing items in the inventory.
     */
    public static void gatherAndConsumeItems(Level level, AABB bounds, IItemHandler inventory, int minSlotInclusive, int maxSlotExclusive) {
        gatherAndConsumeItems(level.getEntitiesOfClass(ItemEntity.class, bounds, EntitySelector.ENTITY_STILL_ALIVE), inventory, minSlotInclusive, maxSlotExclusive, Integer.MAX_VALUE);
    }

    /**
     * Gathers and consumes items within a bounding box, storing them in an inventory.
     * Allows specifying a maximum number of items to be gathered.
     *
     * @param level            The level in which the items reside.
     * @param bounds           The bounding box within which to gather items.
     * @param inventory        The inventory to store gathered items.
     * @param minSlotInclusive The starting slot (inclusive) for storing items in the inventory.
     * @param maxSlotInclusive The ending slot (inclusive) for storing items in the inventory.
     * @param maxItemsOverride The maximum number of items to be gathered. If this limit is reached, no further items will be gathered.
     */
    public static void gatherAndConsumeItems(Level level, AABB bounds, IItemHandler inventory, int minSlotInclusive, int maxSlotInclusive, int maxItemsOverride) {
        gatherAndConsumeItems(level.getEntitiesOfClass(ItemEntity.class, bounds, EntitySelector.ENTITY_STILL_ALIVE), inventory, minSlotInclusive, maxSlotInclusive, maxItemsOverride);
    }

    /**
     * Gathers and consumes items from a collection of item entities, storing them in an inventory.
     * Allows specifying a maximum number of items to be gathered.
     *
     * @param items            A collection of ItemEntity objects to be gathered and consumed.
     * @param inventory        The inventory to store gathered items.
     * @param minSlotInclusive The starting slot (inclusive) for storing items in the inventory.
     * @param maxSlotInclusive The ending slot (inclusive) for storing items in the inventory.
     * @param maxItemsOverride The maximum number of items to be gathered. If this limit is reached, no further items will be gathered.
     */
    public static void gatherAndConsumeItems(Collection<ItemEntity> items, IItemHandler inventory, int minSlotInclusive, int maxSlotInclusive, int maxItemsOverride) {
        final List<ItemEntity> availableItemEntities = new ArrayList<>();
        int availableItems = 0;
        for (ItemEntity entity : items) {
            if (inventory.isItemValid(maxSlotInclusive, entity.getItem())) {
                availableItems += entity.getItem().getCount();
                availableItemEntities.add(entity);
            }
            if (availableItems > maxItemsOverride) {
                availableItems = maxItemsOverride;
            }
        }
        safelyConsumeItemsFromEntitiesIndividually(availableItemEntities, availableItems, item -> InventoryHelpers.insertSlots(inventory, item, minSlotInclusive, 1 + maxSlotInclusive).isEmpty());
    }

    /**
     * Consumes item entities from a collection, up to a specified maximum number of items.
     * Each item is passed to the provided consumer one at a time.
     * Assumes that consumption will always succeed.
     *
     * @param entities A collection of ItemEntity objects to be consumed.
     * @param maximum  The maximum number of items to consume.
     * @param consumer A consumer function that processes each item stack.
     */
    public static void consumeItemsFromEntitiesIndividually(Collection<ItemEntity> entities, int maximum, Consumer<ItemStack> consumer) {
        int consumed = 0;
        for (ItemEntity entity : entities) {
            final ItemStack stack = entity.getItem();
            while (consumed < maximum && !stack.isEmpty()) {
                consumer.accept(stack.split(1));
                consumed++;
                if (stack.isEmpty()) {
                    entity.discard();
                }
            }
        }
    }

    /**
     * Safely consumes item entities from a collection, up to a specified maximum number of items.
     * Each item is passed to the provided consumer one at a time.
     * If the consumer fails to process an item (returns {@code false}), the process stops.
     *
     * @param entities A collection of ItemEntity objects to be consumed.
     * @param maximum  The maximum number of items to consume.
     * @param consumer A predicate function that processes each item stack. Returns {@code true} if the stack was successfully consumed, {@code false} otherwise.
     */
    public static void safelyConsumeItemsFromEntitiesIndividually(Collection<ItemEntity> entities, int maximum, Predicate<ItemStack> consumer) {
        int consumed = 0;
        for (ItemEntity entity : entities) {
            final ItemStack stack = entity.getItem();
            while (consumed < maximum && !stack.isEmpty()) {
                final ItemStack offer = stack.copyWithCount(1);
                if (!consumer.test(offer)) {
                    return;
                }
                consumed++;
                stack.shrink(1);
                if (stack.isEmpty()) {
                    entity.discard();
                }
            }
        }
    }

    /**
     * Spawns an item entity at the specified position in the given level.
     *
     * @param level The level to spawn the item in.
     * @param pos   The position to spawn the item at.
     * @param stack The item stack to spawn.
     * @return True if the item was spawned successfully, false otherwise.
     */
    public static boolean spawnItem(Level level, Vec3 pos, ItemStack stack) {
        return level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, stack));
    }

    /**
     * Spawns an item entity at the specified block position with a vertical offset in the given level.
     *
     * @param level   The level to spawn the item in.
     * @param pos     The block position to spawn the item at.
     * @param stack   The item stack to spawn.
     * @param yOffset The vertical offset to apply to the spawn position.
     * @return True if the item was spawned successfully, false otherwise.
     */
    public static boolean spawnItem(Level level, BlockPos pos, ItemStack stack, double yOffset) {
        return level.addFreshEntity(
                new ItemEntity(
                        level,
                        pos.getX() + 0.5D,
                        pos.getY() + yOffset,
                        pos.getZ() + 0.5D,
                        stack
                )
        );
    }

    /**
     * Spawns an item entity at the specified block position in the given level, with a default vertical offset of 0.5D.
     *
     * @param level The level to spawn the item in.
     * @param pos   The block position to spawn the item at.
     * @param stack The item stack to spawn.
     * @return True if the item was spawned successfully, false otherwise.
     */
    public static boolean spawnItem(Level level, BlockPos pos, ItemStack stack) {
        return spawnItem(level, pos, stack, 0.5D);
    }

    /**
     * Checks if the given ItemStack matches the specified Item.
     *
     * @param stack the ItemStack to check
     * @param item  the Item to match
     * @return true if the ItemStack matches the Item, false otherwise
     */
    public static boolean isItem(ItemStack stack, ItemLike item) {
        return stack.is(item.asItem());
    }

    /**
     * Checks if the given ItemStack matches the specified Tag.
     *
     * @param stack the ItemStack to check
     * @param tag   the Tag to match
     * @return true if the ItemStack matches the Tag, false otherwise
     */
    public static boolean isItem(ItemStack stack, TagKey<Item> tag) {
        return stack.is(tag);
    }

    /**
     * Checks if the given Item matches the specified Tag.
     *
     * @param item the Item to check
     * @param tag  the Tag to match
     * @return true if the Item matches the Tag, false otherwise
     */
    @SuppressWarnings("deprecation")
    public static boolean isItem(Item item, TagKey<Item> tag) {
        return item.builtInRegistryHolder().is(tag);
    }

    /**
     * Returns a random Item from the specified Tag.
     *
     * @param tag    the Tag to select from
     * @param random the RandomSource to use for selection
     * @return a random Item from the Tag, or an empty Optional if the Tag is empty
     */
    public static Optional<Item> isItem(TagKey<Item> tag, RandomSource random) {
        return RandomHelpers.getRandomElement(BuiltInRegistries.ITEM, tag, random);
    }

    /**
     * Returns a Stream of all Items in the specified Tag.
     *
     * @param tag the Tag to select from
     * @return a Stream of all Items in the Tag
     */
    public static Stream<Item> allItems(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getOrCreateTag(tag).stream().map(Holder::value);
    }
}
