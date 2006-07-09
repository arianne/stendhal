/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path

if (player != null) {
	
	if (args.length >= 3) {

		// read zone and x,y. Use player's data as default on "-".
		myZone = args[0];
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
		game.privateText(player, "Syntax: adminsign.groovy <zone> <x> <y> <text> The first 3 parameters can be \"-\".");
	}
}
