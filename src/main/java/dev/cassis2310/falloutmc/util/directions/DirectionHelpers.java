package dev.cassis2310.falloutmc.util.directions;

import net.minecraft.core.Direction;

public class DirectionHelpers {
    /**
     * An array containing all possible directions in Minecraft, useful for iterating through all directions.
     */
    public static final Direction[] DIRECTIONS = Direction.values();

    /**
     * An array containing all directions except UP, useful for operations that need to exclude the upward direction.
     */
    public static final Direction[] DIRECTIONS_NOT_UP = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * An array containing all directions except DOWN, useful for operations that need to exclude the downward direction.
     */
    public static final Direction[] DIRECTIONS_NOT_DOWN = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * An array containing all directions except NORTH, useful for operations that need to exclude the northern direction.
     */
    public static final Direction[] DIRECTIONS_NOT_NORTH = new Direction[]{Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * An array containing all directions except EAST, useful for operations that need to exclude the eastern direction.
     */
    public static final Direction[] DIRECTIONS_NOT_EAST = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST};

    /**
     * An array containing all directions except SOUTH, useful for operations that need to exclude the southern direction.
     */
    public static final Direction[] DIRECTIONS_NOT_SOUTH = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST};

    /**
     * An array containing all directions except WEST, useful for operations that need to exclude the western direction.
     */
    public static final Direction[] DIRECTIONS_NOT_WEST = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST};
}
