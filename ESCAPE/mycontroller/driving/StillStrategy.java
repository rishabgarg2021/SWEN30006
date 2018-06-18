package mycontroller.driving;

import mycontroller.IControls;
import mycontroller.IMapIntelligence;
import mycontroller.ISensor;
import mycontroller.MyAIController;
import mycontroller.graph.DrivingPath;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy used for when the car is not moving (i.e. is still)
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class StillStrategy implements IDrivingStrategy {

	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {
		DrivingPath prevPath = intelligence.getCurrentPath();
		DrivingPath newPath = intelligence.getDefaultPathFinder().getPath(controls.getCoordinate(), prevPath.getLast());

		intelligence.setNewPath(newPath);
		if (newPath.getPathSize() > 1) {
			actor.setDrivingStrategy(new AligningStrategy(), true);
		}
	}
}
