/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

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

	"ban": {
		execute: function(type, params, remainder) {
			var action = {
				type: "ban",
				target: params[0],
				hours: params[1],
				reason: remainder
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
				type: type,
				text: remainder
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
				type: "gag",
				target: params[0],
				minutes: params[1],
				reason: remainder
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
				type: "jail",
				target: params[0],
				minutes: params[1],
				reason: remainder
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
					type: "tell",
					target: stendhal.slashActionRepository.lastPlayerTell,
					text: remainder
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
				type: "emote",
				text: remainder
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
				type: "tell",
				target: params[0],
				text: remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 1,
		getMaxParams: 1
	},

	"summon": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type,
				creature: params[0],
				x: params[1],
				y: params[2]
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
				target: params[0],
				slot: params[1],
				amount: params[2],
				item: remainder
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
				type: "support",
				text: remainder
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
				type: "supportanswer",
				target: params[0],
				text: remainder
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
				type: "teleport",
				target: params[0],
				zone: params[1],
				x: params[2],
				y: params[3]
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
				type: "teleportto",
				target: remainder,
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
				type: "tellall",
				text: remainder
			};
			marauroa.clientFramework.sendAction(action);
			return true;
		},
		getMinParams: 0,
		getMaxParams: 0
	},


	"_default": {
		execute: function(type, params, remainder) {
			var action = {
				"type": type
			};
			if (typeof(params[0] != "undefined")) {
				action.target = params[0];
				if (remainder != "") {
					action.args = remainder;
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
		if (typeof(this[name]) == "undefined") {
			action = this["_default"];
		} else {
			action = this[name];
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
}
stendhal.slashActionRepository.supporta = stendhal.slashActionRepository.supportanswer;
