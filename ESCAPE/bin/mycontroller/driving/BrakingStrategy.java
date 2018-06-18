package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy for when a car needs to slow down to a stationary (still) state.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class BrakingStrategy implements IDrivingStrategy {

	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {
		if (controls.getSpeed() < 0.1) {
			actor.setDrivingStrategy(new StillStrategy());
		} else {
			controls.applyBrake();
		}
	}
}
