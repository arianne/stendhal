/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.constants.Events;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.game.RPEvent;

/**
 * Provides convenience methods for SpeakerNPC creation. the Created NPC extends
 * <p>
 * SpeakerNPC and overrides <code>registerTheNewNameInTheConversationParserWordList</code> to
 * avoid database access
 *
 */
public abstract class SpeakerNPCTestHelper {

	public static SpeakerNPC createSpeakerNPC() {
		return createSpeakerNPC("bob");
	}

	public static SpeakerNPC createSpeakerNPC(final String name) {
		PlayerTestHelper.generateNPCRPClasses();
		return new SpeakerNPC(name);
	}

	/**
	 * Query the events for public visible text messages.
	 *
	 * @param npc
	 * 		The entity who's events should be checked.
	 * @return
	 * 		Most recent text message.
	 */
	public static String getReply(final SpeakerNPC npc) {
		String reply = null;

		for (RPEvent event : npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				reply = event.get("text");
			}
		}

		npc.clearEvents();

		return reply;
	}

	/**
	 * Query the events for public visible text messages.
	 *
	 * @param npc
	 * 		The entity who's events should be checked.
	 * @return
	 * 		List of text messages.
	 */
	public static List<String> getReplies(final SpeakerNPC npc) {
		final List<String> replies = new ArrayList<>();

		for (final RPEvent event : npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				replies.add(event.get("text"));
			}
		}

		npc.clearEvents();

		return replies;
	}

	/**
	 * Query the events for public visible text messages in order of appearance.
	 *
	 * @param npc
	 * 		The entity who's events should be checked.
	 * @return
	 * 		List of text messages.
	 */
	public static List<String> getOrderedReplies(final SpeakerNPC npc) {
		final List<String> replies = new LinkedList<>();

		for (final RPEvent event : npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				replies.add(event.get("text"));
			}
		}

		npc.clearEvents();

		return replies;
	}
}
