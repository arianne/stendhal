package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: Imperial princess
 
 * PARTICIPANTS: 
 * <ul>
 * <li> The princess and King in Kalavan Castle</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Princess asks you to fetch a number of herbs and potions</li>
 * <li> You bring them</li>
 * <li> She recommends you to her father</li>
 * <li> you speak with him</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> XP</li>
 * <li> ability to buy houses in kalavan</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class ImperialPrincess extends AbstractQuest {
	private static final int ARANDULA_DIVISOR = 40;

	private static final int POTION_DIVISOR = 10;

	private static final int ANTIDOTE_DIVISOR = 20;

	private static final String QUEST_SLOT = "imperial_princess";

	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {

		final SpeakerNPC npc = npcs.get("Princess Ylflia");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("The trapped creatures looked much better last time I dared venture down to the basement, thank you!");

						} else if (!player.hasQuest(QUEST_SLOT)) {
							engine.say("I cannot free the captives in the basement but I could do one thing: ease their pain. I need #herbs for this.");
						} else if (player.getQuest(QUEST_SLOT).equals("recommended")) {
							engine.say("Speak to my father, the King. I have asked him to grant you citizenship of Kalavan, to express my gratitude to you.");
						} else {
							engine.say("I'm sure I asked you to do something for me, already.");
						}
					}
				});

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.ATTENDING, "herbs",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						engine.say("I need "
								+ Integer.toString(1 + player.getLevel()
										/ ARANDULA_DIVISOR)
								+ " arandula, 1 kokuda, 1 sclaria, 1 kekik, "
								+ Integer.toString(1 + player.getLevel()
										/ POTION_DIVISOR)
								+ " potions and "
								+ Integer.toString(1 + player.getLevel()
										/ ANTIDOTE_DIVISOR)
								+ " antidotes. Will you get these items?");
					}
				});

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you! We must be subtle about this, I do not want the scientists suspecting I interfere. When you return with the items, please say codeword #herbs.",
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						// store the current level incase it increases before
						// she see them next.
						player.setQuest(QUEST_SLOT, Integer.toString(player.getLevel()));
						player.addKarma(10);						
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"So you'll just let them suffer! How despicable.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		// give some hints of where to find herbs. No warranties!
		npc.addReply(
				"kokuda",
				"I believe that herb can only be found on Athor, though they guard their secrets closely over there.");
		npc.addReply(
				"sclaria",
				"Healers who use sclaria gather it in all sorts of places - around Or'ril, in Nalwor forest, I am sure you will find that without trouble.");
		npc.addReply(
				"kekik",
				"My maid's friend Jenny has a source not far from her. The wooded areas at the eastern end of Nalwor river may have it. too.");
	}

	private void step_2() {
		final SpeakerNPC npc = npcs.get("Princess Ylflia");

		/** If player has quest and has brought the herbs, get them */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("herb", "herbs"),
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final int required_arandula = 1
								+ Integer.valueOf(player.getQuest(QUEST_SLOT))
								/ ARANDULA_DIVISOR;
						final int required_antidote = 1
								+ Integer.valueOf(player.getQuest(QUEST_SLOT))
								/ ANTIDOTE_DIVISOR;
						final int required_potion = 1
								+ Integer.valueOf(player.getQuest(QUEST_SLOT))
								/ POTION_DIVISOR;
						if (player.hasQuest(QUEST_SLOT)
								&& !player.getQuest(QUEST_SLOT).equals(
										"recommended")
								&& player.isEquipped("kekik")
								&& player.isEquipped("kokuda")
								&& player.isEquipped("sclaria")
								&& player.isEquipped("arandula",
										required_arandula)
								&& player.isEquipped("potion", required_potion)
								&& player.isEquipped("antidote",
										required_antidote)) {
							player.drop("kekik");
							player.drop("kokuda");
							player.drop("sclaria");
							player.drop("antidote", required_antidote);
							player.drop("potion", required_potion);
							player.drop("arandula", required_arandula);
							engine.say("Perfect! I will recommend you to my father, as a fine, helpful person. He will certainly agree you are eligible for citizenship of Kalavan.");
							player.addXP(Integer.valueOf(player.getQuest(QUEST_SLOT)) * 400);
							player.setQuest(QUEST_SLOT, "recommended");
							player.notifyWorldAboutChanges();
						} else if (player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("recommended")) {
							engine.say("The herbs you brought did a wonderful job. I told my father you can be trusted, you should go speak with him now.");
						} else { 
							//reminder
							engine.say("Shh! Don't say it till you have the "
									+ required_arandula
									+ " arandula, 1 #kokuda, 1 #sclaria, 1 #kekik, "
									+ required_potion
									+ " potions and "
									+ required_antidote
									+ " antidotes. I don't want anyone suspecting our code.");
						}
					}
				});

	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("King Cozart");

		/** Complete the quest by speaking to King */
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "recommended"), 
			ConversationStates.IDLE,
			"Greetings! My wonderful daughter requests that I grant you citizenship of Kalavan City. Consider it done. Now, forgive me while I go back to my meal. Goodbye.",
			new MultipleActions(new IncreaseXPAction(500), new SetQuestAction(QUEST_SLOT, "done")));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestNotInStateCondition(QUEST_SLOT, "recommended"), 
			ConversationStates.IDLE, 
			"Leave me! Can't you see I am trying to eat?",
			null);
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
		return "ImperialPrincess";
	}
}
