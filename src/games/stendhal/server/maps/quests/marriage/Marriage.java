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
package games.stendhal.server.maps.quests.marriage;

import java.awt.Rectangle;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

class Marriage {
	private final NPCList npcs = SingletonRepository.getNPCList();
	private MarriageQuestInfo marriage;

	private SpeakerNPC priest;

	private Player groom;
	private Player bride;

	public Marriage(final MarriageQuestInfo marriage) {
		this.marriage = marriage;
	}

	private void marriageStep() {

		/**
		 * Creates a priest NPC who can celebrate marriages between two players.
		 *
		 * Note: in this class, the Player variables are called groom and bride.
		 * However, the game doesn't know the concept of genders. The player who
		 * initiates the wedding is just called groom, the other bride.
		 *
		 * @author daniel
		 *
		 */

		priest = npcs.get("Priest");
		priest.add(ConversationStates.ATTENDING,
					"marry",
					new ChatCondition() {
						@Override
						public boolean fire(final Player player, final Sentence sentence,
								final Entity npc) {
							return player.hasQuest(marriage.getQuestSlot())
									&& player.getQuest(marriage.getQuestSlot()).startsWith(
											"engaged")
									&& player.isEquipped("wedding ring");
						}
					},
					// TODO: make sure the pair getting married are engaged to each
					// other, if this is desired.
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {

						@Override
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							// find out whom the player wants to marry.
							final String brideName = sentence.getSubjectName();

							if (brideName == null) {
								npc.say("You have to tell me who you want to marry.");
							} else {
								startMarriage((SpeakerNPC) npc.getEntity(), player, brideName);
							}
						}
					});

		priest.add(ConversationStates.QUESTION_1,
					ConversationPhrases.YES_MESSAGES,
					null,
					ConversationStates.QUESTION_2,
					null,
					new ChatAction() {

						@Override
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							askBride();
						}
					});

		priest.add(ConversationStates.QUESTION_1,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.IDLE,
					"What a pity! Goodbye!",
					null);

		priest.add(ConversationStates.QUESTION_2,
					ConversationPhrases.YES_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {

						@Override
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							finishMarriage();
						}
					});

		priest.add(ConversationStates.QUESTION_2,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.IDLE,
					"What a pity! Goodbye!",
					null);

		// What he responds to marry if you haven't fulfilled all objectives
		// before hand
		priest.add(ConversationStates.ATTENDING,
					"marry",
					new ChatCondition() {
						@Override
						public boolean fire(final Player player, final Sentence sentence,
								final Entity npc) {
							return (!player.hasQuest(marriage.getQuestSlot())
									|| (player.hasQuest(marriage.getQuestSlot())	&& player.getQuest(marriage.getQuestSlot()).startsWith("engaged") && !player.isEquipped("wedding ring")));
						}
					},
					ConversationStates.ATTENDING,
					"You're not ready to be married yet. Come back when you are properly engaged, and bring your wedding ring. And try to remember not to leave your partner behind ....",
					null);

		// What he responds to marry if you are already married
		priest.add(ConversationStates.ATTENDING,
				"marry",
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence,
							final Entity npc) {
						return (player.isQuestCompleted(marriage.getQuestSlot()));
					}
				},
				ConversationStates.ATTENDING,
				"You're married already, so you cannot marry again.",
				null);
	}

	private void startMarriage(final SpeakerNPC priest, final Player player,
			final String partnerName) {
		final StendhalRPZone churchZone = priest.getZone();
		final Area inFrontOfAltar = new Area(churchZone, new Rectangle(10, 9, 4, 1));

		groom = player;
		bride = SingletonRepository.getRuleProcessor().getPlayer(partnerName);

		if (!inFrontOfAltar.contains(groom)) {
			priest.say("You must step in front of the altar if you want to marry.");
		} else if (marriage.isMarried(groom)) {
			priest.say("You are married already, " + groom.getName()
					+ "! You can't marry again.");
		} else if ((bride == null) || !inFrontOfAltar.contains(bride)) {
			priest.say("You must bring your partner to the altar if you want to marry.");
		} else if (bride.getName().equals(groom.getName())) {
			priest.say("You can't marry yourself!");
		} else if (marriage.isMarried(bride)) {
			priest.say("You are married already, " + bride.getName()
					+ "! You can't marry again.");
		} else if (!bride.hasQuest(marriage.getQuestSlot())) {
			priest.say(bride.getName() + " isn't engaged.");
		} else if (bride.hasQuest(marriage.getQuestSlot())
				&& !bride.getQuest(marriage.getQuestSlot()).startsWith("engaged")) {
			priest.say(bride.getName() + " isn't engaged.");
		}  else if (!bride.isEquipped("wedding ring")) {
			priest.say(bride.getName()
					+ " hasn't got a wedding ring to give you.");
		} else {
			askGroom();
		}
	}

	private void askGroom() {
		priest.say(groom.getName() + ", do you really want to marry "
				+ bride.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_1);
	}

	private void askBride() {
		priest.say(bride.getName() + ", do you really want to marry "
				+ groom.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_2);
		priest.setAttending(bride);
	}

	private void finishMarriage() {
		exchangeRings();
		priest.say("Congratulations, "
				+ groom.getName()
				+ " and "
				+ bride.getName()
				+ ", you are now married! I don't really approve of this, but if you would like a honeymoon, go ask Linda in the hotel. Just say 'honeymoon' to her and she will understand.");
		// Memorize that the two married so that they can't just marry other
		// persons
		groom.setQuest(marriage.getSpouseQuestSlot(), bride.getName());
		bride.setQuest(marriage.getSpouseQuestSlot(), groom.getName());
		groom.setQuest(marriage.getQuestSlot(), "just_married");
		bride.setQuest(marriage.getQuestSlot(), "just_married");
		// Clear the variables so that other players can become groom and bride
		// later
		groom = null;
		bride = null;
	}

	private void giveRing(final Player player, final Player partner) {
		// players bring their own golden rings
		player.drop("wedding ring");
		final Item ring = SingletonRepository.getEntityManager().getItem(
				"wedding ring");
		ring.setInfoString(partner.getName());
		ring.setBoundTo(player.getName());
		player.equipOrPutOnGround(ring);
	}

	private void exchangeRings() {
		giveRing(groom, bride);
		giveRing(bride, groom);
	}

	public void addToWorld() {
		marriageStep();
	}

}
