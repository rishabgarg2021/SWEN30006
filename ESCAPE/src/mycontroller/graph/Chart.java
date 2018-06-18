package mycontroller.graph;

import mycontroller.utils.Utils;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * <p>
 * Class for retaining information about the discovered game world.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class Chart {

	/**
	 * A 2D array of all the tiles in the world that have been provided to this {@link Chart}
	 */
	private MapTile[][] map = new MapTile[World.MAP_WIDTH][World.MAP_HEIGHT];

	/**
	 * A set of the coordinates which have been 'observed' by the car
	 */
	private HashMap<Coordinate, Boolean> tileObservationStatus = new HashMap<>();

	/**
	 * A set of the coordinates which were first observed in the last update cycle
	 */
	private Set<Coordinate> mostRecentDiscoveries = new HashSet<>();

	/**
	 * Coordinate of the starting tile
	 */
	private Coordinate start;

	/**
	 * A set of all provided tiles
	 *
	 * @see World#getMap()
	 */
	private HashMap<Coordinate, MapTile> providedTiles;

	/**
	 * A set of all coordinates which contain a finishing tile
	 */
	private Set<Coordinate> finish = new HashSet<>();

	/**
	 * A set of all found keys and their coordinates
	 */
	private HashMap<Coordinate, Integer> keys = new HashMap<>();

	/**
	 * A set of all reachable coordinates. This is determined at initialisation
	 * by recursively exploring the chart from the starting tile, stopping at any providedTiles
	 */
	private Set<Coordinate> reachableLocations = new HashSet<>();


	/**
	 * A set of all coordinates in which lavaTrapLocations was discovered
	 */
	private Set<Coordinate> lavaTrapLocations = new HashSet<>();

	/**
	 * A set of all coordinates in which a healthTrapLocations trap was discovered
	 */
	private Set<Coordinate> healthTrapLocations = new HashSet<>();

	public boolean isRoad(Coordinate c){
		MapTile tile = map[c.x][c.y];
		if(tile.isType(MapTile.Type.ROAD) ){
			return true;


		}
		return false;
	}

	/**
	 * Create a new chart with an initial view from the car
	 *
	 * @param view Current view from the car
	 * @see Car#getView()
	 */
	public Chart(HashMap<Coordinate, MapTile> view) {

		for (int i = 0; i < World.MAP_HEIGHT; i++) {
			for (int j = 0; j < World.MAP_WIDTH; j++) {
				map[j][i] = null;
			}
		}

		providedTiles = World.getMap();
		updateInitialInfo();
		updateView(view);

	}


	/**
	 * @return True if a lavaTrapLocations trap has been discovered at the specified coordinate. False otherwise.
	 */
	public boolean isLavaCoordinate(Coordinate coordinate) {
		return lavaTrapLocations.contains(coordinate);
	}

	/**
	 * @return True if a healthTrapLocations trap has been discovered at the specified coordinate. False otherwise.
	 */
	public boolean isHealthCoordinate(Coordinate coordinate) {
		return healthTrapLocations.contains(coordinate);
	}


	public Set<Coordinate> getHealthCoordinates(){
		return healthTrapLocations;
	}
	/**
	 * @return The coordinate in which the key of highest value can be found or null if no keys are known to be in the chart.
	 */
	public Coordinate getHighestKeyCoordinate() {
		int highestKey = -1;
		Coordinate keyCoordinate = null;
		for (Coordinate c : keys.keySet()) {
			if (keys.get(c) > highestKey) {
				highestKey = keys.get(c);
				keyCoordinate = c;
			}

		}
		return keyCoordinate;
	}


	/**
	 * @return A coordinate which has not yet been observed. Or null if none can be found
	 */
	public Coordinate getNonVisitCoord() {
		for (Coordinate c : tileObservationStatus.keySet()) {
			if (!tileObservationStatus.get(c) && reachableLocations.contains(c)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Updated the chart with the initial information provided from the {@link World} class.
	 */
	private void updateInitialInfo() {
		for (int y = 0; y < World.MAP_HEIGHT; y++) {
			for (int x = 0; x < World.MAP_WIDTH; x++) {
				Coordinate position = new Coordinate(x, y);

				MapTile tile = providedTiles.get(position);

				if (!(tile.getType().equals(MapTile.Type.WALL))) {
					tileObservationStatus.put(new Coordinate(x, y), false);
				}
				if ((tile.getType().equals(MapTile.Type.START))) {
					start = new Coordinate(x, y);
				}
				if ((tile.getType().equals(MapTile.Type.FINISH))) {
					finish.add(new Coordinate(x, y));
				}


			}
		}

		for (Coordinate position : providedTiles.keySet()) {
			map[position.x][position.y] = providedTiles.get(position);
		}



		performReachableAnalysis(start);
	}


	/**
	 * Determine what tiles in the chart are reachable.
	 * i.e. A path exists between the start tile location and the tile under question
	 * which does not traverse a wall.
	 *
	 * @param start Location to analyse
	 */
	private void performReachableAnalysis(Coordinate start) {
		if (reachableLocations.add(start)) {
			/* Go through all cardinal directions and recursively check those tiles  */
			for (WorldSpatial.Direction cardinalDirection : WorldSpatial.Direction.values()) {
				Coordinate offset = Utils.offset(start, cardinalDirection, 1);
				if (providedTiles.containsKey(offset) && !providedTiles.get(offset).isType(MapTile.Type.WALL)) {
					performReachableAnalysis(offset);
				}
			}
		}
	}


	void printMap() {
		for (int y = World.MAP_HEIGHT - 1; y >= 0; y--) {
			for (int x = 0; x < World.MAP_WIDTH; x++) {
				System.out.print(map[x][y].getType() + " ");

			}
		}
	}

	/**
	 * Get the chart tile at a given coordinate
	 *
	 * @param coordinate The coordinate to get the chart tile from
	 * @return The chart tile at the given coordinate, or null if the coordinate either falls outside of the
	 * world or the tile has neither been provided or discovered.
	 */
	public MapTile getTileAt(Coordinate coordinate) {
		if (coordinate.x >= 0 && coordinate.y >= 0 && coordinate.x < World.MAP_WIDTH && coordinate.y < World.MAP_HEIGHT)
			return map[coordinate.x][coordinate.y];
		return null;
	}


	/**
	 * Retrieves the value of a key at the specified coordinate
	 *
	 * @param position Location to check for keys
	 * @return The value of a key at this coordinate, or -1 if none is found
	 */
	public int getKeyFromCoordinate(Coordinate position) {
		if (map[position.x][position.y] instanceof LavaTrap) {
			return ((LavaTrap) map[position.x][position.y]).getKey();
		}
		return -1;

	}

	/**
	 * Retrieves the coordinate a specific-valued key can be found
	 *
	 * @param key Value of the key to find
	 * @return The coordinate containing the requested key, or null if the key value has not been found
	 */
	public Coordinate getCoordinateFromKey(int key) {
		for (Coordinate c : keys.keySet()) {
			if (keys.get(c) == key) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @return The coordinate of the first finish tile in the chart.
	 */
	public Coordinate getFinishCoordinate() {
		return finish.iterator().next();
	}

	/**
	 * @return The set of all coordinates which were discovered in the most recent update cycle
	 */
	public Set<Coordinate> getMostRecentDiscoveries() {
		return mostRecentDiscoveries;
	}

	/**
	 * Update the chart with the current view from the car.
	 * To retrieve a set of the "newly-discovered" coordinates in this update see {@link Chart#getMostRecentDiscoveries()}
	 *
	 * @see Chart#getMostRecentDiscoveries()
	 */
	public void updateView(HashMap<Coordinate, MapTile> currentView) {
		mostRecentDiscoveries.clear();

		for (Coordinate position : currentView.keySet()) {
			if (position.x >= 0 && position.y >= 0 && position.x < World.MAP_WIDTH && position.y < World.MAP_HEIGHT) {
				map[position.x][position.y] = currentView.get(position);
			}


			if (currentView.get(position) != null) {
				if (!tileObservationStatus.containsKey(position) || !tileObservationStatus.get(position))
					mostRecentDiscoveries.add(position);
				tileObservationStatus.put(position, true);
			}
			if ((currentView.get(position).getType().equals(MapTile.Type.TRAP))) {
				if (currentView.get(position) instanceof HealthTrap) {
					healthTrapLocations.add(position);
				}
			}

			if (currentView.get(position).getType().equals(MapTile.Type.TRAP)) {
				if (currentView.get(position) instanceof LavaTrap && ((LavaTrap) currentView.get(position)).getKey() != 0) {
					keys.put(position, ((LavaTrap) currentView.get(position)).getKey());
				}
				if (currentView.get(position) instanceof LavaTrap) {
					lavaTrapLocations.add(position);
				}
			}
		}
	}

	//can't move to diagonal in chart.
	public boolean possibleDirection(int x, int y) {
		if ((x == 1 && y == 1) || (x == -1 && y == -1) || (x == 1 && y == -1) || (x == -1 && y == 1) || (x == 0 && y == 0)) {
			return false;
		}
		return true;

	}


	/**
	 * Query if a location has been observed or not.
	 *
	 * @param coordinate The coordinate to query
	 * @return True if the coordinate has been observed, false otherwise
	 */
	public boolean hasObserved(Coordinate coordinate) {
		return tileObservationStatus.getOrDefault(coordinate, false);
	}
}
