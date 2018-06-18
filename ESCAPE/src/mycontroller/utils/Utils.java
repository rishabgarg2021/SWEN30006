package mycontroller.utils;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;

import static world.WorldSpatial.Direction.*;
import static world.WorldSpatial.RelativeDirection.LEFT;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Class providing various utility methods for use in other classes.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class Utils {

	private Utils() {
	}

	/**
	 * Determine if a direction matches a degree value.
	 *
	 * @param degree Degree to test with.
	 * @param dir    Direction to match against.
	 * @return True if the angle and direction are equivalent.
	 */
	public static boolean angleFromOrientation(float degree, WorldSpatial.Direction dir) {
		//TODO: float to int comparisons
		switch (dir) {
			case NORTH:
				if (degree == WorldSpatial.NORTH_DEGREE) {
					return true;
				}
			case SOUTH:
				if (degree == WorldSpatial.SOUTH_DEGREE) {
					return true;
				}
			case EAST:
				if (degree == WorldSpatial.EAST_DEGREE_MIN) {
					return true;
				}
			case WEST:
				if (degree == WorldSpatial.WEST_DEGREE) {
					return true;
				}

		}
		return false;
	}

	/**
	 * Compute the direction from one coordinate to the other.
	 * <p>
	 * This method assumes that the coordinates are perpendicular to one-another.
	 *
	 * @param first  First coordinate.
	 * @param second Second coordinate.
	 * @return The direction from the first coordinate to the second.
	 */
	public static WorldSpatial.Direction getDifference(Coordinate first, Coordinate second) {
		//Assume always next to each other and not diagonal
		if (first.y == second.y) {
			return first.x < second.x ? EAST : WEST;
		} else {
			return first.y < second.y ? NORTH : SOUTH;
		}
	}

	/**
	 * Move the specified coordinate in the world in a specified direction and by a specified distance.
	 *
	 * @param coordinate Coordinate to move in the world.
	 * @param direction  Direction to move the coordinate in.
	 * @param distance   Distance to move the coordinate in that direction.
	 * @return The new, offset, coordinate.
	 */
	public static Coordinate offset(Coordinate coordinate, WorldSpatial.Direction direction, int distance) {
		switch (direction) {
			case NORTH:
				return new Coordinate(coordinate.x, coordinate.y + distance);
			case EAST:
				return new Coordinate(coordinate.x + distance, coordinate.y);
			case SOUTH:
				return new Coordinate(coordinate.x, coordinate.y - distance);
			case WEST:
				return new Coordinate(coordinate.x - distance, coordinate.y);
			default:
				return coordinate;
		}
	}

	/**
	 * Same as {@link Utils#offset(Coordinate, WorldSpatial.Direction, int)} with a distance of 1.
	 *
	 * @see Utils#offset(Coordinate, WorldSpatial.Direction, int)
	 */
	public static Coordinate offset(Coordinate coordinate, WorldSpatial.Direction direction) {
		return offset(coordinate, direction, 1);
	}

	/**
	 * Checks if a tile of a certain type is within a specified raius in our current view.
	 *
	 * @param view            Current view to search in.
	 * @param position        Starting position to begin the search.
	 * @param radiusInclusive Radius to search for.
	 * @param direction       Direction from the starting point to search in.
	 * @param type            Type of tile to search for.
	 * @return True if a tile of the specified type was found in the search. Returns false at the first coordinate which is not in the current view.
	 */
	public static boolean checkWithinView(HashMap<Coordinate, MapTile> view, Coordinate position, int radiusInclusive, WorldSpatial.Direction direction, MapTile.Type type) {
		for (int i = 0; i <= radiusInclusive; i++) {
			Coordinate coordinate = offset(position, direction, i);
			if (!view.containsKey(coordinate))
				return false;
			if (view.get(coordinate).isType(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Same as {@link Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)} with {@link WorldSpatial.Direction#EAST} as the direction.
	 * <p>
	 *
	 * @see Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)
	 */
	public static boolean checkWithinViewEast(HashMap<Coordinate, MapTile> view, Coordinate position, int radiusInclusive, MapTile.Type type) {
		return checkWithinView(view, position, radiusInclusive, WorldSpatial.Direction.EAST, type);
	}

	/**
	 * Same as {@link Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)} with {@link WorldSpatial.Direction#WEST} as the direction.
	 * <p>
	 *
	 * @see Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)
	 */
	public static boolean checkWithinViewWest(HashMap<Coordinate, MapTile> view, Coordinate position, int radiusInclusive, MapTile.Type type) {
		return checkWithinView(view, position, radiusInclusive, WorldSpatial.Direction.WEST, type);
	}

	/**
	 * Same as {@link Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)} with {@link WorldSpatial.Direction#NORTH} as the direction.
	 * <p>
	 *
	 * @see Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)
	 */
	public static boolean checkWithinViewNorth(HashMap<Coordinate, MapTile> view, Coordinate position, int radiusInclusive, MapTile.Type type) {
		return checkWithinView(view, position, radiusInclusive, WorldSpatial.Direction.NORTH, type);
	}

	/**
	 * Same as {@link Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)} with {@link WorldSpatial.Direction#SOUTH} as the direction.
	 * <p>
	 *
	 * @see Utils#checkWithinView(HashMap, Coordinate, int, WorldSpatial.Direction, MapTile.Type)
	 */
	public static boolean checkWithinViewSouth(HashMap<Coordinate, MapTile> view, Coordinate position, int radiusInclusive, MapTile.Type type) {
		return checkWithinView(view, position, radiusInclusive, WorldSpatial.Direction.SOUTH, type);
	}

	/**
	 * Rotates the given direction by 90 degrees in the specified relative direction.
	 *
	 * @param direction Absolute direction to rotate.
	 * @param rotation  Relative direction to rotate by.
	 * @return The new, rotated absolute direction.
	 */
	public static WorldSpatial.Direction rotate(WorldSpatial.Direction direction, WorldSpatial.RelativeDirection rotation) {
		if (rotation == null)
			return direction;

		switch (direction) {
			case NORTH:
				return rotation == LEFT ? WEST : EAST;
			case EAST:
				return rotation == LEFT ? NORTH : SOUTH;
			case SOUTH:
				return rotation == LEFT ? EAST : WEST;
			case WEST:
				return rotation == LEFT ? SOUTH : NORTH;
			default:
				return direction;
		}
	}
}
