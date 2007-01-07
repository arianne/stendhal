package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/** 
 * QUEST: Look book for Ceryl
 * PARTICIPANTS: 
 * - Ceryl
 * - Jynath
 * 
 * STEPS: 
 * - Talk with Ceryl to activate the quest.
 * - Talk with Jynath for the book.
 * - Return the book to Ceryl
 *
 * REWARD: 
 * - 100 XP
 * - 50 gold coins
 *
 * REPETITIONS:
 * - None.
 */
public class LookBookforCeryl extends AbstractQuest {
	private static final String QUEST_SLOT = "ceryl_book";

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
		if (player.isQuestInState(QUEST_SLOT, "start", "jynath", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("jynath") && player.isEquipped("book_black")) || questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("jynath") && !player.isEquipped("book_black")) {
			res.add("LOST_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {

		SpeakerNPC npc = npcs.get("Ceryl");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("I have nothing for you now.");
						} else {
							engine.say("I am looking for a very special #book.");
						}
					}
				});

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING,
				"book",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"I already got the book. Thank you!",
				null);

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.ATTENDING,
				"book",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return !player.hasQuest(QUEST_SLOT);
					}
				},
				ConversationStates.QUEST_OFFERED,
				"Could you ask #Jynath to return her book? She's had it for months now, and people are looking for it.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Great! Please get me it as quickly as possible... there's a huge waiting list!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh... I suppose I will have to get somebody else to do it, then.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"jynath",
				null,
				ConversationStates.QUEST_OFFERED,
				"Jynath is the witch who lives south of Or'ril castle, southwest of here. So, will you get me the book?",
				null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING,
				"book",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				},
				ConversationStates.ATTENDING,
				"I really need that book now! Go to talk with #Jynath.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"jynath",
				null,
				ConversationStates.ATTENDING,
				"Jynath is the witch who lives south of Or'ril castle, southwest of here.",
				null);
	}

	private void step_2() {
		SpeakerNPC npc = npcs.get("Jynath");

		/** If player has quest and is in the correct state, just give him the book. */
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT)
										.equals("start");
					}
				},
				ConversationStates.ATTENDING,
				"Oh, Ceryl's looking for that book back? My goodness! I completely forgot about it... here you go!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "jynath");

						Item item = StendhalRPWorld.get().getRuleManager().getEntityManager()
								.getItem("book_black");
						player.equip(item, true);
					}
				});

		/** If player keep asking for book, just tell him to hurry up */
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(
										"jynath");
					}
				},
				ConversationStates.ATTENDING,
				"You'd better take that book back to #Ceryl quickly... he'll be waiting for you.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"ceryl",
				null,
				ConversationStates.ATTENDING,
				"Ceryl is the librarian at Semos, of course.",
				null);

		/** Finally if player didn't start the quest, just ignore him/her */
		npc.add(ConversationStates.ATTENDING,
				"book",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return !player.hasQuest(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"Sssh! I'm concentrating on this potion recipe... it's a tricky one.",
				null);
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Ceryl");

		/** Complete the quest */
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(
										"jynath");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.drop("book_black")) {
							engine.say("Oh, you got the book back! Phew, thanks!");
							StackableItem money = (StackableItem) StendhalRPWorld.get()
									.getRuleManager().getEntityManager()
									.getItem("money");

							money.setQuantity(50);
							player.equip(money);
							player.addXP(100);

							player.notifyWorldAboutChanges();

							player.setQuest(QUEST_SLOT, "done");
						} else {
							engine.say("Haven't you got that #book back from #Jynath? Please go look for it, quickly!");
							player.removeQuest(QUEST_SLOT);
						}
					}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
