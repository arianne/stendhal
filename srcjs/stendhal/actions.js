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
				action.newlevel = params[1];
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


	"help": {
		execute: function(type, params, remainder) {
			var msg = [
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal_Manual",
				"Here are the most-used commands:",
				"* CHATTING:",
				"- /me <action> \tShow a message about what you are doing.",
				"- /tell <player> <message> \tSend a private message to #player.",
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
				"- /ignore <player> [minutes|*|- [reason...]] \t\tAdd #player to your ignore list.",
				"- /ignore \tFind out who is on your ignore list.",
				"- /unignore <player> \t\tRemove #player from your ignore list.",
/*				"* STATUS:",
				"- /away <message> \t\tSet an away message.",
				"- /away \tRemove away status.",
				"- /grumpy <message> \t\tSet a message to ignore all non-buddies.",
				"- /grumpy \tRemove grumpy status.", */
				"- /name <pet> <name> \t\tGive a name to your pet.",
/*				"- /profile [name] \tOpens a player profile page on stendhalgame.org.",
				"* PLAYER CONTROL:",
				"- /clickmode \tSwitches between single click mode and double click mode.",
				"- /walk \tToggles autowalk on/off.",
				"- /stopwalk \tTurns autowalk off.",
				"- /movecont \tToggle continuous movement (allows players to continue walking after map change or teleport without releasing direction key).",
				"* CLIENT SETTINGS:",
				"- /mute \tMute or unmute the sounds.",
				"- /volume \tLists or sets the volume for sound and music.",*/
				"* MISC:",
				"- /info \t\tFind out what the current server time is.",
				"- /clear \tClear chat log.",
				"- /help \tShow help information."
			];
			for (var i = 0; i < msg.length; i++) {
				stendhal.ui.chatLog.addLine("info", msg[i]);
			}
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
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

	"summon": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"creature": params[0],
				"x": params[1],
				"y": params[2]
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 3,
		getMaxParams: 3
	},

	"summonat": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				"target": params[0],
				"slot": params[1],
				"amount": params[2],
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
