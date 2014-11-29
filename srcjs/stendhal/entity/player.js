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
marauroa.rpobjectFactory.player = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity);
marauroa.rpobjectFactory.player.minimapShow = true;
marauroa.rpobjectFactory.player.minimapStyle = "rgb(255, 255, 255)";
marauroa.rpobjectFactory.player.dir = 3;



/** Is this player an admin? */
marauroa.rpobjectFactory.player.isAdmin = function() {
	return (typeof(this.adminlevel) != "undefined" && this.adminlevel > 600);
}

/** Can the player hear this chat message? */
marauroa.rpobjectFactory.player.isInHearingRange = function(entity) {
	return (this.isAdmin() || ((Math.abs(this.x - entity.x) < 15) && (Math.abs(this.y - entity.y) < 15)));
}

