package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.player.Player;

public class HerbsForJynath extends AbstractQuest {

    public static final String QUEST_SLOT = "herbs_jynath";
    // The things that need to be collected by user to
    protected static final String NEEDED_ITEMS = "beer=2;wood=2";
    // "herbs" added to quest messages
    List<String> NEW_QUEST_MESSAGES = ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "herbs");

    @Override
    public void addToWorld() {
        prepareQuestStep();
        prepareBringingStep();
    }

    @Override
    public String getSlotName() {
        return QUEST_SLOT;
    }

    @Override
    public String getName() {
        return "HerbsForJynath";
    }

    // Add the following in at a later date

    // Other quests have a getTitle() method, I thought I should add this
    // as well
    public String getTitle() {
    	return "Herbs for Jynath";
	}

    /*
	@Override
	public int getMinLevel() {
		return 3;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	} */

    // TODO: update this for the different stages of the quest
    @Override
	public List<String> getHistory(final Player player) {
        final List<String> questHistory = new ArrayList<String>();
        if (!player.hasQuest(QUEST_SLOT)) {
            return questHistory;
        }
        questHistory.add("Jynath needs herbs to cast a spell and asked me to collect them for her.");
        final String questState = player.getQuest(QUEST_SLOT);
        if ("rejected".equals(questState)) {
        	questHistory.add("I chose not to help Jynath.");
        } else if (!"done".equals(questState)) {
            questHistory.add("I need to bring Jynath her herbs.");
        } else {
        	questHistory.add("I brought Jynath some herbs. I think she will use them to make a potion.");
        }
        return questHistory;
    }

    public void prepareQuestStep() {
        // get a reference to the Jynath npc
        SpeakerNPC npc = npcs.get("Jynath");

        // make sure that #herbs is only replied to if the quest has been
        // offered, (accepted or not) but not yet completed.

        // The first message when the player says "quest" and initiates the
        // quest (note: no asking if the players wants to accept)
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            // the quest is not active and not completed
            // ie. first time
            new AndCondition(new QuestNotActiveCondition(QUEST_SLOT), new QuestNotCompletedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
            ConversationStates.QUEST_OFFERED,
            "I want to brew a magic potion, but I need some #herbs for the recipe. Will you get them for me?",
            null);

        // if the player wants to ask what herbs are needed, just after
        // being offered the quest
        npc.add(ConversationStates.QUEST_OFFERED,
        	"herbs",
        	null,
        	ConversationStates.QUEST_OFFERED,
        	"The list of #herbs I need is [list]",
            null);

        // if the quest is accepted
        npc.add(ConversationStates.QUEST_OFFERED,
        	ConversationPhrases.YES_MESSAGES,
        	null,
        	ConversationStates.ATTENDING,
        	"Wonderful! The herbs I need are [list]",
        	new SetQuestAction(QUEST_SLOT, "start"));

        // if the quest is rejected
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES,
            null,
            ConversationStates.ATTENDING,
            "Oh well, if you change your mind let me know.",
            new SetQuestAction(QUEST_SLOT, "rejected"));

        // if the player wants to start the quest, and has rejected it earlier

        npc.add(ConversationStates.ATTENDING,
        	NEW_QUEST_MESSAGES,
        	new QuestInStateCondition(QUEST_SLOT, "rejected"),
        	ConversationStates.QUEST_OFFERED,
        	"Change our mind, did we. If you would like, you can still help me by collecting #herbs. Would you still like to do that?",
        	null);

        // this lists the herbs needed. This npc.add() is for QUEST_OFFERED, just after the
        // quest was offered, code is repeated just below for ATTENDING, (otherwise the same)
        npc.add(ConversationStates.ATTENDING,
        	NEW_QUEST_MESSAGES,
        	new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotCompletedCondition(QUEST_SLOT)),
        	ConversationStates.ATTENDING,
        	"I need #herbs for my potion. What I need is [list]",
            null);

        // send him away if he has completed the quest already.
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new QuestCompletedCondition(QUEST_SLOT),
            ConversationStates.ATTENDING,
            "Thank you again for fetching those herbs. I can now brew my magic potion!",
            null);
    }

    public void prepareBringingStep() {

    	// get a reference to the Jynath npc
        SpeakerNPC npc = npcs.get("Jynath");

        // created conditions and actions for the player having all the necessary items
        // purpose: to clean up the code
        AndCondition playerHasAllCondition = new AndCondition(new PlayerHasItemWithHimCondition("beer", 2), new PlayerHasItemWithHimCondition("wood", 2));
        AndCondition playerIsInQuestCondition = new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotCompletedCondition(QUEST_SLOT));

    	// create the reward that is given to player for winning
    	List<ChatAction> rewardActions = new LinkedList<ChatAction>();
        rewardActions.add(new DropItemAction("beer", 2));
        rewardActions.add(new DropItemAction("wood", 2));
        rewardActions.add(new EquipItemAction("money", 1234));
        rewardActions.add(new IncreaseXPAction(1234));
        rewardActions.add(new IncreaseKarmaAction(10));
        rewardActions.add(new SetQuestAction(QUEST_SLOT, "done"));

        // change text
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotCompletedCondition(QUEST_SLOT)),
            ConversationStates.ATTENDING,
            "Greetings! How may I help you? Have you brought my #herbs. I need them for a magic potion.",
            null);

        // add a yes response to the above

        npc.add(
            ConversationStates.ATTENDING,
            NEW_QUEST_MESSAGES,
            new AndCondition(playerHasAllCondition, playerIsInQuestCondition),
            ConversationStates.ATTENDING,
            "Exactly what I needed! Wonderful! Thank you for your help.",
            new MultipleActions(rewardActions));
    }

	@Override
	public String getNPCName() {
		return "Jynath";
	}
}
