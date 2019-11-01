/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
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

/**
 * QUEST: Ad Memoria In Portfolio
 * 
 * @author omero
 * 
 * PARTICIPANTS:
 * <ul>
 *  <li> Brosoklelo, somewhere in semos mountains, near the wizard tower
 *  <li> Blasyklela, somewhere in Kirdneh
 * </ul>
 *
 * STEPS:
 * <ul>
 *  <li> admemoriainportfolio_step1</li>
 *  <li> Talk with Brosoklelo to activate the quest.</li>
 *  <li> Collect 1x purple apple</li>
 *  <li> admemoriainportfolio_step2</li>
 *  <li> Talk with Blasyklela in Kirdneh. have 1x purple apple</li>
 *  <li> Collect 1x mauve apple</li>
 *  <li> admemoriainportfolio_step3</li>
 *  <li> Return to Brosoklelo with 1x mauve apple</li>
 *  <li> Brosoklelo will unlock portfolio</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 *  <li> 1000 XP</li>
 *  <li> 1000 Karma</li>
 *  <li> ability to use portfolio</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *  <li> None</li>
 * </ul> 
 */

/**
 * QUEST NOTES: Ad Memoria In Portfolio
 *   + Brosoklelo stepbrother of Blasyklela
 *   + Brosoklelo lost his memories in a magical duel
 *   + Brosoklelo still remembers his sister Blasyklela
 *   + Brosoklelo still remembers about Kirdneh
 *
 *   + Blasyklela is the stepsister of Brosoklelo
 *   + Blasyklela is living in Kirdneh
 *   + Blasyklela is an accomplished magician that can compile a magic memory log
 *   + Blasyklela will turn 1x purple apple into 1x mauve apple to restore Brosoklelo memory log
 */

/**
 * QUEST TEST: Ad Memoria In Portfolio
 * /alterquest <player> admemoriainportfolio <null>
 */


import java.util.List;

//import com.google.common.collect.ImmutableList;

import java.util.Arrays;
//import java.util.LinkedList;
import java.util.ArrayList;

//import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;

//import games.stendhal.server.entity.npc.action.CreateSlotAction;

import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.DropItemAction;

import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.DisableFeatureAction;

import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

public class AdMemoriaInPortfolio extends AbstractQuest {
	private static final int SCROLL_AMOUNT = 1;
	private static final String QUEST_SLOT = "admemoriainportfolio";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	/** admemoriainportfolio_step_1 */
	private void admemoriainportfolio_step_1() {
		final SpeakerNPC npc = npcs.get("Brosoklelo");

		/** RESET */
		npc.add(
            ConversationStates.ATTENDING,
			"reset", //reset request
			null,
			ConversationStates.IDLE,
			"reset complete",
	        new MultipleActions(
               new DisableFeatureAction("back"),
               new DisableFeatureAction("belt"),
               new DisableFeatureAction("portfolio"),
               new EnableFeatureAction("portfolio")
            ),
            null
		);
		
		/** quest is not started yet, quest asked */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
                "I am stranded ..." + " " +
                "I remember a name... #Blasyklela..."   + " " +
                "I remember a place... #Kirdneh..."         + " " +
                "I should recover my #memory...",
				null);

		/** quest is not started yet, ask about Blasyklela */
		npc.add(
			ConversationStates.ATTENDING,
			"Blasyklela",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Blasyklela is my stepsister... I remember... Blasyklela lives in #Kirdneh",
			null);

		/** quest is not started yet, ask about Kirdneh */
		npc.add(
			ConversationStates.ATTENDING,
			"Kirdneh",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Kirdneh... I remember that place... It is the place where my stepsister #Blasyklela lives",
			null);

		/** quest not started, quest offered */
		npc.add(
			ConversationStates.ATTENDING,
			"memory",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Laaah lah lah laaah... Duel in magical mist..." + " " +
            "My memory lost again... Dih.. Dah.. Duh.. Dah!" + " " +
            "Here stranded I stand. Yet not lost at all..."  + " " +
            "Oh please... Could you help recover my memory?" + " " +
            "A yes or no answer will do...",
			null);

