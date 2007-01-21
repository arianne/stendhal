package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

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
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("My mouth is dry, but I can't be seen to abandon my post! Could you bring me some #beer from the #tavern?");
						} else {
							engine.say("Thanks all the same, but I don't want to get too heavily into drinking; I'm still on duty, you know! I'll need my wits about me if a monster shows up...");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thanks! I'll be right here, waiting. And guarding, of course.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh, well forget it then. I guess I'll just hope for it to start raining, and then stand with my mouth open.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"tavern",
				null,
				ConversationStates.QUEST_OFFERED,
				"If you don't know where the inn is, you could ask old Monogenes; he's good with directions. Are you going to help?",
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
				"Margaret is the pretty maid in the tavern, of course! Quite a looker, too... heh. Will you go for me?",
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
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.isEquipped("beer")) {
							engine.say("Hey! Is that beer for me?");
						} else {
							engine.say("Hey, I'm still waiting for that beer, remember? Anyway, what can I do for you?");
							engine.setCurrentState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				SpeakerNPC.YES_MESSAGES,
				// make sure the player isn't cheating by putting the beer
				// away and then saying "yes"
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.isEquipped("beer");
					}
				}, 
				ConversationStates.ATTENDING,
				"*glug glug* Ah! That hit the spot. Let me know if you need anything, ok?",
				new SpeakerNPC.ChatAction() {
					@Override
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
				"Drat! You remembered that I asked you for one, right? I could really use it right now.",
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
