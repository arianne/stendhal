/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import marauroa.common.game.*;


if (player != null) {

	// read zone and x,y. Use player's data as default on "-".
	temp = args[0];
	if (temp.equals("list")) {
		List objects = game.getCreatedRPObjects();
		int i = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("Listing signs:\r\n");
		for (RPObject object : objects) {
			if (object instanceof Sign) {
				Sign sign = (Sign) object;
				sb.append(i);
				sb.append(". ");
				String zone = sign.getWorld().getRPZone(sign.getID()).getID().getID();
				sb.append(zone);
				sb.append(" ");
				sb.append(sign.getx());
				sb.append(" ");
				sb.append(sign.gety());
				sb.append(" ");
				sb.append("\"" + sign.get("text") + "\"");
				sb.append("\r\n");
				i++;
			}
		}
		game.privateText(player, sb.toString());
	} else if (temp.equals("del")) {
		List objects = game.getCreatedRPObjects();
		int i = 1;
		int j = Integer.parseInt(args[1]);
		for (RPObject object : objects) {
			if (object instanceof Sign) {
				if (i == j) {
					game.remove(object);
					break;
				}
				i++;
			}
		}
	} else {
		if (args.length >= 3) {
			myZone = temp;
			if (myZone == "-") {
				myZone = game.getZone(player);
			}
			if (args[1] == "-") {
				x = player.getx() + 1;
			} else {
				x = Integer.parseInt(args[1]);
			}
			if (args[2] == "-") {
				y = player.gety();
			} else {
				y = Integer.parseInt(args[2]);
			}
			game.setZone(myZone);
		
			sign=new Sign();
			sign.setx(x);
			sign.sety(y);
			StringBuffer sb = new StringBuffer();
			int i = 0;
			for (String temp : args) {
				if (i >= 3) {
					sb.append(args[i] + " ");
				}
				i++;
			}
			sign.setText(sb.toString().trim());
			game.add(sign);
		} else {
			game.privateText(player, "Syntax: adminsign.groovy <zone> <x> <y> <text> The first 3 parameters can be \"-\".\r\nadminsign.groovy list\r\nadminsign.groovy del <n>");
		}
	}
}
