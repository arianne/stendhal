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

			// read zone and x,y. Use player's data as default on "-".
			String myZone = args[0];
			if (myZone == "-") {
				game.setZone(game.getZone(player));
			} else {
				game.setZone(myZone); // TODO seems not to work
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
			sign.setText(sb.toString().trim().replace("|", "\n"));

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
			game.privateText(player, "This script creates, lists or removes signs. Syntax: \r\nadminsign.groovy <zone> <x> <y> <text> The first 3 parameters can be \"-\".\r\nadminsign.groovy list\r\nadminsign.groovy del <n>");
		}
	}
	
	public void delete(Player player, String[] args) {
		int i = Integer.parseInt(args[1]);
		Sign sign = storage.get(i);
		if (sign != null) {
			storage.remove(i);
			game.remove(sign);
			StringBuilder sb = new StringBuilder();
			sb.append("Removed sign ");
			signToString(sb, sign);
			game.privateText(player, sb.toString());
		} else {
			game.privateText(player, "Sign " + i + " does not exist");
		}
	}
	
	private void signToString(StringBuilder sb, Sign sign) {
		String zone = sign.getWorld().getRPZone(sign.getID()).getID().getID();
		sb.append(zone);
		sb.append(" ");
		sb.append(sign.getx());
		sb.append(" ");
		sb.append(sign.gety());
		sb.append(" ");
		sb.append("\"" + sign.get("text") + "\"");
	}
	
	public void list(Player player, String[] args) {
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
				signToString(sb, sign);
			}
			i++;
		}
		game.privateText(player, sb.toString());
	}
	
	public void doit(Player player, String[] args) {
		String temp = args[0];
		if (temp.equals("list")) {
			list(player, args);
		} else if (temp.equals("del") || temp.equals("delete") || temp.equals("remove")) {
			delete(player, args);
		} else {
			add(player, args);
		}
	}
}

if (player != null) {
	AdminSignManager asm = new AdminSignManager(game, storage);
	asm.doit(player, args);
}
