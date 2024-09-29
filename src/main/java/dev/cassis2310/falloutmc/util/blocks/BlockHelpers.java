package dev.cassis2310.falloutmc.util.blocks;

import dev.cassis2310.falloutmc.util.lists.RandomHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockHelpers {
    /**
     * Copies the properties from one BlockState to another.
     * This method allows for transferring all properties from the source BlockState to the target BlockState.
     *
     * @param copyTo   The BlockState to which properties will be copied.
     * @param copyFrom The BlockState from which properties will be copied.
     * @return The BlockState with properties copied from the source.
     */
    public static BlockState copyProperties(BlockState copyTo, BlockState copyFrom) {
        for (Property<?> property : copyFrom.getProperties()) {
            copyTo = copyProperty(copyTo, copyFrom, property);
        }
        return copyTo;
    }

    /**
     * Copies a specific property from one BlockState to another.
     * This method ensures that only the specified property is copied from the source BlockState to the target BlockState.
     *
     * @param copyTo   The BlockState to which the property will be copied.
     * @param copyFrom The BlockState from which the property will be copied.
     * @param property The property to copy.
     * @param <T>      The type of the property value.
     * @return The BlockState with the specified property copied from the source.
     */
    public static <T extends Comparable<T>> BlockState copyProperty(BlockState copyTo, BlockState copyFrom, Property<T> property) {
        return copyTo.hasProperty(property) ? copyTo.setValue(property, copyFrom.getValue(property)) : copyTo;
    }

    /**
     * Sets a property on a BlockState to a specified value.
     *
     * @param state    The BlockState to modify.
     * @param property The property to set.
     * @param value    The value to assign to the property.
     * @param <T>      The type of the property value.
     * @return The modified BlockState with the updated property, or the original state if the property doesn't exist.
     */
    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /**
     * Removes a block at the specified position {@link Level#removeBlock(BlockPos, boolean)} but with all flags available.
     * Uses the fluid state at the position to create a legacy block in its place.
     *
     * @param level The level in which the block resides.
     * @param pos   The position of the block to remove.
     * @param flags The flags controlling the block removal process.
     */
    public static void removeBlock(LevelAccessor level, BlockPos pos, int flags) {
        level.setBlock(pos, level.getFluidState(pos).createLegacyBlock(), flags);
    }

    /**
     * Destroys a block at the given position in the world and drops its items manually,
     * allowing customization of the loot context. This method is based on
     * {@link Level#destroyBlock(BlockPos, boolean, Entity, int)} but with additional flexibility.
     *
     * @param level   The server-level where the block is to be destroyed.
     * @param pos     The position of the block to destroy.
     * @param builder A consumer that allows modification of the loot context.
     */
    public static void destroyBlockAndDropBlocksManually(ServerLevel level, BlockPos pos, Consumer<LootParams.Builder> builder) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir()) {
            FluidState fluidState = level.getFluidState(pos);
            if (!(state.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, pos, Block.getId(state));
            }
            dropWithContext(level, state, pos, builder, true);
            level.setBlock(pos, fluidState.createLegacyBlock(), 3, 512);
        }
    }

    /**
     * Drops items from a block with a customizable loot context.
     * This method supports both randomized and non-randomized drop locations.
     *
     * @param level      The server-level where the items should be dropped.
     * @param state      The block state from which the drops are generated.
     * @param pos        The position of the block being processed.
     * @param consumer   A consumer to modify the loot context before processing drops.
     * @param randomized If true, the drops will be randomized in position; otherwise, they will be centered.
     */
    public static void dropWithContext(ServerLevel level, BlockState state, BlockPos pos, Consumer<LootParams.Builder> consumer, boolean randomized) {
        BlockEntity tileEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

        LootParams.Builder params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, null)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileEntity);
        consumer.accept(params);

        state.getDrops(params).forEach(stackToSpawn -> {
            if (randomized) {
                Block.popResource(level, pos, stackToSpawn);
            } else {
                spawnDropsAtExactCenter(level, pos, stackToSpawn);
            }
        });
        state.spawnAfterBreak(level, pos, ItemStack.EMPTY, false);
    }

    /**
     * Spawns an item stack at the exact center of the block's position without any randomness in velocity or position.
     * This is a deterministic version of {@link Block#popResource(Level, BlockPos, ItemStack)}.
     *
     * @param level The level where the item should be spawned.
     * @param pos   The position at which to spawn the item.
     * @param stack The ItemStack to spawn at the given position.
     */
    public static void spawnDropsAtExactCenter(Level level, BlockPos pos, ItemStack stack) {
        if (!level.isClientSide && !stack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack, 0D, 0D, 0D);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    /**
     * Rotates a VoxelShape by 90 degrees around the specified axis.
     *
     * @param direction the direction of rotation (must be NORTH, EAST, SOUTH, or WEST).
     * @param x1        the minimum x-coordinate of the shape.
     * @param y1        the minimum y-coordinate of the shape.
     * @param z1        the minimum z-coordinate of the shape.
     * @param x2        the maximum x-coordinate of the shape.
     * @param y2        the maximum y-coordinate of the shape.
     * @param z2        the maximum z-coordinate of the shape.
     * @return the rotated shape.
     * @throws IllegalArgumentException if the direction is not horizontal.
     */
    public static VoxelShape rotateShape(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2) {
        return switch (direction) {
            case NORTH -> Block.box(x1, y1, z1, x2, y2, z2);
            case EAST -> Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case SOUTH -> Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case WEST -> Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default -> throw new IllegalArgumentException("Not Horizontal!");
        };
    }

    /**
     * Computes the horizontal shapes for a given shape getter function.
     * Follows indexes for {@link Direction#get2DDataValue()}.
     *
     * @param shapeGetter a function that returns a shape for a given direction.
     * @return an array of shapes for the four horizontal directions (SOUTH, WEST, NORTH, EAST).
     */
    public static VoxelShape[] computeHorizontalShapes(Function<Direction, VoxelShape> shapeGetter) {
        return new VoxelShape[]{
                shapeGetter.apply(Direction.SOUTH),
                shapeGetter.apply(Direction.WEST),
                shapeGetter.apply(Direction.NORTH),
                shapeGetter.apply(Direction.EAST)
        };
    }

    /**
     * Checks if the given BlockState matches the specified Block.
     *
     * @param block the BlockState to check
     * @param other the Block to match
     * @return true if the BlockState matches the Block, false otherwise
     */
    public static boolean isBlock(BlockState block, Block other) {
        return block.is(other);
    }

    /**
     * Checks if the given BlockState matches the specified Tag.
     *
     * @param state the BlockState to check
     * @param tag   the Tag to match
     * @return true if the BlockState matches the Tag, false otherwise
     */
    public static boolean isBlock(BlockState state, TagKey<Block> tag) {
        return state.is(tag);
    }

    /**
     * Checks if the given Block matches the specified Tag.
     *
     * @param block the Block to check
     * @param tag   the Tag to match
     * @return true if the Block matches the Tag, false otherwise
     */
    @SuppressWarnings("deprecation")
    public static boolean isBlock(Block block, TagKey<Block> tag) {
        return block.builtInRegistryHolder().is(tag);
    }

    /**
     * Returns a random Block from the specified Tag.
     *
     * @param tag    the Tag to select from
     * @param random the RandomSource to use for selection
     * @return a random Block from the Tag, or an empty Optional if the Tag is empty
     */
    public static Optional<Block> randomBlock(TagKey<Block> tag, RandomSource random) {
        return RandomHelpers.getRandomElement(BuiltInRegistries.BLOCK, tag, random);
    }

    /**
     * Returns a Stream of all Blocks in the specified Tag.
     *
     * @param tag the Tag to select from
     * @return a Stream of all Blocks in the Tag
     */
    public static Stream<Block> allBlocks(TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.getOrCreateTag(tag).stream().map(Holder::value);
    }
}
