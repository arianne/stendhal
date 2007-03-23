/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;
import games.stendhal.common.MathHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enables admins to create (list and remove) signs.
 *
 * @author hendrik
 */
public class AdminSign extends ScriptImpl {

	private Map<Integer, Sign> storage = new HashMap<Integer, Sign>();

	private int signcounter = 0;

	/**
	 * adds a sign
	 *
	 * @param player admin
	 * @param args zone x y text
	 */
	public void add(Player player, List<String> args) {
		if (args.size() >= 3) {

			// read zone and x,y. Use player's data as default on "-".
			String myZone = args.get(0);
			if (myZone.equals("-")) {
				sandbox.setZone(sandbox.getZone(player));
			} else {
				sandbox.setZone(myZone);
			}
			int x = 0;
			if (args.get(1).equals("-")) {
				x = player.getX();
			} else {
				x = MathHelper.parseInt(args.get(1));
			}
			int y = 0;
			if (args.get(2).equals("-")) {
				y = player.getY();
			} else {
				y = MathHelper.parseInt(args.get(2));
			}

			games.stendhal.server.entity.Sign sign = new games.stendhal.server.entity.Sign();
			sign.setX(x);
			sign.setY(y);

			// concat text ignoring first 3 args
			StringBuffer sb = new StringBuffer();
			for (int i = 3; i < args.size(); i++) {
				sb.append(args.get(i) + " ");
			}
			sign.setText(sb.toString().trim().replace("|", "\n"));

			// add sign to game 
			sandbox.add(sign);

			// put it into our storage for later "list" or "del" commands
			signcounter++;
			storage.put(new Integer(signcounter), sign);
		} else {
			// syntax error, print help text
			sandbox
			        .privateText(
			                player,
			                "This script creates, lists or removes signs. Syntax: \r\nAdminSign.class <zone> <x> <y> <text> The first 3 parameters can be \"-\".\r\nAdminSign.class list\r\nAdminSign.class del <n>");
		}
	}

	/**
	 * Removes the specified sign
	 *
	 * @param player admin
	 * @param args sign number at index 1
	 */
	public void delete(Player player, List<String> args) {
		int i;
		try {
			i = Integer.parseInt(args.get(1));
		} catch (NumberFormatException e) {
			sandbox.privateText(player, "Please specify a number");
			return;
		}
		Sign sign = storage.get(new Integer(i));
		if (sign != null) {
			storage.remove(new Integer(i));
			sandbox.remove(sign);
			StringBuilder sb = new StringBuilder();
			sb.append("Removed sign ");
			signToString(sb, sign);
			sandbox.privateText(player, sb.toString());
		} else {
			sandbox.privateText(player, "Sign " + i + " does not exist");
		}
	}

	private void signToString(StringBuilder sb, Sign sign) {
		StendhalRPWorld world = StendhalRPWorld.getInstance();
		String zone = world.getRPZone(sign.getID()).getID().getID();
		sb.append(zone);
		sb.append(" ");
		sb.append(sign.getX());
		sb.append(" ");
		sb.append(sign.getY());
		sb.append(" ");
		sb.append("\"" + sign.get("text") + "\"");
	}

	/**
	 * Lists all signs
	 *
	 * @param player admin invoking this script
	 */
	public void list(Player player) {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing signs:");

		int i = 1;
		while (i <= signcounter) {
			Sign sign = storage.get(new Integer(i));
			if (sign != null) {
				sb.append("\r\n");
				sb.append(i);
				sb.append(". ");
				signToString(sb, sign);
			}
			i++;
		}
		sandbox.privateText(player, sb.toString());
	}

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() == 0) {
			admin
			        .sendPrivateText("/script AdminSign.class add zone x y text (the first three parameters may be \"-\"\n/script AdminSign.class list\n/script AdminSign.class del <n>");
			return;
		}

		String temp = args.get(0);
		if (temp.equals("list")) {
			list(admin);
		} else if (temp.equals("del") || temp.equals("delete") || temp.equals("remove")) {
			delete(admin, args);
		} else {
			add(admin, args);
		}
	}

}
