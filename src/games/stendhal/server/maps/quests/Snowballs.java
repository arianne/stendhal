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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Snowballs
 * <p>
 * PARTICIPANTS:
 * <li> Mr. Yeti, a creature in a dungeon needs help
 * <p>
 * STEPS:
 * <li> Mr. Yeti asks for some snow, and wants you to get 25 snowballs.
 * <li> You collect 25 snowballs from ice golems.
 * <li> You give the snowballs to Mr. Yeti.
 * <li> Mr. Yeti gives you 20 cod or perch.
 * <p>
 * REWARD: <li> 20 cod or perch <li> 50 XP <li> 22 karma in total (20 + 2)
 * <p>
 * REPETITIONS: <li> Unlimited, but 2 hours of waiting is
 * required between repetitions
 */

public class Snowballs extends AbstractQuest {

	private static final int REQUIRED_SNOWBALLS = 25;

	private static final int REQUIRED_MINUTES = 120;

	private static final String QUEST_SLOT = "snowballs";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT)
				&& !player.getQuest(QUEST_SLOT).equals("start")
				&& !player.getQuest(QUEST_SLOT).equals("rejected");
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)).fire(player, null, null);
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I went down into the icy caves and met Mr. Yeti.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I didn't want to help Mr. Yeti out this time and he harshly send me away...");
			return res;
		}
		res.add("Mr. Yeti asked me to collect some snowballs for him and I promised it.");
		if (player.isEquipped("snowball", REQUIRED_SNOWBALLS) || isCompleted(player)) {
			res.add("I found some snowballs after killing some ice golems.");
		}
		if (isCompleted(player)) {
			res.add("I made Mr. Yeti happy when I gave him the snowballs he wanted.");
		}
		if(isRepeatable(player)){
			res.add("Mr. Yeti needs snowballs again!");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Mr. Yeti");

		// says hi without having started quest before
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Greetings stranger! Have you seen my snow sculptures? I need a #favor from someone friendly like you.",
				null);

		// says hi - got the snow yeti asked for
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS)),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?",
				null);

		// says hi - didn't get the snow yeti asked for
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS))),
				ConversationStates.ATTENDING,
				"You're back already? Don't forget that you promised to collect a bunch of snowballs for me!",
				null);

		// says hi - quest was done before and is now repeatable
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStartedCondition(QUEST_SLOT),
						new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES)),
				ConversationStates.ATTENDING,
				"Greetings again! Have you seen my latest snow sculptures? I need a #favor again ...",
				null);

		// says hi - quest was done before and is not yet repeatable
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStartedCondition(QUEST_SLOT),
						new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, REQUIRED_MINUTES, "I have enough snow for my new sculpture. Thank you for helping! "
						+ "I might start a new one in" ));

		// asks about quest - has never started it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.",
				null);

		// asks about quest but already on it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"You already promised me to bring some snowballs! Twenty five pieces, remember ...",
				null);

		// asks about quest - has done it but it's repeatable now
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "start"), new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.",
				null);

		// asks about quest - has done it and it's too soon to do again
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "start"), new NotCondition(new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				"I have enough snow to finish my sculpture, but thanks for asking.",
				null);

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 2.0));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"So what are you doing here? Go away!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {

		final SpeakerNPC npc = npcs.get("Mr. Yeti");

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("snowball", REQUIRED_SNOWBALLS));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT));
		// player gets either cod or perch, which we don't have a standard action for
		// and the npc says the name of the reward, too
		reward.add(new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						String rewardClass;
						if (Rand.throwCoin() == 1) {
							rewardClass = "cod";
						} else {
							rewardClass = "perch";
						}
						npc.say("Thank you! Here, take some " + rewardClass + "! I do not like to eat them.");
						final StackableItem reward = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
						reward.setQuantity(20);
						player.equipOrPutOnGround(reward);
						player.addKarma(20.0);
						player.notifyWorldAboutChanges();
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS),
			ConversationStates.ATTENDING,
			null,
			new MultipleActions(reward));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS)),
			ConversationStates.ATTENDING,
			"Hey! Where did you put the snowballs?",
			null);

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh I hope you bring me them soon! I would like to finish my sculpture!",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Snowballs for Mr. Yeti",
				"The inhabitant of the icy region in Faiumoni needs your help to collect some snowballs for him.",
				false);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getName() {
		return "Snowballs";
	}

	// the djinns, ice golems and ice elementals on the way to yeti caves are quite dangerous
	@Override
	public int getMinLevel() {
		return 60;
	}

	@Override
	public String getNPCName() {
		return "Mr. Yeti";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_YETI_CAVE;
	}

}
