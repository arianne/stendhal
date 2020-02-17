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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Plink's Toy
 * <p>
 * PARTICIPANTS: <ul><li> Plink <li> some wolves </ul>
 *
 * STEPS: <ul><li> Plink tells you that he got scared by some wolves and ran away
 * dropping his teddy. <li> Find the teddy in the Park Of Wolves <li> Bring it back to
 * Plink </ul>
 *
 * REWARD: <ul><li> a smile <li> 20 XP <li> 10 Karma </ul>
 *
 * REPETITIONS: <ul><li> None. </ul>
 */
public class PlinksToy extends AbstractQuest {

	private static final String QUEST_SLOT = "plinks_toy";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			if (player.isEquipped("teddy")) {
				res.add("I found a bear which might belong to someone else.");
			}
			return res;
		}
		res.add("I have met Plink");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to find Plink's toy bear.");
			return res;
		}
		res.add("Plink begged me to look for his teddy in a garden with lots of wolves.");
		if (player.isEquipped("teddy") || isCompleted(player)) {
			res.add("I have found Plink's toy bear");
		}
		if (isCompleted(player)) {
			res.add("I gave Plink his bear.");
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Plink");

		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotCompletedCondition(QUEST_SLOT),
					new NotCondition(new PlayerHasItemWithHimCondition("teddy"))),
			ConversationStates.QUEST_OFFERED,
			"*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?",
			null);

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, "*sniff* Thanks a lot! *smile*",
			new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.QUEST_OFFERED,
			"*sniff* But... but... PLEASE! *cries*", null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("wolf", "wolves"),
			null,
			ConversationStates.QUEST_OFFERED,
			"They came in from the plains, and now they're hanging around the #park over to the east a little ways. I'm not allowed to go near them, they're dangerous.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"park",
			null,
			ConversationStates.QUEST_OFFERED,
			"My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?",
			null);

		npc.add(ConversationStates.QUEST_OFFERED, "teddy", null,
			ConversationStates.QUEST_OFFERED,
			"Teddy is my favourite toy! Please will you bring him back?",
			null);
	}

	private void step_2() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
		final PassiveEntityRespawnPoint teddyRespawner = new PassiveEntityRespawnPoint("teddy", 1500);
		teddyRespawner.setPosition(107, 84);
		teddyRespawner.setDescription("There's a teddy-bear-shaped depression in the sand here.");
		zone.add(teddyRespawner);

		teddyRespawner.setToFullGrowth();
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Plink");

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("teddy"));
		reward.add(new IncreaseXPAction(20));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(10));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
							new OrCondition(
									new QuestNotStartedCondition(QUEST_SLOT),
									new QuestNotCompletedCondition(QUEST_SLOT)),
							new PlayerHasItemWithHimCondition("teddy")),
			ConversationStates.ATTENDING,
			"You found him! *hugs teddy* Thank you, thank you! *smile*",
			new MultipleActions(reward));

		npc.add(
			ConversationStates.ATTENDING,
			"teddy",
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("teddy"))),
			ConversationStates.ATTENDING,
			"I lost my teddy in the #park over east, where all those #wolves are hanging about.",
			null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"You found my teddy already near by the wolves! I still squeeze and hug it :)", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Plink's Toy",
				"Plink is a sweet little boy, and like many little boys, is frightened of wolves.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "PlinksToy";
	}

	@Override
	public String getNPCName() {
		return "Plink";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}

}
