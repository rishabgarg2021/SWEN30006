package mycontroller.reference;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Reference class is used to hold some global constants.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class Reference {

	/**
	 * Location of our custom properties file within our package
	 */
	public static final String GROUP_PROPERTIES_FILE = "group63.properties";

	/**
	 * As per the above variable, we have a properties file in our package.
	 * We are, however, unsure about whether or not this file will survive
	 * the assumed automated extraction process. As such this is here as a backup.
	 *
	 * @see Reference#GROUP_PROPERTIES_FILE
	 */
	public static final String PROPERTIES_FILE_SOURCE = "######################################################################\n" +
																"# Created for SWEN30006 - Project Part C (Learning to Escape)\n" +
																"#\n" +
																"# @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)\n" +
																"######################################################################\n" +
																"# Default implementation class to use for our IPathFinder instance(s)\n" +
																"GraphStrategy=mycontroller.graph.DijkstraPathFinder\n" +
																"# Default implementation class to use for our IPathCalculator instance(s)\n" +
																"PathCalculator=mycontroller.graph.BasicPathCalculator\n" +
																"# Constant to inject into Simulation.class for running the simulation faster during testing\n" +
																"SimulationSpeed=5";

	/**
	 * Key into our properties file for our IPathFinder implementation class (Default).
	 * Note that this implementing class MUST have a constructor which takes a {@link mycontroller.graph.Chart}
	 * and {@link mycontroller.graph.IPathCalculator} (in the opposite order).
	 */
	public static final String GRAPH_STRAT_IMPL_KEY = "GraphStrategy";

	/**
	 * Key into our properties file for our IPathCalculator implementation class (Default)
	 */
	public static final String PATH_CALC_IMPL_KEY = "PathCalculator";
}
