/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;

import { singletons } from "./SingletonRepo";

import { DebugAction } from "./action/DebugAction";
import { OpenWebsiteAction } from "./action/OpenWebsiteAction";
import { SettingsAction } from "./action/SettingsAction";
import { SlashAction } from "./action/SlashAction";

import { ui } from "./ui/UI";
import { UIComponentEnum } from "./ui/UIComponentEnum";

import { ChatLogComponent } from "./ui/component/ChatLogComponent";

import { Chat } from "./util/Chat";


interface Action {
	[key: string]: string;
	type: string;
}

export class SlashActionRepo {
	[index: string]: any;

	/** Singleton instance. */
	private static instance: SlashActionRepo;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): SlashActionRepo {
		if (!SlashActionRepo.instance) {
			SlashActionRepo.instance = new SlashActionRepo();
		}
		return SlashActionRepo.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	private sendAction(action: Action) {
		marauroa.clientFramework.sendAction(action);
	}

	"add": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			if (params == null) {
				return false;
			};

			const action: Action = {
				"type": "addbuddy",
				"target": params[0]
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"adminnote": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type,
				"target": params[0],
				"note": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"adminlevel": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type,
				"target": params[0],
			};
			if (params.length >= 2) {
				action["newlevel"] = params[1];
			}
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 2
	};

	"alter": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type,
				"target": params[0],
				"stat": params[1],
				"mode": params[2],
				"value": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 3,
		maxParams: 3
	};

	"altercreature": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "altercreature",
				"target": params[0],
				"text": params[1]
			};

			this.sendAction(action);
			return true;
		},
		minParams: 2,
		maxParams: 2
	};

	"alterkill": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const target = params[0];
			const killtype = params[1];
			const count = params[2];
			var creature = null;

			if (remainder != null && remainder != "") {
				// NOTE: unlike Java client, Javascript client automatically trims whitespace in "remainder" parameter
				creature = remainder;
			}

			const action: Action = {
				"type": "alterkill",
				"target": target,
				"killtype": killtype,
				"count": count
			};
			if (creature != null) {
				action["creature"] = creature;
			}

			this.sendAction(action);
			return true;
		},
		minParams: 3,
		maxParams: 3
	};

	"alterquest": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "alterquest",
				"target": params[0],
				"name": params[1]
			};

			if (params[2] != null) {
				action["state"] = this.checkQuoted(params[2], remainder);
			}

			this.sendAction(action);
			return true;
		},
		minParams: 2,
		maxParams: 3
	};

	"answer": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			if (remainder == null || remainder == "") {
				return false;
			};

			const action: Action = {
				"type": "answer",
				"text": remainder
			};

			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 0
	};

	"away": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "away",
			};
			if (remainder.length != 0) {
				action["message"] = remainder;
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"ban": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "ban",
				"target": params[0],
				"hours": params[1],
				"reason": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 2,
		maxParams: 2
	};

	"chat": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type,
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"clear": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			(ui.get(UIComponentEnum.ChatLog) as ChatLogComponent).clear();
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	/*
	"clickmode": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const newMode = !stendhal.config.getBoolean("input.doubleclick");
			stendhal.config.set("input.doubleclick", newMode);
			stendhal.ui.gamewindow.updateClickMode();

			if (newMode) {
				Chat.log("info", "Click mode is now set to double click.");
			} else {
				Chat.log("info", "Click mode is now set to single click.");
			}
			return true;
		},
		minParams: 0,
		maxParams: 0
	};
	*/

	"debug" = new DebugAction();

	/* FIXME:
	 * - not in help output
	 */
	"drop": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			console.log(type, params, remainder);
			let name = remainder;
			let quantity = parseInt(params[0], 10);
			console.log(name, quantity);
			if (isNaN(quantity)) {
				name = (params[0] + " " + remainder).trim();
				quantity = 0;
			}
			console.log(name, quantity);
			const action: Action = {
				"type": "drop",
				"source_name": name,
				"quantity": "" + quantity,
				"x": "" + marauroa.me.x,
				"y": "" + marauroa.me.y
			};
			console.log(action);
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 1
	};

	"emojilist": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const emojilist = singletons.getEmojiStore().getEmojiList().sort();
			for (const idx in emojilist) {
				emojilist[idx] = "&nbsp;&nbsp;- :" + emojilist[idx] + ":";
			}
			emojilist.splice(0, 0, emojilist.length + " emojis available:");
			Chat.log("client", emojilist);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"gag": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "gag",
				"target": params[0],
				"minutes": params[1],
				"reason": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 2,
		maxParams: 2
	};

	/* FIXME:
	 * - not included in help info
	 */
	"group": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "group_management",
				"action": params[0],
				"params": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"grumpy": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "grumpy",
			};
			if (remainder.length != 0) {
				action["reason"] = remainder;
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"help": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var msg = [
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal_Manual",
				"Here are the most-used commands:",
				"* CHATTING:",
				"- /me <action> \tShow a message about what you are doing.",
				"- /tell <player> <message> \tSend a private message to #player.",
				"- /answer <message>",
				"\t\tSend a private message to the last player who sent a message to you.",
				"- // <message>\tSend a private message to the last player you sent a message to.",
				"- /storemessage <player> <message> \t\tStore a private message to deliver for an offline #player.",
				"- /who \tList all players currently online.",
				"- /where <player> \tShow the current location of #player.",
				"- /sentence <text> \tSet message on stendhalgame.org profile page and what players see when using #Look.",
				"* SUPPORT:",
				"- /support <message> \t\tAsk an administrator for help.",
				"- /faq \t\tOpen Stendhal FAQs wiki page in browser.",
				"* ITEM MANIPULATION:",
				"- /markscroll <text> \t\tMark your empty scroll and add a #text label.",
				"* BUDDIES AND ENEMIES:",
				"- /add <player> \tAdd #player to your buddy list.",
				"- /remove <player>",
				"\t\tRemove #player from your buddy list.",
				"- /ignore <player> [minutes|*|- [reason...]] \t\tAdd #player to your ignore list.",
				"- /ignore \tFind out who is on your ignore list.",
				"- /unignore <player> \t\tRemove #player from your ignore list.",
				"* STATUS:",
				"- /away <message> \t\tSet an away message.",
				"- /away \tRemove away status.",
				"- /grumpy <message> \t\tSet a message to ignore all non-buddies.",
				"- /grumpy \tRemove grumpy status.",
				"- /name <pet> <name> \t\tGive a name to your pet.",
				"- /profile [name] \tOpens a player profile page on stendhalgame.org.",
				"* PLAYER CONTROL:",
//				"- /clickmode \tSwitches between single click mode and double click mode.",
				"- /walk \tToggles autowalk on/off.",
				"- /stopwalk \tTurns autowalk off.",
				"- /movecont <on|off> \tToggle continuous movement (allows players to continue walking after map change or teleport without releasing direction key).",
				"* CLIENT SETTINGS:",
				"- /mute \tMute or unmute the sounds.",
				"- /volume \tLists or sets the volume for sound and music.",
				"* MISC:",
				"- /info \t\tFind out what the current server time is.",
				"- /clear \tClear chat log.",
				"- /help \tShow help information.",
				"- /removedetail \tRemove the detail layer (e.g. balloon, umbrella, etc.) from character.",
				"- /emojilist \tList available emojis."
			];
			for (var i = 0; i < msg.length; i++) {
				Chat.log("info", msg[i]);
			}
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"gmhelp": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var msg = null;

			if (params[0] == null) {
				msg = [
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
					"- /inspectquest <player> [<quest_slot>]",
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
					"\t\tList the jailed players and their sentences."
				];
			} else if ((params.length == 1) && (params[0] != null)) {
				if ("alter" == params[0]) {
					msg = [
						"/alter <player> <attrib> <mode> <value>",
						"\t\tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, SET or UNSET.",
						"\t\t- Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
						"\t\t- When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
						"\t\t  For example: #'/alter testplayer outfit set 12109901'",
						"\t\t  This will make <testplayer> look like danter"
					];
				} else if ("script" == params[0]) {
					msg = [
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
					];
				/* TODO:
				} else if ("support" == params[0]) {
					msg = buildHelpSupportResponse();
				*/
				} else {
					return false;
				}
			} else {
				return false;
			}

			for (var i = 0; i < msg.length; i++) {
				Chat.log("info", msg[i]);
			}

			return true;
		},
		minParams: 0,
		maxParams: 1
	};


	"ignore": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
					"type": "ignore"
			};

			if (params[0] == null) {
				action["list"] = "1";
			} else {
				action["target"] = params[0];
				var duration = params[1];

				if (duration != null) {
					/*
					 * Ignore "forever" values
					 */
					if (duration != "*" && duration != "-") {
						/*
						 * Validate it's a number
						 */
						if (isNaN(parseInt(duration, 10))) {
							return false;
						}

						action["duration"] = duration;
					}
				}

				if (remainder.length != 0) {
					action["reason"] = remainder;
				}
			}

			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 2
	};

	"inspectkill": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const target = params[0];
			var creature = null;
			if (remainder != null && remainder != "") {
				// NOTE: unlike Java client, Javascript client automatically trims whitespace in "remainder" parameter
				creature = remainder;
			}

			const action: Action = {
				"type": "inspectkill",
				"target": target
			};
			if (creature != null) {
				action["creature"] = creature;
			}

			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"inspectquest": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
					"type": "inspectquest",
					"target": params[0]
			};
			if (params.length > 1) {
					action["quest_slot"] = params[1];
			}
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 2
	};


	"jail": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "jail",
				"target": params[0],
				"minutes": params[1],
				"reason": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 2,
		maxParams: 2
	};

	"me": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "emote",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"movecont": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const enable = !stendhal.config.getBoolean("input.movecont");
			const action: Action = {
				"type": "move.continuous",
			};
			if (enable) {
				action["move.continuous"] = "";
			}
			this.sendAction(action);
			// update config
			stendhal.config.set("input.movecont", enable);
			Chat.log("info", "Continuous movement "
					+ (enable ? "enabled" : "disabled") + ".");
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	// name of player most recently messaged
	private lastPlayerTell?: string;

	"msg": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			this.lastPlayerTell = params[0];
			const action: Action = {
				"type": "tell",
				"target": params[0],
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};
	"tell": SlashAction = this["msg"];

	"/": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			if (typeof(this.lastPlayerTell) != "undefined") {
				const action: Action = {
					"type": "tell",
					"target": this.lastPlayerTell,
					"text": remainder
				};
				this.sendAction(action);
			}
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"mute": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			stendhal.main.toggleSound();
			if (stendhal.config.getBoolean("ui.sound")) {
				Chat.log("info", "Sounds are now on.");
			} else {
				Chat.log("info", "Sounds are now off.");
			}
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"p": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "group_message",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	/* FIXME:
	 * - parameters don'e work
	 */
	"progressstatus": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type
			}

			if (remainder.length > 0) {
				if (remainder.indexOf("Open Quests") > -1) {
					action["progress_type"] = "Open Quests";
					remainder = remainder.substring(12);
				} else if (remainder.indexOf("Completed Quests") > -1) {
					action["progress_type"] = "Completed Quests";
					remainder = remainder.substring(17);
				} else if (remainder.indexOf("Production") > -1) {
					action["progress_type"] = "Production";
					remainder = remainder.substring(11);
				} else {

				}
				if (remainder) {
					action["item"] = remainder;
				}
			}
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"remove": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			if (params == null) {
				return false;
			}

			const action: Action = {
				"type": "removebuddy",
				"target": params[0]
			};

			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"screenshot": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			stendhal.ui.gamewindow.createScreenshot();
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"sentence": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			if (params == null) {
				return false;
			};

			const action: Action = {
				"type": "sentence",
				"value": remainder
			};

			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"settings" = new SettingsAction();

	"stopwalk": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "walk",
				"mode": "stop"
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"volume": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const layername = params[0];
			let vol = params[1];
			if (typeof(layername) === "undefined") {
				const layers = ["master", ...stendhal.ui.soundMan.getLayerNames()];
				Chat.log("info", "Please use /volume <layer> <value> to adjust the volume.");
				Chat.log("client", "<layer> is one of \"" + layers.join("\", \"") + "\"");
				Chat.log("client", "<value> is a number in the range 0 to 100.");
				Chat.log("client", "Current volume levels:");
				for (const l of layers) {
					Chat.log("client", "&nbsp;&nbsp;- " + l + " -> " + stendhal.ui.soundMan.getVolume(l) * 100);
				}
			} else if (typeof(vol) !== "undefined") {
				if (!/^\d+$/.test(vol)) {
					Chat.log("error", "Value must be a number.");
					return true;
				}
				if (stendhal.ui.soundMan.setVolume(layername, parseInt(vol, 10) / 100)) {
					Chat.log("client", "Channel \"" + layername + "\" volume set to "
							+ (stendhal.ui.soundMan.getVolume(layername) * 100) + ".");
				} else {
					Chat.log("error", "Unknown layer \"" + layername + "\".");
				}
			} else {
				Chat.log("error", "Please use /volume for help.");
			}

			return true;
		},
		minParams: 0,
		maxParams: 2
	};

	"summon": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var x = null;
			var y = null;
			var quantity = null;

			var nameBuilder = [];
			for (var idx = 0; idx < params.length; idx++) {
				const str = params[idx];
				if (str.match("[0-9].*")) {
					if (x == null) {
						x = str;
					} else if (y == null) {
						y = str;
					} else if (quantity == null) {
						quantity = str;
					} else {
						nameBuilder.push(str);
					}
				} else {
					nameBuilder.push(str);
				}
			}

			// use x value as quantity if y was not specified
			if (quantity == null && y == null && x != null) {
				quantity = x;
				x = null;
			}

			var creature = nameBuilder.join(" ");
			if (x == null || y == null) {
				// for some reason, the action does not accept the x,y coordinates if they are not a string
				x = marauroa.me.x.toString();
				y = marauroa.me.y.toString();
			}

			const action: Action = {
				"type": type,
				"creature": creature,
				"x": x,
				"y": y,
				"quantity": quantity || "0"
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: -1 // XXX: is this the proper way to allow an unlimited number of arguments?
	};

	"summonat": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var amount = params[2];
			// don't require first parameter to be integer amount
			if (isNaN(parseInt(amount, 10))) {
				if (remainder) {
					remainder = amount + " " + remainder;
				} else {
					remainder = amount;
				}
				amount = "1";
			}

			const action: Action = {
				"type": type,
				"target": params[0],
				"slot": params[1],
				"amount": amount,
				"item": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 3,
		maxParams: 3
	};

	"support": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "support",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"supportanswer": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "supportanswer",
				"target": params[0],
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};
	"supporta": SlashAction = this["supportanswer"];

	"teleport": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "teleport",
				"target": params[0],
				"zone": params[1],
				"x": params[2],
				"y": params[3]
			};
			this.sendAction(action);
			return true;
		},
		minParams: 4,
		maxParams: 4
	};

	"teleportto": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "teleportto",
				"target": remainder,
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"tellall": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "tellall",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"walk": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "walk"
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"atlas": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			window.location.href = "https://stendhalgame.org/world/atlas.html?me="
				+ marauroa.currentZoneName + "." + marauroa.me.x + "." + marauroa.me.y;
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"beginnersguide": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			window.location.href = "https://stendhalgame.org/wiki/Stendhal_Beginner's_Guide";
			return true;
		},
		minParams: 0,
		maxParams: 0
	};

	"characterselector" = new OpenWebsiteAction("https://stendhalgame.org/account/mycharacters.html");

	"faq" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_FAQ");

	"manual" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_Manual/Controls_and_Game_Settings");

	"profile": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var url = "https://stendhalgame.org/character/";
			var name = marauroa.me["_name"] || singletons.getSessionManager().getCharName();

			if (params.length > 0 && params[0] != null) {
				name = params[0];
			}
			if (!name) {
				console.warn("failed to get default character name!");
				return true;
			}

			url += name + ".html";
			Chat.log("info", "Trying to open #" + url + " in your browser.");
			window.location.href = url;
			return true;
		},
		minParams: 0,
		maxParams: 1
	};

	"rules" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_Rules");

	"changepassword" = new OpenWebsiteAction("https://stendhalgame.org/account/change-password.html");

	"loginhistory" = new OpenWebsiteAction("https://stendhalgame.org/account/history.html");

	"halloffame" = new OpenWebsiteAction("https://stendhalgame.org/world/hall-of-fame/active_overview.html");

	"storemessage": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "storemessage",
				"target": params[0],
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: 1
	};

	"_default": SlashAction = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type
			};
			if (typeof(params[0] != "undefined")) {
				action["target"] = params[0];
				if (remainder != "") {
					action["args"] = remainder;
				}
			}
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 1
	};

	execute(line: string): boolean {
		line = line.trim();

		// double slash is a special command, that should work without
		// entering a space to separate it from the arguments.
		if (line.startsWith("//") && !line.startsWith("// ")) {
			line = "// " + line.substring(2);
		}

		var array = line.split(" ");

		// clean whitespace
		for (var el in array) {
			array[el] = array[el].trim();
		}
		array = array.filter(Boolean);
		if (array.length == 0) {
			return false;
		}

		var name = array[0];
		if (name[0] != "/") {
			name = "/chat";
		} else {
			array.shift();
		}
		name = name.substr(1);
		var action: SlashAction;
		if (typeof(this[name]) == "undefined") {
			action = this["_default"];
		} else {
			action = this[name];
		}

		// use executing character if name parameter not supplied
		if (name == "where" && array.length == 0) {
			array[0] = marauroa.me["_name"];
		}

		if (action.minParams <= array.length) {
			var remainder = "";
			for (var i = action.maxParams; i < array.length; i++) {
				remainder = remainder + array[i] + " ";
			}
			array.slice(action.maxParams);
			return action.execute(name, array, remainder.trim());
		} else {
			Chat.log("error", "Missing arguments. Try /help");
			return false;
		}
		return true;
	}

	/**
	 * Checks for quoted whitepace to be included in parameter.
	 *
	 * @param p
	 *     The parameter to be amended.
	 * @param remainder
	 *     String to be checked for quoted whitespace.
	 * @return
	 *     Amended parameter.
	 */
	checkQuoted(p: string, remainder: string): string {
		if (p.includes("\"") && remainder.includes("\"")) {
			let endQuote = false;
			let paramEnd = 0;
			const arr = Array.from(remainder);
			for (const c of arr) {
				if (c === " " && endQuote) {
					break;
				} else if (c === "\"") {
					endQuote = !endQuote;
				}
				paramEnd++;
			}
			p = (p + " " + remainder.substring(0, paramEnd+1)).replace(/\"/g, "").trim();
		}
		return p;
	}
}
