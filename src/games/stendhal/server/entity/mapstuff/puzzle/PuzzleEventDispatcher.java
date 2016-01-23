package games.stendhal.server.entity.mapstuff.puzzle;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * manages puzzle building blocks
 */
public class PuzzleEventDispatcher {

	/** singleton instance */
	private static PuzzleEventDispatcher instance;

	private static final String SEP = ".!.";

	/** use strings to prevent having to deal with instantiation order */
	private Multimap<String, String> notifies = LinkedListMultimap.create();

	/** map building block names to entities */
	private Map<String, PuzzleBuildingBlock> buildingBlocks = new HashMap<>();

	/**
	 * gets the PuzzleEventDispatcher
	 *
	 * @return 
	 */
	public static PuzzleEventDispatcher get() {
		if (instance == null) {
			instance = new PuzzleEventDispatcher();
		}
		return instance;
	}

	/**
	 * singleton constructor
	 */
	private PuzzleEventDispatcher() {
		// hide constructor
	}

	/**
	 * registers a building block
	 *
	 * @param zone 
	 * @param name 
	 * @param block 
	 */
	public void register(StendhalRPZone zone, String name, PuzzleBuildingBlock block) {
		if (name.contains(SEP)) {
			throw new RuntimeException("Entity \"" + name + "\" in zone " + zone.getName() + " must not contain " + SEP + ".");
		}
			
		String prefix = zone.getName() + SEP;
		String qualifiedName = prefix + name;
		buildingBlocks.put(qualifiedName, block);

		for (String dependency : block.getDependencies()) {
			if (!dependency.contains(SEP)) {
				dependency = prefix + dependency;
			}
			notifies.put(dependency, qualifiedName);
		}
	}
}
