package games.stendhal.server.maps;

import java.util.Map;
import java.util.HashMap;
import games.stendhal.server.entity.npc.*;

/**
 * This Singleton contains all NPCs in the Stendhal world.
 */
public class NPCList {

	private static NPCList instance;
	
	private Map<String, SpeakerNPC> contents;


	/**
	 * Returns the Singleton instance.
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

	public SpeakerNPC get(String name) {
		return contents.get(name);
	}

	/**
	 * Checks whether an NPC with the given name exists.
	 * @param name The NPC's name
	 * @return true iff an NPC with the given name exists 
	 */
	public boolean has(String name) {
		return contents.containsKey(name);
	}

	public void add(SpeakerNPC npc) {
		if (!contents.containsKey(npc.getName())) {
			contents.put(npc.getName(), npc);
		}
	}

	public SpeakerNPC remove(String name) {
		return contents.remove(name);
	}
}
