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


/**
 * NPC
 */
marauroa.rpobjectFactory["npc"] = marauroa.util.fromProto(marauroa.rpobjectFactory["rpentity"], {
	minimapStyle: "rgb(0,0,255)",
	spritePath: "npc",
	titleStyle: "#c8c8ff",
	hp: 100,
	base_hp: 100
});
