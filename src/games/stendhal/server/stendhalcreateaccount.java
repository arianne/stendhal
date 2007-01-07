/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server;

import marauroa.common.game.*;
import marauroa.server.game.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.*;
import marauroa.common.Configuration;
import marauroa.common.Log4J;

/**
 * The stendhalcreateaccount extends the createaccount class of marauroa package
 * so that it defines the specific behaviour for an account of stendhal
 */
public class stendhalcreateaccount extends marauroa.server.createaccount {
	public static void main(String[] args) {
		Log4J.init("data/conf/stendhalcreateaccount.properties");
		Entity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();

		stendhalcreateaccount instance = new stendhalcreateaccount();
		if(instance.run(args)==Result.OK_ACCOUNT_CREATED) {
			System.exit(0);			
		} else {
			System.exit(1);
		}
			
	}

	public stendhalcreateaccount() {
		super();
	}

	public marauroa.server.createaccount.Result execute(String username, String password, String email) {
		String[] args = { "-u", username, "-p", password, "-c", username, "-e",
				email, "-i", Configuration.getConfigurationFile() };

		return run(args);
	}

	@Override
	public RPObject populatePlayerRPObject(IPlayerDatabase playerDatabase)
			throws Exception {
		RPObject object = new RPObject(RPObject.INVALID_ID);
		object.put("type", "player");
		object.put("name", get("character"));
		object.put("outfit", 0);
		object.put("base_hp", 100);
		object.put("hp", 100);
		object.put("atk", 10);
		object.put("atk_xp", 0);
		object.put("def", 10);
		object.put("def_xp", 0);
		object.put("xp", 0);

		RuleManager manager = RuleSetFactory.getRuleSet("default");

		RPSlot slot = new RPSlot("armor");
		object.addSlot(slot);
		Entity entity = manager.getEntityManager().getItem("leather_armor");
		slot.assignValidID(entity);
		slot.add(entity);

		slot = new RPSlot("rhand");
		object.addSlot(slot);
		entity = manager.getEntityManager().getItem("club");
		slot.assignValidID(entity);
		slot.add(entity);

		return object;
	}
}
