package mycontroller;

import com.badlogic.gdx.math.Vector2;
import controller.CarController;
import mycontroller.graph.Chart;
import tiles.MapTile;
import utilities.Coordinate;
import utilities.PeekTuple;
import world.WorldSpatial;

import java.util.HashMap;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * <p>
 * Interface specifying what a Car can sense around it
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface ISensor {

	/**
	 * Tries to predict where a car will end up
	 *
	 * @see controller.CarController#peek
	 */
	public PeekTuple peek(Vector2 velocity, float targetDegree, WorldSpatial.RelativeDirection turnDirection, float delta);

	/**
	 * @return The current view around the ISensor.
	 * @see ISensor#getViewSquare()
	 * @see CarController#getView
	 */
	public HashMap<Coordinate, MapTile> getView();

	/**
	 * @see CarController#getViewSquare()
	 */
	public int getViewSquare();

	/**
	 * @return The current {@link Chart} of explored tiles.
	 */
	public Chart getExplored();
}
