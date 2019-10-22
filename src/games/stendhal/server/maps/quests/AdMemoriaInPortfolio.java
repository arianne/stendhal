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

/**
 * QUEST: Ad Memoria In Portfolio
 * <p>
 * PARTICIPANTS:
 * <ul>
 *  <li> strandedwizard, somewhere in semos mountains, near the wizard tower
 *   + lost his memories in a magical duel
 *   + still remembers his sister strandedwitch
 *   + still remembers about Kirdneh</li>
 *  <li> strandedwitch,  somewhere in Kirdneh
 *   + sister of strandedwizard lives in kirdneh
 *   + accomplished magician that can compile a magic memory log
 *   + needs blank scrolls to restore strandedwizard memory log</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 *  <li> Talk with strandedwizard to activate the quest.</li>
 *  <li> Collect blank scrolls</li>
 *  <li> Talk with strandedwitch in Kirdneh.</li>
 *  <li> Return to strandedwizard with a message from strandedwitch.</li>
 *  <li> strandedwizard will unlock portfolio</li>
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
 *  <li> None.</li>
 * </ul>
 */

/**
 * testing:
 * /alterquest <player> admemoriainportfolio
 */

package games.stendhal.server.maps.quests;

//import com.google.common.collect.ImmutableList;
//import games.stendhal.common.grammar.Grammar;
//import games.stendhal.server.entity.npc.action.DropItemAction;
//import games.stendhal.server.entity.npc.condition.NotCondition;
//import games.stendhal.server.entity.npc.action.SetQuestAction;
//import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
//import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
//import games.stendhal.server.entity.npc.action.IncreaseXPAction;
//import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;

import java.util.List;
//import java.util.LinkedList;

