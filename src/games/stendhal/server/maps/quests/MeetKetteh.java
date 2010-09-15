package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NakedCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Speak with Ketteh
 * 
 * PARTICIPANTS: - Ketteh Wehoh, a woman 
 * 
 * STEPS: - Talk to Ketteh to activate the quest and keep speaking with Ketteh.
 * 
 * REWARD: - No XP - No money
 * 
 * REPETITIONS: - As much as wanted.
 */
public class MeetKetteh extends AbstractQuest {
	private static final String QUEST_SLOT = "Ketteh";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private void step1() {

		final SpeakerNPC npc = npcs.get("Ketteh Wehoh");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new NakedCondition(), ConversationStates.ATTENDING,
				"Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT!\nIt's lucky you met me as I teach good #manners. My next lesson for you is that if anyone says a word in #blue it is polite to repeat it back to them. So, repeat after me: #manners.",
				new SetQuestAction(QUEST_SLOT, "seen_naked"));

		
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new NotCondition(new NakedCondition()), new QuestInStateCondition(QUEST_SLOT, "seen_naked")),
				ConversationStates.ATTENDING, null,
					new ChatAction() {
						public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
							// OK, player is NOT naked this time, but was last
							// time.
							raiser.say("Hi again, " + player.getTitle()	+ ". I'm so glad you have some clothes on now.");
							// don't be unforgiving
							player.setQuest(QUEST_SLOT, "seen"); 
						}
					});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new NotCondition(new NakedCondition()), new QuestNotInStateCondition(QUEST_SLOT, "seen_naked")),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.hasQuest(QUEST_SLOT)) {
							// We have met the player before and he was NOT
							// naked last time nor is he now
							raiser.say("Hi again, " + player.getTitle() + ".");
						} else {
							// We haver never seen the player before.
							raiser.say("Hi " + player.getTitle() + ", nice to meet you. You know, we have something in common - good #manners. Did you know that if someone says something in #blue it is polite to repeat it back to them? So, repeat after me: #manners.");
							player.setQuest(QUEST_SLOT, "seen");
						}
					}
				});

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES, new NakedCondition(),
				ConversationStates.IDLE,
				"If you don't put on some clothes and leave, I shall scream!",
				null);

		npc.add(ConversationStates.ATTENDING, "manners", 
				null,
				ConversationStates.ATTENDING, 
				"If you happen to talk to any of the other citizens, you should always begin the conversation saying \"hi\". People here are quite predictable and will always enjoy talking about their \"job\", they will respond if you ask for \"help\" and if you want to do a \"task\" for them, just say it. If they look like the trading type, you can ask for their \"offers\". To end the conversation, just say \"bye\".",
				new SetQuestAction(QUEST_SLOT, "learnt_manners"));

		npc.add(ConversationStates.ATTENDING, "blue", 
				null,
				ConversationStates.ATTENDING, 
				"Oh, aren't you the clever one!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Meet Ketteh Wehoh",
				"A lady sits infront of a hut in Semos Village and takes care, that new inhabitants of Faiumoni will walk around without freezing.",
				false);
		step1();
	}

	@Override
	public String getName() {
		return "MeetKetteh";
	}
	
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,"seen").fire(player, null, null);
	}
}
