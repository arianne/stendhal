package games.stendhal.server.entity.mapstuff.puzzle;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

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
	public void register(PuzzleBuildingBlock block) {
		if (block.getName().contains(SEP)) {
			throw new RuntimeException("Entity \"" + block.getName() + "\" in zone " + block.getZoneName() + " must not contain " + SEP + ".");
		}
			
		String prefix = block.getZoneName() + SEP;
		String qualifiedName = prefix + block.getName();
		buildingBlocks.put(qualifiedName, block);

		for (String dependency : block.getDependencies()) {
			if (!dependency.contains(SEP)) {
				dependency = prefix + dependency;
			}
			notifies.put(dependency, qualifiedName);
		}
	}

	/**
	 * notifies about property changes 
	 *
	 * @param sourceBlock PuzzleBuildingBlock which had a property changed
	 */
	public void notify(PuzzleBuildingBlock sourceBlock) {
		String qualifiedName = sourceBlock.getZoneName() + SEP + sourceBlock.getName();
		for (String notifyName : notifies.get(qualifiedName)) {
			PuzzleBuildingBlock targetBlock = buildingBlocks.get(notifyName);
			if (targetBlock != null) {
				targetBlock.onInputChanged();
			}
		}
	}

}
