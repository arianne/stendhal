/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "./stendhal";

import { singletons } from "./SingletonRepo";

import { AboutAction } from "./action/AboutAction";
import { DebugAction } from "./action/DebugAction";
import { OpenWebsiteAction } from "./action/OpenWebsiteAction";
import { ProgressStatusAction } from "./action/ProgressStatusAction";
import { ReTellAction } from "./action/ReTellAction";
import { ScreenCaptureAction } from "./action/ScreenCaptureAction";
import { SettingsAction } from "./action/SettingsAction";
import { SlashActionImpl } from "./action/SlashAction";
import { TellAction } from "./action/TellAction";

import { ui } from "./ui/UI";
import { UIComponentEnum } from "./ui/UIComponentEnum";

import { ChatLogComponent } from "./ui/component/ChatLogComponent";

import { Chat } from "./util/Chat";
import { Debug } from "./util/Debug";


/**
 * Action type representation.
 */
interface Action {
	[key: string]: string;
	type: string;
}

/**
 * Registered slash actions.
 */
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

	/**
	 * Forwards action information to server.
	 *
	 * @param action {SlashActionRepo.Action}
	 *   Action object.
	 */
	private sendAction(action: Action) {
		marauroa.clientFramework.sendAction(action);
	}

	/**
	 * Retrieves registered types excluding generic defaults & aliases.
	 *
	 * FIXME: not detecting some property names such as "manual"
	 *
	 * @return {string[]}
	 *   List of unique type names.
	 */
	private getTypes(): string[] {
		// type names
		const types: string[] = [];
		// excludes including alias duplicates
		const actions: SlashActionImpl[] = [this["_default"], this["debug"]];
		for (const t of Object.keys(this)) {
			const action = this[t];
			if (actions.indexOf(action) > -1) {
				continue;
			}
			actions.push(action);
			types.push(t);
		}
		return types;
	}

	/**
	 * Retrieves help data for standard user actions.
	 *
	 * @return {any}
	 */
	private getUserHelpData(): any {
		const grouping: {[index: string]: any} = {
			"CHATTING": [
				"chat",
				"me",
				"tell",
				"answer",
				"/",
				"p",
				"storemessage",
				{
					type: "who",
					getHelp: function(): string[] {
						return ["", "List all players currently online."];
					}
				},
				{
					type: "where",
					getHelp: function(): string[] {
						return ["[<player>]", "Show the current location of #player."];
					}
				},
				"sentence"
			],
			"TOOLS": [
				"progressstatus",
				"screenshot",
				//"screencap",
				"atlas",
				"beginnersguide"
			],
			"SUPPORT": [
				"support",
				{
					type: "faq",
					getHelp: function(): string[] {
						return ["", "Open Stendhal FAQs wiki page in browser."];
					}
				}
			],
			"ITEM MANIPULATION": [
				"drop",
				{
					type: "markscroll",
					getHelp: function(): string[] {
						return ["<text>", "Mark your empty scroll and add a #text label."];
					}
				}
			],
			"BUDDIES AND ENEMIES": [
				"add",
				"remove",
				{type: "ignore", sparams: "<player> [minutes|*|- [reason...]]"},
				"ignore",
				{
					type: "unignore",
					getHelp: function(): string[] {
						return ["<player>", "Remove #player from your ignore list."];
					}
				}
			],
			"STATUS": [
				{type: "away", sparams: "<message>"},
				"away",
				{type: "grumpy", sparams: "<message>"},
				"grumpy",
				{
					type: "name",
					getHelp: function(): string[] {
						return ["<pet> <name>", "Give a name to your pet."];
					}
				},
				"profile"
			],
			"PLAYER CONTROL": [
				//"clickmode", // Switches between single click mode and double click mode.
				"walk",
				"stopwalk",
				"movecont"
			],
			"CLIENT SETTINGS": [
				"settings",
				"mute",
				{type: "volume", sparams: "<layer> <value>"},
				"volume"
			],
			"MISC": [
				"about",
				{
					type: "info",
					getHelp: function(): string[] {
						return ["", "Find out what the current server time is."];
					}
				},
				"clear",
				"help",
				{
					type: "removedetail",
					getHelp: function(): string[] {
						return [
							"",
							"Remove the detail layer (e.g. balloon, umbrella, etc.) from character. #WARNING:"
									+ " Cannot be undone."
						];
					}
				},
				"emojilist",
				{type: "group", sparams: "invite <player>"},
				{type: "group", sparams: "join <player"},
				{type: "group", sparams: "leader <player>"},
				{type: "group", sparams: "lootmode single|shared"},
				{type: "group", sparams: "kick <player>"},
				{type: "group", sparams: "part"},
				{type: "group", sparams: "status"}
			]
		};

		if (Debug.isActive("screencap")) {
			grouping["TOOLS"].push("screencap");
		}

		return {
			info: [
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal_Manual",
				"Here are the most-used commands:"
			],
			grouping: grouping
		}
	}

	/**
	 * Retrieves help data for game master actions.
	 *
	 * @return {any}
	 */
	private getGMHelpData(): any {
		const grouping: {[index: string]: any} = {
			"GENERAL": [
				{type: "gmhelp", sparams: "[alter|script|support]"},
				"gmhelp",
				"adminnote",
				{
					type: "inspect",
					getHelp: function(): string[] {
						return ["<player>", "Show complete details of #player."];
					}
				},
				"inspectkill",
				"inspectquest",
				{
					type: "script",
					getHelp: function(): string[] {
						return [
							"<scriptname>",
							"Load (or reload) a script on the server. See #/gmhelp #script for details."
						];
					}
				}
			],
			"CHATTING": [
				"supportanswer",
				"tellall"
			],
			"PLAYER CONTROL": [
				"teleportto",
				{
					type: "teleclickmode",
					getHelp: function(): string[] {
						return ["", "Makes you teleport to the location you double click."];
					}
				},
				{
					type: "ghostmode",
					getHelp: function(): string[] {
						return ["", "Makes yourself invisible and intangible."];
					}
				},
				{
					type: "invisible",
					getHelp: function(): string[] {
						return ["", "Toggles whether or not you are invisible to creatures."];
					}
				}
			],
			"ENTITY MANIPULATION": [
				"adminlevel",
				"jail",
				"gag",
				"ban",
				"teleport",
				"alter",
				"altercreature",
				"alterkill",
				"alterquest",
				{type: "summon", sparams: "<creature>|<item> [<x> <y>]"},
				{type: "summon", sparams: "<stackable_item> [<x> <y>] [quantity]"},
				"summonat",
				{
					type: "destroy",
					getHelp: function(): string[] {
						return ["<entity>", "Destroy an entity completely."];
					}
				}
			],
			"MISC": [
				{
					type: "jailreport",
					getHelp: function(): string[] {
						return ["[<player>]", "List the jailed players and their sentences."];
					}
				}
			]
		};
		return {
			info: [
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal:Administration",
				"Here are the most-used GM commands:"
			],
			grouping: grouping
		};
	}

	/**
	 * Compiles help information for registered actions.
	 *
	 * TODO: cache compiled help info
	 *
	 * @param gm {boolean}
	 *   Pull info for GM help commands instead of standard user help (default: `false`).
	 * @return {string[]}
	 *   Help info string array.
	 */
	private getHelp(gm=false): string[] {
		let help: any;
		let stripHelp: any;
		if (gm) {
			help = this.getGMHelpData();
			stripHelp = this.getUserHelpData();
		} else {
			help = this.getUserHelpData();
			stripHelp = this.getGMHelpData();
		}

		const types = this.getTypes();
		// remove types not available for this help set
		for (const gname in stripHelp.grouping) {
			for (let t of stripHelp.grouping[gname]) {
				if (typeof(t) !== "string") {
					t = t.type;
				}
				types.splice(types.indexOf(t), 1);
			}
		}

		const unavailable: string[] = [];
		for (const gname in help.grouping) {
			// add spacing for clarity
			help.info.push("&nbsp;");
			help.info.push(gname + ":");
			for (let t of help.grouping[gname]) {
				// underscore command line
				let actionHelp = ["- §'/"];
				let action: any;
				if (typeof(t) === "string") {
					actionHelp[0] += t;
					action = this[t];
				} else {
					actionHelp[0] += t.type;
					action = t || this[t.type];
					action.getHelp = action.getHelp || this[t.type].getHelp;
					t = t.type;
				}

				const typeIndex = types.indexOf(t);
				if (!action || !action.getHelp) {
					unavailable.push(t);
					actionHelp[0] += "'";
					help.info.push(actionHelp[0]);
					if (typeIndex > -1) {
						types.splice(typeIndex, 1);
					}
					continue;
				}
				const helpTemp = action.getHelp(action.sparams);
				if (helpTemp && helpTemp.length) {
					if (helpTemp[0]) {
						actionHelp[0] += " " + helpTemp[0];
					}
					helpTemp.splice(0, 1);
					for (const li of helpTemp) {
						actionHelp.push("&nbsp;&nbsp;" + li);
					}
				} else {
					unavailable.push(t);
				}
				if (action.aliases) {
					actionHelp.push("&nbsp;&nbsp;Aliases: " + action.aliases.join(", "));
				}
				actionHelp[0] += "'";
				help.info = [...help.info, ...actionHelp];
				if (typeIndex > -1) {
					types.splice(typeIndex, 1);
				}
			}
		}
		for (const t of types) {
			if (unavailable.indexOf(t) < 0) {
				unavailable.push(t);
			}
		}
		if (unavailable.length) {
			console.warn("help information is unavailable for actions:", unavailable.join(", "));
		}
		return help.info;
	}

	"help": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const msg = this.getHelp();
			for (var i = 0; i < msg.length; i++) {
				Chat.log("info", msg[i]);
			}
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Show this help message."];
		}
	};

	"gmhelp": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			var msg = null;
			if (params[0] == null) {
				msg = this.getHelp(true);
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
		maxParams: 1,
		getHelp: function(sparams?: string): string[] {
			if (sparams) {
				return [sparams, "For more info about alter, script or the supportanswer shortcuts."];
			}
			return ["", "Show this help message."];
		}
	};

	"about" = new AboutAction();

	"add": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["<player>", "Add #player to your buddy list."];
		}
	};

	"adminnote": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["<player> <note>", "Logs a note about #player."];
		}
	};

	"adminlevel": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return ["<player> [<newlevel>]", "Display or set the adminlevel of the specified #player."];
		}
	};

	"alter": SlashActionImpl = {
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
		maxParams: 3,
		getHelp: function(): string[] {
			return [
				"<player> <attrib> <mode> <value>",
				"Alter stat #attrib of #player by the given amount; #mode can be ADD, SUB, SET or UNSET."
						+ " See #/gmhelp #alter for details."
			];
		}
	};

	"altercreature": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return [
				"<id> set|unset|add|sub <attribute> [<value>]",
				"Change values of the creature. Use #- as a placeholder to keep default value. Useful in"
						+ " raids."
			];
		}
	};

	"alterkill": SlashActionImpl = {
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
		maxParams: 3,
		getHelp: function(): string[] {
			return [
				"<player> <type> <count> <creature>",
				"Change number of #creature killed #type (\"solo\" or \"shared\") to #count for #player."
			];
		}
	};

	"alterquest": SlashActionImpl = {
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
		maxParams: 3,
		getHelp: function(): string[] {
			return ["<player> <questslot> <value>", "Update the #questslot for #player to be #value."];
		}
	};

	"answer": SlashActionImpl = {
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
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<message>", "Send a private message to the last player who sent a message to you."];
		}
	};

	"away": SlashActionImpl = {
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
		maxParams: 0,
		getHelp: function(sparams: string): string[] {
			if (sparams === "<message>") {
				return [sparams,  "Set an away message."];
			}
			return ["", "Remove away status."];
		}
	};

	"ban": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return [
					"<character> <hours> <reason>",
					"Bans the account of the character from logging onto the game server or website for the"
							+ " specified amount of hours (-1 till end of time)."
			];
		}
	};

	"chat": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": type,
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return [
				"<message>",
				"Send a public chat message (same as sending text without prefixed chat command)."
			];
		}
	};

	"clear": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			(ui.get(UIComponentEnum.ChatLog) as ChatLogComponent).clear();
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Clear chat log."];
		}
	};

	/*
	"clickmode": SlashActionImpl = {
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

	"drop": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["[<quantity>] <item_name>", "Drops items from inventory where you stand."];
		}
	};

	"emojilist": SlashActionImpl = {
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
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "List available emojis."];
		}
	};

	"gag": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return [
				"<player> <minutes> <reason>",
				"Gags #player for a given length of time (player is unable to send messages to anyone)."
			];
		}
	};

	"group": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(sparams: string): string[] {
			const desc: string[] = [];
			if (sparams === "invite <player>") {
				desc.push("Invite a player to join your group.");
			} else if (sparams === "join <player") {
				desc.push("Accept invite to join #player's group.");
			} else if (sparams === "leader <player>") {
				desc.push("Make #player leader of group.");
			} else if (sparams === "lootmode single|shared") {
				desc.push("Set looting mode for group.");
				desc.push("&nbsp;&nbsp;single: Only group leader can loot.");
				desc.push("&nbsp;&nbsp;shared: Any group member can loot.");
			} else if (sparams === "kick <player>") {
				desc.push("Kick #player from group.");
			} else if (sparams === "part") {
				desc.push("Leave group.");
			} else if (sparams === "status") {
				desc.push("Broken?");
			}
			return [sparams, ...desc];
		}
	};

	"grumpy": SlashActionImpl = {
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
		maxParams: 0,
		getHelp: function(sparams: string): string[] {
			if (sparams === "<message>") {
				return [sparams,  "Set a message to ignore all non-buddies."];
			}
			return ["", "Remove grumpy status."];
		}
	};

	"ignore": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(sparams?: string): string[] {
			if (sparams) {
				return [sparams, "Add #player to your ignore list."];
			}
			return ["", "Find out who is on your ignore list."];
		}
	};

	"inspectkill": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["<player> <creature>", "Show creature kill counts of #player for #creature."];
		}
	};

	"inspectquest": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return ["<player> [<quest_slot>]", "Show the state of quest for #player."];
		}
	};


	"jail": SlashActionImpl = {
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
		maxParams: 2,
		getHelp: function(): string[] {
			return ["<player> <minutes> <reason>", "Imprisons #player for a given length of time."];
		}
	};

	"me": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "emote",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<action>", "Show a message about what you are doing."];
		}
	};

	"movecont": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const enable = !stendhal.config.getBoolean("move.cont");
			const action: Action = {
				"type": "move.continuous",
			};
			if (enable) {
				action["move.continuous"] = "";
			}
			this.sendAction(action);
			// update config
			stendhal.config.set("move.cont", enable);
			Chat.log("info", "Continuous movement "
					+ (enable ? "enabled" : "disabled") + ".");
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return [
				"",
				"Toggle continuous movement (allows players to continue walking after map change or"
						+ " teleport without releasing direction key)."
			];
		}
	};

	"tell" = new TellAction();
	"msg" = this["tell"];
	"/" = new ReTellAction();

	"mute": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			stendhal.sound.toggleSound();
			if (stendhal.config.getBoolean("sound")) {
				Chat.log("info", "Sounds are now on.");
			} else {
				Chat.log("info", "Sounds are now off.");
			}
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Mute or unmute the sounds."];
		}
	};

	"p": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "group_message",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<message>", "Send a message to group members."];
		}
	};

	"progressstatus" = new ProgressStatusAction();

	"remove": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["<player>", "Remove #player from your buddy list."];
		}
	};

	"screenshot": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			return singletons.getDownloadUtil().buildScreenshot().execute();
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Capture a screenshot of the viewport area."];
		}
	};

	"screencap" = new ScreenCaptureAction();

	"sentence": SlashActionImpl = {
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
		maxParams: 0,
		getHelp: function(): string[] {
			return [
				"<text>",
				"Set message on stendhalgame.org profile page and what players see when using #Look."
			];
		}
	};

	"settings" = new SettingsAction();

	"stopwalk": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "walk",
				"mode": "stop"
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Turns autowalk off."];
		}
	};

	"volume": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const layername = params[0];
			let vol = params[1];
			if (typeof(layername) === "undefined") {
				const layers = ["master", ...stendhal.sound.getLayerNames()];
				Chat.log("info", "Please use /volume <layer> <value> to adjust the volume.");
				Chat.log("client", "<layer> is one of \"" + layers.join("\", \"") + "\"");
				Chat.log("client", "<value> is a number in the range 0 to 100.");
				Chat.log("client", "Current volume levels:");
				for (const l of layers) {
					Chat.log("client", "&nbsp;&nbsp;- " + l + " -> " + stendhal.sound.getVolume(l) * 100);
				}
			} else if (typeof(vol) !== "undefined") {
				if (!/^\d+$/.test(vol)) {
					Chat.log("error", "Value must be a number.");
					return true;
				}
				if (stendhal.sound.setVolume(layername, parseInt(vol, 10) / 100)) {
					Chat.log("client", "Channel \"" + layername + "\" volume set to "
							+ (stendhal.sound.getVolume(layername) * 100) + ".");
				} else {
					Chat.log("error", "Unknown layer \"" + layername + "\".");
				}
			} else {
				Chat.log("error", "Please use /volume for help.");
			}

			return true;
		},
		minParams: 0,
		maxParams: 2,
		getHelp: function(sparams?: string): string[] {
			if (sparams) {
				return [sparams, "Lists or sets the volume for sound and music."];
			}
			return ["", "Shows current volume levels."];
		}
	};

	"summon": SlashActionImpl = {
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
				"y": y
			};
			if (quantity != null) {
				action["quantity"] = quantity;
			}
			this.sendAction(action);
			return true;
		},
		minParams: 1,
		maxParams: -1, // XXX: is this the proper way to allow an unlimited number of arguments?
		getHelp: function(sparams: string): string[] {
			let desc: any;
			if (sparams === "<creature>|<item> [<x> <y>]") {
				desc = "Summon a creature.";
			} else if (sparams === "<stackable_item> [<x> <y>] [quantity]") {
				desc = "Summon the specified item or creature at co-ordinates #x, #y in the current zone.";
			}
			return [sparams, desc];
		}
	};

	"summonat": SlashActionImpl = {
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
		maxParams: 3,
		getHelp: function(): string[] {
			return [
				"<player> <slot> [amount] <item>",
				"Summon the specified item into the specified slot of <player>; <amount> defaults to 1 if"
						+ " not specified."
			];
		}
	};

	"support": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "support",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<message>", "Ask an administrator for help."];
		}
	};

	"supportanswer": SlashActionImpl = {
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
		maxParams: 1,
		aliases: ["supporta"],
		getHelp: function(): string[] {
			return [
				"<player> <message>",
				"Replies to a support question. Replace #message with $faq, $faqsocial, $ignore, $faqpvp,"
						+ " $wiki, $knownbug, $bugstracker, $rules, $notsupport or $spam shortcuts if desired."
			];
		}
	};
	"supporta": SlashActionImpl = this["supportanswer"];

	"teleport": SlashActionImpl = {
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
		maxParams: 4,
		getHelp: function(): string[] {
			return ["<player> <zone> <x> <y>", "Teleport #player to the given location."];
		}
	};

	"teleportto": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "teleportto",
				"target": remainder,
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<name>", "Teleport yourself near the specified player or NPC."];
		}
	};

	"tellall": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "tellall",
				"text": remainder
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["<message>", "Send a private message to all logged-in players."];
		}
	};

	"walk": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			const action: Action = {
				"type": "walk"
			};
			this.sendAction(action);
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Toggles autowalk on/off."];
		}
	};

	"atlas": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			window.location.href = "https://stendhalgame.org/world/atlas.html?me="
				+ marauroa.currentZoneName + "." + marauroa.me.x + "." + marauroa.me.y;
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Opens atlas page on stendhalgame.org."];
		}
	};

	"beginnersguide": SlashActionImpl = {
		execute: (type: string, params: string[], remainder: string): boolean => {
			window.location.href = "https://stendhalgame.org/wiki/Stendhal_Beginner's_Guide";
			return true;
		},
		minParams: 0,
		maxParams: 0,
		getHelp: function(): string[] {
			return ["", "Opens beginner's guide wiki page on stendhalgame.org."];
		}
	};

	"characterselector" = new OpenWebsiteAction("https://stendhalgame.org/account/mycharacters.html");

	"faq" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_FAQ");

	"manual" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_Manual/Controls_and_Game_Settings");

	"profile": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["[<name>]", "Opens a player profile page on stendhalgame.org."];
		}
	};

	"rules" = new OpenWebsiteAction("https://stendhalgame.org/wiki/Stendhal_Rules");

	"changepassword" = new OpenWebsiteAction("https://stendhalgame.org/account/change-password.html");

	"loginhistory" = new OpenWebsiteAction("https://stendhalgame.org/account/history.html");

	"logout" = new OpenWebsiteAction("/account/logout.html");

	"halloffame" = new OpenWebsiteAction("https://stendhalgame.org/world/hall-of-fame/active_overview.html");

	"storemessage": SlashActionImpl = {
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
		maxParams: 1,
		getHelp: function(): string[] {
			return ["<player> <message>", "Store a private message to deliver for an offline #player."];
		}
	};

	/** Default action executed if a type is not registered. */
	"_default": SlashActionImpl = {
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

	/**
	 * Parses a slash action formatted string & executes the registered action.
	 *
	 * @param line {string}
	 *   Complete slash action line including parameters.
	 * @return {boolean}
	 *   `true` to represent successful execution.
	 */
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
		var action: SlashActionImpl;
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
	 * @param p {string}
	 *   The parameter to be amended.
	 * @param remainder {string}
	 *   String to be checked for quoted whitespace.
	 * @return {string}
	 *   Amended parameter.
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
