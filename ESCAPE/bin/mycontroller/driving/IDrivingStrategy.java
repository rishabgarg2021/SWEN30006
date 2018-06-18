package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public interface IDrivingStrategy {

	/**
	 * Causes updates to happen to the car
	 *
	 * @param delta
	 * @param aiController
	 * @return Either this driving behaviour or a new driving behaviour
	 */
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController);
}
