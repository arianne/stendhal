package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;
import java.util.HashMap;

/**
 * QUEST: Cast Iron. This is not really a quest, but a service offered
 * by the blacksmith. Because casting iron takes time, we abuse the
 * player's quest slot to store the time and amount of the player's order.
 * 
 * PARTICIPANTS:
 * - Xoderos, the blacksmith in Semos
 * 
 * STEPS:
 * - You bring wood and iron ore to Xoderos.
 * - You ask Xoderos to cast it for you.
 * - Xoderos starts to cast.
 * - You come back later and get the cast iron.
 * 
 * REWARD:
 * - none (except for the iron)
 * 
 * REPETITIONS:
 * - As much as you want.
 */
public class CastIron extends AbstractQuest {
	

	private static final String QUEST_SLOT = "cast_iron";

	/**
	 * The time it takes Xoderos to cast one piece of iron.  
	 */
	private static final int SECONDS_PER_IRON = 5 * 60; // 5 minutes
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		SpeakerNPC xoderos = npcs.get("Xoderos");

		Map<String, Integer> requiredResources = new HashMap<String, Integer>();
		requiredResources.put("wood", new Integer(1));
		requiredResources.put("iron_ore", new Integer(1));

		final ProducerBehaviour behaviour = new ProducerBehaviour(
				QUEST_SLOT, "cast", "bars", "iron", requiredResources, SECONDS_PER_IRON);

		xoderos.add(ConversationStates.IDLE,
					SpeakerNPC.GREETING_MESSAGES,
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, SpeakerNPC engine) {
							return !player.hasQuest(QUEST_SLOT)
									|| player.isQuestCompleted(QUEST_SLOT);
						}
					},
					ConversationStates.ATTENDING,
					"Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.",
					null);

		xoderos.add(ConversationStates.ATTENDING,
				"cast",
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
						int amount = Integer.MAX_VALUE;
						if (words.length > 1) {
							amount = Integer.parseInt(words[1].trim());
						}
						behaviour.giveResources(player, npc, amount);
					}
				});

		xoderos.add(ConversationStates.ATTENDING,
				"cast",
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

		xoderos.add(ConversationStates.IDLE,
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
						behaviour.fetchProduct(player, npc);
					}
				});
	}
}