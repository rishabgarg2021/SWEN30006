package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;
import mycontroller.graph.DrivingPath;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static world.WorldSpatial.RelativeDirection.LEFT;
import static world.WorldSpatial.RelativeDirection.RIGHT;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy for when the car is traversing the map.
 * Has the following states:
 * Exploring,
 * Targeting,
 * Finishing
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class TraversingStrategy implements IDrivingStrategy {

	private static TraverseState movingState = TraverseState.EXPLORE;
	private static Set<Coordinate> lastHealthView = new HashSet<>();

	private static Set<Coordinate> getHealthTileInView(IMapIntelligence intelligence, Set<Coordinate> view) {
		Set<Coordinate> healthTiles = new HashSet<>();

		for (Coordinate c : view) {
			if (intelligence.isHealthCoordinate(c)) {
				healthTiles.add(c);
			}
		}
		return healthTiles;
	}

	private static void updateHealthView(IMapIntelligence intelligence, HashMap<Coordinate, MapTile> view) {
		lastHealthView = getHealthTileInView(intelligence, view.keySet());
	}

	private int lavaTileAhead(IMapIntelligence intelligence, DrivingPath path, Set<Coordinate> recentView) {
		//check if in path you are going has a lava tile in first 4 size of path, try to find alternative paths at every point, until you
		// you get rid of lava tiles in first 4 places.
		int lavaTiles = 0;
		int counterToCheck = 0;
		for (Coordinate c : path.getCoordinates()) {
			counterToCheck += 1;

			if (counterToCheck == 12) {

				break;
			}
			if (recentView.contains(c) && intelligence.isLavaCoordinate(c)) {
				lavaTiles += 1;
			}

		}

		return lavaTiles;
	}

	private DrivingPath foundSafePathToHealthTile(ISensor sensor, IControls controls, IMapIntelligence intelligence) {
		Coordinate lastHealth = null;
		if (lastHealthView != null) {
			for (Coordinate c : lastHealthView) {
				lastHealth = new Coordinate(c.x, c.y);
				break;
			}
		}

		Set<Coordinate> view = getHealthTileInView(intelligence, sensor.getView().keySet());
		view.removeAll(lastHealthView);

		for (Coordinate c : sensor.getView().keySet()) {
			if (intelligence.isLavaCoordinate(c)) {
				view.add(c);
			}
		}
		if (lastHealth != null) {

			view.add(lastHealth);
			for (Coordinate c : view) {
				DrivingPath path = intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), c);
				for (Coordinate coord : path.getCoordinates()) {
					if (intelligence.isLavaCoordinate(coord)) {
						break;
					}
					if (intelligence.isHealthCoordinate(coord)) {
						return path;
					}
				}

			}
		}
		return null;
	}

	private boolean isHealthInView(IMapIntelligence intelligence, HashMap<Coordinate, MapTile> view) {
		for (Coordinate c : view.keySet()) {
			if (intelligence.isHealthCoordinate(c)) {
				return true;
			}
		}
		return false;
	}

	public boolean isHealthTileNear(IMapIntelligence intelligence){
		int counter=0;

		for(Coordinate c: intelligence.getCurrentPath().getCoordinates()){
			counter+=1;
			if(counter>7){
				break;
			}
			if(intelligence.isHealthCoordinate(c)){
				return true;
			}


		}
		return false;
	}
	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {
		//checks if you are not on the lava tile while recalculating the path.
		//check if your recent view just explored the lavatile, we need to take alternative path if it really exist and should assign it.(checked by dijkstra to do so)


		if (isHealthInView(intelligence, sensor.getView()) && controls.getHealth() < 90 && !intelligence.isLavaCoordinate(controls.getCoordinate())) {

			DrivingPath healthPath = intelligence.getCurrentPath().clone();
			if (!intelligence.isHealthCoordinate(intelligence.getCurrentPath().getLast())) {
				healthPath = foundSafePathToHealthTile(sensor, controls, intelligence);
			}

			if (healthPath != null && isHealthTileNear(intelligence)) {
				intelligence.setNewPath(healthPath);
				actor.setDrivingStrategy(new HealingStrategy(), false);
				updateHealthView(intelligence, sensor.getView());
				return;


			}
		}
		updateHealthView(intelligence, sensor.getView());


		if (!intelligence.isLavaCoordinate(controls.getCoordinate()) && lavaTileAhead(intelligence, intelligence.getCurrentPath(), intelligence.getMostUpdateView()) > 0) {
			//need to recalculate the path to target and find alternative path to go there.

			Coordinate endTarget = intelligence.getCurrentPath().getLast();
			if (intelligence.isLavaCoordinate(endTarget)) {
				endTarget = intelligence.getChart().getNonVisitCoord();
			}

			DrivingPath newPath = intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), endTarget);

			if (lavaTileAhead(intelligence, newPath, intelligence.getMostUpdateView()) > 0) {

			} else {
				intelligence.setNewPath(newPath);
			}


		}

		for (int i = 0; i < Math.min(3, intelligence.getCurrentPath().getCoordinateCount()); i++) {
			Coordinate coordinate = intelligence.getCurrentPath().get(i);
			if (intelligence.isLavaCoordinate(coordinate)) {
				controls.applyForwardAcceleration();
				break;
			}
		}


		switch (movingState) {

			case EXPLORE:
				//we have collected all the keys.

				if (controls.getKey() == 1) {

					intelligence.setNewPath(
							intelligence.getDefaultPathFinder().getPath(
									controls.getCoordinate(), intelligence.getFinishCoordinate()
							)
					);
					movingState = TraverseState.FINISH;

				} else if (intelligence.getCoordinateFromKey(controls.getKey() - 1) != null && controls.getKey() - 1 == intelligence.getKeyFromCoordinate(intelligence.getCoordinateFromKey(controls.getKey() - 1))) {
					Coordinate keyCoordinate = intelligence.getCoordinateFromKey(controls.getKey() - 1);
					movingState = TraverseState.TARGET;
					//found the highest key, need to go there to collect, change the path of traversing to this if you have tagret not same.
					if (!intelligence.getCurrentPath().getLast().equals(keyCoordinate)) {
						intelligence.setNewPath(
								intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), keyCoordinate)
						);
					}


				} else {
					if (sensor.getView().containsKey(intelligence.getCurrentPath().getLast())) {
						intelligence.setNewPath(
								intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), intelligence.getChart().getNonVisitCoord())
						);
					}
				}

				break;
			case TARGET:

				if (intelligence.getCoordinateFromKey(controls.getKey() - 1) == null) {
					if (intelligence.getChart().getNonVisitCoord() != null) {
						intelligence.setNewPath(
								intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(),
										intelligence.getChart().getNonVisitCoord())
						);
					}
					movingState = TraverseState.EXPLORE;


				} else if (intelligence.getCoordinateFromKey(controls.getKey() - 1) != null) {
					Coordinate keyCoordinate = intelligence.getCoordinateFromKey(controls.getKey() - 1);

					if (!intelligence.getCurrentPath().getLast().equals(keyCoordinate)) {
						intelligence.setNewPath(
								intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(),
										intelligence.getCoordinateFromKey(controls.getKey() - 1))
						);
					}
				}

				break;
			case FINISH:
				if (intelligence.getCurrentPath().getLast() != intelligence.getFinishCoordinate() && controls.getHealth() > 90) {

					intelligence.setNewPath(
							intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(),
									intelligence.getFinishCoordinate())
					);
					movingState = TraverseState.EXPLORE;
				} else {
					actor.setDrivingStrategy(new HealingStrategy(), true);
				}
				break;

		}
