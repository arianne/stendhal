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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Xaruhwaiyz, the demon lord
 *
 * see games.stendhal.server.maps.quests.WizardMalleusPlainQuest
 */
public class RedDemonLordNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDemonlord(zone);
	}

	private void buildDemonlord(final StendhalRPZone zone) {
		final SpeakerNPC demonlord = new SpeakerNPC("Xaruhwaiyz") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("HUMAN! Who dares to enter my throne room?!");
				addGoodbye("Flee, human!");

			} //remaining behaviour defined in maps.quests.WizardMalleusPlainQuest
		};

		demonlord.setDescription("You see Xaruhwaiyz the demon lord");
		demonlord.setEntityClass("reddemonnpc");
		demonlord.setPosition(15, 4);
		demonlord.initHP(100);
		zone.add(demonlord);
	}
}
