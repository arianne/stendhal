package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Beer For Hayunn
 * PARTICIPANTS:
 * - Hayunn Naratha (the veteran warrior in Semos)
 *
 * STEPS:
 * - Hayunn asks you to buy a beer from Margaret.
 * - Margaret sells you a beer.
 * - Hayunn sees your beer, asks for it and then thanks you.
 *
 * REWARD:
 * - 10 XP
 * - 20 gold coins
 *
 * REPETITIONS:
 * - None.
 */
public class BeerForHayunn extends AbstractQuest {
	
	private static final String QUEST_SLOT = "beer_hayunn";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("beer")) || questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("My mouth is dry and I can't abandon my place. Could you bring me some #beer from the #tavern?");
						} else {
							engine.say("Thanks bud, but I don't want to abuse beer. I will need to have my senses fully aware if a monster decides to appear. If you need anything from me just say it.");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks, bud. I'll be waiting for your return. Now if I can help you in anything just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Yes, forget it bud. Now that I think about it you do not look like you can afford inviting this old guy. Now if I can help you in anything just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"tavern",
				null,
				ConversationStates.QUEST_OFFERED,
				"You don't know where the inn is? Go and ask Monogenes. So, will you do it?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"beer",
				null,
				ConversationStates.QUEST_OFFERED,
				"A bottle of cool beer from #Margaret will be more than enough. So, will you do it?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"Margaret",
				null,
				ConversationStates.QUEST_OFFERED,
				"Margaret is the pretty tavernmaid hehehe... Well, definitely... will you do it?",
				null);
	}

	private void step_2() {
		// Just buy the beer from Margaret. It isn't a quest
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.isEquipped("beer")) {
							engine.say("Hey! Is that beer for me?");
						} else {
							engine.say("Hurry up bud! I am still waiting for that beer! Anyway, what can I do for you?");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"yes",
				// make sure the player isn't cheating by putting the beer
				// away and then saying "yes"
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isEquipped("beer");
					}
				}, 
				ConversationStates.ATTENDING,
				"Slurp! Thanks for the beer bud! If there is anything I can do for you now just say it.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("beer");
						StackableItem money = (StackableItem) StendhalRPWorld.get().getRuleManager()
								.getEntityManager().getItem("money");
						money.setQuantity(20);
						player.equip(money);
		
						player.addXP(10);
		
						player.notifyWorldAboutChanges();
						player.setQuest(QUEST_SLOT, "done");
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Darn! Ok, but remember I asked you a beer for me too. How can I help you then?",
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