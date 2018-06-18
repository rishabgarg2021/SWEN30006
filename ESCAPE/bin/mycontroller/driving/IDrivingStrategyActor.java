package mycontroller.driving;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Specifies the actions of something which acts upon a strategy.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IDrivingStrategyActor {

	public IDrivingStrategy getCurrentDrivingState();

	public default void setDrivingStrategy(IDrivingStrategy state) {
		setDrivingStrategy(state, false);
	}

	public void setDrivingStrategy(IDrivingStrategy state, boolean shouldExecute);
}
