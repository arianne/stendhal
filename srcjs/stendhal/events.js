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
		if (rpobject !== marauroa.me) {
			return;
		}
		new stendhal.ui.ImageViewer(this["title"], this["caption"], this["path"]);
	}
});


marauroa.rpeventFactory["global_visual_effect"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new GlobalVisualEffectEvent();
	}
});


marauroa.rpeventFactory["group_change_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		if (rpobject !== marauroa.me) {
			return;
		}
		stendhal.data.group.updateGroupStatus(this["members"], this["leader"], this["lootmode"]);
	}
});


marauroa.rpeventFactory["group_invite_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		if (rpobject !== marauroa.me) {
			return;
		}
		if (this["expire"]) {
			stendhal.ui.chatLog.addLine("normal", "Your group invite by " + this["leader"] + " has expired.");
		} else {
			stendhal.ui.chatLog.addLine("normal", "Your have been invited by " + this["leader"] + " to join a group.");
			stendhal.ui.chatLog.addLine("normal", "To join, type: /group join " + this["leader"]);
			stendhal.ui.chatLog.addLine("normal", "To leave the group at any time, type: /group part " + this["leader"]);
		}
	}
});


marauroa.rpeventFactory["image_event"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		// TODO: new ImageEffectEvent();
		console.log("image_event", this, rpobject);
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
		var progressType = this["progress_type"];
		var dataItems = this["data"].substring(1, this["data"].length - 1).split(/\t/);

		if (!this["progress_type"]) {
			stendhal.ui.travellog.open(dataItems);
		} else if (!this["item"]) {
			stendhal.ui.travellog.progressTypeData(progressType, dataItems);
		} else {
			stendhal.ui.travellog.itemData(progressType, this["item"], this["description"], dataItems);
		}
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
		var radius = parseInt(this["radius"], 10);

		stendhal.ui.sound.playLocalizedEffect(rpobject["_x"], rpobject["_y"], radius, this["layer"], this["sound"], volume);
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
// Dummy comment to prevent accidental re-push of a rebase done into the wrong direction

marauroa.rpeventFactory["bestiary"] = marauroa.util.fromProto(marauroa.rpeventFactory["_default"], {
	execute: function(rpobject) {
		if (!this.hasOwnProperty("enemies")) {
			// FIXME: proper logging of errors?
			console.log("ERROR: event does not have \"enemies\" attribute");
			return;
		}

		const title = "Bestiary";
		var header = ["\"???\" = unknown"];

		const hasRare = this["enemies"].includes("(rare)");
		const hasAbnormal = this["enemies"].includes("(abnormal)");

		// show explanation of "rare" & "abnormal" creatures in header
		if (hasRare || hasAbnormal) {
			var subheader = "";
			if (!hasRare) {
				subheader += "\"abnormal\"";
			} else {
				subheader += "\"rare\"";
				if (hasAbnormal) {
					subheader += " and \"abnormal\"";
				}
			}

			header[1] = subheader + " creatures not required for achievements";
		}

		// spacing for clarity
		header[2] = "------------------";

		// FIXME: hack until a proper window is implemented
		stendhal.ui.chatLog.addLine("normal", title + ":");
		for (h of header) {
			stendhal.ui.chatLog.addLine("normal", h);
		}

		const enemies = this["enemies"].split(";");
		for (e of enemies) {
			const info = e.split(",");
			const name = info[0];
			var solo = " ";
			var shared = " ";
			if (info[1] == "true") {
				solo = "✔";
			}
			if (info[2] == "true") {
				shared = "✔";
			}

			// FIXME: hack until a proper window is implemented
			stendhal.ui.chatLog.addLine("normal", name + ":   solo [" + solo + "], shared [" + shared + "]");
		}
	}
});
