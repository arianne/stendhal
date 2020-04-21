/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};

stendhal.slashActionRepository = {
	"add": {
		execute: function(type, params, remainder) {
			if (params == null) {
				return false;
			};

			const action = {
				"type": "addbuddy",
				"target": params[0]
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"adminnote": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"target": params[0],
				"note": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"adminlevel": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"target": params[0],
			};
			if (params.length >= 2) {
				action["newlevel"] = params[1];
			}
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 2
	},

	"alter": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"target": params[0],
				"stat": params[1],
				"mode": params[2],
				"value": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 3,
		getMaxParams: 3
	},

	"altercreature": {
		execute: function(type, params, remainder) {
			const action = {
				"type": "altercreature",
				"target": params[0],
				"text": params[1]
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 2
	},

	"alterkill": {
		execute: function(type, params, remainder) {
			const target = params[0];
			const killtype = params[1];
			const count = params[2];
			var creature = null;

			if (remainder != null && remainder != "") {
				// NOTE: unlike Java client, Javascript client automatically trims whitespace in "remainder" parameter
				creature = remainder;
			}

			const action = {
				"type": "alterkill",
				"target": target,
				"killtype": killtype,
				"count": count
			};
			if (creature != null) {
				action["creature"] = creature;
			}

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 3,
		getMaxParams: 3
	},

	"alterquest": {
		execute: function(type, params, remainder) {
			const action = {
				"type": "alterquest",
				"target": params[0],
				"name": params[1]
			};

			if (params[2] != null) {
				action["state"] = params[2];
			}

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 3
	},

	"answer": {
		execute: function(type, params, remainder) {
			if (remainder == null || remainder == "") {
				return false;
			};

			const action = {
				"type": "answer",
				"text": remainder
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 0
	},

	"away": {
		execute: function(type, params, remainder) {
			var msg = null;
			if (remainder.length != 0) {
				msg = remainder;
			};

			var action = {
				"type": "away",
				"message": msg
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"ban": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "ban",
				"target": params[0],
				"hours": params[1],
				"reason": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 2
	},

	"chat": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"clear": {
		execute: function(type, params, remainder) {
			stendhal.ui.chatLog.clear();
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"drop": {
		execute: function(type, params, remainder) {
			console.log(type, params, remainder);
			let name = remainder;
			let quantity = parseInt(params[0], 10);
			console.log(name, quantity);
			if (isNaN(quantity)) {
				name = (params[0] + " " + remainder).trim();
				quantity = 0;
			}
			console.log(name, quantity);
			var action = {
				"type": "drop",
				"source_name": name,
				"quantity": "" + quantity,
				"x": "" + marauroa.me.x,
				"y": "" + marauroa.me.y
			};
			console.log(action);
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 1
	},

	"gag": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "gag",
				"target": params[0],
				"minutes": params[1],
				"reason": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 2
	},

	"group": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "group_management",
				"action": params[0],
				"params": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"grumpy": {
		execute: function(type, params, remainder) {
			var reason = null;
			if (remainder.length != 0) {
				reason = remainder;
			};

			var action = {
				"type": "grumpy",
				"reason": reason
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},


	"help": {
		execute: function(type, params, remainder) {
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
/*				"* CLIENT SETTINGS:",
				"- /mute \tMute or unmute the sounds.",
				"- /volume \tLists or sets the volume for sound and music.",*/
				"* MISC:",
				"- /info \t\tFind out what the current server time is.",
				"- /clear \tClear chat log.",
				"- /help \tShow help information.",
				"- /removedetail \tRemove the detail layer (e.g. balloon, umbrella, etc.) from character."
			];
			for (var i = 0; i < msg.length; i++) {
				stendhal.ui.chatLog.addLine("info", msg[i]);
			}
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},


	"gmhelp": {
		execute: function(type, params, remainder) {
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
				stendhal.ui.chatLog.addLine("info", msg[i]);
			}

			return true;
		},
		getMinParams: 0,
		getMaxParams: 1
	},


	"ignore": {
		execute: function(type, params, remainder) {
			const action = {
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
					if (duration != "*" || duration != "-") {
						/*
						 * Validate it's a number
						 */
						if (isNaN(duration)) {
							return false;
						}

						action["duration"] = duration;
					}
				}

				if (remainder.length != 0) {
					action["reason"] = remainder;
				}
			}

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 2
	},

	"inspectkill": {
		execute: function(type, params, remainder) {
			const target = params[0];
			var creature = null;
			if (remainder != null && remainder != "") {
				// NOTE: unlike Java client, Javascript client automatically trims whitespace in "remainder" parameter
				creature = remainder;
			}

			const action = {
				"type": "inspectkill",
				"target": target
			};
			if (creature != null) {
				action["creature"] = creature;
			}

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"inspectquest": {
		execute: function(type, params, remainder) {
			var action = {
					"type": "inspectquest",
					"target": params[0],
					"quest_slot": params[1]
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 2
	},


	"jail": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "jail",
				"target": params[0],
				"minutes": params[1],
				"reason": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 2,
		getMaxParams: 2
	},

	"/" : {
		execute: function(type, params, remainder) {
			if (typeof(stendhal.slashActionRepository.lastPlayerTell) != "undefined") {
				var action = {
					"type": "tell",
					"target": stendhal.slashActionRepository.lastPlayerTell,
					"text": remainder
				};
				marauroa.clientFramework.sendAction(action);
				return true;
			}
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"me": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "emote",
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"movecont": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "move.continuous",
			};

			const state = params[0].toLowerCase();

			if (state == "on") {
				action["move.continuous"] = "";
			} else if (state != "off") {
				stendhal.ui.chatLog.addLine("error", "Argument must be either \"on\" or \"off\".");
				return false;
			};

			marauroa.clientFramework.sendAction(action);

			var msg = "Continuous movement ";
			if (state == "on") {
				msg += "enabled";
			} else {
				msg += "disabled";
			}
			msg += ".";

			stendhal.ui.chatLog.addLine("info", msg);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"msg" : {
		execute: function(type, params, remainder) {
			stendhal.slashActionRepository.lastPlayerTell = params[0];
			var action = {
				"type": "tell",
				"target": params[0],
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"p": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "group_message",
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"progressstatus": {
		execute: function(type, params, remainder) {
			var action = {
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
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"remove": {
		execute: function(type, params, remainder) {
			if (params == null) {
				return false;
			}

			const action = {
				"type": "removebuddy",
				"target": params[0]
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"sentence": {
		execute: function(type, params, remainder) {
			if (params == null) {
				return false;
			};

			const action = {
				"type": "sentence",
				"value": remainder
			};

			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"settings": {
		execute: function(type, params, remainder) {
			stendhal.ui.settings.onOpenSettingsMenu();
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"stopwalk": {
		execute: function(type, params, remainder) {
			const action = {
				"type": "walk",
				"mode": "stop"
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"summon": {
		execute: function(type, params, remainder) {
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

			var action = {
				"type": type,
				"creature": creature,
				"x": x,
				"y": y,
				"quantity": quantity
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: -1 // XXX: is this the proper way to allow an unlimited number of arguments?
	},

	"summonat": {
		execute: function(type, params, remainder) {
			var amount = params[2];
			// don't require first parameter to be integer amount
			if (isNaN(amount)) {
				if (remainder) {
					remainder = amount + " " + remainder;
				} else {
					remainder = amount;
				}
				amount = "1";
			}

			var action = {
				"type": type,
				"target": params[0],
				"slot": params[1],
				"amount": amount,
				"item": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 3,
		getMaxParams: 3
	},

	"support": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "support",
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"supportanswer" : {
		execute: function(type, params, remainder) {
			var action = {
				"type": "supportanswer",
				"target": params[0],
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"teleport": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "teleport",
				"target": params[0],
				"zone": params[1],
				"x": params[2],
				"y": params[3]
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 4,
		getMaxParams: 4
	},

	"teleportto": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "teleportto",
				"target": remainder,
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"tellall": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "tellall",
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"walk": {
		execute: function(type, params, remainder) {
			const action = {
				"type": "walk"
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"atlas": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/world/atlas.html?me="
				+ marauroa.currentZoneName + "." + marauroa.me.x + "." + marauroa.me.y;
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"beginnersguide": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/wiki/Stendhal_Beginner's_Guide";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"characterselector": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/account/mycharacters.html";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"faq": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/wiki/Stendhal_FAQ";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"manual": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/wiki/Stendhal_Manual/Controls_and_Game_Settings";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"profile": {
		execute: function(type, params, remainder) {
			var url = "https://stendhalgame.org/character/";
			var name = null;

			if (params.length > 0 && params[0] != null) {
				name = params[0];
			} else {
				name = marauroa.me["_name"];
				if (name == null) {
					// DEBUG:
					console.log("Getting default username failed!");

					return true;
				}
			}

			url += name + ".html";
			stendhal.ui.chatLog.addLine("info", "Trying to open #" + url + " in your browser.");
			window.location = url;
			return true;
		},
		getMinParams: 0,
		getMaxParams: 1
	},

	"rules": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/wiki/Stendhal_Rules";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"changepassword": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/account/change-password.html";
		},
		getMinParams: 0,
		getMaxParams: 0
	},


	"loginhistory": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/account/history.html";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"halloffame": {
		execute: function(type, params, remainder) {
			window.location = "https://stendhalgame.org/world/hall-of-fame/active_overview.html";
		},
		getMinParams: 0,
		getMaxParams: 0
	},

	"storemessage": {
		execute: function(type, params, remainder) {
			var action = {
				"type": "storemessage",
				"target": params[0],
				"text": remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"_default": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type
			};
			if (typeof(params[0] != "undefined")) {
				action["target"] = params[0];
				if (remainder != "") {
					action["args"] = remainder;
				}
			}
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 1
	},

	execute: function(line) {
		var array = line.trim().split(" ");

		// clean whitespace
		for (var i in array) {
			array[i] = array[i].trim();
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
		var action;
		if (typeof(stendhal.slashActionRepository[name]) == "undefined") {
			action = stendhal.slashActionRepository["_default"];
		} else {
			action = stendhal.slashActionRepository[name];
		}

		// use executing character if name parameter not supplied
		if (name == "where" && array.length == 0) {
			array[0] = marauroa.me["_name"];
		}

		if (action.getMinParams <= array.length) {
			var remainder = "";
			for (var i = action.getMaxParams; i < array.length; i++) {
				remainder = remainder + array[i] + " ";
			}
			array.slice(action.getMaxParams);
			return action.execute(name, array, remainder.trim());
		} else {
			stendhal.ui.chatLog.addLine("error", "Missing arguments. Try /help");
			return false;
		}
	}
};
// answer, sentence, drop, add, remove, away, grumpy, profile, clickmode, walk, stopwalk, movecont, mute, settings
stendhal.slashActionRepository["supporta"] = stendhal.slashActionRepository["supportanswer"];
stendhal.slashActionRepository["tell"] = stendhal.slashActionRepository["msg"];
