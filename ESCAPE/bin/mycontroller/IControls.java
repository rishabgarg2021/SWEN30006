package mycontroller;

import com.badlogic.gdx.math.Vector2;
import controller.CarController;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * <p>
 * Interface specifying something that can control a {@link world.Car}
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IControls {

	/**
	 * @see CarController#applyBrake()
	 */
	public void applyBrake();

	/**
	 * @see CarController#applyForwardAcceleration()
	 */
	public void applyForwardAcceleration();

	/**
	 * @see CarController#applyReverseAcceleration()
	 */
	public void applyReverseAcceleration();

	/**
	 * @see CarController#turnLeft(float)
	 */
	public void turnLeft(float delta);

	/**
	 * @see CarController#turnRight(float)
	 */
	public void turnRight(float delta);

	/**
	 * @return The current coordinate of the car.
	 */
	public Coordinate getCoordinate();

	/**
	 * @return A finer-granularity coordinate of the car.
	 * @see IControls#getCoordinate()
	 * @see CarController#getX()
	 * @see CarController#getY()
	 */
	public Vector2 getFineCoordinate();

	/**
	 * @see CarController#getAngle()
	 */
	public float getAngle();

	/**
	 * @see CarController#getHealth()
	 */
	public float getHealth();

	/**
	 * @see CarController#getSpeed()
	 */
	public float getSpeed();

	/**
	 * @see CarController#getOrientation()
	 */
	public WorldSpatial.Direction getOrientation();

	public void applyLeftTurn(WorldSpatial.Direction orientation, float delta);
	public void applyRightTurn(WorldSpatial.Direction orientation, float delta);
	public int getKey();
}
