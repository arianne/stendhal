/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;
import games.stendhal.common.messages.SupportMessageTemplatesFactory;

/**
 * Display command usage. Eventually replace this with ChatCommand.usage().
 */
class GMHelpAction implements SlashAction {

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
	    List<String> lines;
		if (params[0] == null) {
			lines = Arrays.asList(
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal:Administration",
				"Here are the most-used GM commands:",
				"* GENERAL:",
				"- /gmhelp [alter|script|support]",
				"\t\tFor more info about alter, script or the supportanswer shortcuts.",
				"- /adminnote <player> <note>",
				"\t\tLogs a note about #player.",
				"- /inspect <player>",
				"\t\tShow complete details of #player.",
				"- /inspectkill <player> <creature>",
				"\t\tShow creature kill counts of #player for #creature.",
				"- /inspectquest <player> <quest_slot>",
				"\t\tShow the state of quest for #player.",
				"- /script <scriptname>",
				"\t\tLoad (or reload) a script on the server. See #/gmhelp #script for details.",
				"* CHATTING:",
				"- /supportanswer <player> <message>",
				"\t\tReplies to a support question. Replace #message with $faq, $faqsocial, $ignore, $faqpvp, $wiki, $knownbug, $bugstracker, $rules, $notsupport or $spam shortcuts if desired.",
				"- /tellall <message>",
				"\t\tSend a private message to all logged-in players.",
				"* PLAYER CONTROL:",
				"- /teleportto <name>",
				"\t\tTeleport yourself near the specified player or NPC.",
				"- /teleclickmode \tMakes you teleport to the location you double click.",
				"- /ghostmode \tMakes yourself invisible and intangible.",
				"- /invisible \tToggles whether or not you are invisible to creatures.",
				"* ENTITY MANIPULATION:",
				"- /adminlevel <player> [<newlevel>]",
				"\t\tDisplay or set the adminlevel of the specified #player.",
				"- /jail <player> <minutes> <reason>",
				"\t\tImprisons #player for a given length of time.",
				"- /gag <player> <minutes> <reason>",
				"\t\tGags #player for a given length of time (player is unable to send messages to anyone).",
				"- /ban <character> <hours> <reason>",
				"\t\tBans the account of the character from logging onto the game server or website for the specified amount of hours (-1 till end of time).",
				"- /teleport <player> <zone> <x> <y>",
				"\t\tTeleport #player to the given location.",
				"- /alter <player> <attrib> <mode> <value>",
				"\t\tAlter stat #attrib of #player by the given amount; #mode can be ADD, SUB, SET or UNSET. See #/gmhelp #alter for details.",
				"- /altercreature <id> name;atk;def;hp;xp",
				"\t\tChange values of the creature. Use #- as a placeholder to keep default value. Useful in raids.",
				"- /alterkill <player> <type> <count> <creature>",
				"\t\tChange number of #creature killed #type (\"solo\" or \"shared\") to #count for #player.",
				"- /alterquest <player> <questslot> <value>",
				"\t\tUpdate the #questslot for #player to be #value.",
				"- /summon <creature|item> [x] [y]",
				"- /summon <stackable item> [quantity]",
				"- /summon <stackable item> <x> <y> [quantity]",
				"\t\tSummon the specified item or creature at co-ordinates #x, #y in the current zone.",
				"- /summonat <player> <slot> [amount] <item>",
				"\t\tSummon the specified item into the specified slot of <player>; <amount> defaults to 1 if not specified.",
				"- /destroy <entity> \tDestroy an entity completely.",
				"* MISC:",
				"- /jailreport [<player>]",
				"\t\tList the jailed players and their sentences.");
		} else if ((params.length == 1) && (params[0] != null)) {
			if ("alter".equals(params[0])) {
				lines = Arrays.asList(
					"/alter <player> <attrib> <mode> <value>",
					"\t\tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, SET or UNSET.",
					"\t\t- Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
					"\t\t- When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
					"\t\t  For example: #'/alter testplayer outfit set 12109901'",
					"\t\t  This will make <testplayer> look like danter" );
			} else if ("script".equals(params[0])) {
				lines = Arrays.asList(
					"usage: /script [-list|-load|-unload|-execute] [params]",
					"\t-list : shows available scripts. In this mode can be given one optional parameter for filenames filtering, with using well-known wildcards for filenames ('*' and '?', for example \"*.class\" for java-only scripts).",
					"\t-load : load script with first parameter's filename.",
					"\t-unload : unload script with first parameter's filename from server",
					"\t-execute : run selected script.",
					"",
					"All scripts are ran using: /script scriptname [params]. After running a script you can remove any traces of it with /script -unload scriptname, this would remove any summoned creatures, for example. It's good practise to do this after summoning creatures for a raid using scripts.",
					"#/script #AdminMaker.class : For test servers only, summons an adminmaker to aid testing.",
					"#/script #AdminSign.class #zone #x #y #text : Makes an AdminSign in zone at (x,y) with text. To put it next to you do /script AdminSign.class - - - text.",
					"#/script #AlterQuest.class #player #questname #state : Update the quest for a player to be in a certain state. Omit #state to remove the quest.",
					"#/script #DeepInspect.class #player : Deep inspects a player and all his/her items.",
					"#/script #DropPlayerItems.class #player #[amount] #item : Drop the specified amount of items from the player if they are equipped in the bag or body.",
					"#/script #EntitySearch.class #nonrespawn : Shows the locations of all creatures that don't respawn, for example creatures that were summoned by a GM, deathmatch creatures, etc.",
					"#/script #FixDM.class #player : sets a player's DeathMatch slot to victory status.",
					"#/script #ListNPCs.class : lists all npcs and their position.",
					"#/script #LogoutPlayer.class #player : kicks a player from the game.",
					"#/script #NPCShout.class #npc #text : NPC shouts text.",
					"#/script #NPCShoutZone.class #npc #zone #text : NPC shouts text to players in given zone. Use - in place of zone to make it your current zone.",
					"#/script #Plague.class #1 #creature : summon a plague of raid creatures around you.",
					"#/script #WhereWho.class : Lists where all the online players are",
					"#/script #Maria.class : Summons Maria, who sells food&drinks. Don't forget to -unload her after you're done.",
					"#/script #ServerReset.class : use only in a real emergency to shut down server. If possible please warn the players to logout and give them some time. It kills the server the hard way.",
					"#/script #ResetSlot.class #player #slot : Resets the named slot such as !kills or !quests. Useful for debugging."
					);

			} else if ("support".equals(params[0])) {
				lines = buildHelpSupportResponse();
			} else {
				return false;
			}
		} else {
			return false;
		}
		for (final String line : lines) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
		}

		return true;
	}

	private List<String> buildHelpSupportResponse() {
		List<String> lines = new LinkedList<String>();
		Map<String, String> templates = new SupportMessageTemplatesFactory().getTemplates();
		for (Entry<String, String> template : templates.entrySet()) {
			lines.add(template.getKey() + " - " + template.getValue());
		}
		return lines;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 1;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
