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
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

class Engagement {
	private MarriageQuestInfo marriage;

	private final NPCList npcs = SingletonRepository.getNPCList();
	private SpeakerNPC nun;

	private Player groom;
	private Player bride;

	public Engagement(final MarriageQuestInfo marriage) {
		this.marriage = marriage;
	}

	private void engagementStep() {
		nun = npcs.get("Sister Benedicta");
		nun.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						if (!player.hasQuest(marriage.getQuestSlot())) {
							raiser.say("The great quest of all life is to be #married.");
						} else if (player.isQuestCompleted(marriage.getQuestSlot())) {
							raiser.say("I hope you are enjoying married life.");
						} else {
							raiser.say("Haven't you organised your wedding yet?");
						}
					}
				});

		nun.add(ConversationStates.ATTENDING,
				"married",
				null,
				ConversationStates.ATTENDING,
				"If you have a partner, you can marry them at a #wedding. Once you are married "
				+ "you can use wedding ring to be together instantly. The ring draws its power from "
				+ "the bond between the couple, and works best between #equals.",
				null);

		nun.add(ConversationStates.ATTENDING,
				"equals",
				null,
				ConversationStates.ATTENDING,
				"The #wedding ring needs time to regain its power after each use, and it needs less power to bring equals together. "
				+ "For couples of similar level the time to regain power is less than 10 minutes and can be as little as 5 minutes.",
				null);

		nun.add(ConversationStates.ATTENDING,
				"wedding",
				null,
				ConversationStates.ATTENDING,
				"You may marry here at this church. If you would like to get engaged, bring your partner "
				+ "here, then tell me you would like to #engage #name.",
				null);

		nun.add(ConversationStates.ATTENDING,
				"engage",
				null,
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
							startEngagement((SpeakerNPC) npc.getEntity(), player, brideName);
						}
					}
				});

		nun.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.QUESTION_2,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						askBrideE();
					}
				});

		nun.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"What a shame! Goodbye!",
				null);

		nun.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						finishEngagement();
					}
				});

		nun.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"What a shame! Goodbye!",
				null);
	}

	private void startEngagement(final SpeakerNPC nun, final Player player,
			final String partnerName) {
		final StendhalRPZone outsideChurchZone = nun.getZone();
		final Area inFrontOfNun = new Area(outsideChurchZone, new Rectangle(51, 52, 6, 5));
		groom = player;
		bride = SingletonRepository.getRuleProcessor().getPlayer(partnerName);

		if (!inFrontOfNun.contains(groom)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (marriage.isMarried(groom)) {
			nun.say("You are married already, "
					+ groom.getName()
					+ "! You can't marry again.");
		} else if ((bride == null) || !inFrontOfNun.contains(bride)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (bride.getName().equals(groom.getName())) {
			nun.say("You can't marry yourself!");
		} else if (marriage.isMarried(bride)) {
			nun.say("You are married already, "
					+ bride.getName()
					+ "! You can't marry again.");
		} else {
			askGroomE();
		}

	}

	private void askGroomE() {
		nun.say(groom.getName() + ", do you want to get engaged to "
				+ bride.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_1);
	}

	private void askBrideE() {
		nun.say(bride.getName() + ", do you want to get engaged to "
				+ groom.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_2);
		nun.setAttending(bride);
	}

	private void giveInvite(final Player player) {
		final StackableItem invite = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"invitation scroll");
		invite.setQuantity(4);
		// location of church
		invite.setInfoString("marriage," + player.getName());

		// perhaps change this to a hotel room where they can get dressed into
		// wedding outfits?
		// then they walk to the church?
		player.equipOrPutOnGround(invite);
	}

	private void finishEngagement() {
		// we check if each of the bride and groom are engaged, or both, and only give invites
		// if they were not already engaged.
		String additional;
		if (!marriage.isEngaged(groom)) {
			giveInvite(groom);
			if (!marriage.isEngaged(bride)) {
				giveInvite(bride);
				additional = "And here are some invitations you can give to your guests.";
			} else {
				additional = "I have given invitations for your guests to " + groom.getName() + ". " + bride.getName()
					+ ", if Ognir was already making you a ring, you will now have to go and ask him to make another.";
				}
		} else if (!marriage.isEngaged(bride)) {
			giveInvite(bride);
			additional = "I have given invitations for your guests to " + bride.getName() + ". " + groom.getName()
				+ ", if Ognir was already making you a ring, you will now have to go and ask him to make another.";
		} else {
			additional = "I have not given you more invitation scrolls, as you were both already engaged, and had "
				+ "them before. If you were having rings forged you will both need to make them again.";
		}
		nun.say("Congratulations, "
				+ groom.getName()
				+ " and "
				+ bride.getName()
				+ ", you are now engaged! Please make sure you have been to Ognir to get wedding rings made before "
				+ "you go to the church for the service. " + additional);
		// Memorize that the two engaged so that the priest knows
		groom.setQuest(marriage.getQuestSlot(), "engaged");
		bride.setQuest(marriage.getQuestSlot(), "engaged");
		// Clear the variables so that other players can become groom and bride
		// later
		groom = null;
		bride = null;
	}

	public void addToWorld() {
		engagementStep();
	}
}
