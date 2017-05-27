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

import java.util.List;

import games.stendhal.server.core.config.annotations.ServerModeUtil;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;

/**
 * Summons and enchants a raid creature which has some default altered values
 *
 * @author kymara
 */
public class Enchant extends ScriptImpl {
	private static final int LOW_ATK = 100;
	private static final int HIGH_HP = 3000;

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		if (args.isEmpty()) {
			admin.sendPrivateText("Usage: /script Enchant.class creature");
			return;
		}

		// extract position of admin
		final StendhalRPZone myZone = sandbox.getZone(admin);
		final int x = admin.getX();
		final int y = admin.getY();
		sandbox.setZone(myZone);

		// concatenate torn words into one
		StringBuilder sb = new StringBuilder();
		for (final String part : args) {
			sb.append(part).append(' ');
		}

		String creatureClass  = sb.toString().trim();

		final Creature tempCreature = sandbox.getCreature(creatureClass);

		if (tempCreature == null) {
			admin.sendPrivateText("No such creature");
			return;
		}

		if (tempCreature.isRare() && !ServerModeUtil.isTestServer()) {
			// Rare creatures should not be summoned even in raids
			// Require parameter -Dstendhal.testserver=junk
			admin.sendPrivateText("Rare creatures may not be summoned.");
			return;
		}

		final RaidCreature creature = new RaidCreature(tempCreature);

		// perhaps later have an option to set these, but think about how to do it correctly - dealing with spaces is hard
		// final String creaturename = args.get(1);
		// final int newatk = MathHelper.parseIntDefault(args.get(2), LOW_ATK);
		// final int newHP = MathHelper.parseIntDefault(args.get(3), HIGH_HP);
		final String creaturename = "enchanted " + creature.getTitle();

		final int newatk = LOW_ATK;
		final int newHP = HIGH_HP;
		creature.setName(creaturename);
		creature.setDescription(creature.describe() + " It has been magically enchanted, with its normal attack power converted into longer life.");
		creature.setAtk(newatk);
		creature.initHP(newHP);

		sandbox.add(creature, x, y);
	}
}
