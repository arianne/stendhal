/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.grandfatherswish;

import java.util.LinkedHashMap;
import java.util.Map;

import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;


public class Myling extends Creature {
	private MylingSpawner spawner;

	public Myling(final MylingSpawner spawner) {
		super();
		this.spawner = spawner;

		setName("myling");
		setEntityClass("undead");
		setEntitySubclass("myling");
		setDescription("You see a myling. Could this be Niall!?");
		setBaseHP(100);
		setHP(10);
		setBaseSpeed(0.8);

		final Map<String, String> aiProfiles = new LinkedHashMap<String, String>();
		aiProfiles.put("patrolling", "");
		setAIProfiles(aiProfiles);
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public void onRejectedAttackStart(final RPEntity attacker) {
		if (attacker instanceof Player) {
			((Player) attacker).sendPrivateText("That's not a good idea.");
		}
	}

	@Override
	public void onDead(final Killer killer, final boolean remove) {
		super.onDead(killer, remove);
		spawner.onMylingRemoved();
	}
}
