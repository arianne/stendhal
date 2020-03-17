package games.stendhal.server.maps.quests;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.npc.action.*;
import games.stendhal.server.entity.npc.condition.*;
import java.util.*;

import java.util.List;

import games.stendhal.server.entity.player.Player;

public class UnicornHornsForRer extends AbstractQuest {

	public static final String QUEST_SLOT = "unicorn_rer";
	public static final int REQUIRED_HORNS = 250;
	public static final int REQUIRED_FEATHERS = 1;
	
	@Override
	public List<String> getHistory(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Unicorn Horns for Rer",
				"Rer Ecros promised to make me stronger if I bring him "+REQUIRED_HORNS+" unicorn horns and "
						+ (REQUIRED_FEATHERS > 1? REQUIRED_FEATHERS + " pegasus feathers.":"one pegasus feather."),
				false);
		prepareQuestOfferingStep();
		prepareFinishingStep();
		prepareResetStep();	// just for testing
		
	}

	private void prepareQuestOfferingStep() {
		
		SpeakerNPC npc = npcs.get("Rer Ecros");
		npc.add(ConversationStates.ATTENDING, 
	    		ConversationPhrases.QUEST_MESSAGES, 
	    		null,
	    		ConversationStates.ATTENDING,
	    		"Right now, I'm still setting up my lab down here. I might have a task for you in the future, though.", 
	    		null);
	    
/*		npc.add(ConversationStates.ATTENDING, 
	    		ConversationPhrases.QUEST_MESSAGES, 
	    		new AndCondition(
	    				new QuestNotCompletedCondition(QUEST_SLOT),
	    				new QuestNotStartedCondition(QUEST_SLOT)),
	    		ConversationStates.QUEST_OFFERED,
	    		"I need a supply of unicorn horns. If you bring me some along with a pegasus feather, I could increase your strength! Will you help me?", 
	    		null);
	    
		npc.add(ConversationStates.ATTENDING, 
	    		ConversationPhrases.QUEST_MESSAGES, 
	    		new QuestCompletedCondition(QUEST_SLOT), 
	    		ConversationStates.ATTENDING,
	    		"You already helped me a lot. I can go on with my research now.", 
	    		null);
	    
		npc.add(ConversationStates.QUEST_OFFERED, 
	    		ConversationPhrases.YES_MESSAGES, 
	    		null, 
	    		ConversationStates.QUEST_STARTED,
	    		"Go and bring me "+REQUIRED_HORNS+" unicorn horns and "
						+ (REQUIRED_FEATHERS > 1? REQUIRED_FEATHERS + " pegasus feathers.":"one pegasus feather. Do you have these items with you already?"), 
				new SetQuestAction(QUEST_SLOT, "started"));
	    
		npc.add(ConversationStates.QUEST_OFFERED, 
	    		ConversationPhrases.NO_MESSAGES, 
	    		null, 
	    		ConversationStates.IDLE,
	    		"Your loss. Enjoy the rest of your life in weakness.", 
	    		null);
	    */
		//TODO: Explanations for pegasus feathers and unicorn horns
	}
	private void prepareFinishingStep() {
/*		SpeakerNPC npc = npcs.get("Rer Ecros");
		
		List<ChatAction> reward = new LinkedList<ChatAction>();
	    reward.add(new DropItemAction("unicorn horn", REQUIRED_HORNS));
	    reward.add(new DropItemAction("pegasus feather", REQUIRED_FEATHERS));
	    reward.add(new IncreaseKarmaAction(100));
	    reward.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				player.setAtkXP(player.getAtkXP()+500000);
			}
	    });
	    reward.add(new SetQuestAction(QUEST_SLOT, "done"));
	    
	    AndCondition hasItemsCondition = new AndCondition(
	    		new PlayerHasItemWithHimCondition("unicorn horn", REQUIRED_HORNS),
	    		new PlayerHasItemWithHimCondition("pegasus feather", REQUIRED_FEATHERS));
	    
	    AndCondition questRunningCondition = new AndCondition(
	    		new QuestNotCompletedCondition(QUEST_SLOT),
				new QuestStartedCondition(QUEST_SLOT));
	    
	    
	    // player asks for 'items' after having started the quest.
	    npc.add(ConversationStates.ANY,
	    		"items",
	    		questRunningCondition,
	    		ConversationStates.QUEST_STARTED,
	    		"I need you to bring me "+REQUIRED_HORNS+" unicorn horns and "
						+ (REQUIRED_FEATHERS > 1? REQUIRED_FEATHERS + " pegasus feathers.":"one pegasus feather. Do you have these items with you already?"),
	    		null);
	    
	    // player asks for 'quest' even though it has been started and not yet finished.
	    npc.add(ConversationStates.ANY, 
	    		ConversationPhrases.QUEST_MESSAGES, 
	    		questRunningCondition,
	    		ConversationStates.QUEST_STARTED,
	    		"I already asked you to bring me "+REQUIRED_HORNS+" unicorn horns and "
						+ (REQUIRED_FEATHERS > 1? REQUIRED_FEATHERS + " pegasus feathers.":"one pegasus feather. Do you have these items with you already?"), 
				null);
	    
	    // player returns after having started the quest
	    npc.add(ConversationStates.IDLE,
	    		ConversationPhrases.GREETING_MESSAGES,
	    		questRunningCondition,
	    		ConversationStates.QUEST_STARTED,
	    		"Welcome back. Did you bring the #items I asked for?",
	    		null
	    		);		// TODO auch bei beendetem Quest ausgeführt!
	    
	    // player answers "yes" to the question wether he brought the require items
	    // ... and he has got all the required items
	    npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				hasItemsCondition,
				ConversationStates.IDLE,
				"That was quick, thank you! I will increase your strength, as promised!",
				new MultipleActions(reward));
		
	    // ... and he hasn't got all the required items
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(hasItemsCondition),
				ConversationStates.IDLE,
				"Don't lie to me, now get moving!",
				null); 
		
		// player answers "no" to the question wether he brought the require items
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"That's a pity, I really need these items fast. Please get them as soon as possible!",
				null); 
		*/
	}

	private void prepareResetStep() {
		SpeakerNPC npc = npcs.get("Rer Ecros");
		npc.add(ConversationStates.ANY,
				"reset",
				null,
				ConversationStates.IDLE,
				"The Quest Data has been erased",
				new SetQuestAction(QUEST_SLOT, null)); 
		
	}


	@Override
	public String getName() {
		return "UnicornHornsForRer";
	}

}
