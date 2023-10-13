/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import games.stendhal.common.constants.Actions;


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
		final SlashAction who = new WhoAction();
		final SlashAction help = new HelpAction();
		final GroupMessageAction groupMessage = new GroupMessageAction();

		actions.put("/", new RemessageAction());
		actions.put(Actions.ADD, new AddBuddyAction());
		actions.put(Actions.ADMINLEVEL, new AdminLevelAction());
		actions.put("adminnote", new AdminNoteAction());
		actions.put(Actions.ALTER, new AlterAction());
		actions.put(Actions.ALTERCREATURE, new AlterCreatureAction());
		actions.put(Actions.ALTERKILL, new AlterKillAction());
		actions.put(Actions.ALTERQUEST, new AlterQuestAction());
		actions.put(Actions.ANSWER, new AnswerAction());
		actions.put("atlas", new AtlasBrowserLaunchCommand());
		actions.put(Actions.AWAY, new AwayAction());

		actions.put(Actions.BAN, new BanAction());

		actions.put("clear", new ClearChatLogAction());
		actions.put("clickmode", new ClickModeAction());
		actions.put("clientinfo", new ClientInfoAction());
		actions.put("commands", help);
		actions.put("config", new ConfigAction());

		actions.put("debug", new DebugAction());
		actions.put("drop", new DropAction());

		actions.put("cast", new CastSpellAction());

		actions.put(Actions.GAG, new GagAction());
		actions.put("gmhelp", new GMHelpAction());
		actions.put("group", new GroupManagementAction(groupMessage));
		actions.put("groupmessage", groupMessage);
		actions.put(Actions.GRUMPY, new GrumpyAction());

		actions.put("help", help);

		actions.put(Actions.IGNORE, new IgnoreAction());
		actions.put(Actions.INSPECT, new InspectAction());
		actions.put(Actions.INSPECTKILL, new InspectKillAction());
		actions.put(Actions.INSPECTQUEST, new InspectQuestAction());
		actions.put(Actions.INVISIBLE, new InvisibleAction());

		actions.put(Actions.JAIL, new JailAction());

		actions.put(Actions.LISTPRODUCERS, new ListProducersAction());

		actions.put("me", new EmoteAction());
		actions.put("msg", msg);
		actions.put("mute", new MuteAction());

		actions.put("names", who);

		actions.put("p", groupMessage);
		actions.put("profile", new ProfileAction());
		actions.put("travellog", new TravelLogAction());

		actions.put("quit", new QuitAction());

		actions.put("remove", new RemoveBuddyAction());

		actions.put(Actions.SENTENCE, new SentenceAction());
		actions.put("status", new SentenceAction()); // Alias for /sentence
		actions.put("settings", new SettingsAction());

		actions.put("sound", new SoundAction());
		actions.put("volume", new VolumeAction());
		actions.put("vol", new VolumeAction());

		actions.put("storemessage", new StoreMessageAction());
		actions.put("postmessage", new StoreMessageAction());

		actions.put(Actions.SUMMONAT, new SummonAtAction());
		actions.put(Actions.SUMMON, new SummonAction());
		actions.put(Actions.SUPPORTANSWER, supporta);
		actions.put("supporta", supporta);
		actions.put(Actions.SUPPORT, new SupportAction());

		actions.put("takescreenshot", new ScreenshotAction());
		actions.put(Actions.TELEPORT, new TeleportAction());
		actions.put(Actions.TELEPORTTO, new TeleportToAction());
		actions.put(Actions.TELLALL, new TellAllAction());
		actions.put(Actions.TELL, msg);

		actions.put(Actions.WHERE, new WhereAction());
		actions.put(Actions.WHO, who);
		actions.putAll(BareBonesBrowserLaunchCommandsFactory.createBrowserCommands());
		//actions.put("wrap", new WrapAction());

		// movement
		actions.put(Actions.WALK, new AutoWalkAction());
		actions.put(Actions.STOPWALK, new AutoWalkStopAction());
		actions.put("movecont", new MoveContinuousAction());

		// PvP challenge actions
		actions.put(Actions.CHALLENGE, new CreateChallengeAction());
		actions.put(Actions.ACCEPT, new AcceptChallengeAction());

		// allows players to remove the detail layer manually
		actions.put(Actions.REMOVEDETAIL, new RemoveDetailAction());

		actions.put("emojilist", new EmojiListAction());
	}

	/**
	 * Gets the Action object for the specified Action name.
	 *
	 * @param name
	 *            name of Action
	 * @return Action object
	 */
	public static SlashAction get(String name) {
		String temp = name.toLowerCase(Locale.ENGLISH);
		return actions.get(temp);
	}

	/**
	 * Get all known command names.
	 *
	 * @return set of commands
	 */
	public static Set<String> getCommandNames() {
		return actions.keySet();
	}
}
