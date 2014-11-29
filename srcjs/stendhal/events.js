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
		if (stendhal.ui.sound) {
			stendhal.ui.sound.playEffect(this.sound);
		}
	}
});
