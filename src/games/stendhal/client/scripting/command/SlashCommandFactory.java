package games.stendhal.client.scripting.command;

import java.util.HashMap;

/**
 * Manages Slash Command Objects
 */
public class SlashCommandFactory {

	/** Set of client supported commands */
	private static HashMap<String, SlashCommand> commands;

	/**
	 * registers the available command
	 */
	public static void register() {
		commands = new HashMap<String, SlashCommand>();

//TODO:		commands.put("/", new RemessageCommand());
		commands.put("away", new AwayCommand());
//TODO:		commands.put("tell", new MessageCommand());
		commands.put("answer", new AnswerCommand());
//TODO:		commands.put("msg", new MessageCommand());
		commands.put("support", new SupportCommand());
		commands.put("supporta", new SupportAnswerCommand());
		commands.put("supportanswer", new SupportAnswerCommand());
		commands.put("where", new WhereCommand());
		commands.put("who", new WhoCommand());
		commands.put("drop", new DropCommand());
		commands.put("add", new AddBuddyCommand());
		commands.put("remove", new RemoveBuddyCommand());
		commands.put("ignore", new IgnoreCommand());
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
	 * gets the command object for the specified command name
	 *
	 * @param name name of command
	 * @return command object
	 */
	public static SlashCommand get(String name) {
		return commands.get(name);
	}
}
