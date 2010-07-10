package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPDependentOnLevelAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Weekly Item Fetch Quest.
 * <p>
 * PARTICIPANTS:
 * <ul><li> Hazel, Museum Curator of Kirdneh
 * <li> some items
 * </ul>
 * STEPS:<ul>
 * <li> talk to Museum Curator to get a quest to fetch a rare item
 * <li> bring the item to the Museum Curator
 * <li> if you cannot bring it in 6 weeks she offers you the chance to fetch
 * 
 * another instead </ul>
 * 
 * REWARD:
 * <ul><li> xp
 * <li> between 100 and 600 money
 * <li> can buy kirdneh house if other eligibilities met
 * <li> 10 Karma
 * </ul>
 * REPETITIONS:
 * <ul><li> once a week</ul>
 */
public class WeeklyItemQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "weekly_item";
	
	/** How long until the player can give up and start another quest */
	private final int expireDelay = MathHelper.MINUTES_IN_ONE_WEEK * 6; 
	
	/** How often the quest may be repeated */
	private final int delay = MathHelper.MINUTES_IN_ONE_WEEK; 
	
	/**
	 * All items which are hard enough to find but not tooo hard and not in Daily quest. If you want to do
	 * it better, go ahead. *
	 */
	private static Map<String,Integer> items;

	private static void buildItemsMap() {
		items = new HashMap<String, Integer>();
		items.put("mega potion",5);
		items.put("lucky charm",1);
		items.put("ice sword",1);
		items.put("fire sword",1);
		items.put("great sword",1);
		items.put("immortal sword",1);
		items.put("dark dagger",1);
		items.put("assassin dagger",1);
		items.put("night dagger",1);
		items.put("hell dagger",1);
		items.put("golden cloak",1);
		items.put("shadow cloak",1);
		items.put("chaos cloak",1);
		items.put("mainio cloak",1);
		items.put("obsidian",1);
		items.put("diamond",1);
		items.put("golden legs",1);
		items.put("shadow legs",1);
		items.put("golden armor",1);
		items.put("shadow armor",1);
		items.put("golden shield",1);
		items.put("shadow shield",1);
		items.put("skull staff",1);
		items.put("steel boots",1);
		items.put("golden boots",1);
		items.put("shadow boots",1);
		items.put("stone boots",1);
		items.put("chaos boots",1);
		items.put("golden helmet",1);
		items.put("shadow helmet",1);
		items.put("horned golden helmet",1);
		items.put("chaos helmet",1);
		items.put("golden twoside axe",1);
		items.put("drow sword",1);
		items.put("chaos legs",1);
		items.put("chaos sword",1);
		items.put("chaos shield",1);
		items.put("chaos armor",1);
		items.put("green dragon shield",1);
		items.put("egg",1);
		items.put("golden arrow",5);
		items.put("power arrow",5);
		items.put("mainio legs",1);
		items.put("mainio boots",1);
		items.put("mainio shield",1);
		items.put("mithril bar",1);
		items.put("mithril nugget",1);
		items.put("mainio armor",1);
		items.put("xeno boots",1);
		items.put("xeno legs",1);
		items.put("xeno armor",1);
		items.put("xeno shield",1);
		items.put("mythical egg",1);
		items.put("stone armor",1);
		items.put("demon sword",1);
		items.put("mainio helmet",1);
		items.put("red dragon cloak",1);
	}
	
	private void getQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,expireDelay,1))), 
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to bring the museum [item]"
						+ ". Please say #complete if you have it with you."));
		
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new TimePassedCondition(QUEST_SLOT,expireDelay,1)), 
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to bring the museum [item]"
						+ ". Please say #complete if you have it with you. But, perhaps that is now too rare an item. I can give you #another task, or you can return with what I first asked you."));
	
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,delay,1))), 
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT,"The museum can only afford to send you to fetch an item once a week. Please check back in", delay, 1));
		
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new StartRecordingRandomItemCollectionAction(QUEST_SLOT,0,items,"I want Kirdneh's museum to be the greatest in the land! Please fetch [item]"
				+ " and say #complete, once you've brought it."));		
		
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(new QuestNotStartedCondition(QUEST_SLOT),
								new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
												 new TimePassedCondition(QUEST_SLOT,delay,1))), 
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(actions));
	}
	
	private void completeQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"I don't remember giving you any #task yet.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"You already completed the last quest I had given to you.",
				null);
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new DropRecordedItemAction(QUEST_SLOT,0));
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncrementQuestAction(QUEST_SLOT,2,1));
		actions.add(new IncreaseXPDependentOnLevelAction(5.0/3.0, 290.0));
		actions.add(new IncreaseKarmaAction(10.0));
		actions.add(new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				int goldamount;
				final StackableItem money = (StackableItem) SingletonRepository.getEntityManager()
								.getItem("money");
				goldamount = 100 * Rand.roll1D6();
				money.setQuantity(goldamount);
				player.equipOrPutOnGround(money);
				raiser.say("Wonderful! Here is " + Integer.toString(goldamount) + " money to cover your expenses.");
			}
		});
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES, 
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0)),
				ConversationStates.ATTENDING, 
				null,
				new MultipleActions(actions));
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0))),
				ConversationStates.ATTENDING, 
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You don't seem to have [item]"
						+ " with you. Please get it and say #complete only then."));
		
	}
	
	private void abortQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						 		 new TimePassedCondition(QUEST_SLOT,expireDelay,1)), 
				ConversationStates.ATTENDING, 
				"I see. Please, ask me for another #quest when you think you can help Kirdneh museum again.", 
				new SetQuestAction(QUEST_SLOT, 0, "done"));
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						 		 new NotCondition(new TimePassedCondition(QUEST_SLOT,expireDelay,1))), 
				ConversationStates.ATTENDING, 
				"It hasn't been long since you've started your quest, you shouldn't give up so soon.", 
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new QuestNotActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"I'm afraid I didn't send you on a #quest yet.", 
				null);
		
	}

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
		res.add("I have met Hazel, the curator of Kirdneh museum.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Kirdneh museum become the greatest in the land.");
			return res;
		}
		res.add("I want to help Kirdneh museum become the greatest in the land.");
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			final String[] tokens = (questState + ";0;0;0").split(";");
			final String[] elements = tokens[0].split("=");
			String questItem = elements[0];
			int amount = 1;
			if(elements.length > 1) {
				amount=MathHelper.parseIntDefault(elements[1], 1);
			}
			if (!player.isEquipped(questItem, amount)) {
				res.add("I have been asked to find a rare item for Kirdneh museum.");
			} else {
				res.add("I have the rare item for Kirdneh museum and need to take it.");
			}
		}
		if (player.isQuestCompleted(QUEST_SLOT)) {
			final String[] tokens = (questState + ";0;0;0").split(";");
			final String questLast = tokens[1];
			final long timeRemaining = (Long.parseLong(questLast) + MathHelper.MILLISECONDS_IN_ONE_WEEK)
			- System.currentTimeMillis();

			if (timeRemaining > 0L) {
				res.add("I took the valuable item to Hazel within the last 7 days.");
			} else {
				res.add("I took the valuable item to Hazel and the museum can now afford to send me to find another.");
			}
		}
		return res;
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"",
				"",
				true);
		buildItemsMap();
		
		getQuest();
		completeQuest();
		abortQuest();
	}

	@Override
	public String getName() {
		return "WeeklyItemQuest";
	}
	
	// the items requested are pretty hard to get, so it's not worth prompting player to go till they are higher level.
	@Override
	public int getMinLevel() {
		return 60;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,delay,1)).fire(player, null, null);
	}
}
