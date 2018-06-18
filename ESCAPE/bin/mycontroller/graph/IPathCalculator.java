package mycontroller.graph;

import tiles.MapTile;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * <p>
 * Interface for calculating the costs associated with traversing a path through the world
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IPathCalculator {

	/**
	 * Calculate the cost for traversing a chart tile
	 *
	 * @param tile The tile to calculate a traversing cost for
	 * @return The cost to traverse the specified tile, or 1 if the tile is null
	 */
	public int calculateCost(MapTile tile);

}
