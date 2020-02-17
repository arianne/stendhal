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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
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
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;


/**
 * QUEST: Find Jefs Mother
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Jef</li>
 * <li>Amber</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Jef waits for his mum in Kirdneh for a longer time now and is frightened that something happened to her</li>
 * <li> You go to find Amber somewhere in Fado forest</li>
 * <li> She gives you a flower which you have to bring to Jef</li>
 * <li> You return and give the flower to Jef</li>
 * <li> Jef will reward you well</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 800 XP</li>
 * <li> Red lionfish which Jef got by someone who made holidays on Amazon island earlier (between 1-6)</li>
 * <li> Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Once every 4320 minutes. (3 days)</li>
 * </ul>
 *
 * @author Vanessa Julius
 *
 */
public class FindJefsMom extends AbstractQuest {

	// 4320 minutes (3 days)
	private static final int REQUIRED_MINUTES = 4320;

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
			"I miss my mother! She wanted to go to the market but didn't return so far. Can you watch out for her please?",
			null);

		// player asks about quest which he has done already and he is allowed to repeat it
				npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"), new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"It is a long time ago that you watched out for my mum. May I ask you to take a look at her again and tell me if she is still fine, please?",
				null);

		// player asks about quest but time didn't pass yet
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1,REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I don't want to disturb my mum at the moment, it seems like she needs some time on herself, so you don't have to look out for her currently. You can ask me again in"));


		// Player agrees to find mum
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you so much! I hope that my #mum is ok and will return soon! Please tell her my name, #Jef, to prove that I sent you to her. If you have found her, return to me please and I'll give you something for your efforts.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "start")));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"Oh. Ok. I can understand you... You look like a busy hero so I'll not try to convince you of helping me out.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT, 0, "rejected"),
					new DecreaseKarmaAction(10.0)));

		// Player asks for quest but is already on it

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I hope that you will find my mum soon and tell me, if she is #fine after.",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("mum", "mother", "mom"),
				null,
				ConversationStates.ATTENDING,
				"My mother Amber left me for buying some food on the market, but she didn't return #yet.",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"yet",
				null,
				ConversationStates.ATTENDING,
				"The only thing I know is, that she had a little argument with her boyfriend, #Roger #Frampton earlier...",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"Jef",
				null,
				ConversationStates.ATTENDING,
				"Yes, that is me :)",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("Roger Frampton", "Roger", "Frampton"),
				null,
				ConversationStates.ATTENDING,
				"Maybe Roger has some guess about where she went to. I'm not sure where he is either, I just know he sells houses somewhere here in Kirdneh.",
				null);

	}

	private void findMomStep() {
		final SpeakerNPC amber = npcs.get("Amber");

        // give the flower if it's at least 5 days since the player activated the quest the last time, and set the time slot again
		amber.add(ConversationStates.ATTENDING, "Jef",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0,"start"),
							 new PlayerCanEquipItemCondition("zantedeschia")),

			ConversationStates.IDLE,
			"Oh I see :) My son Jef asked you to take a look after me. He is such a nice and gentle boy! Please give him this zantedeschia here. I love these flowers! Please give it to him and tell him that I'm #fine.",
			new MultipleActions(new EquipItemAction("zantedeschia", 1, true),
                                new SetQuestAction(QUEST_SLOT, 0, "found_mom")));


		// don't put the flower on the ground - if player has no space, tell them
		amber.add(ConversationStates.ATTENDING, "Jef",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"),
								 new NotCondition(new PlayerCanEquipItemCondition("zantedeschia"))),
				ConversationStates.IDLE,
				"Oh, I wanted to give you a flower for my son to show him that I'm fine, but as I see now, you don't have enough space for equipping it. Please return to me when you will have made some space in your bags!",
				null);

        // don't give the flower if the quest state isn't start
	    amber.add(ConversationStates.ATTENDING, "Jef",
		     	new AndCondition(new NotCondition(new QuestActiveCondition(QUEST_SLOT))),
		    	ConversationStates.IDLE,
		    	"I don't trust you. Your voice shivered while you told me my sons name. I bet he is fine and happy and safe.",
		    	null);

	    amber.add(ConversationStates.ATTENDING, "Jef",
	    		new AndCondition(
	    				new QuestInStateCondition(QUEST_SLOT, "found_mom"),
	    				new PlayerHasItemWithHimCondition("zantedeschia")),
	    		ConversationStates.IDLE,
	    		"Please give that flower to my son and let him know that I am #fine.",
	    		null);

	    // replace flower if lost
	    amber.add(ConversationStates.ATTENDING, Arrays.asList("Jef", "flower", "zantedeschia"),
	    		new AndCondition(
	    				new QuestInStateCondition(QUEST_SLOT, 0, "found_mom"),
	    				new NotCondition(new PlayerHasItemWithHimCondition("zantedeschia"))),
	    		ConversationStates.IDLE,
	    		"Oh you lost the flower? I'm afraid I don't have anymore. Speak with Jenny, by the windmill. She may be able to help you.",
	    		null);

	}

	private void bringFlowerToJefStep() {
		final SpeakerNPC npc = npcs.get("Jef");

		ChatAction addRandomNumberOfItemsAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				//add random number of red lionfish
				final StackableItem red_lionfish = (StackableItem) SingletonRepository.getEntityManager()
						.getItem("red lionfish");
				int redlionfishamount;
				redlionfishamount = Rand.roll1D6();
				red_lionfish.setQuantity(redlionfishamount);
				player.equipOrPutOnGround(red_lionfish);
				npc.say("Thank you! Take " + Grammar.thisthese(redlionfishamount) + " " +  Grammar.quantityplnoun(redlionfishamount,"red lionfish","") + "! I got some from a guy who visited Amazon island some time ago, maybe you need " + Grammar.itthem(redlionfishamount) + " for something.");

			}
		};
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flower", "zantedeschia", "fine", "amber", "done"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "found_mom"), new PlayerHasItemWithHimCondition("zantedeschia")),
				ConversationStates.ATTENDING, null,
				new MultipleActions(new DropItemAction("zantedeschia"),
                                    new IncreaseXPAction(800),
                                    new IncreaseKarmaAction(15),
									addRandomNumberOfItemsAction,
									new IncrementQuestAction(QUEST_SLOT, 2, 1),
									new SetQuestToTimeStampAction(QUEST_SLOT,1),
									new SetQuestAction(QUEST_SLOT, 0, "done")));


	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Find Jefs Mother",
				"Jef, a young boy in Kirdneh city, waits for his mum, Amber, who didn't return yet from the market.",
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
		res.add("I found Jef in Kirdneh city. He waits there for his mum.");
        final String questStateFull = player.getQuest(QUEST_SLOT);
        final String[] parts = questStateFull.split(";");
        final String questState = parts[0];

        if ("rejected".equals(questState)) {
			res.add("Finding his mum somewhere costs me too much time at the moment, that is why I rejected his request to find her.");
		}
		if ("start".equals(questState)) {
			res.add("Jef asked me to take a look at his mother Amber who didn't return from the market yet. I hope she will listen to me after I told her the name of her son, Jef.");
		}
		if ("found_mom".equals(questState)) {
			res.add("I found Amber, Jef's mother, while she walked around somewhere in Fado forest. She gave me a flower for her son and told me, that I have to tell him that she is fine.");
		}
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("Its been a while since I checked on Jef's mother and should ask Jef, if he wants me to take a look after her again.");
            } else {
                res.add("I told Jef that his mother is fine. He wants to leave his mother alone for some time now.");
            }
		}

		return res;

	}

	@Override
	public String getName() {
		return "FindJefsMom";
	}


	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"done"),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}

	@Override
	public String getRegion() {
		return Region.KIRDNEH;
	}
	@Override
	public String getNPCName() {
		return "Jef";
	}
}