		//on offered quest
		//YES MESSAGE, start quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Excellent!",
			//new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 15.0));			
	        new MultipleActions(
		       new ChatAction() {
                   @Override
                   public void fire(final Player player,
                   final Sentence sentence,
                   final EventRaiser npc) {
                        npc.say(
                        //give player item to bring to Blasyklela
                        //a normal something
                        //a special something
                        "Here you go... Take this purple apple from me!" + " " +
                        "Find Blasyklela and bring this purple apple along with you." + " " +
                        "Say purple apple to Blasyklela and she will know it is from me!" + " " +
                        "Once you return back here to me, I will reward your efforts!");
                        new EquipItemAction("purple apple", 1, true).fire(player, sentence, npc);
                        new SetQuestAndModifyKarmaAction(getSlotName(), "start", 15.0).fire(player, sentence, npc);
                   }
               }
            )
        );

		//on offered quest
		//NO MESSAGE, reject quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
            //Player say NO on offered quest
			null,
			ConversationStates.IDLE,
			"That is understandable..." + " " +
            "Maybe someone else will be more charitable...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -15.0));
    }

	/** admemoriainportfolio_step_2 */
	/** find Blasyklela in Kirdneh. step_2 */
    // Player has AdMemoriaInPortfolio quest
	// Player has AdMemoriaInPortfolio required items with him
	private void admemoriainportfolio_step_2() {
		final SpeakerNPC npc = npcs.get("Blasyklela");
		
		npc.add(
	        ConversationStates.ATTENDING, Arrays.asList("purple apple"),
	        new AndCondition(
	        	new QuestInStateCondition(QUEST_SLOT, "start"),
	        	new PlayerHasItemWithHimCondition("purple apple", SCROLL_AMOUNT)),
	        ConversationStates.IDLE,
	        null, //say something with multiple actions
	        new MultipleActions(
	        	new ChatAction() {
					@Override
					public void fire(
                        final Player player,
                        final Sentence sentence,
                        final EventRaiser npc) {
								npc.say(
									"Oh that purple apple is coming from Brosoklelo." + " " +
									"Surely Brosoklelo lost another magical duel..." + " " +
									"Here you go... Please take" + " " + 
									"this mauve apple from me!" + " " +
									"Bring mauve apple back to Brosoklelo" + " " +
									"Tell Brosoklelo mauve apple..." + " " +
									"A mauve apple will restore Brosoklelo memory!" + " " +
									"Once again *sigh*");
                                new IncreaseXPAction(1000);
								new EquipItemAction("mauve apple", 1, true).fire(player, sentence, npc);
								new SetQuestAndModifyKarmaAction( getSlotName(), "start", 15.0).fire(player, sentence, npc);
                        }
	        	},
	        	new DropItemAction("purple apple")
	        )
        );
	}

	/** admemoriainportfolio_step_3
	 */
	/** return to Brosoklelo, step_3
	 */
	/** completing admemoriainportfolio_step_3 activates portfolio slot for player
	 */
    /**
    * final List<ChatAction> reward = new LinkedList<ChatAction>();
    * if (System.getProperty("stendhal.container") != null) {
    *     reward.add(new CreateSlotAction(ImmutableList.of("belt", "back")));
    *     reward.add(new EquipItemAction("portfolio", 1, true));
    *     reward.add(new IncreaseXPAction(1));
    *     reward.add(new SetQuestAction(QUEST_SLOT, "done"));
    * } else {
    *     reward.add(new EnableFeatureAction("portfolio"));
    * }
    */
    /** 
    * final List<ChatAction> reward = new LinkedList<ChatAction>();
    * if (System.getProperty("stendhal.container") != null) {
    *     reward.add(new CreateSlotAction(ImmutableList.of("portfolio")));
    *     reward.add(new EquipItemAction("portfolio", 1, true));
    *     reward.add(new EnableFeatureAction("portfolio"));
    *     reward.add(new DropItemAction("black apple"));
    *     reward.add(new IncreaseXPAction(50));
    *     reward.add(new SetQuestAction(QUEST_SLOT, "done"));
    * }
    */
	private void admemoriainportfolio_step_3() {
		final SpeakerNPC npc = npcs.get("Brosoklelo");
		npc.add(
            ConversationStates.ATTENDING, Arrays.asList("mauve apple"),
            new AndCondition(
                new QuestInStateCondition(QUEST_SLOT, "start"),
                new PlayerHasItemWithHimCondition("mauve apple", SCROLL_AMOUNT)),
            ConversationStates.ATTENDING,
	        null, // say nothing here but say something in MultipleActions below
            new MultipleActions(
	        	new ChatAction() {
					@Override
					public void fire(
                        final Player player,
                        final Sentence sentence,
                        final EventRaiser npc) {
								npc.say(
                                    "Thank you indeed!" + " " +
        							"I will now grant you a special gift for your efforts..." + " " +
                                    //
                                    //"Here... Take this Portfolio..." + " " + 
                                    //"A portfolio will help you carry around many scrolls!");
                                    //
                                    "Farewell!");
                        }
	        	},
	        	//
	        	//REWARD:
	        	//
	        	new DropItemAction("mauve apple"),
				new SetQuestAndModifyKarmaAction(getSlotName(), "done", 15.0),
	        	//
	        	//REWARD: activate portfolio slot 
	        	//
    			//new CreateSlotAction(ImmutableList.of("belt", "back")),
    			//new CreateSlotAction(ImmutableList.of("portfolio")),
	        	//new EnableFeatureAction("portfolio"),
	        	//new EquipItemAction("portfolio", 1, false),
	        	//
				//REWARD: after portfolio eventually works
				new EquipItemAction("empty scroll", 3, false),
				new EquipItemAction("home scroll", 2, false),
				new EquipItemAction("kirdneh city scroll", 1, false),
				new EquipItemAction("apple", 1, true)

            )
        );
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				// title of the quest:
				"AdMemoriaInPortfolio",
				// description of quest step:
				"Talk Brosoklelo..." + " " +
				"Find Blasyklela..." + " " +
				"Rtrn Brosoklelo" + " " ,
				// repeat is false
				false);
		// admemoriainportfolio steps:
		admemoriainportfolio_step_1();
		admemoriainportfolio_step_2();
		admemoriainportfolio_step_3();
	}

	@Override
	public String getName() {
		//return a valid quest
		return "AdMemoriaInPortfolio";
	}

	@Override
	public String getRegion() {
		//return a valid zone
		return Region.SEMOS_SURROUNDS;
	}

	@Override
	public String getNPCName() {
		//origin of the quest
		return "Brosoklelo";
	}

	/** travel log */
	public List<String> getHistory(final Player player) {        
		//initialize res
        final List<String> res = new ArrayList<String>();
        
        if (!player.hasQuest(QUEST_SLOT)) {
        	//eject/bail out with something useful to understand
        	res.add(" ... Dont have quest ... " + "AdMemoriaInPortfolio" );
            return res;
        }
        
        final String questState = player.getQuest(QUEST_SLOT);
        if (questState.equals("rejected")) {
        	//eject/bail out with something useful to understand
        	res.add("I have asked Brosoklelo if he has a quest for me.");
            res.add("I do not want to help Brosoklelo recover his memories");
            return res;
        }
        
        if (questState.equals("start")) {
        	res.add("I have asked Brosoklelo if he has a quest for me.");
            res.add("I agreed to help Brosoklelo");
        }
        
        /**
        if (questState.equals("Blasyklela"))  {
            res.add("I took scrolls to Blasyklela and she asked me to tell Brosoklelo 'strandedwitch'.");
        }
        */        

        if (questState.equals("done")) {
            res.add("I returned to Brosoklelo and he has fixed my portfolio for me.");
        }
        
        return res;
    }
}
