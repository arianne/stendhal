/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
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
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerCanEquipItemCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;


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
			new AndCondition(new GreetingMatchesNameCondition(rose.getName()),
							 new QuestInStateCondition(QUEST_SLOT, 0, "start"),
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
				new AndCondition(new GreetingMatchesNameCondition(rose.getName()),
								 new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new TimePassedCondition(QUEST_SLOT, 1, DELAY),
								 new NotCondition(new PlayerCanEquipItemCondition("rhosyd"))),
				ConversationStates.IDLE,
				"Shame you don't have space to take a pretty flower from me. Come back when you can carry my precious blooms without damaging a petal.",
				null);

        // don't give the flower if one was given within the last 5 minutes
        rose.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(rose.getName()),
								 new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, DELAY))),
				ConversationStates.IDLE,
				"I gave you a flower not five minutes past! Her Royal Highness can enjoy that one for a while.",
				null);

	    final ChatCondition lostFlowerCondition = new AndCondition(new GreetingMatchesNameCondition(rose.getName()),
				 // had got the flower before and was supposed to take it to the princess next
	    		 new QuestInStateCondition(QUEST_SLOT, 0, "got_flower"),
				 // check chest and so on first - maybe the player does still have it (though we can't check house chests or the floor)
				 new ChatCondition() {
				     @Override
					public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				    	 return player.getTotalNumberOf("rhosyd") == 0;
				     }
				 },
				// just to check there is space
				new PlayerCanEquipItemCondition("rhosyd"),
				// note: older quest slots will pass this automatically, but they are old now.
                new TimePassedCondition(QUEST_SLOT, 1, 12*MathHelper.MINUTES_IN_ONE_WEEK));

	    // if the player never had a timestamp stored (older quest) we have now added timestamp 1.
	    // but that was a while ago that we changed it (November 2010?)
		rose.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			lostFlowerCondition,
			ConversationStates.QUESTION_1,
			"Hello dearie. Did you lose the flower I gave you last? If you need another say #yes but it's bad luck for me to have to give you it again, so you better be sure!",
			null);

		rose.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				lostFlowerCondition,
				ConversationStates.IDLE,
				"Heres a new flower to take the pretty lady, but mind you don't lose that one.",
				new MultipleActions(new EquipItemAction("rhosyd", 1, true),
                        new SetQuestAction(QUEST_SLOT, 0, "got_flower"),
                        // dock some karma for losing the flower
                        new IncreaseKarmaAction(-20.0),
                        new SetQuestToTimeStampAction(QUEST_SLOT, 1)));

		rose.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				lostFlowerCondition,
				ConversationStates.IDLE,
				"No worries dearie, you probably got it somewhere!",
				null);

        // don't give the flower if the quest state isn't start
        // unless it's been over 12 weeks and are in state got_flower?
	    rose.add(ConversationStates.IDLE,
		    	ConversationPhrases.GREETING_MESSAGES,
		    	new AndCondition(new GreetingMatchesNameCondition(rose.getName()),
		    					 new QuestNotInStateCondition(QUEST_SLOT, 0, "start"),
		    					 new NotCondition(lostFlowerCondition)),
		    	ConversationStates.IDLE,
		    	"I've got nothing for you today, sorry dearie. I'll be on my way now, bye.",
		    	null);
	}

	private void bringFlowerStep() {
		final SpeakerNPC npc = npcs.get("Tywysoga");
		ChatAction addRandomNumberOfItemsAction = new ChatAction() {
			@Override
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
		fillQuestInfo(
				"Rhosyd for Elf Princess",
				"Tywysoga, the Elf Princess in Nalwor Tower, wants to fill her room with precious rhosyds.",
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
		final int repetitions = player.getNumberOfRepetitions(getSlotName(), 2);
		if (repetitions > 0) {
			res.add("I've already taken Princess Tywysoga " + Grammar.quantityplnoun(repetitions, "precious flower", "one") + ".");
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

	// The quest may have been completed a few times already and then re-opened as it's repeatable
	// since this method is used to separate open quests from completed quests, we'll say that being completed
	// means it's done and not re-opened
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,0,"flower_brought").fire(player,null, null);
	}

	@Override
	public String getRegion() {
		return Region.NALWOR_CITY;
	}
	@Override
	public String getNPCName() {
		return "Tywysoga";
	}
}
