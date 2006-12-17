package games.stendhal.client.scripting;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.common.MathHelper;

import java.awt.Color;
import java.io.FileNotFoundException;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Parses the input in the chat box and invokes the appropriateaction.
 */
public class ChatLineParser {
	private static Logger logger = Logger.getLogger(ChatLineParser.class);
	private static ChatLineParser instance = null;
	private StendhalClient client = null;
	private String lastPlayerTell;
	private ScriptRecorder recorder = null;

	
	private ChatLineParser() {
		// hide constructor (Singleton)
		client = StendhalClient.get();
	}

	/**
	 * returns the ChatLineParser
	 *
	 * @return ChatLineParser
	 */
	public static synchronized ChatLineParser get() {
		if (instance == null) {
			instance = new ChatLineParser();
		}
		return instance;
	}

	/**
	 * parses a chat/command line and processes the result
	 *
	 * @param input string to handle
	 */
	public void parseAndHandle(String input) {

		// get line
		String text = input.trim();
		if (text.length() == 0) {
			return;
		}

		// record it (if recording)
		if (recorder != null) {
			recorder.recordChatLine(text);
		}
		
		if (text.charAt(0) != '/') {
			// Chat command. The most frequent one.
			RPAction chat = new RPAction();
			chat.put("type", "chat");
			chat.put("text", text);
			client.send(chat);
		} else {
			if (text.startsWith("//")) {
				if (lastPlayerTell != null) {
					String[] command = parseString(text, 2);
					if (command != null) {
						RPAction tell = new RPAction();
						tell.put("type", "tell");
						tell.put("target", lastPlayerTell);
						tell.put("text", command[1]);
						client.send(tell);
					}
				}
			} else if (text.startsWith("/tell ") || text.startsWith("/msg ")) // Tell
																				// command
			{
				String[] command = parseString(text, 3);
				if (command != null) {
					RPAction tell = new RPAction();
					tell.put("type", "tell");
					lastPlayerTell = command[1];
					tell.put("target", command[1]);
					tell.put("text", command[2]);
					client.send(tell);
				}
			} else if (text.startsWith("/support ")) // Support command
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction tell = new RPAction();
					tell.put("type", "support");
					tell.put("text", command[1]);
					client.send(tell);
				}
			} else if (text.startsWith("/supporta ")
					|| text.startsWith("/supportanswer ")) {
				String[] command = parseString(text, 3);
				if (command != null) {
					RPAction tell = new RPAction();
					tell.put("type", "supportanswer");
					tell.put("target", command[1]);
					tell.put("text", command[2]);
					client.send(tell);
				}
			} else if (text.startsWith("/where ")) {
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction where = new RPAction();
					where.put("type", "where");
					where.put("target", command[1]);
					client.send(where);
				}
			}

			else if (text.equals("/who")) // Who command
			{
				RPAction who = new RPAction();
				who.put("type", "who");
				client.send(who);
			} else if (text.startsWith("/drop ")) // Drop command
			{
				String[] command = parseString(text, 3);
				if (command != null) {
					String itemName = command[2];
					int quantity;

					try {
						quantity = Integer.parseInt(command[1]);
					} catch (NumberFormatException ex) {
						client.addEventLine("Invalid quantity");
						return;
					}
					RPObject player = client.getPlayer();
					int itemID = -1;
					for (RPObject item : player.getSlot("bag")) {
						if (item.get("name").equals(itemName)) {
							itemID = item.getID().getObjectID();
							break;
						}
					}
					if (itemID != -1) {
						RPAction drop = new RPAction();
						drop.put("type", "drop");
						drop.put("baseobject", player.getID().getObjectID());
						drop.put("baseslot", "bag");
						drop.put("x", player.getInt("x"));
						drop.put("y", player.getInt("y") + 1);
						drop.put("quantity", quantity);
						drop.put("baseitem", itemID);
						client.send(drop);
					} else {
						client.addEventLine("You don't have any " + itemName,
								Color.black);
					}
				}
			} else if (text.startsWith("/add ")) // Add a new buddy to buddy
													// list
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction add = new RPAction();
					add.put("type", "addbuddy");
					add.put("target", command[1]);
					client.send(add);
				}
			} else if (text.startsWith("/remove ")) // Removes a existing buddy
													// from buddy list
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction remove = new RPAction();
					remove.put("type", "removebuddy");
					remove.put("target", command[1]);
					client.send(remove);
				}
			} else if (text.startsWith("/tellall ")) // Tell everybody admin
														// command
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction tellall = new RPAction();
					tellall.put("type", "tellall");
					tellall.put("text", command[1]);
					client.send(tellall);
				}
			} else if (text.startsWith("/teleport ")) // Teleport
														// target(PLAYER NAME)
														// to zone-x,y
			{
				String[] command = parseString(text, 5);
				if (command != null) {
					RPAction teleport = new RPAction();
					teleport.put("type", "teleport");
					teleport.put("target", command[1]);
					teleport.put("zone", command[2]);
					teleport.put("x", command[3]);
					teleport.put("y", command[4]);
					client.send(teleport);
				}
			} else if (text.startsWith("/teleportto ")) // TeleportTo
														// target(PLAYER NAME)
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction teleport = new RPAction();
					teleport.put("type", "teleportto");
					teleport.put("target", command[1]);
					client.send(teleport);
				}
			} else if (text.startsWith("/adminlevel ")) // Display or adjust
														// adminlevel
			{
				String[] command = parseString(text, 3);

				if (command != null) {
					RPAction adminlevel = new RPAction();
					adminlevel.put("type", "adminlevel");
					adminlevel.put("target", command[1]);
					if (!command[2].trim().equals("")) {
						adminlevel.put("newlevel", command[2]);
					}
					client.send(adminlevel);
				}
			}

			else if (text.startsWith("/alter ")) // Set/Add/Substract
													// target(PLAYER NAME)
													// attribute
			{
				String[] command = parseString(text, 5);
				if (command != null) {
					RPAction alter = new RPAction();
					alter.put("type", "alter");
					alter.put("target", command[1]);
					alter.put("stat", command[2]);
					alter.put("mode", command[3]);
					alter.put("value", command[4]);
					client.send(alter);
				}
			} else if (text.startsWith("/summon ")) // Summon a creature at x,y
			{
				String[] command = parseString(text, 4);
				if (command != null) {
					RPAction summon = new RPAction();
					summon.put("type", "summon");
					summon.put("creature", command[1]);
					summon.put("x", command[2]);
					summon.put("y", command[3]);
					client.send(summon);
				}
			} else if (text.startsWith("/summonat ")) // Summon an item in a
														// slot
			{
				String[] command = parseString(text, 5);
				if (command != null) {
					RPAction summon = new RPAction();
					summon.put("type", "summonat");
					summon.put("target", command[1]);
					summon.put("slot", command[2]);
					summon.put("item", command[3]);
					if (!command[4].trim().equals("")) {
						summon.put("amount", command[4]);
					}
					client.send(summon);
				}
			} else if (text.startsWith("/inspect ")) // Returns a complete
														// description of the
														// target
			{
				String[] command = parseString(text, 2);
				if (command != null) {
					RPAction add = new RPAction();
					add.put("type", "inspect");
					add.put("target", command[1]);
					client.send(add);
				}
			}

			else if (text.startsWith("/jail ")) // Returns a complete
												// description of the target
			{
				String[] command = parseString(text, 4);
				if (command != null) {
					RPAction add = new RPAction();
					add.put("type", "jail");
					add.put("target", command[1]);
					add.put("minutes", command[2]);
					add.put("reason", command[3]);
					client.send(add);
				}
			}

			else if (text.startsWith("/quit")) {
				client.getGameGUI().showQuitDialog();
			}

			else if (text.startsWith("/invisible")) // Makes admin invisible for
													// creatures
			{
				RPAction invisible = new RPAction();
				invisible.put("type", "invisible");
				client.send(invisible);
			} else if (text.equals("/help")) // Help command
			{
				String[] lines = {
						"For a detailed reference, visit #http://arianne.sourceforge.net/wiki/index.php/StendhalManual",
						"Here are the most-used commands:",
						"- /tell <player> <message> \tSends a private message to <player>",
						"- /msg <player> <message> \tSends a private message to <player>",
						"- // <message> \t\tSends a private message to the last player you sent a message to",
						"- /support <message> \tAsk an administrator for help.",
						"- /who \t\tList all players currently online",
						"- /drop <quantity> <item>\tDrop a certain number of an item",
						"- /add <player> \t\tAdd <player> to your buddy list",
						"- /remove <player> \tRemove <player> from your buddy list",
						"- /where <player> \t\tShow the current location of <player>",
						"- /quit \t\tLeave the game. You will continue where you left off upon your return",
						"- /sound volume <value> \tSet volume to a value from 0 to 100",
						"- /sound mute <on|off> \tMute or unmute the sounds" };
				for (String line : lines) {
					StendhalClient.get().addEventLine(line, Color.gray);
				}
			} else if (text.equals("/gmhelp")) // Help command
			{
				String[] lines = {
						"For a detailed reference, visit #http://arianne.sourceforge.net/wiki/index.php?title=Stendhal:Administration",
						"Here are the most-used GM commands:",
						"- /adminlevel <player> [<newlevel>] \t\tDisplay or set the adminlevel of the specified <player>",
						"- /tellall <message> \t\tSend a private message to all logged-in players",
						"- /jail <player> <minutes> <reason>\t\tImprisons the player for a given length of time",
						"- /script <scriptname> \t\tLoad (or reload) a script on the server",
						"- /teleport <player> <zone> <x> <y> \tTeleport the specified <player> to the given location",
						"- /teleportto <player> \t\tTeleport yourself near the specified player",
						"- /ghostmode \t\t\t Makes yourself invisible and intangible",
						"- /alter <player> <attrib> <mode> <value> \tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, or SET. See /gmhelp_alter for details",
						"- /summon <creature|item> <x> <y> \tSummon the specified item or creature at co-ordinates <x>, <y> in the current zone",
						"- /summonat <player> <slot> <item> <amount> Summon the specified item into the specified slot of <player>; <amount> defaults to 1 if not specified",
						"- /invisible \t\t\tToggles whether or not you are invisible to creatures",
						"- /inspect <player> \t\t\tShow complete details of <player>",
						"- /destroy <entity> \t\t\tDestroy an entity completely" };
				for (String line : lines) {
					StendhalClient.get().addEventLine(line, Color.gray);
				}
			}

			else if (text.equals("/gmhelp_alter")) // Help command
			{
				String[] lines = {
						"/alter <player> <attrib> <mode> <value> \tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, or SET",
						"Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
						"When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
						"For example: #/alter #testplayer #outfit #set #12109901",
						"This will make <testplayer> look like danter" };
				for (String line : lines) {
					StendhalClient.get().addEventLine(line, Color.gray);
				}
			}

			else if (text.startsWith("/sound ")) // Sound Setup command
			{
				String[] command = parseString(text, 3);
				if (command != null) {
					if (command[1].equals("mute")) {
						String param = command[2];
						WtWindowManager.getInstance().setProperty("sound.mute", param);
						boolean mute = param.indexOf("on") != -1;
						SoundSystem.get().setMute(mute);
					}

					if (command[1].equals("volume")) {
						int vol = MathHelper.parseInt_default(command[2], -1);
						if ((vol < 0) || (vol > 100)) {
							client.addEventLine("volume must be an integer between 0 and 100");
							return;
						}
						WtWindowManager.getInstance().setProperty("sound.volume", Integer.toString(vol));
						SoundSystem.get().setVolume(vol);
					}
				}
			} else if (text.startsWith("/record")) {
				if (recorder != null) {
					recorder.end();
					recorder = null;
				}
				String[] command = parseString(text, 2);
				if (!command[1].equals("stop")) {
					try {
						recorder = new ScriptRecorder(command[1]);
						recorder.start();
					} catch (FileNotFoundException e) {
						logger.error(e, e);
					}
				}
			}

			// unhandled /command, may be a ServerExtension command
			else if (text.startsWith("/")) {
				boolean hasTarget = text.indexOf(" ") > 0;
				int parms = 2;
				if (hasTarget) {
					parms = 3;
				}
				String[] command = parseString(text, parms);
				if (command != null) {
					RPAction extension = new RPAction();
					extension.put("type", command[0].substring(1));
					if (hasTarget) {
						extension.put("target", command[1]);
						extension.put("args", command[2]);
					}
					client.send(extension);
				}
			}

		}
	}

	private static String[] parseString(String string, int nbPart) {
		String[] res = new String[nbPart];
		String[] t;
		int i;
		String s = string.trim();
		for (i = 0; i < nbPart - 1; i++) {
			t = nextString(s);
			if (t == null) {
				return null;
			}
			res[i] = t[1];
			s = t[0].trim();
		}
		res[i] = s;
		return res;
	}

	private static String[] nextString(String from) {
		char[] cFrom = from.toCharArray();
		String[] res = new String[2];
		res[0] = "";
		res[1] = "";
		int quote = 0;
		char sep = ' ';
		int i = 0;
		if (cFrom[0] == '\'') {
			quote = 1;
		}
		if (cFrom[0] == '"') {
			quote = 2;
		}
		if (quote != 0) {
			i++;
			sep = cFrom[0];
		}
		for (; i < cFrom.length; i++) {
			switch (quote) {
			case 0:
			case 1:
				if (cFrom[i] == sep) {
					res[0] = from.substring(i + 1);
					return res;
				}
				res[1] += cFrom[i];
				break;

			case 2:
				if (cFrom[i] == '"') {
					res[0] = from.substring(i + 1);
					return res;
				} else {
					i++;
					if (i == cFrom.length) {
						return null;
					}

					res[1] += cFrom[i];
				}
				break;
			}
		}
		if (quote == 0) {
			return res;
		}
		return null;
	}
}
