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
package games.stendhal.server.maps.magic.school;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * Configure Magic School Cellar.
 */
public class SpidersCreatures implements ZoneConfigurator {
	private final List<String> creatures =
		Arrays.asList("spider","poisonous spider","giant spider");
	private final String QUEST_SLOT="kill_all_spiders";

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicSchoolCellarArea(zone);
	}

	protected void updatePlayerQuest(final CircumstancesOfDeath circ) {
		final String victim = circ.getVictim().getName();
		final RPEntity killer = circ.getKiller();
		Logger.getLogger(SpidersCreatures.class).debug(
				"in "+circ.getZone().getName()+
				": "+victim+
				" killed by "+killer.getName());
		// check if was killed by other animal/pet
		if(!circ.getKiller().getClass().getName().equals(Player.class.getName()) ) {
			return;
		}

		final Player player = (Player) killer;
		// check if player started his quest
		if (player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT,0).equals("started")) {
			player.setQuest(QUEST_SLOT, 1+creatures.indexOf(victim), victim);
		}
	}

	class SpidersObserver implements Observer {
		@Override
		public void update(Observable o, Object arg) {
			updatePlayerQuest((CircumstancesOfDeath) arg);
		}
	}

	protected final SpidersObserver observer = new SpidersObserver();

	private void buildMagicSchoolCellarArea(final StendhalRPZone zone) {
		for(CreatureRespawnPoint p:zone.getRespawnPointList()) {
			if(p!=null) {
				if(creatures.indexOf(p.getPrototypeCreature().getName())!=-1) {
					// it is our creature, will add observer now
					p.addObserver(observer);
				}
			}
		}
	}
}
