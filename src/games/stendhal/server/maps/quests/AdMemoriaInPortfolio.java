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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DisableFeatureAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.SystemPropertyCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Ad Memoria in Portfolio
 *
 * @author omero
 *
 * PARTICIPANTS:
 * <ul>
 *  <li> Brosoklelo, a befuddled sorceror
 *  <li> Vlamyklela, a concerned sorceress
 * </ul>
 *
 * STEPS:
 * <ul>
 *  <li> Talk with Brosoklelo to activate the quest.</li>
 *  <li> Talk with Vlamyklela have 1x purple apple</li>
 *  <li> Return to Brosoklelo have 1x mauve apple</li>
 *  <li> Brosoklelo will give a portfolio as a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 *  <li> 1000 XP</li>
 *  <li> 100 Karma</li>
 *  <li> A portfolio</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *  <li> None</li>
 * </ul>
 */

/**
 * QUEST NOTES: Ad Memoria In Portfolio
 *   + Brosoklelo stepbrother of Vlamyklela
 *   + Brosoklelo lost his memories in a magical duel
 *   + Brosoklelo still remembers his sister Vlamyklela
 *   + Brosoklelo still remembers about Kirdneh
 *
 *   + Vlamyklela is the stepsister of Brosoklelo
 *   + Vlamyklela is living in Kirdneh
 *   + Vlamyklela is an accomplished magician that can compile a magic memory log
 *   + Vlamyklela will turn 1x purple apple into 1x mauve apple to restore Brosoklelo memory log
 */

/** QUEST TEST: Ad Memoria In Portfolio
 * a0) Find Brosoklelo, tell reset
 *		this will remove all relevant items from player/character
 *		related to portfolio (brosoklelo/vlamyklela and portfolio)
 *		related to keyring   (xoderos/joshua and keyring)
 *
 * +make sure <player> QUEST_SLOT is clean:
 * b1) /alterquest admemoriainportfolio <enter>
 * 		will cleanse admemoriainportfolio quest slot
 *
 * +make sure <player> QUEST_SLOT is clean:
 * b2) /alterquest hungry_joshua <enter>
 * 		will cleanse hungry_joshua quest slot
 *
 * c1) destroy all remaining items in inventory
 *
 * d) exit client
 * e) exit and restart local server
 * f) restart client
 */

/** QUEST ADDITIONAL NOTES: Ad Memoria In Portfolio
 * portfolio is a portable container
 * portfolio cannot be lost on player death
 * portfolio stores takes 1 slot in player bag/inventory
 * portfolio provides additional slots to store scrolls
 */

