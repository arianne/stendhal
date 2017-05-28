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
package games.stendhal.server.maps.semos.townhall;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Creates the cadet npcs in townhall.
 *
 * @author kymara
 */
public class CadetsNPCs implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPCs(zone);
	}

	private void buildNPCs(final StendhalRPZone zone) {
		final String[] names = {"Super Trainer", "XP Hunter", "Well Rounded"};
		final String[] images = {"supertrainedguynpc", "xpphunternpc", "wellroundedguynpc"};
		final Direction[] directions = {Direction.RIGHT, Direction.UP, Direction.LEFT};
		final String[] descriptions = {"You see a soldier who rarely goes out to fight as he spends so much time training. He has stats: atk 40, def 40.", "You see a cowardly soldier who depends on others to defend against enemies, while he reaps the rewards of attacking them. He has stats: atk 20, def 20.", "You see a well rounded soldier, who is not afraid to face the attack of his enemies and thus earns some skills from them. He has stats: atk 30, def 30."};
		final int[] levels = {20, 60, 40};
		final int[] xposition = {21, 24, 26};
		final int[] yposition = {17, 18, 16};
		for (int i = 0; i < 3; i++) {
			final SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					setPath(null);
				}

				@Override
				protected void createDialog() {
					// no dialog
				}
			};
			npc.setEntityClass(images[i]);
			npc.setPosition(xposition[i], yposition[i]);
			npc.setDirection(directions[i]);
			npc.initHP(100);
			npc.setDescription(descriptions[i]);
			npc.setLevel(levels[i]);
			zone.add(npc);
		}
	}
}
