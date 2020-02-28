/***************************************************************************
 *                    Copyright 2003-2020 © - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

window.marauroa = window.marauroa || {};
window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

stendhal.ui.settings = {

	// FIXME: needs to open a popup for the settings menu
	onOpenSettingsMenu: function(e) {
		// DEBUG:
		console.log("FIXME: not yet functional");

		/*
		if (stendhal.ui.globalpopup) {
			stendhal.ui.globalpopup.popup.close();
		}

		var content = "<div class=\"settingsmenu\"></div>";
		this.popup = new stendhal.ui.Popup("Settings", content, 150, 150);

		this.close = function() {
			this.popup.close();
			stendhal.ui.globalpopup = null;
		}
		stendhal.ui.globalpopup = this;
		//this.popup.open();

		//stendhal.ui.globalpopup.popup.open();
		*/
	}
}
