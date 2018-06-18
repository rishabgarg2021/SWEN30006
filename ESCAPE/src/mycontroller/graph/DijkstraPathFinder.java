package mycontroller.graph;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Implementation of {@link IPathFinder} which uses Dijkstra's algorithm for finding the shortest distance between two points.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class DijkstraPathFinder implements IPathFinder {

	private IPathCalculator calculator;
	private Chart chart;

	public DijkstraPathFinder(IPathCalculator calculator, Chart chart) {
		this.calculator = calculator;
		this.chart = chart;
	}

	@Override
	public DrivingPath getPath(Coordinate start, Coordinate target) {
		return new DrivingPath(getMoves(start, target));
	}

	private ArrayList<Coordinate> getMoves(Coordinate start, Coordinate target) {

		ArrayList<Coordinate> moves = new ArrayList<>();
		HashMap<Coordinate, Boolean> visited = new HashMap<>();
		HashMap<Coordinate, Integer> distance = new HashMap<>();
		HashMap<Coordinate, Coordinate> parent = new HashMap<>();


		for (int i = 0; i < World.MAP_HEIGHT; i++) {
			for (int j = 0; j < World.MAP_WIDTH; j++) {
				distance.put(new Coordinate(j, i), Integer.MAX_VALUE);
				visited.put(new Coordinate(j, i), false);
			}
		}

		djkUtil(start, target, visited, distance, parent);


		return djkPath(parent, start, target);


	}


	private Coordinate minDistanceCoord(HashMap<Coordinate, Boolean> visited, HashMap<Coordinate, Integer> distance) {
		// Initialize min value
		int min = Integer.MAX_VALUE;
		Coordinate min_cord = null;
		for (int i = 0; i < World.MAP_HEIGHT; i++) {
			for (int j = 0; j < World.MAP_WIDTH; j++) {
				if (!visited.get(new Coordinate(j, i)) && distance.get(new Coordinate(j, i)) <= min) {
					min = distance.get(new Coordinate(j, i));
					min_cord = new Coordinate(j, i);
				}
			}
		}


		return min_cord;
	}

	private ArrayList<Coordinate> djkPath(HashMap<Coordinate, Coordinate> parent, Coordinate start, Coordinate target) {

		LinkedList<Coordinate> path = new LinkedList<>();
		Coordinate curr = target;
		Coordinate parent_start = new Coordinate(-100, -100);
		if (parent.get(curr) == null) {
			return null;
		}
		while (!parent.get(curr).equals(parent_start)) {
			path.push(curr);
			curr = parent.get(curr);
		}
		path.push(start);
		ArrayList<Coordinate> traversePath = new ArrayList<>();
		for (int i = 0; i < path.size(); i++) {
			traversePath.add(path.get(i));
		}


		return traversePath;


	}

	private void djkUtil(Coordinate start, Coordinate target, HashMap<Coordinate, Boolean> visited, HashMap<Coordinate, Integer> distance,
						 HashMap<Coordinate, Coordinate> parent) {

		distance.put(start, 0);
		parent.put(start, new Coordinate(-100, -100));
		for (int i = 0; i < World.MAP_HEIGHT; i++) {
			for (int j = 0; j < World.MAP_WIDTH; j++) {


				Coordinate minCord = minDistanceCoord(visited, distance);


				visited.put(minCord, true);
				if (minCord.x == target.x && minCord.y == target.y) {

					return;
				}

				{


					// need to check the weight of tile and then append it.
					//1 default.
					//add weight if you find the traps.
					//added for lavaTrap now.

					for (Coordinate newCoord : getSafeChild(minCord)) {

						if (visited.get(new Coordinate(newCoord.x, newCoord.y)) == false && distance.get(minCord) != Integer.MAX_VALUE
									&& distance.get(minCord) + getWeightFromMap(newCoord.x, newCoord.y) < distance.get(new Coordinate(newCoord.x, newCoord.y))) {
							distance.put(new Coordinate(newCoord.x, newCoord.y), distance.get(minCord) + getWeightFromMap(newCoord.x, newCoord.y));


							parent.put(new Coordinate(newCoord.x, newCoord.y), minCord);
						}

					}
				}
			}
		}
	}

	private int getWeightFromMap(int x, int y) {
		return calculator.calculateCost(chart.getTileAt(new Coordinate(x, y)));
	}

	private ArrayList<Coordinate> getSafeChild(Coordinate cord) {

		ArrayList<Coordinate> child = new ArrayList<>();
		int[] x_p = {1, 0, -1};
		int[] y_p = {1, 0, -1};
		for (int m = 0; m < x_p.length; m++) {
			for (int n = 0; n < y_p.length; n++) {
				int x = x_p[m];
				int y = y_p[n];

				if (chart.possibleDirection(x, y) && (x + cord.x >= 0) && (y + cord.y >= 0) &&
							((x + cord.x) < World.MAP_WIDTH) && ((y + cord.y) < World.MAP_HEIGHT)
							&& (!chart.getTileAt(new Coordinate(x + cord.x, y + cord.y)).getType().equals(MapTile.Type.WALL))) {
					child.add(new Coordinate(x + cord.x, y + cord.y));
				}
			}
		}
		return child;
	}


}
