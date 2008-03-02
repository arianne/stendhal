package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
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
 * PARTICIPANTS: - Ketteh Wehoh, a woman who lives in the house next to the
 * bakery.
 * 
 * STEPS: - Talk to Ketteh to activate the quest and keep speaking with Ketteh.
 * 
 * REWARD: - No XP - No money
 * 
 * REPETITIONS: - As much as wanted.
 */
public class MeetKetteh extends AbstractQuest {
	private static final String QUEST_SLOT = "Ketteh";

	private void step1() {

		SpeakerNPC npc = npcs.get("Ketteh Wehoh");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new NakedCondition(), ConversationStates.ATTENDING,
				"Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT!\nShhh! Don't even think on clicking on the white bar at the bottom and writing to reply to me! And if you happen to talk to any of the other citizens, you'd better begin the conversation saying \"hi\". And don't be rude and just leave; say \"bye\" to end the conversation.\nAnd use Ctrl+Arrows to turn around and face me when I'm talking to you! Wait! I'm sure I've seen you with that fellow Nomyr, who's always peeking at the windows! Now use the arrow keys and get out of my room!",
				new SetQuestAction(QUEST_SLOT, "seen_naked"));

		
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new NotCondition(new NakedCondition()), new QuestInStateCondition(QUEST_SLOT, "seen_naked")),
				ConversationStates.ATTENDING, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
							// OK, player is NOT naked this time, but was last
							// time.
							engine.say("Hi again, " + player.getTitle()	+ ". How can I #shout at you this time?");
							player.setQuest("Ketteh", "seen"); // don't be unforgiving
						}
					});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new NotCondition(new NakedCondition()), new QuestNotInStateCondition(QUEST_SLOT, "seen_naked")),
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (player.hasQuest("Ketteh")) {
							// We have met the player before and he was NOT
							// naked last time nor is he now
							engine.say("Hi again, " + player.getTitle() + ".");
						} else {
							// We haver never seen the player before.
							engine.say("Hi " + player.getTitle() + ", nice to meet you.");
							player.setQuest("Ketteh", "seen");
						}
					}
				});

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.IDLE,
				"If you don't put on some clothes and leave, I shall scream!",
				null);

		npc.add(ConversationStates.ATTENDING, "shout", 
				new QuestInStateCondition(QUEST_SLOT, "seen_naked"),
				ConversationStates.ATTENDING, 
				"I am glad to see you've acquired some clothes. There really is no need for walking around naked.",
				null);

		npc.add(ConversationStates.ATTENDING, "shout",
				new QuestNotInStateCondition(QUEST_SLOT, "seen_naked"),
				ConversationStates.ATTENDING, 
				"Sometimes naked people pass by; it makes me very angry. They are bringing down the tone of the whole place!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step1();
	}
}
