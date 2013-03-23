package games.stendhal.server.maps.quests;
 
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.EquipRandomAmountOfItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Fruits for Coralia
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Coralia (Bar-maid of Ado tavern)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Coralia introduces herself and asks for a variety of fresh fruits for her hat.</li>
 * <li>You collect the items.</li>
 * <li>Coralia sees your items, asks for them then thanks you.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>XP: 300</li>
 * <li><1-5> Crepes Suzettes</li>
 * <li><2-8> Minor Potions</li>
 * <li>Karma: 5</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>After 1 week, fit with the withering of the fruits</li>
 * </ul>
 * 
 * @author pinchanzee
 */
public class FruitsForCoralia extends AbstractQuest {
 
	
	
	/**
	 * NOTE: Reward has not been set, nor has the XP.
	 * left them default here, but in the JUnit test
	 * called reward item "REWARD" temporarily
	 */
	
    public static final String QUEST_SLOT = "fruits_coralia";
    
    /** 
     * The delay between repeating quests.
     * 1 week
     */
	private static final int REQUIRED_MINUTES = 1440;
    
    /**
	 * Required items for the quest.
	 */
	protected static final String NEEDED_ITEMS = "apple=4;banana=5;cherry=9;grapes=2;pear=4;watermelon=1;pomegranate=2";
 
    @Override
    public void addToWorld() {
        super.addToWorld();
        fillQuestInfo("Fruits for Coralia",
				"The Ados Tavern barmaid, Coralia, searches for fresh fruits for her exotic hat.",
				true);
        prepareQuestStep();
        prepareBringingStep();
    }
 
    @Override
    public String getSlotName() {
        return QUEST_SLOT;
    }
 
    @Override
    public String getName() {
        return "FruitsForCoralia";
    }
    
 	@Override
 	public int getMinLevel() {
 		return 0;
 	}
 	
 	@Override
 	public boolean isRepeatable(final Player player) {
 		return new AndCondition(
 					new QuestStateStartsWithCondition(QUEST_SLOT, "done;"),
 					new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player, null, null);
 	}
 	
 	@Override
 	public String getRegion() {
 		return Region.ADOS_CITY;
 	}
 
 	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Coralia asked me for some fresh fruits for her hat.");
		final String questState = player.getQuest(QUEST_SLOT);
		
