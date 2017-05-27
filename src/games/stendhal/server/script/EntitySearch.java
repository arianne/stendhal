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
package games.stendhal.server.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

/**
 * Search...
 * <p>
 * /script EntitySearch.class cname &lt;creatureName &gt; Respawn points for archrat
 * [1] int_admin_playground [7] -3_orril_dungeon
 * <p>
 * /script EntitySearch.class nonrespawn Non-Respawn creatures (minus sheep)
 * balrog (350) 0_semos_plains_n 11 11 death (30) 0_semos_plains_n 27 24
 * black_death (300) 0_semos_plains_n 44 44
 * <p>
 * /script EntitySearch.class zname &lt; partialZoneName &gt; Respawn points for zone
 * names containing: 0_semos_plain Respawn points for 0_semos_plains_ne [1]
 * gnome(2) [17] rat(0) Respawn points for 0_semos_plains_n_e2 [2] snake(3) [1]
 * gnome(2) ...
 *
 */

public class EntitySearch extends ScriptImpl {

	public void findByCreatureName(final Player player, String targetName) {
		final StringBuilder res = new StringBuilder();
		final Map<String, Integer> zoneCount = new HashMap<String, Integer>();

		// check targetName

		final Creature tempc = SingletonRepository.getEntityManager().getCreature(targetName);
		if (tempc != null) {
			// get the proper case of the characters in the string
			targetName = tempc.getName();
		} else {
			sandbox.privateText(player, "Not Found");
			return;
		}

		// count for each zone
		for (final IRPZone irpzone : SingletonRepository.getRPWorld()) {
			final StendhalRPZone zone = (StendhalRPZone) irpzone;

			for (final CreatureRespawnPoint p : zone.getRespawnPointList()) {
				final Creature c = p.getPrototypeCreature();
				if (targetName.equals(c.getName())) {
					final String zoneName = zone.getName();
					if (zoneCount.containsKey(zoneName)) {
						final int tempi = zoneCount.get(zoneName) + 1;
						zoneCount.put(zoneName, tempi);
					} else {
						zoneCount.put(zoneName, 1);
					}
				}
			}
		}

		// make string
		res.append("\r\nRespawn points for " + targetName + " : ");
		for (final Map.Entry<String, Integer> e : zoneCount.entrySet()) {
			res.append("\r\n[" + e.getValue() + "]\t" + e.getKey());
		}

		sandbox.privateText(player, res.toString());

	}

	public void findNonRespawn(final Player player) {
		final StringBuilder res = new StringBuilder();

		res.append("\r\nNon-Respawn creatures (minus domestic animals):");

		for (final IRPZone irpzone : SingletonRepository.getRPWorld()) {
			final StendhalRPZone zone = (StendhalRPZone) irpzone;

			for (final RPObject rpObj : zone) {
				if (isACreatureButNoPet(rpObj)) {
					final Creature c = (Creature) rpObj;
					if (!c.isSpawned()) {
						final String zoneName = zone.getName();
						res.append("\r\n" + c.getName() + " (" + c.getLevel()
								+ ")");
						res.append("\t" + zoneName + " " + c.getX() + " "
								+ c.getY());
					}
				}
			}

		}

		sandbox.privateText(player, res.toString());
	}


	private void findPet(Player player) {
		final StringBuilder res = new StringBuilder();

		res.append("\r\nDomestic animals):");

		for (final IRPZone irpzone : SingletonRepository.getRPWorld()) {
			final StendhalRPZone zone = (StendhalRPZone) irpzone;

			for (final RPObject rpObj : zone) {
				if (rpObj instanceof DomesticAnimal) {
					final DomesticAnimal pet = (DomesticAnimal) rpObj;
					final String zoneName = zone.getName();
					res.append("\r\n" + pet.getRPClass().getName());
					res.append(" named " + pet.getTitle());
					res.append(" (" + pet.getLevel() + ")");
					res.append(" at " + zoneName + " " + pet.getX() + " " + pet.getY());
					if (pet.getOwner() != null) {
						res.append(" owned by " + pet.getOwner().getName());
					}
				}
			}

		}

		sandbox.privateText(player, res.toString());
	}


	private boolean isACreatureButNoPet(final RPObject rpObj) {
		return (rpObj instanceof Creature) && !(rpObj instanceof DomesticAnimal);
	}

	public void findByZoneName(final Player player, final String targetName) {
		final StringBuilder res = new StringBuilder();

		res.append("\r\nRespawn points for zone names containing: "
				+ targetName);
		for (final IRPZone irpzone : SingletonRepository.getRPWorld()) {
			final StendhalRPZone zone = (StendhalRPZone) irpzone;

			final String zoneName = zone.getName();
			if (zoneName.contains(targetName)) {
				// Count one zone
				final Map<String, Integer> creatureCount = new HashMap<String, Integer>();
				for (final CreatureRespawnPoint p : zone.getRespawnPointList()) {
					final Creature c = p.getPrototypeCreature();
					final String cn = c.getName() + "(" + c.getLevel() + ")";

					if (creatureCount.containsKey(cn)) {
						final int tempi = creatureCount.get(cn) + 1;
						creatureCount.put(cn, tempi);
					} else {
						creatureCount.put(cn, 1);
					}

				}
				// Output one zone
				if (!creatureCount.isEmpty()) {
					res.append("\r\nRespawn points for " + zoneName);
				}
				for (final Map.Entry<String, Integer> e : creatureCount.entrySet()) {
					res.append("\r\n[" + e.getValue() + "]\t" + e.getKey());
				}
			}
		}

		sandbox.privateText(player, res.toString());
	}

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		if ((args.size() == 2) && args.get(0).equals("cname")) {
			findByCreatureName(admin, args.get(1));
		} else if ((args.size() == 1) && args.get(0).equals("nonrespawn")) {
			findNonRespawn(admin);
		} else if ((args.size() == 1) && args.get(0).equals("pet")) {
			findPet(admin);
		} else if ((args.size() == 2) && args.get(0).equals("zname")) {
			findByZoneName(admin, args.get(1));
		} else {
			admin.sendPrivateText(
					"/script EntitySearch.class cname '<creatureName>'\n"
					+ "/script EntitySearch.class nonrespawn\n"
					+ "/script EntitySearch.class pet\n"
					+ "/script EntitySearch.class zname <partialZoneName>");
		}

	}

}
