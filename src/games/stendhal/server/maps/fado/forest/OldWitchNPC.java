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
package games.stendhal.server.maps.fado.forest;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Creates Mircea, an old witch who lives in Imorgens house, Fado forest.
 *
 * @author Vanessa Julius
 */
public class OldWitchNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Mircea") {

			@Override
			protected void createPath() {
				setPath(null);

			}


			@Override
			protected void createDialog() {
				addGreeting("Hello *Cough*");
				addJob("I was once a powerful witch. But I took a great risk and lost my powers, fighting against some #werewolves.");
				addReply("werewolves", "They are really dangerous! I met some on the way to Kikareukin earlier and have been deeply poisoned. That is why I have to rest here.");
				addQuest("Oh dear, you seem to be really nice and trustworthy, but I can't give you any work at the moment.");
				addHelp("Maybe my daughter #Imorgen outside can help you. She knows lots of people around Faiumoni.");
				addReply("Imorgen", "Hopefully she'll become a powerful witch like me in the future. But she still has to learn a lot.");
				addOffer("I would like to sell you one of my famous drinks but I can't *cough*.");
				addGoodbye("I hope we'll meet again soon *sigh*.");
			}
		};

		npc.setEntityClass("oldwitchnpc");
		npc.setPosition(12, 4);
		npc.initHP(100);
		npc.setDescription("You see Mircea. She is an old thin witch, who seems to become weaker each second.");
		zone.add(npc);
	}
}
