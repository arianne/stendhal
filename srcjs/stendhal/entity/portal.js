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

/**
 * Portal
 */
marauroa.rpobjectFactory["portal"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	minimapShow: true,
	minimapStyle: "rgb(0,0,0)",
	zIndex: 5000
});
