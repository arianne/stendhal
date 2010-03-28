package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Take gold for Grafindle
 * 
 * PARTICIPANTS: <ul>
 * <li> Grafindle
 * <li> Lorithien </ul>
 * 
 * STEPS:<ul>
 * <li> Talk with Grafindle to activate the quest.
 * <li> Talk with Lorithien for the money.
 * <li> Return the gold bars to Grafindle</ul>
 * 
 * REWARD:<ul>
 * <li> 200 XP
 * <li> key to nalwor bank customer room
 * </ul>
 * REPETITIONS: <ul><li> None.</ul>
 */
public class TakeGoldforGrafindle extends AbstractQuest {
	private static final int GOLD_AMOUNT = 25;

	private static final String QUEST_SLOT = "grafindle_gold";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "lorithien", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("lorithien") && player.isEquipped("gold bar",
				GOLD_AMOUNT))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("lorithien")
				&& !player.isEquipped("gold bar", GOLD_AMOUNT)) {
			res.add("LOST_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {

		final SpeakerNPC npc = npcs.get("Grafindle");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					if (player.isQuestCompleted(QUEST_SLOT)) {
						engine.say("I ask only that you are honest.");
					} else {
						engine.say("I need someone who can be trusted with #gold.");
					}
				}
			});

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING, "gold",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"The bank has the gold safe now. Thank you!", null);

		/** If quest is not started yet, start it. */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Thank you. I hope to see you soon with the gold bars ... unless you are tempted to keep them.",
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					player.setQuest(QUEST_SLOT, "start");
					player.addKarma(5.0);
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Well, at least you are honest and told me from the start.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Lorithien",
			null,
			ConversationStates.QUEST_OFFERED,
			"She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?",
			null);

		/** Remind player about the quest */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"#Lorithien will be getting so worried with all that gold not safe! Please fetch it!",
			null);

		npc.add(ConversationStates.ATTENDING, "lorithien", null,
			ConversationStates.ATTENDING,
			"She works in the post office here in Nalwor.", null);
	}

	private void step_2() {
		final SpeakerNPC npc = npcs.get("Lorithien");

		/**
		 * If player has quest and is in the correct state, just give him the
		 * gold bars.
		 */
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"I'm so glad you're here! I'll be much happier when this gold is safely in the bank.",
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					player.setQuest(QUEST_SLOT, "lorithien");

					final StackableItem goldbars = (StackableItem) SingletonRepository.getEntityManager().getItem("gold bar");
					goldbars.setQuantity(GOLD_AMOUNT);
					goldbars.setBoundTo(player.getName());
					player.equipOrPutOnGround(goldbars);
				}
			});

		/** If player keep asking for book, just tell him to hurry up */
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "lorithien"),
			ConversationStates.ATTENDING,
			"Oh, please take that gold back to #Grafindle before it gets lost!",
			null);

		npc.add(ConversationStates.ATTENDING, "grafindle", null,
			ConversationStates.ATTENDING,
			"Grafindle is the senior banker here in Nalwor, of course!",
			null);

		/** Finally if player didn't start the quest, just ignore him/her */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Sorry, I have so many things to remember ... I didn't understand you.",
			null);
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Grafindle");

		/** Complete the quest */
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "lorithien"),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					if (player.drop("gold bar", GOLD_AMOUNT)) {
						engine.say("Oh, you brought the gold! Wonderful, I knew I could rely on you. Please, have this key to our customer room.");
						final Item nalworkey = SingletonRepository.getEntityManager()
								.getItem("nalwor bank key");
						nalworkey.setBoundTo(player.getName());
						player.equipToInventoryOnly(nalworkey);
						player.addXP(200);
						player.addKarma(10.0);

						player.notifyWorldAboutChanges();

						player.setQuest(QUEST_SLOT, "done");
					} else {
						engine.say("Haven't you got the gold bars from #Lorithien yet? Please go get them, quickly!");
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

	@Override
	public String getName() {
		return "TakeGoldforGrafindle";
	}
	
	// it is not easy to get to Nalwor
	@Override
	public int getMinLevel() {
		return 50;
	}
}
