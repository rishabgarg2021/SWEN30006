package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;
import utilities.Coordinate;
import world.WorldSpatial;

import static world.WorldSpatial.RelativeDirection.LEFT;
import static world.WorldSpatial.RelativeDirection.RIGHT;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy for aligning orientation and grid position offset.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class AligningStrategy implements IDrivingStrategy {

	public boolean isLavaInFront(IMapIntelligence intelligence) {
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

	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {
		//has edited the speed while aligining the car the correctly go to next path.
		aiController.setMaximumCarSpeed(1);
		alignCar(intelligence.getCurrentPath().getFirstDirection(), delta, sensor, controls, intelligence, aiController);
		WorldSpatial.RelativeDirection toTurn = aiController.getTurnDirection(intelligence.getCurrentPath().getFirstDirection(), sensor.getView());


//
//		if(   !GraphHelper.isLavaCoordinate(controller.getPos()) &&
//
//				!isLavaInFront(controller) && controller.getOrientation().equals(controller.getDifference(controller.path.get(0),controller.path.get(1)))
//				&& !isRange(controller.getAngle(),getDegree(toTurn,controller.getOrientation()))){
//
//
		Coordinate target = intelligence.getCurrentPath().getLast();


		if (!intelligence.isLavaCoordinate(controls.getCoordinate()) &&
					isLavaInFront(intelligence)
					&& !isRange(controls.getAngle(), getDegree(toTurn, controls.getOrientation()))) {
			actor.setDrivingStrategy(new AligningStrategy());

			target = intelligence.getCurrentPath().getLast();
			if (!controls.getCoordinate().equals(intelligence.getCurrentPath().getFirst()) && target != intelligence.getCurrentPath().getLast()) {
				intelligence.setNewPath(
						intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), target)
				);
			}
			return;
		}
		if (intelligence.isLavaCoordinate(controls.getCoordinate())) {
			actor.setDrivingStrategy(new TraversingStrategy(), false);
			if (!controls.getCoordinate().equals(intelligence.getCurrentPath().getFirst()) && target != intelligence.getCurrentPath().getLast()) {
				intelligence.setNewPath(
						intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), target)
				);
			}
			return;
		}


		if (!controls.getOrientation().equals(intelligence.getCurrentPath().getFirstDirection()) && controls.getSpeed() > 0.1
					&& isRange(controls.getAngle(), getDegree(toTurn, controls.getOrientation())
		)) {
			aiController.setMaximumCarSpeed(3.f);
			actor.setDrivingStrategy(new TraversingStrategy());
		}
		if (controls.getOrientation().equals(intelligence.getCurrentPath().getFirstDirection())) {
            if(!isRange(controls.getAngle(), getDegree(toTurn, controls.getOrientation()))){
                aiController.setMaximumCarSpeed(1.0f);
            }
            else {
                aiController.setMaximumCarSpeed(3.f);
            }
            actor.setDrivingStrategy(new TraversingStrategy());


		}
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
				if (toTurn.equals(WorldSpatial.RelativeDirection.RIGHT)) {
					return 270;

				}
				if (toTurn.equals(WorldSpatial.RelativeDirection.LEFT)) {
					return 90;

				}
			case NORTH:
				if (toTurn.equals(WorldSpatial.RelativeDirection.RIGHT)) {
					return 0;

				}
				if (toTurn.equals(WorldSpatial.RelativeDirection.LEFT)) {
					return 180;

				}
			case SOUTH:
				if (toTurn.equals(WorldSpatial.RelativeDirection.RIGHT)) {
					return 270;

				}
				if (toTurn.equals(WorldSpatial.RelativeDirection.LEFT)) {
					return 0;

				}
			case WEST:
				if (toTurn.equals(WorldSpatial.RelativeDirection.RIGHT)) {
					return 90;

				}
				if (toTurn.equals(WorldSpatial.RelativeDirection.LEFT)) {
					return 270;

				}

		}
		return 0;
	}

	private void alignCar(WorldSpatial.Direction dir, float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, MyAIController aiController) {
		WorldSpatial.Direction toMove = intelligence.getCurrentPath().getFirstDirection();
		WorldSpatial.RelativeDirection toTurn = aiController.getTurnDirection(toMove, sensor.getView());
		if ((Math.floor(aiController.getTimePassed() * 100) % 2) == 0 && controls.getSpeed() < 0.3) {
			controls.applyForwardAcceleration();
		}

		if (aiController.checkSideFollowingWall(LEFT, sensor.getView()) && !aiController.checkSideFollowingWall(RIGHT, sensor.getView())) {
			controls.turnRight(delta);
		} else if (aiController.checkSideFollowingWall(RIGHT, sensor.getView()) && !aiController.checkSideFollowingWall(LEFT, sensor.getView())) {
			controls.turnLeft(delta);
		}

		//if we have don't have the walls on the same side everytime.

		else {


			Coordinate s = null;
			//if we going in same orientation to next orientation we need  check further wall ahead.
			//if we go south then check next tile and see if you are going right anf there is wall, and no wall to left then go
			// to left of the wall.
			if (toMove.equals(controls.getOrientation())) {
				//check the wall, if its going south and you take one step down, see if wall is left to it turn right .
				switch (controls.getOrientation()) {
					case SOUTH:
						s = new Coordinate(controls.getCoordinate().x, controls.getCoordinate().y - 1);
						if (aiController.checkMyEast(sensor.getView(), s) && !aiController.checkMyWest(sensor.getView(), s)) {
							controls.turnRight(delta);
						} else if (!aiController.checkMyEast(sensor.getView(), s) && aiController.checkMyWest(sensor.getView(), s)) {
							controls.turnLeft(delta);
						}
						break;
					case NORTH:
						s = new Coordinate(controls.getCoordinate().x, controls.getCoordinate().y + 1);
						if (aiController.checkMyEast(sensor.getView(), s) && !aiController.checkMyWest(sensor.getView(), s)) {
							controls.turnLeft(delta);
						} else if (!aiController.checkMyEast(sensor.getView(), s) && aiController.checkMyWest(sensor.getView(), s)) {
							controls.turnRight(delta);
						}
						break;
					case EAST:
						s = new Coordinate(controls.getCoordinate().x + 1, controls.getCoordinate().y);
						if (aiController.checkMyNorth(sensor.getView(), s) && !aiController.checkMySouth(sensor.getView(), s)) {
							controls.turnRight(delta);
						} else if (!aiController.checkMyNorth(sensor.getView(), s) && aiController.checkMySouth(sensor.getView(), s)) {
							controls.turnLeft(delta);
						}
						break;
					case WEST:
						s = new Coordinate(controls.getCoordinate().x - 1, controls.getCoordinate().y);
						if (aiController.checkMyNorth(sensor.getView(), s) && !aiController.checkMySouth(sensor.getView(), s)) {
							controls.turnLeft(delta);
						} else if (!aiController.checkMyNorth(sensor.getView(), s) && aiController.checkMySouth(sensor.getView(), s)) {
							controls.turnRight(delta);
						}
						break;
				}

			} //car is in still position or in start when orientation doesnot match the actual path, it needs
			//to fix its allignment in such a way that it alligns to the next move accordingly as there is no wall.
			else {
				if (toTurn.equals(LEFT)) {
					controls.turnLeft(delta);
				} else if (toTurn.equals(RIGHT)) {
					controls.turnRight(delta);
				}
			}
		}
	}
}
