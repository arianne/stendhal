/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
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

/**
 * InvisibleEntity
 */
marauroa.rpobjectFactory["invisible_entity"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {

	isVisibleToAction: function(filter) {
		return false;
	}

});

marauroa.rpobjectFactory["area"] = marauroa.rpobjectFactory["invisible_entity"];
marauroa.rpobjectFactory["looped_sound_source"] = marauroa.rpobjectFactory["invisible_entity"];
marauroa.rpobjectFactory["tiled_entity"] = marauroa.rpobjectFactory["invisible_entity"];
marauroa.rpobjectFactory["wall"] = marauroa.rpobjectFactory["invisible_entity"];
marauroa.rpobjectFactory["blocktarget"] = marauroa.rpobjectFactory["invisible_entity"];
