package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Meet Marie-Henri
 * <p>
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Marie-Henrie, the famous french writer in Ados library</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Marie-Henri asks you to find out the pseudonym he uses when writing
 * novels</li>
 * <li>Find out the pseudonym (the Wikipedian might help)</li>
 * <li>Name the pseudonym to Marie-Henrie</li>
 * <li>Marie-Henri gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +5</li>
 * <li>An empty scroll</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>No repetitions.</li>
 * </ul>
 *
 * @author RedQueen
 */
public class MeetMarieHenri extends AbstractQuest {

	public static final String QUEST_SLOT = "meet_marie_henri";

	@Override
	public void addToWorld() {
		fillQuestInfo("Meet Marie-Henri",
				"A famous French writer tests general knowledge in Ados Library.",
				false);
		createSteps();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "MeetMarieHenri";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Marie-Henri";
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met Marie-Henri in Ados library.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("He asked me to find out the pseudonym he uses when writing his novels. But I don't feel smart enough for such a big task.");
		}
		if ("start".equals(questState) || "done".equals(questState)) {
			res.add("I will try to find out the pseudonym he uses when writing his novels.");
		}
		if ("done".equals(questState)) {
			res.add("I answered the question correctly and Marie-Henri gave me a nice reward.");
		}
		return res;
	}

	private void createSteps() {
		// player is asking for a quest
		SpeakerNPC npc = npcs.get("Marie-Henri");


		// TODO: rewrite this to use standard conditions and actions
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.QUEST_OFFERED, null, new ChatAction() {
					@Override
					public void fire(final Player player,
							final Sentence sentence, final EventRaiser npc) {
						final String questState = player.getQuest(QUEST_SLOT);
						if ("done".equals(questState)) {
							npc.say("I already know you are a smart one. I do not have another task for you to solve at the moment.");
							npc.setCurrentState(ConversationStates.ATTENDING);
						} else if ("start".equals(questState)) {
							npc.say("Have you already found out my pseudonym?");
							npc.setCurrentState(ConversationStates.QUESTION_1);
						} else {
							npc.say("I am currently testing the general knowledge of the adventurers around here. "
									+ "If you are able to tell me the #pseudonym I am using for my novels, I'll reward you. "
									+ "Do you feel smart enough for that?");
						}
					}
				});

		// player accepts quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Fine! Think about the question and visit me again when you think you know the answer.",
				new SetQuestAction(QUEST_SLOT, "start"));

		// player rejects quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"So you don't even want to try solving this easy task... How disappointing.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// player asks for 'pseudonym' when asked to accept quest
		npc.add(ConversationStates.QUEST_OFFERED,
				"pseudonym",
				null,
				ConversationStates.QUEST_OFFERED,
				"I do not sign my works with my real name, I use a 'pen name'. So will you try to solve that task?",
				null);

		// player wants to answer the question
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("pseudonym", "answer", "question"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new QuestInStateCondition(QUEST_SLOT, "done"))),
				ConversationStates.QUESTION_1,
				"Have you already found out my pseudonym?", null);

		// player says 'yes' when asked if he knows the correct answer
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_2, "So, what is it?", null);

		// player says 'no' when asked if he knows the correct answer
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Take your time to think about it. Follow the #hints around you to find the answer.",
				null);

		// player asks for hints
		npc.add(ConversationStates.ATTENDING, Arrays.asList("hint", "hints"),
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"Maybe the books on that table will give you a clue...", null);

		// analyzing the answer

		// TODO: rewrite this to use standard conditions and actions
		npc.addMatching(ConversationStates.QUESTION_2, Expression.JOKER,
				new JokerExprMatcher(), null, ConversationStates.ATTENDING,
				null, new ChatAction() {
					@Override
					public void fire(final Player player,
							final Sentence sentence, final EventRaiser npc) {
						final Sentence answer = sentence.parseAsMatchingSource();
						final Sentence expected = ConversationParser.parse("stendhal");
						final Sentence lastname = ConversationParser.parse("Beyle");

						if (answer.matchesFull(expected)) {
							// answer is correct -> get reward
							npc.say("Yes, that's it! Here, take this empty sheet of paper as a reward. It is the most valuable item for thinkers like us.");
							final Item reward = SingletonRepository
									.getEntityManager().getItem("empty scroll");
							reward.setBoundTo(player.getName());
							player.equipOrPutOnGround(reward);
							player.addXP(200);
							player.setQuest(QUEST_SLOT, "done");
							player.notifyWorldAboutChanges();
							npc.setCurrentState(ConversationStates.IDLE);
						} else if (answer.matchesFull(lastname)) {
							// player says 'Beyle'
							npc.say("You are on the right way. But I asked for my pseudonym, not for my last name.");
							npc.setCurrentState(ConversationStates.IDLE);
						} else if (ConversationPhrases.GOODBYE_MESSAGES
								.contains(sentence.getTriggerExpression()
										.getNormalized())) {
							// player says 'bye'
							npc.say("Au revoir!");
							npc.setCurrentState(ConversationStates.IDLE);
						} else {
							// answer is not correct
							npc.say("No, that is not correct. Follow the #hints around you to find the answer.");
							npc.setCurrentState(ConversationStates.IDLE);
						}
					}
				});
	}
}
