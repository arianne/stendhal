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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CreateSlotAction;
import games.stendhal.server.entity.npc.action.DisableFeatureAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

//import games.stendhal.common.grammar.Grammar;
//import games.stendhal.server.actions.admin.AdministrationAction;

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
 *  <li> Talk with Brosoklelo to activate the quest.</li>
 *  <li> Talk with Blasyklela in Kirdneh. have 1x purple apple</li>
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
 * +make sure <player> QUEST_SLOT is clean:
 * /alterquest <player> admemoriainportfolio <null>
 */

public class AdMemoriaInPortfolio extends AbstractQuest {

	private static Logger logger = Logger.getLogger(AdMemoriaInPortfolio.class);

	private static final int APPLE_AMOUNT = 1;
	private static final String QUEST_SLOT = "admemoriainportfolio";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}


	/** A convenience function to make it easier to test quest */
    /** admemoriainportfolio_step_0 */
	private void admemoriainportfolio_step_0() {

		final SpeakerNPC npc = npcs.get("Brosoklelo");
		final List<ChatAction> reset_brosoklelo = new LinkedList<ChatAction>();

		reset_brosoklelo.add(new DropItemAction("purple apple"));
		reset_brosoklelo.add(new DropItemAction("mauve apple"));
		reset_brosoklelo.add(new DropItemAction("keyring"));
		reset_brosoklelo.add(new DropItemAction("portfolio"));
        reset_brosoklelo.add(new DisableFeatureAction("back"));
        reset_brosoklelo.add(new DisableFeatureAction("belt"));
        reset_brosoklelo.add(new DisableFeatureAction("keyring"));
        reset_brosoklelo.add(new DisableFeatureAction("portfolio"));

        npc.add(
            ConversationStates.ATTENDING, //initial state
            "reset", //trigger word
            null, //chat condition
            ConversationStates.IDLE, //state after reset
            "reset complete", //reply to trigger word
            new MultipleActions(reset_brosoklelo), //reset actions
            null
        );

    }

	/** admemoriainportfolio_step_1 */
	private void admemoriainportfolio_step_1() {
		final SpeakerNPC npc = npcs.get("Brosoklelo");

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
			"Blasyklela! My beloved stepsister... I remember... #Kirdneh...",
			null);

		/** quest is not started yet, ask about Kirdneh */
		npc.add(
			ConversationStates.ATTENDING,
			"Kirdneh",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Kirdneh! My stepsister #Blasyklela lives in Kirdneh! A place far from here...",
			null);

		/** quest not started, offer quest */
		npc.add(
			ConversationStates.ATTENDING,
			"memory",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Laaah lah lah laaah... Dueling in magical mist..." + " " +
            "Memory lost again... Dih.. Dah.. Duh.. Dah!" + " " +
            "Here stranded I stand. Yet not lost at all..."  + " " +
            "My memory... Are you here to help?" + " " +
            "Please... A #YES or #NO answer will do...",
			null);

		//on offered quest
		//YES MESSAGE, start quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Excellent! Now, listen to me with great attention...",
	        new MultipleActions(
		       new ChatAction() {
                   @Override
                   public void fire(final Player player,
                   final Sentence sentence,
                   final EventRaiser npc) {
                        npc.say(
                        "Take this purple apple from me!" + " " +
                        "Bring this purple apple along with you and find my stepsister, Blasyklela..." + " " +
                        "Say purple apple to Blasyklela and she will know it is from me!" + " " +
                        "My stepsister Blasyklela will give you something that I need to recover my memory..." + " " +
                        "Once you return back to me, here... I will reward your efforts!");
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
	private void admemoriainportfolio_step_2() {
		final SpeakerNPC npc = npcs.get("Vlamyklela");

	  // Player has AdMemoriaInPortfolio quest
		// Player has AdMemoriaInPortfolio required items with him
		npc.add(
	        ConversationStates.ATTENDING, Arrays.asList("purple apple"),
	        new AndCondition(
	        	new QuestInStateCondition(QUEST_SLOT, "start"),
	        	new PlayerHasItemWithHimCondition("purple apple", APPLE_AMOUNT)),
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

	/** admemoriainportfolio_step_3	 */
	/** return to Brosoklelo, step_3 */
	/** completing admemoriainportfolio_step_3 activates portfolio slot for player */
	private void admemoriainportfolio_step_3() {
		final SpeakerNPC npc = npcs.get("Brosoklelo");

		final List<ChatAction> reward_brosoklelo = new LinkedList<ChatAction>();

		reward_brosoklelo.add(new DropItemAction("mauve apple"));

		if (System.getProperty("stendhal.container") != null) {
			reward_brosoklelo.add(new CreateSlotAction(ImmutableList.of("belt", "back")));
			reward_brosoklelo.add(new CreateSlotAction(ImmutableList.of("portfolio")));
			reward_brosoklelo.add(new EquipItemAction("portfolio", 1, true));
		} else {
			reward_brosoklelo.add(new EnableFeatureAction("portfolio"));
            reward_brosoklelo.add(new EquipItemAction("portfolio", 1, true));
		}

        reward_brosoklelo.add(new EquipItemAction("kirdneh city scroll", 1, false));
        reward_brosoklelo.add(new EquipItemAction("deniran city scroll", 1, false));
        reward_brosoklelo.add(new EquipItemAction("home scroll", 2, false));
        reward_brosoklelo.add(new EquipItemAction("empty scroll", 3, false));
		reward_brosoklelo.add(new IncreaseXPAction(1000));
		reward_brosoklelo.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done", 100));

		npc.add(
            ConversationStates.ATTENDING, Arrays.asList("mauve apple"),
            new AndCondition(
                new QuestInStateCondition(QUEST_SLOT, "start"),
                new PlayerHasItemWithHimCondition("mauve apple", APPLE_AMOUNT)),
            ConversationStates.IDLE,
	        null, // say nothing here but say something in MultipleActions below
            new MultipleActions(
	        	new ChatAction() {
					@Override
					public void fire(
                        final Player player,
                        final Sentence sentence,
                        final EventRaiser npc) {
                            npc.say(
                                "Oh a mauve apple... That surely comes from Blasyklela..." + " " +
                                "Thank you indeed!" + " " +
                                "I will now grant you a special gift for your efforts..." + " " +
                                "Here... Take this Portfolio..." + " " +
                                "A portfolio will help you carry around many scrolls!" + " " +
                                "Fare thee well!");
                        }
	        	},
	        	new MultipleActions(reward_brosoklelo)
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
		admemoriainportfolio_step_0();
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
	@Override
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

        if (questState.equals("done")) {
            res.add("I returned to Brosoklelo and he has fixed my portfolio for me.");
        }

        return res;
    }
}