import java.util.Arrays;
import java.util.ArrayList;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
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
		final SpeakerNPC npc = npcs.get("strandedwizard");

		/** quest is not started yet, quest asked */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
                "I stranded out somewhere..." + " " + "\n" +
                "I remember #strandedwitch..." + " " + "\n" +
                "I remember #kirdneh..." + " " + "\n" +
                "I should recover #memory",
				null);

		/** quest is not started yet, ask about strandedwitch */
		npc.add(
			ConversationStates.ATTENDING,
			"strandedwitch",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"strandedwitch is my stepsister... Lives in #Kirdneh",
			null);

		/** quest is not started yet, ask about kirdneh */
		npc.add(
			ConversationStates.ATTENDING,
			"kirdneh",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Kirdneh is the place where #strandedwitch lives",
			null);

		/** quest not started, quest offered */
		npc.add(
			ConversationStates.ATTENDING,
			"memory",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"\n" +
			"A duel..." + "Mages duel..."                       + " " + "\n" +
			"A mist..." + "Mages mist..."                       + " " + "\n" +
			"Stranded and yet not lost..."                      + " " + "\n" +
			"Catch those memories..."                           + " " + "\n" +
			"Laaah lah lah laaah..."                            + " " + "\n" +
			"Laaah lah laah laaaaah..."                         + " " + "\n" +
			"-------------------------------------------------" + " " + "\n" +
            "Will you help recovery strandedwizard memory?"     + " " + "\n" +
            "It is a question answered with: (yes/no)",
			null);

		//on offered quest
		//NO MESSAGE, reject quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"\n" +
			"-------------------------------------------------" + " " + "\n" +
			"Player say NO on offered quest"                    + " " + "\n" +
			"Player looses some karma"                          + " " + "\n" +
			"-------------------------------------------------" + " " + "\n" +
			"Maybe someone else will be more charitable..."     + " " + "\n" +
			"loose karma"                                       + " " + "\n",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -15.0));

		//on offered quest
		//YES MESSAGE, start quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			//give player item to bring to strandedwitch
			//a normal something
			//a special something
			"\n" +
			"-------------------------------------------------" + " " + "\n" +
			"Player say YES on offered quest"                   + " " + "\n" +
			"Player gains some karma"                           + " " + "\n" +
			"Player transitions to start/step2"                 + " " + "\n" +
			"-------------------------------------------------" + " " + "\n" +
			"find  strandedwitch this stone"                    + " " + "\n" +
			"bring strandedwitch this stone"                    + " " + "\n" +
			"tell  strandedwitch this stone"                    + " " + "\n" +
			"strandedwitch use   this stone"                    + " " + "\n" +
			"-------------------------------------------------" + " " + "\n",
			//new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 15.0));			
	        new MultipleActions(
		       new ChatAction() {
			   @Override
			   public void fire(final Player player,
			   final Sentence sentence,
			   final EventRaiser npc) {
					npc.say(
						"Thank you!" + " " +
						"Here you go... Please take" + 
						"this purple apple from me!");
						new EquipItemAction("purple apple", 1, true).fire(player, sentence, npc);
						new SetQuestAndModifyKarmaAction(
								getSlotName(), "start", 15.0).fire(player, sentence, npc);
			    }},
                //mark status not necessary
                new InflictStatusOnNPCAction("purple apple"))
		    );
		}

	/** admemoriainportfolio_step_2 */
	/** find strandedwitch in Kirdneh. step_2 */
    // Player has AdMemoriaInPortfolio quest
	// Player has AdMemoriaInPortfolio required items with him
	private void admemoriainportfolio_step_2() {
		final SpeakerNPC npc = npcs.get("strandedwitch");
		npc.add(
	        ConversationStates.ATTENDING, Arrays.asList("apple"),
	        new AndCondition(
	        	new QuestInStateCondition(QUEST_SLOT, "start"),
	        	new PlayerHasItemWithHimCondition("purple apple", SCROLL_AMOUNT)),
	        ConversationStates.ATTENDING,
	        null, //say something with multiple actions
	        new MultipleActions(
	        	new DropItemAction("purple apple"),
	        	new ChatAction() {
					@Override
					public void fire(
							final Player player,
							final Sentence sentence,
							final EventRaiser npc) {
								npc.say(
									"Thank you!" + " " +
									"Here you go... Please take" + " " + 
									"this black apple from me!");
								new EquipItemAction("black apple", 1, true).fire(player, sentence, npc);
								//switch to step3
								new SetQuestAndModifyKarmaAction(
									getSlotName(), "inprogres1", 15.0).fire(player, sentence, npc);
							}
	        	},
                //mark status not necessary
	        	new InflictStatusOnNPCAction("black apple")
	        ));
	}

	/** admemoriainportfolio_step_3 */
	/** return to strandedwizard, step_3 */ 
	private void admemoriainportfolio_step_3() {
		final SpeakerNPC npc = npcs.get("strandedwizard");
		npc.add(
		        ConversationStates.ATTENDING, Arrays.asList("purple apple"),
		        new AndCondition(
		        	new QuestInStateCondition(QUEST_SLOT, "start"),
		        	new PlayerHasItemWithHimCondition("purple apple", SCROLL_AMOUNT)),
		        ConversationStates.ATTENDING,
		        null, //say something with multiple actions
		        new MultipleActions(
		        	new DropItemAction("black apple"),
		        	new ChatAction() {
						@Override
						public void fire(final Player player,
								final Sentence sentence,
								final EventRaiser npc) {
                                    npc.say(
                                        "Thank you!" + " " +
                                        "Here you go..." +
                                        "Please take this portfolio from me!");
                                    new EquipItemAction(
                                    		"portfolio", 1, true).fire(player, sentence, npc);
                                    
                                    new SetQuestAndModifyKarmaAction(
                                    		getSlotName(), "done", 15.0).fire(player, sentence, npc);
						        }
		        	},
		        	new InflictStatusOnNPCAction("portfolio"))
		    );
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				// title of the quest:
				"AdMemoriaInPortfolio",
				// description of the quest:
				"Talk strandedwizard           step_1" + "\n" +
				"Find strandedwitch            step_2" + "\n" +
				"Retr strandedwizard           step_3" + "\n" ,
				// repeat is false
				false);

		// steps:
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
		return "strandedwizard";
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
        	res.add("I have asked strandedwizard if he has a quest for me.");
            res.add("I do not want to help strandedwizard recover his memories");
            return res;
        }
        
        if (questState.equals("start")) {
        	res.add("I have asked strandedwizard if he has a quest for me.");
            res.add("I agreed to help strandedwizard");
        }
        
        /**
        if (questState.equals("strandedwitch"))  {
            res.add("I took scrolls to strandedwitch and she asked me to tell strandedwizard 'strandedwitch'.");
        }
        */        

        if (questState.equals("done")) {
            res.add("I returned to strandedwizard and he has fixed my portfolio for me.");
        }
        

        return res;
    }
}
