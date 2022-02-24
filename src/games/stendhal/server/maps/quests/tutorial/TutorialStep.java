/***************************************************************************
 *                  Copyright (C) 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.tutorial;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
//import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


public abstract class TutorialStep {

	private static final Logger logger = Logger.getLogger(TutorialStep.class);

	protected static String SLOT;
	protected static String tutorBasename;

	protected static final String ST_RULES = "rules";
	protected static final String ST_NPCS = "npcs";
	protected static final String ST_CHAT = "chat";
	protected static final String ST_ITEMS = "items";
	protected static final String ST_COMBAT = "combat";
	protected static final String ST_QUESTS = "quests";
	protected static final String ST_PETS = "pets";
	protected static final String ST_FINAL = "final";


	public abstract void init(final String pname);

	public static void setAttributes(final String slot, final String basename) {
		SLOT = slot;
		tutorBasename = basename;
	}

	/*
	public static boolean playerIsNew(final Player player) {
		return false;
	}
	*/

	/**
	 * Creates a chat action that has NPC say a phrase after a set
	 * number of turns has passed.
	 *
	 * @param delay
	 *     Number of turns to wait.
	 * @param msg
	 *     The phrase that the NPC will say.
	 * @return
	 *     New ChatAction instance.
	 */
	public static ChatAction delayMessage(final int delay, final String msg) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				SingletonRepository.getTurnNotifier().notifyInTurns(delay, new TurnListener() {
					@Override
					public void onTurnReached(final int currentTurn) {
						raiser.say(msg);
					}
				});
			}
		};
	}

	public static void dismantleIsland(final Player player) {
		final String pname = player.getName();

		final String tname = tutorBasename + "_" + pname;
		final String zname = pname + "_" + SLOT;

		if (!ActiveTutors.get().removeFromWorld(pname)) {
			logger.warn("failed to remove NPC: " + tname);
		}

		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = world.getZone(zname);

		if (zone != null) {
			world.removeZone(zone);
		}

		if (world.getZone(zname) != null) {
			logger.warn("failed to remove zone: " + zname);
		}
	}
}
