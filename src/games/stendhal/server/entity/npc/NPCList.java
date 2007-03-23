package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This Singleton should contain all NPCs in the Stendhal world that are
 * unique.
 */
public class NPCList {

	/**
	 * The Singleton instance.
	 */
	private static NPCList instance;

	private Map<String, SpeakerNPC> contents;

	/**
	 * Returns the Singleton instance.
	 *
	 * @return The instance
	 */
	static public NPCList get() {
		if (instance == null) {
			instance = new NPCList();
		}
		return instance;
	}

	private NPCList() {
		contents = new HashMap<String, SpeakerNPC>();
	}

	/**
	 * Returns the NPC with the given name. 
	 * @param name The NPC's name
	 * @return The NPC, or null if there is no NPC with this name
	 */
	public SpeakerNPC get(String name) {
		return contents.get(name);
	}

	/**
	 * Checks whether an NPC with the given name exists.
	 *
	 * @param name The NPC's name
	 * @return true iff an NPC with the given name exists 
	 */
	public boolean has(String name) {
		return contents.containsKey(name);
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 *
	 * @param npc The NPC that should be added
	 */
	public void add(SpeakerNPC npc) {
		if (!contents.containsKey(npc.getName())) {
			contents.put(npc.getName(), npc);
		}
	}

	/**
	 * Removes an NPC from the NPCList. Does nothing if no NPC with the given
	 * name exists.
	 *
	 * @param name The name of the NPC that should be removed
	 * @return SpeakerNPC or null in case it was not in the list
	 */
	public SpeakerNPC remove(String name) {
		return contents.remove(name);
	}

	/**
	 * returns a list of all NPCs
	 *
	 * @return list of npcs
	 */
	public Set<String> getNPCs() {
		// do not expose the internal structure but return a copy instead
		return new TreeSet<String>(contents.keySet());
	}
}
