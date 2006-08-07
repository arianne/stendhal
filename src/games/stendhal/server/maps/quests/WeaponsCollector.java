package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: The Weapons Collector
 * 
 * PARTICIPANTS:
 * - Balduin, a hermit living on a mountain between Semos and Ados
 * 
 * STEPS:
 * - Balduin asks you for some weapons.
 * - You get one of the weapons somehow, e.g. by killing a monster.
 * - You bring the weapon up the mountain and give it to Balduin.
 * - Repeat until Balduin received all weapons. (Of course you can
 *   bring up several weapons at the same time.)
 * - Balduin gives you an ice sword in exchange.
 * 
 * REWARD:
 * - ice sword
 * - 1000 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class WeaponsCollector extends AbstractQuest {

	private static final List<String> neededWeapons = Arrays.asList(
		"axe",
		"bardiche",
		"battle_axe",
		"biting_sword",
		"broadsword",
		"flail",
		"hammer_+3",
		"halberd",
		"hammer",
		"katana",
		"mace_+2",
		"scimitar",
		"scythe",
		"twoside_axe",
		"war_hammer"
	);
	
	/**
	 * Returns a list of the names of all weapons that the given player
	 * still has to bring to fulfil the quest.
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of weapon names
	 */
	private List<String> missingWeapons(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();
		
		String doneText = player.getQuest("weapons_collector");
		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		for (String weapon: neededWeapons) {
			if (! done.contains(weapon)) {
				if (hash) {
					weapon = "#" + weapon;
				}
				result.add(weapon);
			}
		}
		return result;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Balduin");
		
		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest("weapons_collector");
					}
				},
				ConversationStates.ATTENDING,
				"Greetings. I am Balduin. Are you interested in weapons? I certainly am, I have been collecting them since I was young. Maybe you can do a #task for me.",
				null);

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest("weapons_collector");
					}
				},
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("weapons_collector")) {
							engine.say("Although I have collected weapons for such a long time, some are still missing in my collection. Do you think you can help me?");
						} else {
							engine.say("My collection is now complete. Thanks again.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("If you help me to complete my #collection, I will give you something nice in exchange.");
						player.setQuest("weapons_collector", "");
					}
				});
		
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Too bad. You were my last hope to complete my collection.",
				null
				);

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING,
				"collection",
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("weapons_collector") &&
								!player.isQuestCompleted("weapons_collector");
					}
				},
				ConversationStates.QUESTION_1,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						List<String> needed = missingWeapons(player, true);
						engine.say("There are " + needed.size() + " weapons which are still missing in my collection: "
								+ SpeakerNPC.enumerateCollection(needed) + ". Do you have any of them with you?");
					}
				});

		// player says he doesn't have required weapons with him
		npc.add(ConversationStates.QUESTION_1,
				"no",
				null,
				ConversationStates.IDLE,
				"Come back when you find them. Farewell.",
				null);

		// player says he has a required weapon with him
		npc.add(ConversationStates.QUESTION_1,
				"yes",
				null,
				ConversationStates.QUESTION_1,
				"Which one?",
				null);
		
		for (String weapon: neededWeapons) {
			npc.add(ConversationStates.QUESTION_1,
					weapon,
					null,
					ConversationStates.QUESTION_1,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text, SpeakerNPC engine) {
							List<String> missing = missingWeapons(player, false);
							if (missing.contains(text)) {
								if (player.drop(text)) {
									// register weapon as done
									String doneText = player.getQuest("weapons_collector");
									player.setQuest("weapons_collector", doneText + ";" + text);
									// check if the player has brought all weapons
									missing = missingWeapons(player, true);
									if (missing.size() > 0) {
										engine.say("Thank you very much! Do you have any other weapon for me?");
									} else {
										Item iceSword = world.getRuleManager().getEntityManager().getItem("ice_sword");            
										player.equip(iceSword, true);
										player.addXP(1000);
										engine.say("Yippie! My collection is complete! Thank you very much! Here, take this ice_sword in exchange!");										
										player.setQuest("weapons_collector", "done");
										world.modify(player);
									}
								} else {
									engine.say("I may be old, but I'm not senile! You don't have a " + text + "! What weapon do you really have for me?");
								}
							} else {
								engine.say("I already have that one. Do you have any other weapon for me?");
							}
						}
					});
		}
	}

	private void step_2() {
		// Just find some of the weapons somewhere and bring them to Balduin.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Balduin");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("weapons_collector")
								&& ! player.isQuestCompleted("weapons_collector");
					}
				},
				ConversationStates.ATTENDING,
				"Welcome back. I hope you have come to help me with my #collection.",
				null);
		
		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isQuestCompleted("weapons_collector");
					}
				},
				ConversationStates.ATTENDING,
				"Welcome! Thanks again for completing my collection.",
				null);
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);
		step_1();
		step_2();
		step_3();
	}
}