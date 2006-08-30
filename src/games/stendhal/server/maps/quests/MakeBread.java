package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;
import java.util.HashMap;

/**
 * QUEST: Bake bread. This is not really a quest, but a service offered
 * by the miller. Because milling flour takes time, we abuse the
 * player's quest slot to store the time and amount of the player's order.
 * 
 * PARTICIPANTS:
 * - Jenny, the miller north of in Semos
 * 
 * STEPS:
 * - You bring grain to Jenny.
 * - You ask Jenny to mill flour from it for you.
 * - Jenny starts to mill.
 * - You come back later and get the flour.
 * 
 * REWARD:
 * - none (except for the flour)
 * 
 * REPETITIONS:
 * - As much as you want.
 */
public class MakeBread extends AbstractQuest {
	
	// TODO: There is much code duplication with CastIron.java. Find a way to
	// reduce this.

	private static final String QUEST_SLOT = "jenny_mill_flour";

	/**
	 * The time it takes Jenny to cast one bag of flour.
	 */
	private static final int SECONDS_PER_FLOUR = 60; // 1 minute
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		SpeakerNPC jenny = npcs.get("Jenny");

		Map<String, Integer> requiredResources = new HashMap<String, Integer>();
		requiredResources.put("grain", new Integer(5));

		final ProducerBehaviour behaviour = new ProducerBehaviour(
				QUEST_SLOT, "mill", "bags", "flour", requiredResources, SECONDS_PER_FLOUR);

		jenny.add(ConversationStates.IDLE,
					SpeakerNPC.GREETING_MESSAGES,
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, SpeakerNPC engine) {
							return !player.hasQuest(QUEST_SLOT)
									|| player.isQuestCompleted(QUEST_SLOT);
						}
					},
					ConversationStates.ATTENDING,
					"Greetings. I am Jenny, the local miller. If you bring me #grain, I can #mill flour for you.",
					null);

		jenny.add(ConversationStates.ATTENDING,
				"mill",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest(QUEST_SLOT)
								|| player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {

						String[] words = text.split(" ");
						int amount = 1;
						if (words.length > 1) {
							amount = Integer.parseInt(words[1].trim());
						}
						if (behaviour.askForResources(npc, player, amount)) {
							npc.setCurrentState(ConversationStates.PRODUCTION_OFFERED);
						}
					}
				});
		
		jenny.add(ConversationStates.PRODUCTION_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {
						behaviour.transactAgreedDeal(npc, player);
					}
				});

		jenny.add(ConversationStates.PRODUCTION_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"OK, no problem.",
				null);

		jenny.add(ConversationStates.ATTENDING,
				"mill",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"I still haven't finished your last order. Come back later!",
				null);

		jenny.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {
						behaviour.giveProduct(npc, player);
					}
				});
	}
}