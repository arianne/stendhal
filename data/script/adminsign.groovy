/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import marauroa.common.game.*;

class AdminSignManager {
	private StendhalGroovyScript game = null;
	private Map storage = null;
	
	public AdminSignManager(StendhalGroovyScript game, Map storage) {
		this.game = game;
		this.storage = storage;
	}
	
	public void add(Player player, String[] args) {
		if (args.length >= 3) {

			// parse parameters
			String myZone = args[0];
			if (myZone == "-") {
				game.setZone(game.getZone(player));
			} else {
				game.setZone(myZone);
			}
			int x = 0;
			if (args[1] == "-") {
				x = player.getx() + 1;
			} else {
				x = Integer.parseInt(args[1]);
			}
			int y = 0;
			if (args[2] == "-") {
				y = player.gety();
			} else {
				y = Integer.parseInt(args[2]);
			}

			Sign sign=new Sign();
			sign.setx(x);
			sign.sety(y);

			// concat text ignoring first 3 args
			// (is there no better way to do that in Groovy?)
			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (String temp : args) {
				if (i >= 3) {
					sb.append(args[i] + " ");
				}
				i++;
			}
			sign.setText(sb.toString().trim());

			// add sign to game 
			game.add(sign);

			// put it into our storage for later "list" or "del" commands
			int signcounter = storage.get("signcounter");
			if (signcounter == null) {
				signcounter = 0;
			}
			signcounter++;
			storage.put(signcounter, sign);
			storage.put("signcounter", signcounter);
		} else {
			// syntax error, print help text
			game.privateText(player, "Syntax: adminsign.groovy <zone> <x> <y> <text> The first 3 parameters can be \"-\".\r\nadminsign.groovy list\r\nadminsign.groovy del <n>");
		}
	}
	
	public void delete(String[] args) {
		int i = Integer.parseInt(args[1]);
		Sign sign = storage.get(i);
		if (sign != null) {
			storage.remove(i);
			game.remove(sign);
			// TODO put above display-code in a own method and invoke it here
			game.privateText(player, "Sign removed");
		} else {
			game.privateText(player, "Sign " + i + " does not exist");
		}
	}
	
	public void list(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing signs:");
		int signcounter = storage.get("signcounter");
		if (signcounter == null) {
			signcounter = 0;
		}
		int i = 1;
		while (i <= signcounter) {
			Sign sign = storage.get(i);
			if (sign != null) {
				sb.append("\r\n");
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
			}
			i++;
		}
		game.privateText(player, sb.toString());
	}
	
	public void doit(Player player, String[] args) {

		// read zone and x,y. Use player's data as default on "-".
		String temp = args[0];
		if (temp.equals("list")) {
			list(args);
		} else if (temp.equals("del")) {
			del(args);
		} else {
			add(player, args);
		}
	}
}

if (player != null) {
	AdminSignManager asm = new AdminSignManager(game, storage);
	asm.doit(player, args);
}
