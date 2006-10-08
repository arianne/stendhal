package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
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
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest("weapons_collector");
					}
				},
				ConversationStates.ATTENDING,
				"Greetings. I am Balduin. Are you interested in weapons? I certainly am, I have been collecting them since I was young. Maybe you can do a little #task for me.",
				null);

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest("weapons_collector");
					}
				},
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("weapons_collector")) {
							engine.say("Although I have collected weapons for such a long time, I still don't have everything I want. Do you think you can help me?");
						} else {
							engine.say("My collection is now complete! Thanks again.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("If you help me to complete my #collection, I will give you something very interesting and useful in exchange.");
						player.setQuest("weapons_collector", "");
					}
				});
		
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Well, maybe someone else will happen by and help me.",
				null
				);

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING,
				"collection",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("weapons_collector") &&
								!player.isQuestCompleted("weapons_collector");
					}
				},
				ConversationStates.QUESTION_1,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						List<String> needed = missingWeapons(player, true);
						engine.say("There " + Grammar.isare(needed.size()) + " " + Grammar.quantityplnoun(needed.size(), "weapon") + " still missing from my collection: "
								+ SpeakerNPC.enumerateCollection(needed) + ". Do you have anything of that nature with you?");
					}
				});

		// player says he doesn't have required weapons with him
		npc.add(ConversationStates.QUESTION_1,
				"no",
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					List<String> missing = missingWeapons(player, false);
					engine.say("Let me know as soon as you find " + Grammar.itthem(missing.size()) + ". Farewell.");
				}});

		// player says he has a required weapon with him
		npc.add(ConversationStates.QUESTION_1,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.QUESTION_1,
				"What is it that you found?",
				null);
		
		for (String weapon: neededWeapons) {
			npc.add(ConversationStates.QUESTION_1,
					weapon,
					null,
					ConversationStates.QUESTION_1,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
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
										engine.say("Thank you very much! Do you have anything else for me?");
									} else {
										Item iceSword = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("ice_sword");            
										player.equip(iceSword, true);
										player.addXP(1000);
										engine.say("At last, my collection is complete! Thank you very much; here, take this #ice #sword in exchange!");
										player.setQuest("weapons_collector", "done");
										player.notifyWorldAboutChanges();
									}
								} else {
									engine.say("I may be old, but I'm not senile, and you clearly don't have " + Grammar.a_noun(text) + ". What do you really have for me?");
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
					@Override
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
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isQuestCompleted("weapons_collector");
					}
				},
				ConversationStates.ATTENDING,
				"Welcome! Thanks again for completing my collection.",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
}
