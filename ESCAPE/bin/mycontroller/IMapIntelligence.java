package mycontroller;

import mycontroller.graph.Chart;
import mycontroller.graph.DrivingPath;
import mycontroller.graph.IPathFinder;
import mycontroller.graph.IPathCalculator;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * This interface is designed as a Pure Fabrication. It provides a service
 * between the caller and the implemented {@link Chart}, {@link IPathCalculator }and {@link IPathFinder}.
 * As such, many methods in here are default-methods. This is INTENTIONAL. This is meant to be an
 * interface.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IMapIntelligence {

	/**
	 * @return Our current {@link Chart} of the maze world.
	 */
	public Chart getChart();

	/**
	 * @return The default {@link IPathCalculator} used for weighing paths throughout the maze.
	 */
	public IPathCalculator getDefaultCalculator();

	/**
	 * @return The default {@link IPathFinder} used for finding paths throughout the maze.
	 */
	public IPathFinder getDefaultPathFinder();

	/**
	 * @return The current path for traversing the map.
	 */
	public DrivingPath getCurrentPath();

	/**
	 * @param path New path to traverse the map with.
	 */
	public void setNewPath(DrivingPath path);

	///////////////////////////////////////////////////////////////////////////
	//
	// This interface is designed as a Pure Fabrication. It provides a service
	// between the caller and the implemented Chart, IPathCalculator and
	//
	///////////////////////////////////////////////////////////////////////////

	public default Set<Coordinate> getMostUpdateView() {
		return getChart().getMostRecentDiscoveries();
	}

	public default boolean isLavaCoordinate(Coordinate coordinate) {
		return getChart().isLavaCoordinate(coordinate);
	}

	public default boolean isHealthCoordinate(Coordinate coordinate) {
		return getChart().isHealthCoordinate(coordinate);
	}

	public default Coordinate getHighestKeyCoordinate() {
		return getChart().getHighestKeyCoordinate();
	}

	public default int getKeyFromCoordinate(Coordinate coordinate) {
		return getChart().getKeyFromCoordinate(coordinate);
	}

	public default Coordinate getCoordinateFromKey(int key) {
		return getChart().getCoordinateFromKey(key);
	}

	public default Coordinate getFinishCoordinate() {
		return getChart().getFinishCoordinate();
	}


}
