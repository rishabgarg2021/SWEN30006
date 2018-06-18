package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy for when the car is stuck (i.e. against a wall)
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class StuckStrategy implements IDrivingStrategy {

	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {

		if ((aiController.getTryBackTime() < 1 && aiController.getTryBackTime() != -1) || !aiController.isRevrseForwardTry() && (aiController.getTimePassed() - aiController.getLastTimeWasMoving()) > 1) {
			controls.applyReverseAcceleration();
			aiController.setTryBackTime(aiController.getTryBackTime() + delta);

			if (aiController.getTryBackTime() > 1) {
				aiController.setTryBackTime(-1);
			}
		} else if ((aiController.getTryForwardTime() < 1 && aiController.getTryForwardTime() != -1) || aiController.isRevrseForwardTry() && (aiController.getTimePassed() - aiController.getLastTimeWasMoving()) > 1) {
			controls.applyForwardAcceleration();
			aiController.setTryForwardTime(aiController.getTryForwardTime() + delta);

			if (aiController.getTryForwardTime() > 1) {
				aiController.setTryForwardTime(-1);
			}
		} else {
			actor.setDrivingStrategy(new TraversingStrategy());
		}
	}
}
