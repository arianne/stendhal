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
package utilities;

import marauroa.common.game.RPEvent;
import games.stendhal.common.constants.Events;
import games.stendhal.server.entity.npc.SpeakerNPC;

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
	 * @param npc
	 * @return message text
	 */
	public static String getReply(SpeakerNPC npc) {
		String reply = null;
		
		for (RPEvent event : npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				reply = event.get("text");
			}
		}
		
		npc.clearEvents();
		
		return reply;
	}
}