public class AdMemoriaInPortfolio extends AbstractQuest {

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
            new SystemPropertyCondition("stendhal.testserver"), //chat condition
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
                "I remember a name... #Vlamyklela..."   + " " +
                "I remember a place... #Kirdneh..."         + " " +
                "I should recover my #memory...",
				null);

		/** quest is not started yet, ask about Vlamyklela */
		npc.add(
			ConversationStates.ATTENDING,
			"Vlamyklela",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Vlamyklela! My beloved stepsister... I remember a place... #Kirdneh...",
			null);

		/** quest is not started yet, ask about Kirdneh */
		npc.add(
			ConversationStates.ATTENDING,
			"Kirdneh",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Kirdneh! My beloved stepsister #Vlamyklela lives in Kirdneh! A place far from here...",
			null);

		/** quest not started, offer quest */
		npc.add(
			ConversationStates.ATTENDING,
			"memory",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Laaah lah lah laaah... Dueling in magical mist..." + " " +
            "Memory lost again... Dih.. Dah.. Duh.. Dah!" + " " +
            "Here stranded I stand and yet not lost at all..."  + " " +
            "My memory... Are you here to help?" + " " +
            "A simple #yes or #no will do, if you please...",
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
                        "Bring this purple apple along with you and find my stepsister Vlamyklela in Kirdneh..." + " " +
                        "Say purple apple to Vlamyklela and she will know it is from me!" + " " +
                        "My stepsister Vlamyklela will give you something that I need to recover my memory..." + " " +
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

		//on offered quest
		//try to leave without a proper yes/no answer
		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				"I asked a simple question! A plain #yes or #no will suffit...",
				null);

    }

	/** admemoriainportfolio_step_2 */
	/** find Vlamyklela in Kirdneh. step_2 */
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

							new SetQuestAndModifyKarmaAction( getSlotName(), "inprogress", 15.0).fire(player, sentence, npc);
							new IncreaseXPAction(1000);

							final Item mauveApple = SingletonRepository.getEntityManager().getItem("mauve apple");
							mauveApple.setInfoString("A special mauve apple for Brosoklelo");
							mauveApple.setDescription("You see a special mauve apple for Brosoklelo");
							mauveApple.setBoundTo("Brosoklelo");

							npc.say(
								"Oh! That purple apple must be coming from Brosoklelo." + " " +
								"Surely Brosoklelo lost another magical duel..." + " " +
								"Once again *sigh*"
							);

							if (player.equipOrPutOnGround(mauveApple)){
								npc.say(
									"Here you go... Please take this mauve apple from me!" + " " +
									"Bring that *mauve apple* back to Brosoklelo..." + " " +
									"Tell Brosoklelo *mauve apple* and he will know it is from me..." + " " +
									"That *mauve apple* will restore Brosoklelo memory for good..." + "\n" +
									"MAUVEAPPLEYES"
                                );
							} else {
								npc.say(
									"MAUVEAPPLENOT"
								);
							};
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

		//remove mauve apple from player inventory first
		reward_brosoklelo.add(new DropItemAction("mauve apple"));


		/**
		 * NOTE:
		 * we want a portable container as reward
		 * we want a scalable portable container
		 * portfolio for scrolls only
		 */

		/**
		 * NOTE:
		 * we do not want to create a new fixed player slot
		 * we do not want to enable features we know nothing about
		 *
		 * portfolio should be portable container and scalable
		 * portfolio should NOT work as the keyring
		 * if (System.getProperty("stendhal.container") != null) {
		 *   //reward_brosoklelo.add(new CreateSlotAction(ImmutableList.of("belt", "back")));
		 *   //reward_brosoklelo.add(new EquipItemAction("portfolio", 1, true));
		 * } else {
		 *   //reward_brosoklelo.add(new EnableFeatureAction("portfolio"));
		 * }
		 */

		//special reward when portfolio works as intended
		reward_brosoklelo.add(new EquipItemAction("portfolio", 1, true));

		//default reward even when portfolio is not available
		reward_brosoklelo.add(new IncreaseXPAction(1000));
		reward_brosoklelo.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done", 100));
		reward_brosoklelo.add(new EquipItemAction("home scroll", 1, false));
		reward_brosoklelo.add(new EquipItemAction("ados city scroll", 1, false));
		reward_brosoklelo.add(new EquipItemAction("kirdneh city scroll", 1, false));
		reward_brosoklelo.add(new EquipItemAction("deniran city scroll", 1, false));
		reward_brosoklelo.add(new EquipItemAction("empty scroll", 3, false));

		npc.add(
            ConversationStates.ATTENDING, Arrays.asList("mauve apple"),
            new AndCondition(
                new QuestInStateCondition(QUEST_SLOT, "inprogress"),
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
                                "Oh a mauve apple... That surely comes from Vlamyklela..." + " " +
                                "Thank you indeed!" + " " +
                                "I will now grant you a special gift for your efforts..." + " " +
                                "Here... Take this Portfolio..." + " " +
                                "A portfolio will help you carry around several scrolls!" + " " +
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
            "Ad Memoria in Portfolio",
            // description of quest step:
            "Talk to Brosoklelo and agree to help him recover his memory." + " " +
            "Find Vlamyklela and talk with her..." + " " +
            "Return to Brosoklelo for getting a reward!" + " " ,
            // repeat is false
            false
        );

		//admemoriainportfolio steps:
		admemoriainportfolio_step_0(); //testing only
		admemoriainportfolio_step_1(); //activate quest
		admemoriainportfolio_step_2(); //progress quest
		admemoriainportfolio_step_3(); //reclaim quest reward
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
        	res.add("I have met a befuddled sorceror, Brosoklelo");
        	res.add("I have asked Brosoklelo if he has a quest for me.");
            res.add("I do not want to help Brosoklelo recover his memories");
            return res;
        }

        if (questState.equals("start")) {
        	res.add("I have met a befuddled sorceror, Brosoklelo");
        	res.add("I have asked Brosoklelo for a quest.");
            res.add("I agreed to help Brosoklelo recover his memory ");
            res.add("Brosoklelo gave me a purple apple");
            res.add("I have to find Vlamyklela and tell her I have a purple apple");
        }

        if (questState.equals("inprogress")) {
        	res.add("I have met a befuddled sorceror named Brosoklelo");
        	res.add("I have asked Brosoklelo for a quest");
            res.add("I agreed to help Brosoklelo recover his memory and he promised a reward");
        	res.add("I have found Vlamyklela in Kirdneh and gave her a purple apple");
        	res.add("I have received a mauve apple from Vlamyklela in Kirdneh");
            res.add("I should now return to where I first met Brosoklelo to claim a reward for my efforts");
        }

        if (questState.equals("done")) {
        	res.add("I have met a befuddled sorceror, Brosoklelo");
        	res.add("I have asked Brosoklelo for a quest");
            res.add("I agreed to help Brosoklelo recover his memory by finding Vlamyklela in Kirdneh");
        	res.add("I have found Vlamyklela in Kirdneh and gave her a purple apple");
        	res.add("I have received a mauve apple from Vlamyklela in Kirdneh");
            res.add("I returned to where I first met Brosoklelo and gave him a mauve apple");
            res.add("I received a portfolio from Brosoklelo as a reward for my efforts!");
        }

        return res;
    }
}
