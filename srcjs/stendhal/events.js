/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

"use strict";

marauroa.rpeventFactory.attack = marauroa.util.fromProto(marauroa.rpeventFactory._default, {
	execute: function(entity) {
		
		var target = entity.getAttackTarget();
		if (!target) {
			return;
		}
		if (this.hasOwnProperty("hit")) {
			var damage = parseInt(this.damage);
			if (damage != 0) {
				target.onDamaged(entity, damage);
			} else {
				target.onBlocked(entity);
			}
		} else {
			target.onMissed(entity);
		}
		entity.onAttackPerformed(this.type, this.hasOwnProperty("ranged"));
	}
});


marauroa.rpeventFactory.private_text = marauroa.util.fromProto(marauroa.rpeventFactory._default, {
	execute: function(rpobject) {
		stendhal.ui.chatLog.addLine(this.texttype.toLowerCase(), this.text);
	}
});


marauroa.rpeventFactory.text = marauroa.util.fromProto(marauroa.rpeventFactory._default, {
	execute: function(rpobject) {
		rpobject.say(this.text);
	}
});


marauroa.rpeventFactory.sound_event = marauroa.util.fromProto(marauroa.rpeventFactory._default, {
	execute: function(rpobject) {
		var volume = 1;
		// Adjust by the server specified volume, if any
		if (this.hasOwnProperty("volume")) {
			volume *= parseInt(this.volume) / 100;
		}
		// Further adjustments if the sound has a radius
		if (this.hasOwnProperty("radius")) {
			if (!marauroa.me) {
				// Can't calculate the distance yet. Ignore the sound.
				return;
			}
			var radius = parseInt(this.radius);
			var xdist = marauroa.me._x - rpobject._x;
			var ydist = marauroa.me._y - rpobject._y;
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
			stendhal.ui.sound.playEffect(this.sound, volume);
		}
	}
});

marauroa.rpeventFactory.show_item_list = marauroa.util.fromProto(marauroa.rpeventFactory._default, {
	execute: function(rpobject) {
		if (this.hasOwnProperty("title")) {
			stendhal.ui.chatLog.addLine("normal", this.title);
		}
		if (this.hasOwnProperty("caption")) {
			stendhal.ui.chatLog.addLine("normal", this.caption);
		}
		if (this.hasOwnProperty("content")) {
			stendhal.ui.chatLog.addLine("normal", "Item\t-\tPrice\t-\tDescription");
			for (var obj in this.content) {
				if (this.content.hasOwnProperty(obj)) {
					var slotObj = this.content[obj];
					console.log("logging thingy: " + obj + " : " + typeof(slotObj));
					var data = this.content[obj].a;
					stendhal.ui.chatLog.addLine("normal", data["subclass"] + "\t"
							+ data["price"] + "\t" + data["description_info"]);
				}
			}
		}
	}
});
