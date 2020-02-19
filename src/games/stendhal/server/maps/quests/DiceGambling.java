/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Dice;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

public class DiceGambling extends AbstractQuest {

	private static final int STAKE = 100;

	@Override
	public String getSlotName() {
		return "dice_gambling";
	}

	@Override
	public void addToWorld() {

		final CroupierNPC ricardo = (CroupierNPC) SingletonRepository.getNPCList().get("Ricardo");

		final Map<Integer, Pair<String, String>> prizes = initPrices();

		ricardo.setPrizes(prizes);

		final StendhalRPZone zone = ricardo.getZone();

		Sign blackboard = new Sign();
		blackboard.setPosition(25, 0);
		blackboard.setEntityClass("blackboard");
		StringBuilder prizelistBuffer = new StringBuilder("PRIZES:\n");
		for (int i = 18; i >= 13; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		blackboard = new Sign();
		blackboard.setPosition(26, 0);
		blackboard.setEntityClass("blackboard");
		prizelistBuffer = new StringBuilder("PRIZES:\n");
		for (int i = 12; i >= 7; i--) {
			prizelistBuffer.append("\n" + i + ": " + prizes.get(i).first());
		}
		blackboard.setText(prizelistBuffer.toString());
		zone.add(blackboard);

		ricardo.add(ConversationStates.ATTENDING, "play", null,
				ConversationStates.QUESTION_1,
				"In order to play, you have to stake " + STAKE
						+ " gold. Do you want to pay?", null);

		ricardo.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("money", STAKE),
			ConversationStates.ATTENDING,
			"OK, here are the dice. Just throw them when you're ready. Good luck!",
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.drop("money", STAKE);
					final Dice dice = (Dice) SingletonRepository.getEntityManager()
							.getItem("dice");
					dice.setCroupierNPC((CroupierNPC) npc.getEntity());
					player.equipOrPutOnGround(dice);
				}
			});

		ricardo.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("money", STAKE)),
			ConversationStates.ATTENDING,
			"Hey! You don't have enough money!", null);

		ricardo.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Coward! How will you ever become a hero when you risk nothing?",
			null);

		fillQuestInfo(
				"Dice Gambling",
				"Try your luck at Semos Tavern's gambling table.",
				true);
	}

	private Map <Integer, Pair<String, String>> initPrices() {
		Map<Integer, Pair<String, String>> map = new HashMap<Integer, Pair<String, String>>();
		map.put(3, new Pair<String, String>("blue shield",
				"Dude, you are one unlucky guy! I feel so sorry for you! Here, take this blue shield."));
		map.put(7, new Pair<String, String>("beer",
				"That's enough for a consolation prize, a bottle of beer."));
		map.put(8, new Pair<String, String>("wine",
				"You have won this delicious glass of wine!"));
		map.put(9, new Pair<String, String>("studded shield",
				"Take this simple shield as a reward."));
		map.put(10, new Pair<String, String>("chain legs",
				"I hope you have a use for these chain legs."));
		map.put(11,	new Pair<String, String>("antidote",
			   "This antidote will serve you well when you fight against poisonous creatures."));
		map.put(12, new Pair<String, String>("sandwich",
				"You have won a tasty sandwich!"));
		map.put(13, new Pair<String, String>("cheeseydog",
				"Take this tasty cheesydog!"));
		map.put(14, new Pair<String, String>("home scroll",
		"You have won this very useful home scroll!"));
		map.put(15,	new Pair<String, String>("greater potion",
				"You have won a greater potion, but with your luck you'll probably never have to use it!"));
		map.put(16,	new Pair<String, String>("longbow",
		"You could be a formidable archer with this prize of a longbow!"));
		map.put(17,	new Pair<String, String>("red cloak",
		"You're going to look great in this fashionable red cloak!"));
		map.put(18, new Pair<String, String>("magic chain helmet",
				"You have hit the JACKPOT! A magic chain helmet!"));

		return map;
	}

	@Override
	public String getName() {
		return "DiceGambling";
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getNPCName() {
		return "Ricardo";
	}

}
