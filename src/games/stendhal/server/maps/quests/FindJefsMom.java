/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import games.stendhal.common.MathHelper;
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
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerCanEquipItemCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * QUEST: Find Jefs mom
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Jef</li>
 * <li>Amber</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Jef waits for his mom in Kirdneh for a longer time now and is frightened that something happened to her</li>
 * <li> You go find Amber somewhere in Fado forest</li>
 * <li> She gives you a flower which you have to bring Jef</li>
 * <li> You return and give Jef the flower</li>
 * <li> Jef will reward you well</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 5000 XP</li>
 * <li> red lionfish which Jef got by someone who made holidays on Amazon island earlier</li>
 * <li> Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Once every 120 hours.</li>
 * </ul>
 * 
 * @author Vanessa Julius
 * 
 */
public class FindJefsMom extends AbstractQuest {

	// 120 hours (5 days)
	private static final int REQUIRED_MINUTES = 120 * 60;
	
	private static final String QUEST_SLOT = "find_jefs_mom";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Jef");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
			ConversationStates.QUEST_OFFERED,
			"I miss my #mother! She said she wants to go to the market but didn't return yet again. Can you watch out for her please?",
			null);

		// player asks about quest which he has done already and he is allowed to repeat it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES), new QuestStateStartsWithCondition(QUEST_SLOT, "waiting;")),
				ConversationStates.QUEST_OFFERED,
				"It is a long time ago that you told me the last time that my mother is fine. May you watch out for her again please?",
				null);

		
		// player asks about quest but time didn't pass yet
		npc.add(ConversationStates.ATTENDING, 
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1,REQUIRED_MINUTES))), 
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I don't want to disturb my mom from finding herself back in a way so you don't have to find her currently. You can ask me again in"));
	
		
		// player starts quest again
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new QuestInStateCondition(QUEST_SLOT, 0, "found_mom")),
			ConversationStates.ATTENDING,
			"Thank you so much! I hope that my #mom is still ok and will return soon. Please tell her my name to prove that I sent you to her.",
			null);

		// Player agrees to find mom
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you so much! I hope that my #mom is ok and will return soon! If you have found her and returned to me, I'll maybe have something in return for showing you how thankful I am.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "start"),
								new IncreaseKarmaAction(10.0)));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"Oh. Ok. I can understand you... You look like a busy hero so I'll not try to convince you of helping me out.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "rejected"),
					new DecreaseKarmaAction(10.0)));
		
		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("mom", "mother"),
				null,
				ConversationStates.QUEST_OFFERED,
				"My mother Amber left me for buying some food on the market but she didn't return yet. The only thing I know is, that she broke up with her former boyfriend, Roger Frampton before...",
				null);
	}

	private void findMomStep() {
		final SpeakerNPC amber = npcs.get("Amber");

        // give the flower if it's at least 5 days since the player activated the quest the last time, and set the time slot again
		amber.add(ConversationStates.ATTENDING, "Jef",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
							 new PlayerCanEquipItemCondition("zantedeschia"),
                             new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
			ConversationStates.IDLE, 
			"Oh I see :) My son Jef asked you to take a look after me. He is such a nice and gentle boy! Please give him this zantedeschia here. I love them! He will know that I'm fine when you give it to him!",
			new MultipleActions(new EquipItemAction("zantedeschia", 1, true), 
                                new SetQuestAction(QUEST_SLOT, 0, "found_mom"), 
                                new SetQuestToTimeStampAction(QUEST_SLOT, 1)));

		// don't put the flower on the ground - if player has no space, tell them
		amber.add(ConversationStates.ATTENDING, "Jef",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES),
								 new NotCondition(new PlayerCanEquipItemCondition("zantedeschia"))),
				ConversationStates.IDLE, 
				"Oh, I wanted to give you a flower for my son to show him that I'm fine but as I see now, you don't have enough space for equipping it. Please return to me when you made some space in your bags!",
				null);
		
        // don't give the flower if one was given within the last 5 days
        amber.add(ConversationStates.ATTENDING, "Jef",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
                                 new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.IDLE, 
				"Oh I don't think that my boy sent you so soon again to me. I take trust in him and doubt that he feels lonely already again.",
				null);
	    
        // don't give the flower if the quest state isn't start
	    amber.add(ConversationStates.ATTENDING, "Jef",
		     	new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, 0, "start")),
		    	ConversationStates.IDLE,
		    	"I don't trust you. Your voice shivered while you told my sons name. I bet he is fine and happy and save.", 
		    	null);
	    
	   
	}

	private void bringFlowerToJefStep() {
		final SpeakerNPC npc = npcs.get("Jef");
		ChatAction addRandomNumberOfItemsAction = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				//add random number of red lionfish
				final StackableItem red_lionfish = (StackableItem) SingletonRepository.getEntityManager()
						.getItem("red lionfish");
				int redlionfishamount;
				redlionfishamount = Rand.roll1D6();
				red_lionfish.setQuantity(redlionfishamount);
				player.equipOrPutOnGround(red_lionfish);
				npc.say("Thank you! Take these " + Integer.toString(redlionfishamount) + " red lionfishes, I got some from a guy who visited Amazon island some time ago, maybe you need them.");
				
			}
		};
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flower", "zantedeschia", "mom", "mother"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "found_mom"), new PlayerHasItemWithHimCondition("zantedeschia")),
				ConversationStates.ATTENDING, null,
				new MultipleActions(new DropItemAction("zantedeschia"), 
                                    new IncreaseXPAction(5000), 
                                    new IncreaseKarmaAction(15),
									addRandomNumberOfItemsAction, 
                                    new SetQuestAction(QUEST_SLOT, 0, "flower_brought_to_jef"), 
									new IncrementQuestAction(QUEST_SLOT, 2, 1)));

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("flower", "zantedeschia", "mom", "mother"),
			new NotCondition(new PlayerHasItemWithHimCondition("zantedeschia")),
			ConversationStates.ATTENDING,
			"Please don't lie to me. You promised me to find my mom and now you can't even prove that you found her earlier. I don't believe in you, sorry.",
			null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Find Jefs mother",
				"Jef, a young boy in Kirdneh city, waits for his mom Amber who didn't return yet from the market.",
				false);
		offerQuestStep();
		findMomStep();
		bringFlowerToJefStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I found Jef in Kirdneh city. He waits there for his mom.");
        final String questStateFull = player.getQuest(QUEST_SLOT);
        final String[] parts = questStateFull.split(";");
        final String questState = parts[0];
		if ("rejected".equals(questState)) {
			res.add("Finding his mom somewhere costs me too much time at the moment. I don't want to check on her at the moment.");
		}
		if ("start".equals(questState) || "found_mom".equals(questState) || isCompleted(player)) {
			res.add("Jef asked me to take a look at his mother Amber who didn't return from the market yet. I hope she will listen to me after I tell her the name of her son.");
		}
		if ("found_mom".equals(questState) || isCompleted(player)) {
			res.add("I found Amber, Jefs mother, while she walked around somewhere in Fado forest. She gave me a flower for her son.");
		}
		if (isCompleted(player)) {
			if (isRepeatable(player)) {
				res.add("Although Jef doesn't want me to look for his mother again earlier, I should ask him again if he had not change his mind yet about it.");
			} else {
				res.add("Jef was really happy when he heard that his mother Amber is fine. He told me that he is at least happy with that for the next time.");
			}			
        } 
		return res;
	}
	
	@Override
	public int getNumberOfRepetitions(Player player) {
		String questState = player.getQuest(getSlotName(), 2);
		return MathHelper.parseIntDefault(questState, 0);
	}

	@Override
	public String getName() {
		return "FindJefsMom";
	}
	
	
	@Override
	public boolean isRepeatable(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,0,"found_mom").fire(player,null, null);
	}
	

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,0,"found_mom").fire(player,null, null);
	}
}
