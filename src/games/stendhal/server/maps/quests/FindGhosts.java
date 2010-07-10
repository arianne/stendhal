package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.TriggerList;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * QUEST: Find Ghosts
 * 
 * PARTICIPANTS: 
 * <ul>
 * <li> Carena</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Carena asks you to find the 4 other spirits on Fauimoni</li>
 * <li> You go find them and remember their names</li>
 * <li> You return and say the names</li>
 * <li> Carena checks you have met them, then gives reward</li>
 * <li> Note: you can meet the ghosts before you started the quest with her</li>
 * </ul>
 * 
 * REWARD: 
 * <ul>
 * <li> base HP bonus of 50</li>
 * <li> 5000 XP</li>
 * <li> Karma: 15</li>
 * </ul>
 * 
 * REPETITIONS: 
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class FindGhosts extends AbstractQuest {

	private static Logger logger = Logger.getLogger(FindGhosts.class);

	private static final String QUEST_SLOT = "find_ghosts";
	
	private static final List<String> NEEDED_SPIRITS = 
		Arrays.asList("mary", "ben", "zak", "goran");

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private List<String> missingNames(final Player player) {
		if (!player.hasQuest(QUEST_SLOT)) {
			return NEEDED_SPIRITS;
		}
		/*
		 * the format of the list quest slot is
		 * "looking;name;name;...:said;name;name;..."
		 */
		final String npcDoneText = player.getQuest(QUEST_SLOT).toLowerCase();
		final String[] doneAndFound = npcDoneText.split(":");
		final List<String> result = new LinkedList<String>();
		if (doneAndFound.length > 1) {
		    final String[] done = doneAndFound[1].split(";");
		    final List<String> doneList = Arrays.asList(done);
		    for (final String name : NEEDED_SPIRITS) {
			if (!doneList.contains(name)) {
			    result.add(name);
			}
		    }
		}
		return result;
	}

	private void askingStep() {
		final SpeakerNPC npc = npcs.get("Carena");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, "rejected")),
			ConversationStates.QUEST_OFFERED,
			"I feel so lonely. I only ever see creatures and alive people. If I knew about #spirits like me, I would feel better.",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I want help to find other spirits like me. Please find them, then come back and tell me their names.",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thank you! I feel better now that I know the names of other spirits on Fauimoni.",
			null);

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, 
			null,
			ConversationStates.ATTENDING,
			"That's lovely of you. Good luck searching for them.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "looking:said", 5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh. Never mind. Perhaps since I'm only a ghost I couldn't offer you much reward anyway.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -15.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("spirits", "spirit"),
			null,
			ConversationStates.QUEST_OFFERED,
			"I sense that there are 4 other spirits, but if only I knew their names I could contact them. Will you find them, then come back and tell me their names?",
			null);
	}

	private void findingStep() {
		// see the separate GhostNPC classes for what happens when a player
		// finds a ghost (with or without quest slot defined)
	}

	private void tellingStep() {

		final SpeakerNPC npc = npcs.get("Carena");

		// the player returns to Carena after having started the quest, or found
		// some ghosts.
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.QUESTION_1,
			"If you found any #spirits, please tell me their name.", null);

		npc.add(ConversationStates.QUESTION_1, NEEDED_SPIRITS, null,
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final Expression item = sentence.getTriggerExpression();
					
					// although all names our stored as lower case from now on, 
					// older versions did not,
					// so we have to be compatible with them
					final String npcQuestText = player.getQuest(QUEST_SLOT).toLowerCase();
					final String[] npcDoneText = npcQuestText.split(":");
	    			final String lookingStr;
	    			final String saidStr;
					if (npcDoneText.length > 1) {
						lookingStr = npcDoneText[0];
						saidStr = npcDoneText[1];
					} else {
						// compatibility with broken quests
						logger.warn("Player " + player.getTitle() + " found with find_ghosts quest slot in state " + player.getQuest(QUEST_SLOT) + " - now setting this to done.");
						player.setQuest(QUEST_SLOT, "done");
						npc.say("Sorry, it looks like you have already found them after all. I got confused");
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.ATTENDING);
						return;
					}
	    			
					final TriggerList looking = new TriggerList(Arrays.asList(lookingStr.split(";")));
					final TriggerList said = new TriggerList(Arrays.asList(saidStr.split(";")));
					String reply = "";
					String itemName = "initialized";
					TriggerList missing = new TriggerList(missingNames(player));
					final Expression found = missing.find(item);
					if (found != null) {
						itemName = found.getOriginal().toLowerCase();
					}

					if ((found != null) && looking.contains(item) && !said.contains(item)) {
						// we haven't said the name yet so we add it to the list
						player.setQuest(QUEST_SLOT, lookingStr
								+ ":" + saidStr + ";" + itemName);
						reply = "Thank you.";
					} else if (!looking.contains(item)) {
						// we have said it was a valid name but haven't met them
						reply = "I don't believe you've spoken with any spirit of that name.";
					} else if ((found == null) && said.contains(item)) {
						// we have said the name so we are stupid!
						reply = "You've told me that name already, thanks.";
					}

					// we may have changed the missing list
					missing = new TriggerList(missingNames(player));

					if (missing.size() > 0) {
						reply += " If you met any other spirits, please tell me their name.";
						npc.say(reply);
					} else {
						player.setBaseHP(50 + player.getBaseHP());
						player.heal(50, true);
						player.addXP(5000);
						player.addKarma(15);
						reply += " Now that I know those 4 names, perhaps I can even reach the spirits with my mind. I can't give you anything of material value, but I have given you a boost to your basic wellbeing, which will last forever. May you live long, and prosper.";
						npc.say(reply);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		final List<String> triggers = new ArrayList<String>();
		triggers.add(ConversationPhrases.NO_EXPRESSION);
		triggers.addAll(ConversationPhrases.GOODBYE_MESSAGES);
		npc.add(ConversationStates.QUESTION_1, triggers, null,
				ConversationStates.IDLE, "No problem, come back later.", null);

		// player says something which isn't in the needed spirits list.
		npc.add(
			ConversationStates.QUESTION_1,
			"",
			new NotCondition(new TriggerInListCondition(NEEDED_SPIRITS)),
			ConversationStates.QUESTION_1,
			"Sorry, I don't understand you. What name are you trying to say?",
			null);

		npc.add(
			ConversationStates.QUESTION_1,
			Arrays.asList("spirits", "spirit"),
			null,
			ConversationStates.QUESTION_1,
			"I seek to know more about other spirits who are dead but stalk the earthly world as ghosts. Please tell me any names you know.",
			null);

		// the player goes to Carena and says hi, and has no quest or is completed.
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new NotCondition(new QuestActiveCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, "Wooouhhhhhh!", 
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		askingStep();
		findingStep();
		tellingStep();
	}

	@Override
	public String getName() {
		return "FindGhosts";
	}
	
	@Override
	public int getMinLevel() {
		return 10;
	}
}
