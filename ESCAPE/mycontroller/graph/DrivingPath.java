package mycontroller.graph;

import mycontroller.utils.Utils;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Defines a path through the world.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class DrivingPath {

	private LinkedList<Coordinate> coordinates;

	/**
	 * Creates a new empty path.
	 */
	DrivingPath() {
		coordinates = new LinkedList<>();
	}

	/**
	 * Creates a new path, defined by a list of coordinates.
	 *
	 * @param path Collection of coordinates to define this path.
	 */
	DrivingPath(List<Coordinate> path) {
		coordinates = new LinkedList<>(path);
	}

	/**
	 * @return The number of coordinates defining this  path through the world.
	 */
	public int getCoordinateCount() {
		return coordinates.size();
	}

	/**
	 * @param index Index of the coordinate to return.
	 * @return The index(th) coordinate defining this path.
	 */
	public Coordinate get(int index) {
		return coordinates.get(index);
	}

	/**
	 * @return The first coordinate defining this path.
	 */
	public Coordinate getFirst() {
		return coordinates.getFirst();
	}

	/**
	 * Removes the first coordinate in this path.
	 *
	 * @return The first coordinate in this path after removing it.
	 */
	public Coordinate removeFirst() {
		return coordinates.removeFirst();
	}

	/**
	 * @return The number of coordinates specifying this path.
	 * @see DrivingPath#getCoordinateCount()
	 */
	public int getPathSize() {
		return coordinates.size();
	}

	/**
	 * @return The last coordinate in this path.
	 */
	public Coordinate getLast() {
		return coordinates.getLast();
	}

	/**
	 * Removes the last coordinate from this path.
	 *
	 * @return The last coordinate in this path after removing it.
	 */
	public Coordinate removeLast() {
		return coordinates.removeLast();
	}

	/**
	 * Clears all coordinates from the internal list.
	 */
	public void clear() {
		coordinates.clear();
	}

	/**
	 * Resets this path to use a new collection of coordinates
	 *
	 * @param coordinates The new collection of coordinates.
	 */
	public void reset(Collection<Coordinate> coordinates) {
		this.coordinates.clear();
		this.coordinates.addAll(coordinates);
	}

	/**
	 * @return True if this path contains the specified coordinates.
	 */
	public boolean contains(Coordinate test) {
		return coordinates.contains(test);
	}

	/**
	 * @return The collection of coordinates that this path represents
	 */
	public Collection<Coordinate> getCoordinates() {
		return coordinates;
	}

	/**
	 * @return Creates a new DrivingPath with a copy of the underlying list structure.
	 */
	public DrivingPath clone() {
		return new DrivingPath(new LinkedList<>(coordinates));
	}

	/**
	 * @return The total euclidian distance travelled by this path in full.
	 */
	public int getTotalDistance() {
		int sum = 0;
		for (int i = 1; i < coordinates.size(); i++) {
			Coordinate first = coordinates.get(i - 1);
			Coordinate second = coordinates.get(i);

			/* Here we assume that each coordinate will be perpendicular to the other,
			 * not diagonal */
			sum += Math.abs(second.x - first.x) + Math.abs(second.y - first.y);
		}

		return sum;
	}

	/**
	 * @return True if the path traverses a tile of the given type. Or false if it either doesn't it cannot
	 * be determined.
	 */
	public boolean doesTraverse(Chart chart, MapTile.Type type) {
		for (Coordinate coordinate : coordinates) {
			if (chart.hasObserved(coordinate)) {
				MapTile tile = chart.getTileAt(coordinate);
				if (tile.isType(type))
					return true;
			}
		}
		return false;
	}

	/**
	 * @return The number of tiles of a specified type this path traverses. Will be equal to or less than the
	 * actual amount, as some tiles may not yet have been discovered
	 */
	public int getTraverseCount(Chart chart, MapTile.Type type) {
		int sum = 0;
		for (Coordinate coordinate : coordinates) {
			if (chart.hasObserved(coordinate)) {
				MapTile tile = chart.getTileAt(coordinate);
				if (tile.isType(type))
					sum++;
			}
		}
		return sum;
	}

	/**
	 * @return The direction from the first to the second coordinate int his path, or null if the
	 * path is not long enough to have an orientation.
	 */
	public WorldSpatial.Direction getFirstDirection() {
		if (getCoordinateCount() >= 2) {
			return Utils.getDifference(get(0), get(1));
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DrivingPath[");
		for (int i = 0; i < coordinates.size(); i++) {
			builder.append("(").append(coordinates.get(i)).append(")");

			if (i + 1 < coordinates.size())
				builder.append(",");
		}
		builder.append("]");
		return builder.toString();
	}
}
