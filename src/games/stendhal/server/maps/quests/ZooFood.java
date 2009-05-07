package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Zoo Food
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Katinka, the keeper at the Ados Wildlife Refuge
 * <li> Dr.Feelgood, the veterinary
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Katinka asks you for ham for the animals.
 * <li> You get the ham, e.g. by killing other animals ;)
 * <li> You give the ham to Katinka.
 * <li> Katinka thanks you.
 * <li> You can then buy cheap medicine from Dr. Feelgood.
 * </ul>
 * 
 * REWARD: <ul><li> 200 XP <li> everlasting supply for cheap medicine
 * </ul>
 * REPETITIONS: - None.
 */
public class ZooFood extends AbstractQuest {

	private static final int REQUIRED_HAM = 10;

	private static final String QUEST_SLOT = "zoo_food";

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
			return res;
		}
		res.add("QUEST_ACCEPTED");
		if ((player.isEquipped("ham", REQUIRED_HAM)) || isCompleted(player)) {
			res.add("FOUND_ITEM");
		}
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Katinka");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.ATTENDING, "Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				null
		);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Welcome back to the Ados Wildlife Refuge! Thanks again for rescuing our animals!",
				null
		);

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, "Our tigers, lions and bears are hungry. We need "
						+ Grammar.quantityplnoun(REQUIRED_HAM, "ham") + " to feed them. Can you help us?",
				null
		);

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Thank you, but I think we are out of trouble now.",
				null
		);

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Okay, but please don't let the poor animals suffer too long! Bring me the "
						+ Grammar.plnoun(REQUIRED_HAM, "ham")
						+ " as soon as you get " + Grammar.itthem(REQUIRED_HAM)
						+ ".", new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0)
		);

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Oh dear... I guess we're going to have to feed them with the deer...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0)
		);
	}

	private void step_2() {
		// Just find the ham somewhere. It isn't a quest
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Katinka");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Welcome back! Have you brought the "
						+ Grammar.quantityplnoun(REQUIRED_HAM, "ham") + "?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (player.drop("ham", REQUIRED_HAM)) {
							player.notifyWorldAboutChanges();
							player.setQuest(QUEST_SLOT, "done");
							player.addXP(200);
							player.addKarma(15);
							engine.say("Thank you! You have rescued our rare animals.");
						} else {
							engine.say("*sigh* I SPECIFICALLY said that we need "
										+ Grammar.quantityplnoun(REQUIRED_HAM, "ham") + "!");
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Well, hurry up! These rare animals are starving!",
				null);
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Dr. Feelgood");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?",
				null
		);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE, "Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				null
		);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
		step_4();
	}

	@Override
	public String getName() {
		return "ZooFood";
	}
}
