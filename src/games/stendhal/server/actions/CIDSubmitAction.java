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
package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.CID;
import static games.stendhal.common.constants.Actions.ID;
import games.stendhal.server.core.engine.dbcommand.LogCidCommand;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * handles CID actions.
 */
public class CIDSubmitAction implements ActionListener {
	
	//Key is ID, value contains list of names
	public static final Map<String, String> idList = new HashMap<String, String>();
	
	//Key is name, value is ID
	public static final Map<String, String> nameList = new HashMap<String, String>();
	
	
	public static void register() {
		CommandCenter.register(CID, new CIDSubmitAction());
	}

	public void onAction(final Player player, final RPAction action) {
		if (action.has(ID)) {
			
			final String cid = action.get(ID);
			final String pName = player.getName();
			
			//add to idList
			if (idList.containsKey(cid)) {
				if(!idList.get(cid).contains("," + pName + ",")) {
					final String tempid = idList.get(cid) + pName + ",";
					idList.put(cid, tempid);
				}
			} else {
				idList.put(cid, "," + pName + ",");
			}
			
			//add to nameList
			nameList.put(pName, cid);

			String address = PlayerEntryContainer.getContainer().get(player).getAddress().getHostAddress();
			DBCommandQueue.get().enqueue(new LogCidCommand(pName, address, cid));
			
		}
	}
}
