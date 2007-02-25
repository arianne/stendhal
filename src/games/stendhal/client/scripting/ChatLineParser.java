package games.stendhal.client.scripting;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Parses the input in the chat box and invokes the appropriate action.
 */
public class ChatLineParser {
	private static Logger logger = Logger.getLogger(ChatLineParser.class);
	private static ChatLineParser instance = null;
	private StendhalClient client = null;
	private String lastPlayerTell;
	private ScriptRecorder recorder = null;

	/**
	 * Set of client supported commands
	 */
	protected HashMap<String, ChatCommand>	commands;

	
	private ChatLineParser() {
		// hide constructor (Singleton)
		client = StendhalClient.get();

		commands = new HashMap<String, ChatCommand>();

		commands.put("/", new RemessageCommand());
		commands.put("tell", new MessageCommand());
		commands.put("msg", new MessageCommand());
		commands.put("support", new SupportCommand());
		commands.put("supporta", new SupportAnswerCommand());
		commands.put("supportanswer", new SupportAnswerCommand());
		commands.put("where", new WhereCommand());
		commands.put("who", new WhoCommand());
		commands.put("drop", new DropCommand());
		commands.put("add", new AddBuddyCommand());
		commands.put("remove", new RemoveBuddyCommand());
		commands.put("quit", new QuitCommand());
		commands.put("help", new HelpCommand());
		commands.put("sound", new SoundCommand());
		commands.put("record", new RecordCommand());

		commands.put("tellall", new TellAllCommand());
		commands.put("teleport", new TeleportCommand());
		commands.put("teleportto", new TeleportToCommand());
		commands.put("adminlevel", new AdminLevelCommand());
		commands.put("alter", new AlterCommand());
		commands.put("summon", new SummonCommand());
		commands.put("summonat", new SummonAtCommand());
		commands.put("inspect", new InspectCommand());
		commands.put("jail", new JailCommand());
		commands.put("invisible", new InvisibleCommand());
		commands.put("gmhelp", new GMHelpCommand());
		commands.put("gmhelp_alter", new GMHelpAlterCommand());
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
	 *
	 * @return	<code>true</code> if command was valid enough to
	 *		process, <code>false</code> otherwise.
	 */
	public boolean parseAndHandle(String input) {
		CharacterIterator	ci;
		char		quote;
		char		ch;
		String		name;
		String []	params;
		String		remainder;
		StringBuffer	sbuf;
		int		minimum;
		int		maximum;
		ChatCommand	command;
		int		i;


		// get line
		String text = input.trim();

		if (text.length() == 0) {
			return false;
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

			return true;
		}


		/*
		 * Parse command
		 */
		ci = new StringCharacterIterator(text, 1);
		ch = ci.current();


		/*
		 * Must be non-space after slash
		 */
		if(Character.isSpace(ch))
			return false;


		/*
		 * Extract command name
		 */
		if(Character.isLetterOrDigit(ch)) {
			/*
			 * Word command
			 */
			while((ch != CharacterIterator.DONE)
			 && !Character.isSpace(ch)) {
				ch = ci.next();
			}

			name = text.substring(1, ci.getIndex());
		} else {
			/*
			 * Special character command
			 */
			name = String.valueOf(ch);
			ch = ci.next();
		}


		/*
		 * Find command handler
		 */
		if((command = commands.get(name)) != null) {
			minimum = command.getMinimumParameters();
			maximum = command.getMaximumParameters();
		} else {
			/*
			 * Server extention criteria
			 */
			minimum = 0;
			maximum = 1;
		}


		/*
		 * Extract parameters
		 * (ch already set to first character)
		 */
		params = new String[maximum];

		for(i = 0; i < maximum; i++) {
			/*
			 * Skip leading spaces
			 */
			while(Character.isSpace(ch))
				ch = ci.next();

			/*
			 * EOL?
			 */
			if(ch == CharacterIterator.DONE) {
				/*
				 * Incomplete parameters?
				 */
				if(i < minimum)
					return false;

				break;
			}

			/*
			 * Grab parameter
			 */
			sbuf = new StringBuffer();
			quote = CharacterIterator.DONE;

			while(ch != CharacterIterator.DONE) {
				if(ch == quote) {
					// End of quote
					quote = CharacterIterator.DONE;
				} else if(quote != CharacterIterator.DONE) {
					// Quoted character
					sbuf.append(ch);
				} else if((ch == '"') || (ch == '\'')) {
					// Start of quote
					quote = ch;
				} else if(Character.isSpace(ch)) {
					// End of token
					break;
				} else {
					// Token character
					sbuf.append(ch);
				}

				ch = ci.next();
			}

			/*
			 * Unterminated quote?
			 */
			if(quote != CharacterIterator.DONE)
				return false;

			params[i] = sbuf.toString();
		}


		/*
		 * Remainder text
		 */
		while(Character.isSpace(ch))
			ch = ci.next();

		sbuf = new StringBuffer(ci.getEndIndex() - ci.getIndex() + 1);

		while(ch != CharacterIterator.DONE) {
			sbuf.append(ch);
			ch = ci.next();
		}

		remainder = sbuf.toString();


		/*
		 * Execute
		 */
		if(command != null) {
			return command.execute(params, remainder);
		} else {
			/*
			 * Server Extention
			 */
			RPAction extension = new RPAction();

			extension.put("type", name);

			if(params[0] != null) {
				extension.put("target", params[0]);
				extension.put("args", remainder);
			}

			client.send(extension);

			return true;
		}
	}

	//
	//

	/*
	 * Eventually move these out from inner classes, then make them
	 * dynamically configurable/loadable.
	 */

	/**
	 * A chat command.
	 */
	protected interface ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder);


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters();


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters();


// Not yet
//		/**
//		 * Display usage for this command.
//		 *
//		 * @param	command		The command usage is for.
//		 * @param	detailed	Show detailed help, otherwise
//		 *				just 1-line synopsis.
//		 */
//		public void usage(String command, boolean detailed);
	}


	/**
	 * Send a message to the last player messaged.
	 */
	protected class RemessageCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			if(lastPlayerTell == null)
				return false;

			RPAction tell = new RPAction();

			tell.put("type", "tell");
			tell.put("target", lastPlayerTell);
			tell.put("text", remainder);

			client.send(tell);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Send a message to a player.
	 */
	protected class MessageCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			lastPlayerTell = params[0];

			RPAction tell = new RPAction();

			tell.put("type", "tell");
			tell.put("target", lastPlayerTell);
			tell.put("text", remainder);

			client.send(tell);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Send a support request message.
	 */
	protected class SupportCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction tell = new RPAction();

			tell.put("type", "support");
			tell.put("text", remainder);

			client.send(tell);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Send a support response message.
	 */
	protected class SupportAnswerCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction tell = new RPAction();

			tell.put("type", "supportanswer");
			tell.put("target", params[0]);
			tell.put("text", remainder);

			client.send(tell);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Query for player position.
	 */
	protected class WhereCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction where = new RPAction();

			where.put("type", "where");
			where.put("target", params[0]);

			client.send(where);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Query for online players.
	 */
	protected class WhoCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction who = new RPAction();

			who.put("type", "who");

			client.send(who);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Drop a player item.
	 */
	protected class DropCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			int quantity;

			try {
				quantity = Integer.parseInt(params[0]);
			} catch (NumberFormatException ex) {
				client.addEventLine("Invalid quantity");
				return true;
			}

			String itemName = params[1];

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
				drop.put("baseobject",
					player.getID().getObjectID());

				drop.put("baseslot", "bag");
				drop.put("x", player.getInt("x"));
				drop.put("y", player.getInt("y") + 1);
				drop.put("quantity", quantity);
				drop.put("baseitem", itemID);

				client.send(drop);
			} else {
				client.addEventLine(
					"You don't have any " + itemName,
					Color.black);
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 2;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 2;
		}
	}


	/**
	 * Add a player to buddy list.
	 */
	protected class AddBuddyCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction add = new RPAction();

			add.put("type", "addbuddy");
			add.put("target", params[0]);

			client.send(add);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Remove a player from buddy list.
	 */
	protected class RemoveBuddyCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction remove = new RPAction();

			remove.put("type", "removebuddy");
			remove.put("target", params[0]);

			client.send(remove);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Send a message to all players.
	 */
	protected class TellAllCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if command was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction tellall = new RPAction();

			tellall.put("type", "tellall");
			tellall.put("text", remainder);

			client.send(tellall);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Teleport a player.
	 */
	protected class TeleportCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction teleport = new RPAction();

			teleport.put("type", "teleport");
			teleport.put("target", params[0]);
			teleport.put("zone", params[1]);
			teleport.put("x", params[2]);
			teleport.put("y", params[3]);

			client.send(teleport);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 4;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 4;
		}
	}


	/**
	 * Teleport player to another player's location.
	 */
	protected class TeleportToCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction teleport = new RPAction();

			teleport.put("type", "teleportto");
			teleport.put("target", params[0]);

			client.send(teleport);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Set the admin level of a player.
	 */
	protected class AdminLevelCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction adminlevel = new RPAction();

			adminlevel.put("type", "adminlevel");
			adminlevel.put("target", params[0]);

			if(params[1] != null)
				adminlevel.put("newlevel", params[1]);

			client.send(adminlevel);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 2;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Alter an entity's attributes.
	 */
	protected class AlterCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction alter = new RPAction();

			alter.put("type", "alter");
			alter.put("target", params[0]);
			alter.put("stat", params[1]);
			alter.put("mode", params[2]);
			alter.put("value", params[3]);
			client.send(alter);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 4;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 4;
		}
	}


	/**
	 * Summon an entity.
	 */
	protected class SummonCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction summon = new RPAction();

			summon.put("type", "summon");
			summon.put("creature", params[0]);

			if(params[2] != null) {
				summon.put("x", params[1]);
				summon.put("y", params[2]);
			} else if(params[1] != null) {
				return false;
			} else {
				RPObject player = client.getPlayer();

				summon.put("x", player.getInt("x"));
				summon.put("y", player.getInt("y") + 1);
			}

			client.send(summon);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 3;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Summon an item (presumably) into an entity's slot.
	 */
	protected class SummonAtCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction summon = new RPAction();

			summon.put("type", "summonat");
			summon.put("target", params[0]);
			summon.put("slot", params[1]);
			summon.put("item", params[2]);

			if(params[3] != null) 
				summon.put("amount", params[3]);

			client.send(summon);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 4;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 3;
		}
	}


	/**
	 * Inspect an entity.
	 */
	protected class InspectCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction add = new RPAction();

			add.put("type", "inspect");
			add.put("target", params[0]);

			client.send(add);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}


	/**
	 * Send a player to jail.
	 */
	protected class JailCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction add = new RPAction();

			add.put("type", "jail");
			add.put("target", params[0]);
			add.put("minutes", params[1]);
			add.put("reason", params[2]);

			client.send(add);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 3;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 3;
		}
	}


	/**
	 * Quit the client.
	 */
	protected class QuitCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			client.getGameGUI().showQuitDialog();

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Toggle between invisibility.
	 */
	protected class InvisibleCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			RPAction invisible = new RPAction();

			invisible.put("type", "invisible");

			client.send(invisible);

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Display command usage.
	 * Eventually replace this with ChatCommand.usage().
	 */
	protected class HelpCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
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
				"- /sound mute <on|off> \tMute or unmute the sounds"
			};

			for (String line : lines) {
				StendhalClient.get().addEventLine(
					line, Color.gray);
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Display command usage.
	 * Eventually replace this with ChatCommand.usage().
	 */
	protected class GMHelpCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
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
				"- /destroy <entity> \t\t\tDestroy an entity completely"
			};

			for (String line : lines) {
				StendhalClient.get().addEventLine(
					line, Color.gray);
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Display command usage.
	 * Eventually replace this with ChatCommand.usage("alter", true).
	 */
	protected class GMHelpAlterCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			String[] lines = {
				"/alter <player> <attrib> <mode> <value> \tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, or SET",
				"Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
				"When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
				"For example: #/alter #testplayer #outfit #set #12109901",
				"This will make <testplayer> look like danter"
			};

			for (String line : lines) {
				StendhalClient.get().addEventLine(
					line, Color.gray);
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 0;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 0;
		}
	}


	/**
	 * Set sound characteristics.
	 */
	protected class SoundCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			String command = params[0];


			if(command.equals("mute")) {
				String param = params[1];

				WtWindowManager.getInstance().setProperty(
					"sound.mute", param);

				SoundSystem.get().setMute(param.equals("on"));
			} else if(command.equals("volume")) {
				int vol;

				try {
					vol = Integer.parseInt(params[1]);
				} catch(NumberFormatException ex) {
					vol = -1;
				}

				if ((vol < 0) || (vol > 100)) {
					client.addEventLine("Volume must be an integer between 0 and 100");
				} else {
					WtWindowManager.getInstance().setProperty("sound.volume", Integer.toString(vol));
					SoundSystem.get().setVolume(vol);
				}
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 2;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 2;
		}
	}


	/**
	 * Enable/disable input recording.
	 */
	protected class RecordCommand implements ChatCommand {
		/**
		 * Execute a chat command.
		 *
		 * @param	params		The formal parameters.
		 * @param	remainder	Line content after parameters.
		 *
		 * @return	<code>true</code> if  was handled.
		 */
		public boolean execute(String [] params, String remainder) {
			if (recorder != null) {
				recorder.end();
				recorder = null;
			}

			String name = params[0];

			if(!name.equals("stop")) {
				try {
					recorder = new ScriptRecorder(name);
					recorder.start();
				} catch (FileNotFoundException e) {
					logger.error(e, e);
				}
			}

			return true;
		}


		/**
		 * Get the maximum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMaximumParameters() {
			return 1;
		}


		/**
		 * Get the minimum number of formal parameters.
		 *
		 * @return	The parameter count.
		 */
		public int getMinimumParameters() {
			return 1;
		}
	}
}
