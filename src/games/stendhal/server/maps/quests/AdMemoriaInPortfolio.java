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

import java.util.List;

import com.google.common.collect.ImmutableList;

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
import games.stendhal.server.entity.npc.action.CreateSlotAction;
//import games.stendhal.server.entity.npc.action.CreateSlotAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
//import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
//import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
//import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

/**
 * QUEST: Ad Memoria In Portfolio
 * 
 * PARTICIPANTS:
 * <ul>
 *  <li> strandedwizard, somewhere in semos mountains, near the wizard tower
 *   + lost his memories in a magical duel
 *   + still remembers his sister strandedwitch
 *   + still remembers about Kirdneh</li>
 *  <li> strandedwitch,  somewhere in Kirdneh
 *   + sister of strandedwizard lives in kirdneh
 *   + accomplished magician that can compile a magic memory log
 *   + needs 1x mauve apple to restore strandedwizard memory log</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 *  <li> admemoriainportfolio_step1</li>
 *  <li> Talk with strandedwizard to activate the quest.</li>
 *  <li> Collect 1x purple apple</li>
 *  <li> admemoriainportfolio_step2</li>
 *  <li> Talk with strandedwitch in Kirdneh. have 1x purple apple</li>
 *  <li> admemoriainportfolio_step2</li>
 *  <li> Return to strandedwizard with 1x mauve apple</li>
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

public class AdMemoriaInPortfolio1y extends AbstractQuest {
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
                "I am stranded out somewhere..." + " " +
                "I remember #strandedwitch..."   + " " +
                "I remember #kirdneh..."         + " " +
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
			/**
			"\n" +
			"-------------------------------------------------" + " " + "\n" +
			"Laaah lah lah laaah..."                            + " " + "\n" +
			"A duel..." + "Mages duel..."                       + " " + "\n" +
			"A mist..." + "Mages mist..."                       + " " + "\n" +
			"Stranded and yet not lost..."                      + " " + "\n" +
			"Catch those memories..."                           + " " + "\n" +
			"-------------------------------------------------" + " " + "\n" +
			*/
			"Laaah lah lah laaah... A duel in magical mist..."  + " " +
            "And surely I lost my memory... Again..."           + " " +
            "I stand here stranded but all is not lost yet..."  + " " +
            "Could You help recover strandedwizard memory?"     + " " +
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
                        //give player item to bring to strandedwitch
                        //a normal something
                        //a special something
                        "Here you go... Take this purple apple from me!" + " " +
                        "Find strandedwitch and bring this purple apple along with you." + " " +
                        "Say purple apple to strandedwitch and she will know it is from me!" + " " +
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
	/** find strandedwitch in Kirdneh. step_2 */
    // Player has AdMemoriaInPortfolio quest
	// Player has AdMemoriaInPortfolio required items with him
	private void admemoriainportfolio_step_2() {
		final SpeakerNPC npc = npcs.get("strandedwitch");
		npc.add(
	        ConversationStates.ATTENDING, Arrays.asList("purple apple"),
	        new AndCondition(
	        	new QuestInStateCondition(QUEST_SLOT, "start"),
	        	new PlayerHasItemWithHimCondition("purple apple", SCROLL_AMOUNT)),
	        ConversationStates.ATTENDING,
	        null, //say something with multiple actions
	        new MultipleActions(
	        	new ChatAction() {
					@Override
					public void fire(
                        final Player player,
                        final Sentence sentence,
                        final EventRaiser npc) {
								npc.say(
									"Oh that purple apple is coming from strandedwizard." + " " +
									"Surely strandedwizard lost another magical duel..." + " " +
									"Here you go... Please take" + " " + 
									"this mauve apple from me!" + " " +
									"Bring mauve apple back to strandedwizard" + " " +
									"Tell strandedwizard mauve apple..." + " " +
									"A mauve apple will restore strandedwizard memory!" + " " +
									"Once again *sigh*");
                                new IncreaseXPAction(1);
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
	/** return to strandedwizard, step_3
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
    * 
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
		final SpeakerNPC npc = npcs.get("strandedwizard");
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
                                    "Ad Memoria In Portfolio Quest is Done" + " " +
									"Portfolio will now work!");
                        }
	        	},
    			new CreateSlotAction(ImmutableList.of("belt", "back")),
	        	new EnableFeatureAction("portfolio"),
	        	new DropItemAction("mauve apple"),
                new EquipItemAction("portfolio", 1, true),
				new SetQuestAndModifyKarmaAction(getSlotName(), "done", 15.0),
				new EquipItemAction("apple", 1000, true)
            )
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
