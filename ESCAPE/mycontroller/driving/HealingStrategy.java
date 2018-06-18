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

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Strategy for when the car is healing on health trap.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class HealingStrategy implements IDrivingStrategy {


	@Override
	public void update(float delta, ISensor sensor, IControls controls, IMapIntelligence intelligence, IDrivingStrategyActor actor, MyAIController aiController) {

		Coordinate target = intelligence.getCurrentPath().getLast();



		if (intelligence.isLavaCoordinate(controls.getCoordinate())) {
			actor.setDrivingStrategy(new TraversingStrategy(), false);

		}
		if (controls.getCoordinate().equals(target) && controls.getSpeed() == 0) {
			if (controls.getHealth() == 100) {
				actor.setDrivingStrategy(new TraversingStrategy(), false);
			}
		}

	}


}
