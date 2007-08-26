package games.stendhal.server.scripting;

import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Map;

public class ScriptingNPC extends SpeakerNPC {

	public ScriptingNPC(String name) {
		super(name);
		initHP(100);
	}


	// TODO: use message constants from Behaviours.java
	public void behave(String method, String reply) {
		if ("greet".equalsIgnoreCase(method)) {
			addGreeting(reply);
		} else if ("job".equalsIgnoreCase(method)) {
			addJob(reply);
		} else if ("help".equalsIgnoreCase(method)) {
			addHelp(reply);
		} else if ("quest".equalsIgnoreCase(method)) {
			addQuest(reply);
		} else if ("bye".equalsIgnoreCase(method)) {
			addGoodbye(reply);
		} else {
			addReply(method, reply);
		}
	}

	public void behave(String method, List<String> triggers, String reply) throws NoSuchMethodException {
		if ("reply".equalsIgnoreCase(method)) {
			addReply(triggers, reply);
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method + ") not supported.");
		}
	}

	public void behave(List<String> triggers, String reply) {
		addReply(triggers, reply);
	}

	public void behave(String method, Map<String, Integer> items) throws NoSuchMethodException {
		if ("buy".equalsIgnoreCase(method)) {
			addBuyer(new BuyerBehaviour(items));
		} else if ("sell".equalsIgnoreCase(method)) {
			addSeller(new SellerBehaviour(items));
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method + ") not supported.");
		}
	}

	public void behave(String method, int cost) throws NoSuchMethodException {
		if ("heal".equalsIgnoreCase(method)) {
			addHealer(cost);
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method + ") not supported.");
		}
	}

	public static class NotQuestCondition extends SpeakerNPC.ChatCondition {

		String quest;

		public NotQuestCondition(String quest) {
			this.quest = quest;
		}

		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (!player.hasQuest(quest));
		}
	}

	public static class NotQuestCompletedCondition extends SpeakerNPC.ChatCondition {

		String quest;

		public NotQuestCompletedCondition(String quest) {
			this.quest = quest;
		}

		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (!player.isQuestCompleted(quest));
		}
	}

	public static class QuestCompletedCondition extends SpeakerNPC.ChatCondition {

		String quest;

		public QuestCompletedCondition(String quest) {
			this.quest = quest;
		}

		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.isQuestCompleted(quest));
		}
	}

	@Override
	protected void createPath() {
		// do nothing
	}

	@Override
	protected void createDialog() {
		// do nothing
	}

}
