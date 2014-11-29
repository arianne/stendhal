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

/**
 * Player
 */
marauroa.rpobjectFactory.player = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity, {

	minimapShow: true,
	minimapStyle: "rgb(255, 255, 255)",
	dir: 3,

	/**
	 * Is this player an admin?
	 */
	isAdmin: function() {
		return (typeof(this.adminlevel) != "undefined" && this.adminlevel > 600);
	},

	/** 
	 * Can the player hear this chat message?
	 */
	isInHearingRange: function(entity) {
		return (this.isAdmin() 
			|| ((Math.abs(this.x - entity.x) < 15) 
				&& (Math.abs(this.y - entity.y) < 15)));
	}
});

