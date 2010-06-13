package games.stendhal.server.entity.npc.behaviour.journal;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;

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
	
	
	public String listWorkingProducers(final Player player) {
		final StringBuilder sb = new StringBuilder();

		// Open orders - do not state if ready to collect or not, yet
		sb.append("\r\nOrders: ");
		for (final Pair<String, ProducerBehaviour> producer : producers) {
			String npcName = producer.first();
			ProducerBehaviour behaviour = producer.second();
			String questSlot =  behaviour.getQuestSlot();
			String activity =  behaviour.getProductionActivity();
			String product =  behaviour.getProductName();
			if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
				sb.append("\n" + npcName + " is " + Grammar.gerundForm(activity) + " " + product + ".");
			}
		}


		return sb.toString();
	}
	
}


