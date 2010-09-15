package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerCanEquipItemCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * QUEST: The Elf Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Tywysoga, the Elf Princess in Nalwor Tower</li>
 * <li>Rose Leigh, the wandering flower seller.</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for a rare flower</li>
 * <li>Find the wandering flower seller</li>
 * <li>You are given the flower, provided you've already been asked to fetch it</li>
 * <li>Take flower back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>5000 XP</li>
 * <li>Some gold bars, random between 5,10,15,20,25,30.</li>
 * <li>Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Unlimited, provided you've activated the quest by asking the princess
 * for a task again</li>
 * </ul>
 */
public class ElfPrincess extends AbstractQuest {

    /* delay in minutes */
	private static final int DELAY = 5;
	private static final String QUEST_SLOT = "elf_princess";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Tywysoga");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
			ConversationStates.QUEST_OFFERED,
			"Will you find the wandering flower seller, Rose Leigh, and get from her my favourite flower, the Rhosyd?",
			null);

        // shouldn't happen: is a repeatable quest
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition("QUEST_SLOT"),
			ConversationStates.ATTENDING,
			"I have plenty of blooms now thank you.", null);
		
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, 0, "flower_brought"),
			ConversationStates.QUEST_OFFERED,
			"The last Rhosyd you brought me was so lovely. Will you find me another from Rose Leigh?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new QuestInStateCondition(QUEST_SLOT, 0, "got_flower")),
			ConversationStates.ATTENDING,
			"I do so love those pretty flowers from Rose Leigh ...",
			null);

		// Player agrees to collect flower
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you! Once you find it, say #flower to me so I know you have it. I'll be sure to give you a nice reward.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "start"),
								new IncreaseKarmaAction(10.0)));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"Oh, never mind. Bye then.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "rejected"),
					new DecreaseKarmaAction(10.0)));
	}

	private void getFlowerStep() {
		final SpeakerNPC rose = npcs.get("Rose Leigh");

        // give the flower if it's at least 5 minutes since the flower was last given, and set the time slot again
		rose.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
							 new PlayerCanEquipItemCondition("rhosyd"),
                             new TimePassedCondition(QUEST_SLOT, 1, DELAY)),
			ConversationStates.IDLE, 
			"Hello dearie. My far sight tells me you need a pretty flower for some fair maiden. Here ye arr, bye now.",
			new MultipleActions(new EquipItemAction("rhosyd", 1, true), 
                                new SetQuestAction(QUEST_SLOT, 0, "got_flower"), 
                                new SetQuestToTimeStampAction(QUEST_SLOT, 1)));

		// don't put the flower on the ground - if player has no space, tell them
		rose.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new TimePassedCondition(QUEST_SLOT, 1, DELAY),
								 new NotCondition(new PlayerCanEquipItemCondition("rhosyd"))),
				ConversationStates.IDLE, 
				"Shame you don't have space to take a pretty flower from me. Come back when you can carry my precious blooms without damaging a petal.",
				null);
		
        // don't give the flower if one was given within the last 5 minutes
        rose.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, DELAY))),
				ConversationStates.IDLE, 
				"I gave you a flower not five minutes past! Her Royal Highness can enjoy that one for a while.",
				null);
	    
        // don't give the flower if the quest state isn't start
        // possibly: unless it's been over 12 weeks and are in state got_flower?
        // trouble is old slots don't have a timestamp and so TimePassed will return true	
	    rose.add(ConversationStates.IDLE,
		    	ConversationPhrases.GREETING_MESSAGES,
		    	new QuestNotInStateCondition(QUEST_SLOT, 0, "start"),
		    	ConversationStates.IDLE,
		    	"I've got nothing for you today, sorry dearie. I'll be on my way now, bye.", 
		    	null);
	}

	private void bringFlowerStep() {
		final SpeakerNPC npc = npcs.get("Tywysoga");
		ChatAction addRandomNumberOfItemsAction = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				//add random number of goldbars
				final StackableItem goldbars = (StackableItem) SingletonRepository.getEntityManager()
						.getItem("gold bar");
				int goldamount;
				goldamount = 5 * Rand.roll1D6();
				goldbars.setQuantity(goldamount);
				// goldbars.setBoundTo(player.getName()); <- not sure
				// if these should get bound or not.
				player.equipOrPutOnGround(goldbars);
				npc.say("Thank you! Take these " + Integer.toString(goldamount) + " gold bars, I have plenty. And, listen: If you'd ever like to get me another, be sure to ask me first. Rose Leigh is superstitious, she won't give the bloom unless she senses you need it.");
			}
		};
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flower", "Rhosyd"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "got_flower"), new PlayerHasItemWithHimCondition("rhosyd")),
				ConversationStates.ATTENDING, null,
				new MultipleActions(new DropItemAction("rhosyd"), 
                                    new IncreaseXPAction(5000), 
                                    new IncreaseKarmaAction(15),
									addRandomNumberOfItemsAction, 
                                    new SetQuestAction(QUEST_SLOT, 0, "flower_brought"), 
									new IncrementQuestAction(QUEST_SLOT, 2, 1)));

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("flower", "Rhosyd"),
			new NotCondition(new PlayerHasItemWithHimCondition("rhosyd")),
			ConversationStates.ATTENDING,
			"You don't seem to have a rhosyd bloom with you. But Rose Leigh wanders all over the island, I'm sure you'll find her one day!",
			null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Rhosyd for Elf Princess",
				"Tywysoga, the Elf Princess in Nalwor Tower, requests to find the wandering flower seller Rose Leigh to get a precious rhosyd from her.",
				false);
		offerQuestStep();
		getFlowerStep();
		bringFlowerStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I bravely fought my way to the top of Nalwor Tower to meet Princess Tywysoga.");
        // todo split on ; to put the 0th part in questState		
        final String questStateFull = player.getQuest(QUEST_SLOT);
        final String[] parts = questStateFull.split(";");
        final String questState = parts[0];
		if ("rejected".equals(questState)) {
			res.add("The Elf Princess asked for a pretty flower, but I can't be bothered with that. I'm gonna kill some orcs!");
		}
		if ("start".equals(questState) || "got_flower".equals(questState) || isCompleted(player)) {
			res.add("The Princess requested I find the wandering flower seller Rose Leigh to get a precious rhosyd from her.");
		}
		if ("got_flower".equals(questState) || isCompleted(player)) {
			res.add("I found Rose Leigh and got the flower to take Princess Tywysoga.");
		}
        if (isRepeatable(player)) {
            res.add("I took the flower to the Princess and she gave me gold bars. If I want to make her happy again, I can ask her for another task.");
        } 
		return res;
	}

	@Override
	public String getName() {
		return "ElfPrincess";
	}
	
	@Override
	public int getMinLevel() {
		return 60;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,0,"flower_brought").fire(player,null, null);
	}
	
    // TODO: think about this one.
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,0,"flower_brought").fire(player,null, null);
	}
}
