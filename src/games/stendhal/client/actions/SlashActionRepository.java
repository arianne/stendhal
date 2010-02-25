package games.stendhal.client.actions;

import java.util.HashMap;

/**
 * Manages Slash Action Objects.
 */
public class SlashActionRepository {

	/** Set of client supported Actions. */
	private static HashMap<String, SlashAction> actions = new HashMap<String, SlashAction>();

	/**
	 * Registers the available Action.
	 */
	public static void register() {
		final SlashAction msg = new MessageAction();
		final SlashAction supporta = new SupportAnswerAction();

		actions.put("/", new RemessageAction());
		actions.put("add", new AddBuddyAction());
		actions.put("adminlevel", new AdminLevelAction());
		actions.put("adminnote", new AdminNoteAction());
		actions.put("alter", new AlterAction());
		actions.put("altercreature", new AlterCreatureAction());
		actions.put("alterquest", new AlterQuestAction());
		actions.put("answer", new AnswerAction());
		actions.put("away", new AwayAction());
		
		actions.put("ban", new BanAction());
		
		actions.put("clear", new ClearChatLogAction());
		actions.put("crash", new CrashClientAction());
		
		actions.put("drop", new DropAction());

		actions.put("faq", new FAQAction());

		actions.put("gag", new GagAction());
		actions.put("gmhelp", new GMHelpAction());
		actions.put("grumpy", new GrumpyAction());

		actions.put("help", new HelpAction());

		actions.put("ignore", new IgnoreAction());
		actions.put("inspect", new InspectAction());
		actions.put("invisible", new InvisibleAction());

		actions.put("jail", new JailAction());
		actions.put("joinguild", new CreateGuildAction());

		actions.put("manual", new ManualAction());
		actions.put("me", new EmoteAction());
		actions.put("msg", msg);
		actions.put("mute", new MuteAction());

		actions.put("quit", new QuitAction());

		actions.put("remove", new RemoveBuddyAction());

		actions.put("sentence", new SentenceAction());

		actions.put("sound", new SoundAction());
		actions.put("volume", new VolumeAction());
		actions.put("vol", new VolumeAction());

		actions.put("summonat", new SummonAtAction());
		actions.put("summon", new SummonAction());
		actions.put("supportanswer", supporta);
		actions.put("supporta", supporta);
		actions.put("support", new SupportAction());

		actions.put("teleport", new TeleportAction());
		actions.put("teleportto", new TeleportToAction());
		actions.put("tellall", new TellAllAction());
		actions.put("tell", msg);

		actions.put("where", new WhereAction());
		actions.put("who", new WhoAction());
//		actions.put("wrap", new WrapAction());
	}

	/**
	 * Gets the Action object for the specified Action name.
	 * 
	 * @param name
	 *            name of Action
	 * @return Action object
	 */
	public static SlashAction get(final String name) {
		return actions.get(name);
	}
}
