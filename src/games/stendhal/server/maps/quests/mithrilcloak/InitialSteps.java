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
package games.stendhal.server.maps.quests.mithrilcloak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;

/*
 * @author kymara
 */

class InitialSteps {

	private MithrilCloakQuestInfo mithrilcloak;

	private final NPCList npcs = SingletonRepository.getNPCList();

	public InitialSteps(final MithrilCloakQuestInfo mithrilcloak) {
		this.mithrilcloak = mithrilcloak;
	}

	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Ida");


		// player asks about quest, they haven't started it yet
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(new QuestNotStartedCondition(mithrilcloak.getQuestSlot()), new QuestInStateCondition(mithrilcloak.getQuestSlot(), "rejected")),
				ConversationStates.QUEST_OFFERED,
				"My sewing machine is broken, will you help me fix it?",
				null);

		final Map<String,Integer> items = new HashMap<String, Integer>();
		items.put("leather armor",1);
		items.put("oil",1);
		items.put("bobbin",1);

		// Player says yes they want to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			null,
			new MultipleActions(new SetQuestAction(mithrilcloak.getQuestSlot(), "machine;"),
								new StartRecordingRandomItemCollectionAction(mithrilcloak.getQuestSlot(), 1, items, "Thank you! To fix it, it needs [#item]. I'm ever so grateful for your help.")));

		// player said no they didn't want to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Oh dear, I don't know what I can do without a decent sewing machine. But don't worry I won't bother you any longer!",
			new SetQuestAndModifyKarmaAction(mithrilcloak.getQuestSlot(), "rejected", -5.0));


		// player asks for quest but they already did it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(mithrilcloak.getQuestSlot()),
				ConversationStates.ATTENDING,
				"You've already completed the only quest that I have for you.",
				null);

		//player fixed the machine but hadn't got mithril shield.
		// they return and ask for quest but they still haven't got mithril shield
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot())),
								 new OrCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_mithril_shield"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "fixed_machine"))
								 ),
				ConversationStates.ATTENDING,
								 "I don't have anything for you until you have proved yourself worthy of carrying mithril items, by getting the mithril shield.",
				null);


		// player fixed the machine but hadn't got mithril shield at time or didn't ask to hear more about the cloak.
		// when they have got it and return to ask for quest she offers the cloak
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
								 new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot()),
								 new OrCondition(new QuestInStateCondition(mithrilcloak.getQuestSlot(), "need_mithril_shield"),
												 new QuestInStateCondition(mithrilcloak.getQuestSlot(), "fixed_machine"))
								 ),
				ConversationStates.QUEST_2_OFFERED,
				"Congratulations, you completed the quest for the mithril shield! Now, I have another quest for you, do you want to hear it?",
				null);

		npc.addReply(Arrays.asList("oil", "can of oil", "can"), "The only oil I have ever had is very fishy smelling. I expect a fisherman made it.");
		npc.addReply("bobbin", "Only dwarf smiths make bobbins, no-one else has nimble enough fingers. Try #Alrak.");
		npc.addReply("Alrak", "I thought you kids all knew Alrak, the only dwarf that kobolds have ever liked. Or maybe he's the only dwarf to ever like kobolds, I've never been sure which ...");
		npc.addReply(Arrays.asList("leather armor", "suit of leather armor", "suit"), "Yes, well, it needs a piece of leather for the mechanism, so I can cut a piece from that.");
	}


	private void fixMachineStep() {

		final SpeakerNPC npc = npcs.get("Ida");

		// player returns who has agreed to help fix machine and prompts ida
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("sewing", "machine", "sewing machine", "task", "quest"),
				new QuestStateStartsWithCondition(mithrilcloak.getQuestSlot(), "machine"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"My sewing machine is still broken, did you bring anything to fix it?",
				null);

			// we stored the needed part name as part of the quest slot
			npc.add(ConversationStates.QUEST_ITEM_QUESTION,
					ConversationPhrases.YES_MESSAGES,
					new PlayerHasRecordedItemWithHimCondition(mithrilcloak.getQuestSlot(),1),
					ConversationStates.QUEST_2_OFFERED,
					"Thank you so much! Listen, I must repay the favour, and I have a wonderful idea. Do you want to hear more?",
					new MultipleActions(new DropRecordedItemAction(mithrilcloak.getQuestSlot(),1),
							new SetQuestAction(mithrilcloak.getQuestSlot(), "fixed_machine"),
							new IncreaseXPAction(100)));

			// we stored the needed part name as part of the quest slot
			npc.add(ConversationStates.QUEST_ITEM_QUESTION,
					ConversationPhrases.YES_MESSAGES,
					new NotCondition(new PlayerHasRecordedItemWithHimCondition(mithrilcloak.getQuestSlot(),1)),
					ConversationStates.ATTENDING,
					null,
					new SayRequiredItemAction(mithrilcloak.getQuestSlot(),1,"No, you don't have [the item] I need. What a shame."));


			// player doesn't have the item to fix machine yet
		   npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				   ConversationPhrases.NO_MESSAGES,
				   null,
				   ConversationStates.ATTENDING,
				   null,
				   new SayRequiredItemAction(mithrilcloak.getQuestSlot(),1,"Ok, well if there's anything else I can help you with just say. Don't forget to bring [the item] next time though!"));

			final String startMessage = "I will make you the most amazing cloak of mithril. "
					+ "You just need to get me the fabric and any tools I need! First please bring me a couple yards of "
					+ mithrilcloak.getFabricName() + ". The expert on fabrics is the wizard #Kampusch.";

		   //offer cloak
		   npc.add(ConversationStates.QUEST_2_OFFERED,
				   ConversationPhrases.YES_MESSAGES,
				   new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot()),
				   ConversationStates.ATTENDING,
				   startMessage,
				   new SetQuestAction(mithrilcloak.getQuestSlot(), "need_fabric"));


			// player asks for quest but they haven't completed mithril shield quest
			npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
								 new NotCondition(new QuestCompletedCondition(mithrilcloak.getShieldQuestSlot())),
								 new QuestStartedCondition(mithrilcloak.getShieldQuestSlot())
								 ),
				ConversationStates.ATTENDING,
				"Oh, I see you are already on a quest to obtain a mithril shield. You see, I was going to offer you a mithril cloak. But you should finish that first. Come back when you've finished the mithril shield quest and we will speak again.",
				new SetQuestAction(mithrilcloak.getQuestSlot(), "need_mithril_shield"));

			// player asks for quest but they haven't completed mithril shield quest
			npc.add(ConversationStates.QUEST_2_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new QuestNotStartedCondition(mithrilcloak.getShieldQuestSlot()),
					ConversationStates.ATTENDING,
					"There are legends of a wizard called Baldemar, in the famous underground magic city, who will forge a mithril shield for those who bring him what it needs. You should meet him and do what he asks. Once you have completed that quest, come back here and speak with me again. I will have another quest for you.",
					new SetQuestAction(mithrilcloak.getQuestSlot(), "need_mithril_shield"));

			// player refused to hear more about another quest after fixing machine
			npc.add(ConversationStates.QUEST_2_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Ok then obviously you don't need any mithril items! Forgive me for offering to help...!",
					null);

			// where to find wizard
			npc.addReply("Kampusch", "He is obsessed with antiques so look for him in an antiques shop or a museum.");


			// resetting quest to earlier state

			// allows player to reset quest state to "need_fabric" in case they are unable to finish
			final ChatCondition canResetCondition = new AndCondition(
					new QuestActiveCondition(mithrilcloak.getQuestSlot()),
					new NotCondition(new OrCondition(
							new QuestInStateCondition(mithrilcloak.getQuestSlot(), 0, "machine"),
							new QuestInStateCondition(mithrilcloak.getQuestSlot(), 0, "fixed_machine"),
							//new QuestInStateCondition(mithrilcloak.getQuestSlot(), 0, "need_fabric"),
							new QuestInStateCondition(mithrilcloak.getQuestSlot(), 0, "need_mithril_shield"))));

			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					canResetCondition,
					ConversationStates.ATTENDING,
					"You can ask to #restart the quest if you are stuck getting the items for the mithril cloak.",
					null);

			// player is unable to finish quest & needs to restart
			npc.add(ConversationStates.ATTENDING,
					Arrays.asList("restart", "reset"),
					canResetCondition,
					ConversationStates.RESTART_OFFERED,
					"Are you sure you want to start over?",
					null);

			// allow player to restart in the cases that they have already had the thread or fabric made but have not finished quest
			npc.add(ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new AndCondition(
							canResetCondition,
							new NotCondition(new PlayerHasItemWithHimCondition("silk thread", 40))),
					ConversationStates.ATTENDING,
					startMessage,
					new SetQuestAction(mithrilcloak.getQuestSlot(), "need_fabric"));

			// player wants to reset & already has silk thread
			npc.add(ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new AndCondition(
							canResetCondition,
							new PlayerHasItemWithHimCondition("silk thread", 40)),
					ConversationStates.ATTENDING,
					startMessage,
					new SetQuestAction(mithrilcloak.getQuestSlot(), "got_thread"));

			// player wants to reset & already has mithril thread
			npc.add(ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new AndCondition(
							canResetCondition,
							new PlayerHasItemWithHimCondition("mithril thread", 40)),
					ConversationStates.ATTENDING,
					startMessage,
					new SetQuestAction(mithrilcloak.getQuestSlot(), "got_mithril_thread"));

			// player wants to reset & already has mithril fabric
			npc.add(ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new AndCondition(
							canResetCondition,
							new PlayerHasItemWithHimCondition("mithril fabric")),
					ConversationStates.ATTENDING,
					startMessage,
					new SetQuestAction(mithrilcloak.getQuestSlot(), "got_fabric"));

			npc.add(ConversationStates.RESTART_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					canResetCondition,
					ConversationStates.ATTENDING,
					"Okay.",
					null);
	}

	public void addToWorld() {
		offerQuestStep();
		fixMachineStep();
	}

}
