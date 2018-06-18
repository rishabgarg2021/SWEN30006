package mycontroller.graph;

import utilities.Coordinate;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IPathFinder {

	/**
	 * Compute a path between the two coordinates in the world.
	 *
	 * @param start  Coordinate to start the path from.
	 * @param target Coordinate to finish the path at.
	 */
	public DrivingPath getPath(Coordinate start, Coordinate target);


}
