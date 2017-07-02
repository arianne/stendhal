/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};


marauroa.rpeventFactory["attack"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(entity) {
		
		var target = entity.getAttackTarget();
		if (!target) {
			return;
		}
		if (this.hasOwnProperty("hit")) {
			var damage = parseInt(this["damage"], 10);
			if (damage !== 0) {
				target.onDamaged(entity, damage);
			} else {
				target.onBlocked(entity);
			}
		} else {
			target.onMissed(entity);
		}
		entity.onAttackPerformed(this["type"], this.hasOwnProperty("ranged"));
	}
});


marauroa.rpeventFactory["examine"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ExamineEvent();
	}
});


marauroa.rpeventFactory["global_visual_effect"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new GlobalVisualEffectEvent();
	}
});


marauroa.rpeventFactory["group_change_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new GroupChangeEvent();
	}
});


marauroa.rpeventFactory["group_invite_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new GroupInviteEvent();
	}
});


marauroa.rpeventFactory["image_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ImageEffectEvent();
	}
});


marauroa.rpeventFactory["player_logged_on"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new PlayerLoggedOnEvent();
	}
});


marauroa.rpeventFactory["player_logged_out"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new PlayerLoggedOutEvent();
	}
});


marauroa.rpeventFactory["private_text"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		stendhal.ui.chatLog.addLine(this["texttype"].toLowerCase(), this["text"]);
	}
});


marauroa.rpeventFactory["progress_status_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ProgressStatusEvent();
	}
});


marauroa.rpeventFactory["reached_achievement"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ReachedAchievementEvent();
	}
});


marauroa.rpeventFactory["show_item_list"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		if (this.hasOwnProperty("title")) {
			stendhal.ui.chatLog.addLine("normal", this["title"]);
		}
		if (this.hasOwnProperty("caption")) {
			stendhal.ui.chatLog.addLine("normal", this["caption"]);
		}
		if (this.hasOwnProperty("content")) {
			stendhal.ui.chatLog.addLine("normal", "Item\t-\tPrice\t-\tDescription");
			for (var obj in this["content"]) {
				if (this["content"].hasOwnProperty(obj)) {
					var slotObj = this["content"][obj];
					var data = this["content"][obj]["a"];
					stendhal.ui.chatLog.addLine("normal", data["subclass"] + "\t"
							+ data["price"] + "\t" + data["description_info"]);
				}
			}
		}
	}
});


marauroa.rpeventFactory["sound_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		var volume = 1;
		// Adjust by the server specified volume, if any
		if (this.hasOwnProperty("volume")) {
			volume *= parseInt(this["volume"], 10) / 100;
		}
		// Further adjustments if the sound has a radius
		if (this.hasOwnProperty("radius")) {
			if (!marauroa.me) {
				// Can't calculate the distance yet. Ignore the sound.
				return;
			}
			var radius = parseInt(this["radius"], 10);
			var xdist = marauroa.me["_x"] - rpobject["_x"];
			var ydist = marauroa.me["_y"] - rpobject["_y"];
			var dist2 = xdist * xdist + ydist * ydist;
			if (dist2 > radius * radius) {
				// Outside the specified radius
				return;
			}
			// The sound api does not guarantee anything about how the volume
			// works, so it does not matter much how we scale it.
			volume *= Math.min(radius * radius / (dist2 * 20), 1);
		}
		if (stendhal.ui.sound) {
			stendhal.ui.sound.playEffect(this["sound"], volume);
		}
	}
});


marauroa.rpeventFactory["text"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		rpobject.say(this["text"]);
	}
});


marauroa.rpeventFactory["trade_state_change_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new TradeStateChangeEvent();
	}
});


marauroa.rpeventFactory["transition_graph"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new TransitionGraphEvent();
	}
});


marauroa.rpeventFactory["view_change"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ViewChangeEvent();
	}
});
