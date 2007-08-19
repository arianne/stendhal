package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Beer For Hayunn
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Hayunn Naratha (the veteran warrior in Semos)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Hayunn asks you to buy a beer from Margaret.</li>
 * <li>Margaret sells you a beer.</li>
 * <li>Hayunn sees your beer, asks for it and then thanks you.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>10 XP</li>
 * <li>20 gold coins</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
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
		if ((questState.equals("start") && player.isEquipped("beer"))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void prepareRequestingStep() {
		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (!player.isQuestCompleted(QUEST_SLOT)) {
							npc
									.say("My mouth is dry, but I can't be seen to abandon my post! Could you bring me some #beer from the #tavern?");
						} else {
							npc
									.say("Thanks all the same, but I don't want to get too heavily into drinking; I'm still on duty, you know! I'll need my wits about me if a monster shows up...");
							npc.setCurrentState(1);
						}
					}
				});

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Thanks! I'll be right here, waiting. And guarding, of course.",
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								player.setQuest(QUEST_SLOT, "start");
							}
						});

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Oh, well forget it then. I guess I'll just hope for it to start raining, and then stand with my mouth open.",
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								player.setQuest(QUEST_SLOT, "rejected");
							}
						});

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						"tavern",
						null,
						ConversationStates.QUEST_OFFERED,
						"If you don't know where the inn is, you could ask old Monogenes; he's good with directions. Are you going to help?",
						null);

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						"beer",
						null,
						ConversationStates.QUEST_OFFERED,
						"A bottle of cool beer from #Margaret will be more than enough. So, will you do it?",
						null);

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						"Margaret",
						null,
						ConversationStates.QUEST_OFFERED,
						"Margaret is the pretty maid in the tavern, of course! Quite a looker, too... heh. Will you go for me?",
						null);
	}

	private void prepareBringingStep() {

		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				}, ConversationStates.QUEST_ITEM_BROUGHT, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (player.isEquipped("beer")) {
							npc.say("Hey! Is that beer for me?");
						} else {
							npc
									.say("Hey, I'm still waiting for that beer, remember? Anyway, what can I do for you?");
							npc.setCurrentState(1);
						}
					}
				});

		npc
				.add(
						ConversationStates.QUEST_ITEM_BROUGHT,
						ConversationPhrases.YES_MESSAGES,
						// make sure the player isn't cheating by putting the
						// beer
						// away and then saying "yes"
						new SpeakerNPC.ChatCondition() {
							@Override
							public boolean fire(Player player, String text,
									SpeakerNPC npc) {
								return player.isEquipped("beer");
							}
						},
						ConversationStates.ATTENDING,
						"*glug glug* Ah! That hit the spot. Let me know if you need anything, ok?",
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								player.drop("beer");
								StackableItem money = (StackableItem) StendhalRPWorld
										.get().getRuleManager()
										.getEntityManager().getItem("money");
								money.setQuantity(20);
								player.equip(money);

								player.addXP(10);

								player.notifyWorldAboutChanges();
								player.setQuest(QUEST_SLOT, "done");
							}
						});

		npc
				.add(
						ConversationStates.QUEST_ITEM_BROUGHT,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Drat! You remembered that I asked you for one, right? I could really use it right now.",
						null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareRequestingStep();
		prepareBringingStep();
	}
}
