package games.stendhal.server.entity.npc.behaviour.journal;

import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

public class ProducerRegister {
	
	private static ProducerRegister instance;
	
	private final List<Pair<String, ProducerBehaviour>> producers;
	
	public static ProducerRegister get() {
		if (instance == null) {
			new ProducerRegister();
		}
		return instance;
	}

	protected ProducerRegister() {
		instance = this;
		producers  = new LinkedList<Pair<String, ProducerBehaviour>>();
	}
	
	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 * 
	 * @param npc
	 *            The NPC that should be added
	 */
	public void add(final String npcName, final ProducerBehaviour behaviour) {
		// insert lower case names ?
		// final String name = npcName.toLowerCase();
		Pair<String, ProducerBehaviour> pair = new Pair<String, ProducerBehaviour>(npcName, behaviour);
		producers.add(pair);
	}
	
	public List<Pair<String, ProducerBehaviour>> getProducers() {
		return producers;
	}
	
	
	
	
}
/*"jenny_mill_flour", "mill", "flour", requiredResources, 2 * 60
"xoderos_cast_iron", "cast", "iron", requiredResources, 5 * 60
"erna_bake_bread", "bake", "bread", requiredResources, 10 * 60
"leander_make_sandwiches", "make", "sandwich", requiredResources, 3 * 60*/

