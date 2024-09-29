package dev.cassis2310.falloutmc.util.worlds;

import dev.cassis2310.falloutmc.util.directions.DirectionHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorldHelpers {
    /**
     * Safely retrieves the Level object from an unknown type.
     * This method is useful when dealing with code that may interact with Level objects in unconventional ways.
     *
     * @param maybeLevel An object that might be a Level or related type.
     * @return The Level, or null if the object is not a Level.
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static Level getUnsafeLevel(Object maybeLevel) {
        if (maybeLevel instanceof Level level) {
            return level; // Most obvious case, if we can directly cast up to level.
        }
        if (maybeLevel instanceof WorldGenRegion level) {
            return level.getLevel(); // Special case for world gen, when we can access the level unsafely
        }
        return null; // A modder has done a strange ass thing
    }

    /**
     * Attempts to spread fire in a random direction around a specified position in the world.
     * The fire spread is controlled by game rules and can be influenced by neighboring blocks.
     *
     * @param level  The server-level where the fire spread should occur.
     * @param pos    The starting position for fire spreading.
     * @param random A random source for determining fire spread direction and chance.
     * @param radius The radius within which the fire can spread.
     */
    public static void fireSpreaderTick(ServerLevel level, BlockPos pos, RandomSource random, int radius) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            for (int i = 0; i < radius; i++) {
                pos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                if (level.getRandom().nextFloat() < 0.25F) {
                    pos = pos.above();
                }
                final BlockState state = level.getBlockState(pos);
                if (!state.isAir()) {
                    return;
                }
                if (hasFlammableNeighbours(level, pos)) {
                    level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    return;
                }
            }
        }
    }

    /**
     * Checks if there are any flammable blocks adjacent to the specified position.
     *
     * @param level The level reader to check the blocks.
     * @param pos   The position to check for flammable neighbors.
     * @return {@code true} if there are flammable blocks adjacent to the position, otherwise {@code false}.
     */
    private static boolean hasFlammableNeighbours(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : DirectionHelpers.DIRECTIONS) {
            mutable.setWithOffset(pos, direction);
            if (level.getBlockState(mutable).isFlammable(level, mutable, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Plays a sound at the specified position in the given level.
     *
     * @param level The level to play the sound in.
     * @param pos   The position to play the sound at.
     * @param sound The sound event to play.
     */
    public static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        var rand = level.getRandom();
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f + rand.nextFloat(), rand.nextFloat() + 0.7f + 0.3f);
    }

    /**
     * Plays the placement sound for the given block state at the specified position.
     *
     * @param level The level to play the sound in.
     * @param pos   The position to play the sound at.
     * @param state The block state to play the sound for.
     */
    public static void playPlaceSound(LevelAccessor level, BlockPos pos, BlockState state) {
        playPlaceSound(level, pos, state.getSoundType(level, pos, null));
    }

    /**
     * Plays the placement sound for the given sound type at the specified position.
     *
     * @param level The level to play the sound in.
     * @param pos   The position to play the sound at.
     * @param st    The sound type to play the sound for.
     */
    public static void playPlaceSound(LevelAccessor level, BlockPos pos, SoundType st) {
        level.playSound(null, pos, st.getPlaceSound(), SoundSource.BLOCKS, (st.getVolume() + 1.0f) / 2.0f, st.getPitch() * 0.8f);
    }

    /**
     * Converts a quart position to a block position.
     *
     * @param x the x-coordinate of the quart position.
     * @param y the y-coordinate of the quart position.
     * @param z the z-coordinate of the quart position.
     * @return the corresponding block position.
     * @see net.minecraft.core.QuartPos#toBlock(int)
     */
    public static BlockPos quartToBlock(int x, int y, int z) {
        return new BlockPos(x << 2, y << 2, z << 2);
    }

    /**
     * This exists to fix a horrible case of vanilla seeding, which led to noticeable issues of feature clustering.
     * The key issue was that features with a chance placement, applied sequentially, would appear to generate on the same chunk much more often than was expected.
     * This was then identified as the problem by the lovely KaptainWutax <3. The following is an excerpt / paraphrase from our conversation:
     * <pre>
     * So you're running setSeed(n), setSeed(n + 1) and setSeed(n + 2) on the 3 structure respectively.
     * And n is something we can compute given a chunk and seed.
     * setSeed applies an xor on the lowest 35 bits and assigns that value internally
     * But like, since your seeds are like 1 apart
     * Even after the xor they're at worst 1 apart
     * You can convince yourself of that quite easily
     * So now nextFloat() does seed = 25214903917 * seed + 11 and returns (seed >> 24) / 2^24
     * Sooo lets see what the actual difference in seeds are between your 2 features in the worst case:
     * a = 25214903917, b = 11
     * So a * (seed + 1) + b = a * seed + b + a
     * As you can see the internal seed only varies by "a" amount
     * Now we can measure the effect that big number has no the upper bits since the seed is shifted
     * 25214903917/2^24 = 1502.92539101839
     * And that's by how much the upper 24 bits will vary
     * The effect on the next float are 1502 / 2^24 = 8.95261764526367e-5
     * Blam, so the first nextFloat() between setSeed(n) and setSeed(n + 1) is that distance apart ^
     * Which as you can see... isn't that far from 0
     * </pre>
     */
    public static void seedLargeFeatures(RandomSource random, long baseSeed, int index, int decoration) {
        random.setSeed(baseSeed);
        final long seed = (index * random.nextLong() * 203704237L) ^ (decoration * random.nextLong() * 758031792L) ^ baseSeed;
        random.setSeed(seed);
    }
}
