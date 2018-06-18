package mycontroller.graph;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * <p>
 * Simple implementation of {@link IPathCalculator} which gives a higher weight to lava tiles
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class BasicPathCalculator implements IPathCalculator {

	private static final int LAVA_COST = 100;
	private static final int HEALTH_COST = 1;
	private static final int UNKNOWN_TRAP_COST = 1_000;
	private static final int DEFAULT_COST = 1;

	@Override
	public int calculateCost(MapTile tile) {
		if (tile.isType(MapTile.Type.TRAP)) {
			if (tile instanceof LavaTrap)
				return LAVA_COST;
			if (tile instanceof HealthTrap)
				return HEALTH_COST;
			return UNKNOWN_TRAP_COST;
		}
		return DEFAULT_COST;
	}
}