//

		//needs to slow down the car too, if you feel that there are turns ahead and somehow you might need to align the car.
		Coordinate target = intelligence.getCurrentPath().getLast();

		if (intelligence.getCurrentPath().getPathSize() <= 1) {
			traversingPath(intelligence.getCurrentPath(), delta, sensor, controls, intelligence, actor, aiController);


			intelligence.setNewPath(
					intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), target)
			);

			return;
		}
		traversingPath(intelligence.getCurrentPath(), delta, sensor, controls, intelligence, actor, aiController);

		WorldSpatial.RelativeDirection toTurn = aiController.getTurnDirection(intelligence.getCurrentPath().getFirstDirection(), sensor.getView());


		//if we are not lava and lava is in front, check if orientation matches and we are still not oriented, get orientation done first.
		if (!intelligence.isLavaCoordinate(controls.getCoordinate()) && isLavaInFront(aiController)
					&& !isRange(controls.getAngle(), getDegree(toTurn, controls.getOrientation()))) {
			actor.setDrivingStrategy(new AligningStrategy(), false);
			if (!controls.getCoordinate().equals(intelligence.getCurrentPath().getFirst())) {
				intelligence.setNewPath(
						intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), target)
				);
			}

			return;
		} else if ((!controls.getOrientation().equals(intelligence.getCurrentPath().getFirstDirection()) &&
							!isRange(controls.getAngle(), getDegree(toTurn, controls.getOrientation())))) {
			actor.setDrivingStrategy(new AligningStrategy(), false);
		} else {
			traversingPath(intelligence.getCurrentPath(), delta, sensor, controls, intelligence, actor, aiController);
		}
	}

	// need to check if we are going in front we need to allign the car and should be in front
	private boolean isLavaInFront(IMapIntelligence intelligence) {
		int tiles = 0;
		for (Coordinate c : intelligence.getCurrentPath().getCoordinates()) {
			tiles += 1;
			if (tiles > 8) {
				break;
			}

			if (intelligence.isLavaCoordinate(c)) {
				return true;
			}
		}
		return false;

	}

	private boolean isRange(float degree, float currentAngle) {

		if (degree == 0 && (currentAngle < 5 && currentAngle > 355)) {
			return true;
		}

		if (degree == 90 && (currentAngle < 95 && currentAngle > 75)) {
			return true;
		}
		if (degree == 180 && (currentAngle < 185 && currentAngle > 175)) {
			return true;
		}
		if (degree == 270 && (currentAngle < 285 && currentAngle > 265)) {
			return true;
		}
		return false;

	}

	private float getDegree(WorldSpatial.RelativeDirection toTurn, WorldSpatial.Direction orientation) {
		switch (orientation) {
			case EAST:
				if (toTurn.equals(RIGHT)) {
					return 270;

				}
				if (toTurn.equals(LEFT)) {
					return 90;

				}
			case NORTH:
				if (toTurn.equals(RIGHT)) {
					return 0;

				}
				if (toTurn.equals(LEFT)) {
					return 180;

				}
			case SOUTH:
				if (toTurn.equals(RIGHT)) {
					return 270;

				}
				if (toTurn.equals(LEFT)) {
					return 0;

				}
			case WEST:
				if (toTurn.equals(RIGHT)) {
					return 90;

				}
				if (toTurn.equals(LEFT)) {
					return 270;

				}

		}
		return 0;
	}

	private void traversingPath(DrivingPath path, float delta, ISensor sensor, IControls controls, IMapIntelligence
																										   intelligence, IDrivingStrategyActor actor, MyAIController aiController) {


		if (!controls.getCoordinate().equals(path.get(0)) && path.getCoordinateCount() > 2) {
			path.removeFirst();

		}
		if (!path.contains(controls.getCoordinate())) {
			DrivingPath prevPath = path.clone();
			intelligence.setNewPath(
					intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), prevPath.getLast())
			);

			actor.setDrivingStrategy(new AligningStrategy(), true);
			return;
		}

		WorldSpatial.Direction direction = controls.getOrientation();
		if (path.getPathSize() > 1) {
			direction = intelligence.getCurrentPath().getFirstDirection();
		}

		HashMap<Coordinate, MapTile> currentView = sensor.getView();

		if (controls.getSpeed() < aiController.getMaximumCarSpeed()) {
			controls.applyForwardAcceleration();
		}

		if (!controls.getOrientation().equals(direction)) {
			aiController.setLastTurnDirection(aiController.getTurnDirection(direction, currentView));
			switch (aiController.getLastTurnDirection()) {
				case LEFT:
					aiController.setTurningLeft(true);
					aiController.setTurningRight(false);
					controls.applyLeftTurn(controls.getOrientation(), delta);
					break;
				case RIGHT:
					aiController.setTurningRight(true);
					aiController.setTurningLeft(false);
					controls.applyRightTurn(controls.getOrientation(), delta);
					break;
			}
		} else if (controls.getCoordinate().equals(path.getLast())) {
			if (controls.getSpeed() < 0.1) {
				actor.setDrivingStrategy(new StillStrategy());
			} else {
				actor.setDrivingStrategy(new BrakingStrategy());
			}
		}
	}


	private enum TraverseState {EXPLORE, TARGET, HEALTH, FINISH}


	//need to check health and set the car speed less if you find no lava tile and get the orientation work with the next path you find.


}
