/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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
window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * buddylist
 */
stendhal.ui.stats = {
	update: function() {
		var div = document.getElementById("stats");
		div.update(marauroa.me);
	}
}