		if ("rejected".equals(questState)) {
			// quest rejected
			res.add("I decided not find Coralia some fruits, I have better things to do.");
		} else if (!player.isQuestCompleted(QUEST_SLOT)) {
			// not yet finished
			final ItemCollection missingItems = new ItemCollection();
			missingItems.addFromQuestStateString(questState);
			res.add("I still need to bring Coralia " + Grammar.enumerateCollection(missingItems.toStringList()) + ".");
		} else if (isRepeatable(player)) {
			// may be repeated now
			res.add("It's been a while since I brought Coralia fresh fruits for her hat, I wonder if the fruits have withered?");
        } else {
        	// not (currently) repeatable
        	res.add("I brought Coralia the fruits she needed for her hat and she restored it to its old radiance.");
		}
		return res;
	}
    
    public void prepareQuestStep() {
    	SpeakerNPC npc = npcs.get("Coralia");
    	
    	// various quest introductions
    	
    	// offer quest first time
    	npc.add(ConversationStates.ATTENDING,
    		ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "fruit"),
    		new AndCondition(
    			new QuestNotStartedCondition(QUEST_SLOT),
    			new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
    		ConversationStates.QUEST_OFFERED,
    		"Would you be kind enough to find me some fresh fruits for my hat? I'd be ever so grateful!",
    		null);
    	
    	// ask for quest again after rejected
    	npc.add(ConversationStates.ATTENDING, 
    		ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "hat"),
    		new QuestInStateCondition(QUEST_SLOT, "rejected"),
    		ConversationStates.QUEST_OFFERED, 
    		"Are you willing to find me some fresh fruits for my hat yet?",
    		null);
    	
    	// repeat quest
    	npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "hat"),
            new AndCondition(
            	new QuestCompletedCondition(QUEST_SLOT),
            	new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
            ConversationStates.QUEST_OFFERED,
            "I'm sorry to say that the fruits you brought for my hat aren't very fresh anymore. " +
            "Would you be kind enough to find me some more?",
            null);
    	    	
    	// quest inactive    	
    	npc.add(ConversationStates.ATTENDING,
        	ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "hat"),
        	new AndCondition(
        		new QuestCompletedCondition(QUEST_SLOT),
        		new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
        	ConversationStates.ATTENDING,
        	"Doesn't my hat look so fresh? I don't need any new fresh fruits for it yet, but thanks for enquiring!",
        	null);
    	
    	// end of quest introductions
    	
    	
    	// introduction chat    	
    	npc.add(ConversationStates.ATTENDING,
        	"hat",
        	new AndCondition(
        		new QuestNotStartedCondition(QUEST_SLOT),
        		new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
        	ConversationStates.ATTENDING,
        	"It's a shame for you to see it all withered like this, it really needs some fresh #fruits...",
        	null);
    	
    	// accept quest response
    	npc.add(ConversationStates.QUEST_OFFERED,
    		ConversationPhrases.YES_MESSAGES,
    		null,
    		ConversationStates.QUESTION_1,
    		null,
			new MultipleActions(
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, NEEDED_ITEMS, 5.0),
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "That's wonderful! I'd like these fresh fruits: [items].")));
    	
    	// reject quest response
    	npc.add(ConversationStates.QUEST_OFFERED,
        	ConversationPhrases.NO_MESSAGES,
        	null,
        	ConversationStates.ATTENDING,
        	"These exotic hats don't keep themselves you know...",
        	new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
    	
    	// meet again during quest
    	npc.add(ConversationStates.IDLE, 
    		ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
				new QuestActiveCondition(QUEST_SLOT),
				new GreetingMatchesNameCondition(npc.getName())),
			ConversationStates.ATTENDING,
			"Hello again. If you've brought me some fresh fruits for my #hat, I'll happily take them!",
			null);

   	
    	
    	// specific fruit info
    	npc.add(ConversationStates.QUESTION_1,
        	"apple",
        	new QuestActiveCondition(QUEST_SLOT),
        	ConversationStates.QUESTION_1,
        	"Glowing, radiant apples! The ones I have just now came from somewhere east of Semos.",
        	null);
    	
    	npc.add(ConversationStates.QUESTION_1,
            "banana",
            new QuestActiveCondition(QUEST_SLOT),
            ConversationStates.QUESTION_1,
            "There's one particularly exotic island with bananas.. Keep west, though - lets just say the bananas aren't meaty or fleshy enough for those in the east.",
            null);
    	
    	npc.add(ConversationStates.QUESTION_1,
        	"cherry",
        	new QuestActiveCondition(QUEST_SLOT),
        	ConversationStates.QUESTION_1,
        	"There's an old lady in Fado who sells the most beautifully vibrant cherries.",
        	null);
    	
    	npc.add(ConversationStates.QUESTION_1,
            "grapes",
            new QuestActiveCondition(QUEST_SLOT),
            ConversationStates.QUESTION_1,
            "There's a beautiful little temple in the mountains north of Semos that's covered in grape vines!  I've also heard of some by an old house in Or'ril mountains.",
            null);
    	
    	npc.add(ConversationStates.QUESTION_1,
        	"pear",
        	new QuestActiveCondition(QUEST_SLOT),
        	ConversationStates.QUESTION_1,
        	"I think I saw some pear trees in the northern mountains near a beautiful waterfall.",
        	null);
    	
    	npc.add(ConversationStates.QUESTION_1,
            "watermelon",
            new QuestActiveCondition(QUEST_SLOT),
            ConversationStates.QUESTION_1,
            "One of the huge watermelons from Kalavan gardens would make a nice new centrepiece for my hat.",
            null);
    	
    	npc.add(ConversationStates.QUESTION_1,
            "pomegranate",
            new QuestActiveCondition(QUEST_SLOT),
            ConversationStates.QUESTION_1,
            "I've never seen pomegranate trees growing wild, but I heard of a man living south of the great river cultivating them in his garden.",
            null);
    }
    
    
    private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Coralia");
		
		// ask for required items
    	npc.add(ConversationStates.ATTENDING, 
    		ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, "hat"),
    		new QuestActiveCondition(QUEST_SLOT),
    		ConversationStates.QUESTION_2, 
    		null,
    		new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I'd still like [items]. Have you brought any?"));
    	
    	// player says he didn't bring any items
		npc.add(ConversationStates.QUESTION_2, 
			ConversationPhrases.NO_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.QUESTION_1,
			null,
			new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Oh, that's a shame, do tell me when you find some. I'd still like [items]."));
    	
    	// player says he has a required item with him
		npc.add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.QUESTION_2, 
			"Wonderful, what fresh delights have you brought?",
			null);
    	
		// set up next step
    	ChatAction completeAction = new  MultipleActions(
			new SetQuestAction(QUEST_SLOT, "done"),
			new SayTextAction("My hat has never looked so delightful! Thank you ever so much! Here, take this as a reward."),
			new IncreaseXPAction(300),
			new IncreaseKarmaAction(5),
			new EquipRandomAmountOfItemAction("crepes suzette", 1, 5),
			new EquipRandomAmountOfItemAction("minor potion", 2, 8),
			new SetQuestToTimeStampAction(QUEST_SLOT, 1)
		);
    	
    	// add triggers for the item names
    	final ItemCollection items = new ItemCollection();
    	items.addFromQuestStateString(NEEDED_ITEMS);
    	for (final Map.Entry<String, Integer> item : items.entrySet()) {
    		npc.add(ConversationStates.QUESTION_2,
    			item.getKey(),
    			new QuestActiveCondition(QUEST_SLOT),
    			ConversationStates.QUESTION_2,
    			null,
    			new CollectRequestedItemsAction(item.getKey(),
    				QUEST_SLOT,
    				"Wonderful! Did you bring anything else with you?", "I already have enough of those.",
    				completeAction,
    				ConversationStates.ATTENDING));
    	}
    }

	@Override
	public String getNPCName() {
		return "Coralia";
	}
}