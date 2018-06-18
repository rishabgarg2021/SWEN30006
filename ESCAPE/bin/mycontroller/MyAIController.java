package mycontroller;

import com.badlogic.gdx.math.Vector2;
import controller.CarController;
import mycontroller.driving.*;
import mycontroller.graph.*;
import mycontroller.reference.Reference;
import mycontroller.utils.Configuration;
import mycontroller.utils.ReflectUtils;
import mycontroller.utils.Utils;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

import java.io.IOException;
import java.util.HashMap;

import static world.WorldSpatial.Direction.EAST;
import static world.WorldSpatial.Direction.WEST;
import static world.WorldSpatial.RelativeDirection.LEFT;
import static world.WorldSpatial.RelativeDirection.RIGHT;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Main controller for the project. Implementing {@link ISensor} and {@link IControls} to decouple the Controller from
 * the strategies (when they need to access information about the car) and {@link IMapIntelligence} to increase the
 * level of cohesion with accessing the currently explored region of the map and plotting new paths. Lastly,
 * is a type of {@link IDrivingStrategyActor} to allow strategies
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class MyAIController extends CarController implements IDrivingStrategyActor, ISensor, IControls, IMapIntelligence {


	private DrivingPath path;
	private float maximumCarSpeed = 5f;
	private double timePassed = 0;
	private Chart chart;
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isRevrseForwardTry = false;
	private double lastTimeWasMoving = 0f;
	private double tryForwardTime = 0;
	private double tryBackTime = 0;
	private int wallSensitivity = 1;
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	private boolean isCurrentlyInUpdate = false;
	private float currentUpdateDelta = -1;
	private IDrivingStrategy state;
	private IPathFinder pathFinder;
	private IPathCalculator pathCalculator;

	public MyAIController(Car car) {
		super(car);

		Configuration.Builder cfgBuilder = Configuration.builder()
												   .withDefault(Reference.GRAPH_STRAT_IMPL_KEY, DijkstraPathFinder.class.getName())
												   .withDefault(Reference.PATH_CALC_IMPL_KEY, BasicPathCalculator.class.getName());
		try {
			cfgBuilder.load(Reference.GROUP_PROPERTIES_FILE);
		} catch (IOException ignored) {
			try {
				cfgBuilder.loadFromString(Reference.PROPERTIES_FILE_SOURCE);
			} catch (IOException wtf) {
				/* How is this even POSSIBLE? We're reading from a string!!! */
				wtf.printStackTrace();
				System.exit(-666);
			}
		}

		Configuration configuration = cfgBuilder.build();

		chart = new Chart(getView());

		try {
			pathCalculator = ReflectUtils.newInstance(configuration.get(Reference.PATH_CALC_IMPL_KEY));
			pathFinder = ReflectUtils.newInstance(configuration.get(Reference.GRAPH_STRAT_IMPL_KEY), pathCalculator, chart);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		path = getDefaultPathFinder().getPath(getCoordinate(), chart.getNonVisitCoord());

		/* Default to traversing */
		setDrivingStrategy(new TraversingStrategy());
	}

	public double getLastTimeWasMoving() {
		return lastTimeWasMoving;
	}

	public boolean isRevrseForwardTry() {

		return isRevrseForwardTry;
	}

	public boolean isTurningLeft() {
		return isTurningLeft;
	}

	public void setTurningLeft(boolean turningLeft) {
		isTurningLeft = turningLeft;
	}

	public boolean isTurningRight() {
		return isTurningRight;
	}

	public void setTurningRight(boolean turningRight) {
		isTurningRight = turningRight;
	}

	public WorldSpatial.RelativeDirection getLastTurnDirection() {
		return lastTurnDirection;
	}

	public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		this.lastTurnDirection = lastTurnDirection;
	}

	public double getTimePassed() {

		return timePassed;
	}

	public float getMaximumCarSpeed() {
		return maximumCarSpeed;
	}

	public void setMaximumCarSpeed(float maximumCarSpeed) {
		this.maximumCarSpeed = maximumCarSpeed;
	}

	@Override
	public IDrivingStrategy getCurrentDrivingState() {
		return state;
	}

	@Override
	public void setDrivingStrategy(IDrivingStrategy state, boolean shouldExecute) {
		this.state = state;


		if (isCurrentlyInUpdate && shouldExecute) {
			state.update(currentUpdateDelta, this, this, this, this, this);
		}
	}

	@Override
	public void update(float delta) {

		/* Update our chart with any new observations */
		chart.updateView(getView());

		checkStateChange();

		timePassed += delta;

		/* Some strategy-common checks and transitions */

		if (isBrakeForTurnOrEnd(path)) {
			maximumCarSpeed = 1;
		} else if (!isBrakeForTurnOrEnd(path) && Utils.angleFromOrientation(getAngle(), getOrientation())) {
			maximumCarSpeed = 3;
		}


		if (getSpeed() < 0.1 && !(getCurrentDrivingState() instanceof AligningStrategy)) {
			setDrivingStrategy(new StillStrategy());
		} else if (getSpeed() > 0.1) {
			if (!isRevrseForwardTry && getCurrentDrivingState() instanceof StuckStrategy) {
				isRevrseForwardTry = true;
			}
			if (isRevrseForwardTry && getCurrentDrivingState() instanceof StuckStrategy) {
				isRevrseForwardTry = false;
			}
			if (path.getPathSize() == 1) {
				setDrivingStrategy(new TraversingStrategy());
			} else if (getOrientation().equals(path.getFirstDirection())) {

				setDrivingStrategy(new TraversingStrategy());

			}
			lastTimeWasMoving = timePassed;

		}

		if ((timePassed - lastTimeWasMoving) > 1) {
			setDrivingStrategy(new StuckStrategy());
		}



		/* Begin update cycle */
		isCurrentlyInUpdate = true;
		currentUpdateDelta = delta;


		state.update(delta, this, this, this, this, this);


		isCurrentlyInUpdate = false;
		/* end update cycle */
	}


	private boolean isBrakeForTurnOrEnd(DrivingPath path) {
		WorldSpatial.Direction targ1;
		WorldSpatial.Direction targ2;
		WorldSpatial.Direction targ3;
		WorldSpatial.Direction targ4;
		if (path.getCoordinateCount() > 5 && getSpeed() > 4) {
			targ2 = Utils.getDifference(path.get(2), path.get(3));
			targ1 = Utils.getDifference(path.get(1), path.get(2));
			targ3 = Utils.getDifference(path.get(3), path.get(4));
			targ4 = Utils.getDifference(path.get(4), path.get(5));
			if (!getOrientation().equals(targ1) || !getOrientation().equals(targ2) || !getOrientation().equals(targ3) || !getOrientation().equals(targ4)) {
				return true;

			}

		}
		if (path.getCoordinateCount() > 4) {
			targ2 = Utils.getDifference(path.get(2), path.get(3));
			targ1 = Utils.getDifference(path.get(1), path.get(2));
			targ3 = Utils.getDifference(path.get(3), path.get(4));
			if (!getOrientation().equals(targ1) || !getOrientation().equals(targ2) || !getOrientation().equals(targ3)) {
				return true;

			}
		} else if (path.getCoordinateCount() > 3) {
			targ2 = Utils.getDifference(path.get(2), path.get(3));
			targ1 = Utils.getDifference(path.get(1), path.get(2));
			if (!getOrientation().equals(targ1) || !getOrientation().equals(targ2)) {
				return true;

			}
		} else if (path.getCoordinateCount() > 2) {
			targ1 = Utils.getDifference(path.get(1), path.get(2));
			if (!getOrientation().equals(targ1)) {
				return true;

			}
		}


		return false;


	}

	private void checkStateChange() {

		if (previousState == null) {

			previousState = getOrientation();

		} else {


			if (previousState != getOrientation()) {

				if (isTurningLeft) {

					isTurningLeft = false;
				}
				if (isTurningRight) {

					isTurningRight = false;
				}
				previousState = getOrientation();
			}

		}
	}

	public WorldSpatial.RelativeDirection getTurnDirection(WorldSpatial.Direction desiredDirection, HashMap<Coordinate, MapTile> view) {

		switch (getOrientation()) {
			case NORTH:
				switch (desiredDirection) {
					case EAST:
						return RIGHT;
					case SOUTH:
						if (!checkEast(view))
							return RIGHT;
						if (!checkWest(view))
							return LEFT;
						throw new RuntimeException("NEED TO REVERSE");
					default:
						return LEFT;
				}
			case EAST:
				switch (desiredDirection) {
					case NORTH:
						return LEFT;
					case WEST:
						if (!checkSouth(view))
							return RIGHT;
						if (!checkNorth(view))
							return LEFT;
						throw new RuntimeException("NEED TO REVERSE");
					default:
						return RIGHT;
				}
			case SOUTH:
				switch (desiredDirection) {
					case NORTH:
						if (!checkEast(view))
							return LEFT;
						if (!checkWest(view))
							return RIGHT;
						throw new RuntimeException("NEED TO REVERSE");
					case EAST:
						return LEFT;
					default:
						return RIGHT;
				}
			case WEST:
				switch (desiredDirection) {
					case SOUTH:
						return LEFT;
					case EAST:
						if (!checkNorth(view))
							return RIGHT;
						if (!checkSouth(view))
							return LEFT;
						throw new RuntimeException("NEED TO REVERSE");
					default:
						return RIGHT;
				}
		}
		return null;
	}

	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	public void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch (orientation) {
			case EAST:
				if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
					turnLeft(delta);
				}
				break;
			case NORTH:
				if (!getOrientation().equals(WEST)) {
					turnLeft(delta);
				}
				break;
			case SOUTH:
				if (!getOrientation().equals(EAST)) {
					turnLeft(delta);
				}
				break;
			case WEST:
				if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
					turnLeft(delta);
				}
				break;
			default:
				break;

		}

	}

	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	public void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch (orientation) {
			case EAST:
				if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
					turnRight(delta);
				}
				break;
			case NORTH:
				if (!getOrientation().equals(EAST)) {
					turnRight(delta);
				}
				break;
			case SOUTH:
				if (!getOrientation().equals(WEST)) {
					turnRight(delta);
				}
				break;
			case WEST:
				if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
					turnRight(delta);
				}
				break;
			default:
				break;

		}

	}

	/**
	 * Check if the wall is on your left hand side given your orientation
	 *
	 * @param currentView
	 * @return
	 */

	public boolean checkSideFollowingWall(WorldSpatial.RelativeDirection
												  turn, HashMap<Coordinate, MapTile> currentView) {

		switch (getOrientation()) {
			case EAST:
				if (turn.equals(LEFT)) {
					return checkNorth(currentView);
				} else if (turn.equals(RIGHT)) {
					return checkSouth(currentView);
				}
				break;


			case NORTH:
				if (turn.equals(LEFT)) {
					return checkWest(currentView);
				} else if (turn.equals(RIGHT)) {
					return checkEast(currentView);
				}
				break;
			case SOUTH:
				if (turn.equals(LEFT)) {
					return checkEast(currentView);
				} else if (turn.equals(RIGHT)) {
					return checkWest(currentView);
				}
				break;
			case WEST:
				if (turn.equals(LEFT)) {
					return checkSouth(currentView);
				} else if (turn.equals(RIGHT)) {
					return checkNorth(currentView);
				}
				break;
			default:
				return false;
		}
		return false;

	}


	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkWest(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkNorth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSouth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}


	public boolean checkMyEast(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		// Check tiles to my right

		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkMyWest(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		// Check tiles to my left
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkMyNorth(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		// Check tiles to towards the top
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkMySouth(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		// Check tiles towards the bottom
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public Coordinate getCoordinate() {
		return new Coordinate(getPosition());
	}

	@Override
	public Vector2 getFineCoordinate() {
		return new Vector2(getX(), getY());
	}

	@Override
	public Chart getExplored() {
		return chart;
	}

	@Override
	public Chart getChart() {
		return chart;
	}

	@Override
	public IPathCalculator getDefaultCalculator() {
		return pathCalculator;
	}

	@Override
	public IPathFinder getDefaultPathFinder() {
		return pathFinder;
	}

	@Override
	public DrivingPath getCurrentPath() {
		return path;
	}

	@Override
	public void setNewPath(DrivingPath path) {
		this.path = path;
	}

	public double getTryForwardTime() {
		return tryForwardTime;
	}

	public void setTryForwardTime(double tryForwardTime) {
		this.tryForwardTime = tryForwardTime;
	}

	public double getTryBackTime() {
		return tryBackTime;
	}

	public void setTryBackTime(double tryBackTime) {
		this.tryBackTime = tryBackTime;
	}
}


