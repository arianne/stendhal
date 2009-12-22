package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Learn about Karma
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Sarzina, the friendly wizardess who also sells potions in Fado</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Sarzina asks if you are a helpful person</li>
 * <li>You get good or bad karma depending on what you say</li>
 * <li>You get the chance to learn about karma and find out what yours is.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>Some Karma</li>
 * <li>Knowledge</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>Can always learn about karma but not get the bonus each time</li>
 * </ul>
 */
public class LearnAboutKarma extends AbstractQuest {

	private static final String QUEST_SLOT = "learn_karma";


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
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step1() {
		final SpeakerNPC npc = npcs.get("Sarzina");
		
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"Are you someone who likes to help others?", null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"If you want to get good #karma all you have to do is be helpful to others. I know a hunter girl called Sally who needs wood, and " 
			+ "I know another girl called Annie who loves icecream, well, I know many people who needs tasks doing for them regularly and I'm sure if you help them you will be rewarded, that's how karma works after all.", null);

		// player is willing to help other people
		// player gets a little karma bonus
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Wonderful! You must have good #karma.",
			new MultipleActions(new IncreaseKarmaAction(5.0), new SetQuestAction(QUEST_SLOT, "done")));

		// player is not willing to help other people
		// player gets a little karma removed
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"I knew it ... you probably have bad #karma.",
			new MultipleActions(new DecreaseKarmaAction(10.0), new SetQuestAction(QUEST_SLOT, "done")));

		// player wants to know what karma is
		npc.add(
			ConversationStates.ATTENDING,
			"karma",
			null,
			ConversationStates.QUESTION_1,
			"When you do a good thing like a #task for someone else, you get good karma. Good karma means you're likely to " 
			+ "do well in battle and when fishing or searching for something like gold. " 
			+ "Do you want to know what your karma is now?",
			null);

		// player wants to know what his own karma is
		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final long roundedkarma = Math.round(player.getKarma());
					final String Yk = "Your karma ";
					final String rk = Long.toString(roundedkarma);
                    if (roundedkarma > 4999 ) {
                        npc.say(Yk+"is unbelievably high, "+rk+"! You are very, very lucky!" );
                    } else if (roundedkarma > 999) {
                        npc.say(Yk+"is great, "+rk+".");
                    } else if (roundedkarma > 99) {
                        npc.say(Yk+"of "+rk+" is good.");
                    } else if (roundedkarma > -100) {
                        npc.say(Yk+"is roughly "+rk+".");
                    } else if (roundedkarma > -1000) {
                        npc.say(Yk+"of "+rk+" is bad.");
                    } else if (roundedkarma > -5000) {
                        npc.say(Yk+"is terrible, "+rk+"!");
                    } else {
                        npc.say(Yk+"is disastrous, "+rk+"!!! You are so unlucky...");
                    }
				}
			});

		// player doesn't want to know what his own karma is
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			null, ConversationStates.ATTENDING,
			"Fair enough! I could help you another way?", null);
		
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.QUEST_MESSAGES,
				null, ConversationStates.QUESTION_1,
				"If you ask around for tasks, and you complete them, you'll increase your karma. Do you want to know what your karma is now?", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step1();

	}
	@Override
	public String getName() {
		return "LearnAboutKarma";
	}
}
