/***************************************************************************
 *                   Copyright (C) 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;

public class RingMakerStage extends AVRStage {
	private final SpeakerNPC ringmaker;

	// time required to fuse the ring
	private static final int FUSE_TIME_DAYS = 3;
	private static final int FUSE_TIME = MathHelper.MINUTES_IN_ONE_DAY * FUSE_TIME_DAYS;

	public static final String QUEST_STATE_NAME = "fusing";

	private final List<String> keywords = Arrays.asList(
			"Jameson", "apothecary", "antivenom", "antivenom ring", "fuse", QUEST_STATE_NAME
		);

	// fee required to fuse the ring
	private static final int FEE = 1000;

	public RingMakerStage(final String npcName, final String questName) {
		super(questName);

		ringmaker = SingletonRepository.getNPCList().get(npcName);
	}

	@Override
	public void addToWorld() {
		addRequestFuseDialogue();
		addFusingDialogue();
	}

	private void addRequestFuseDialogue() {
		// player asks for ring to be fused but does not have items
		ringmaker.add(
			ConversationStates.ATTENDING,
			keywords,
			new AndCondition(
				new QuestInStateCondition(questName, "ringmaker"),
				new NotCondition(new AndCondition(
					new PlayerHasItemWithHimCondition("antivenom"),
					new PlayerHasItemWithHimCondition("medicinal ring"),
					new PlayerHasItemWithHimCondition("money", FEE)
				))
			),
			ConversationStates.ATTENDING,
			"I can make a stronger ring for you that resists poison, but I need you to bring me antivenom and a medicinal ring."
			+ " I also require a fee of 1000 money.",
			null);

		/* player has items
		 *
		 * Checked other quests related to Ognir. Using QUESTION_1 state appears to be safe to use here.
		 */
		ringmaker.add(
			ConversationStates.ATTENDING,
			keywords,
			new AndCondition(
				new QuestInStateCondition(questName, "ringmaker"),
				new PlayerHasItemWithHimCondition("antivenom"),
				new PlayerHasItemWithHimCondition("medicinal ring"),
				new PlayerHasItemWithHimCondition("money", FEE)
				),
			ConversationStates.QUESTION_1,
			"I can make your medicinal ring stronger, but I need a vial of antivenom."
			+ " I also require a fee of 1000 money. Do you want to pay that price?",
			null);

		ringmaker.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new QuestInStateCondition(questName, "ringmaker"),
			ConversationStates.IDLE,
			"I will get to work immediately infusing your ring with the antivenom. Please come back in "
			+ Integer.toString(FUSE_TIME_DAYS) + " days. And be sure to ask for your #'antivenom ring'.",
			new MultipleActions(
				new DropItemAction("antivenom"),
				new DropItemAction("medicinal ring"),
				new DropItemAction("money", 1000),
				new SetQuestAction(questName, QUEST_STATE_NAME + ";" + Long.toString(System.currentTimeMillis()))
			));

		ringmaker.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			new QuestInStateCondition(questName, "ringmaker"),
			ConversationStates.ATTENDING,
			"Okay. Let me know if you change your mind.",
			null);
	}

	private void addFusingDialogue() {
		// player returns before ring is ready
		ringmaker.add(
			ConversationStates.ATTENDING,
			keywords,
			new AndCondition(
				new QuestInStateCondition(questName, 0, QUEST_STATE_NAME),
				new NotCondition(new TimePassedCondition(questName, 1, FUSE_TIME))
			),
			ConversationStates.ATTENDING,
			null,
			new SayTimeRemainingAction(questName, 1, FUSE_TIME, "Your antivenom ring is not ready. Please come back in"));

		// ring is ready
		ringmaker.add(
			ConversationStates.ATTENDING,
			keywords,
			new AndCondition(
					new QuestInStateCondition(questName, 0, QUEST_STATE_NAME),
					new TimePassedCondition(questName, 1, FUSE_TIME)
				),
			ConversationStates.IDLE,
			"Your antivenom ring is ready.",
			new MultipleActions(
				new EquipItemAction("antivenom ring", 1, true),
				new IncreaseXPAction(2000),
				new SetQuestAndModifyKarmaAction(questName, "done", 150.0)
			));
	}
}
