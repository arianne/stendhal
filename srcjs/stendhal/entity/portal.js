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
	zIndex: 5000,

	buildActions: function(list) {
		marauroa.rpobjectFactory["portal"].proto.buildActions.apply(this, arguments);

		if (this["_rpclass"] == "house_portal") {
			list.push({
				title: "Use",
				type: "use"
			});
			list.push({
				title: "Kock",
				type: "knock"
			});

		} else {

			// remove default action "look" unless it is a house portal
			list.splice(list.indexOf({title: "Look", type: "look"}), 1);

			list.push({
				title: "Use",
				type: "use"
			});
		}
	},

	isVisibleToAction: function(filter) {
		return true;
	},


	/**
	 * Create the default action for this entity. If the entity specifies a
	 * default action description, interpret it as an action command.
	 */
	getDefaultAction: function() {
		return {
			"type": "moveto",
			"x": "" + this["x"],
			"y": "" + this["y"],
			"zone": marauroa.currentZoneName
		};
	},

	getCursor: function(x, y) {
		return "url(/data/sprites/cursor/portal.png) 1 3, auto";
	}

});

marauroa.rpobjectFactory["house_portal"] = marauroa.rpobjectFactory["portal"];
