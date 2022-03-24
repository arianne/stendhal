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

var stendhal = window.stendhal = window.stendhal || {};

stendhal.config = {

	init: function(args) {
		this.sound = {
			play: false
		};

		this.gamescreen = {};
		this.gamescreen.blood = args.get("noblood") == null;
		this.gamescreen.shadows = args.get("noshadows") == null;

		this.character = args.get("char") || args.get("character") || args.get("name");
		this.itemDoubleClick = args.get("item_doubleclick") != null;
		this.moveCont = args.get("movecont") != null;

		// initialize custom theme
		const tmp = args.get("theme");
		if (tmp != null) {
			this.theme.set(tmp);
		}

		// store window information for this session
		this.dialogstates = {};
		this.dialogstates["menu"] = {x: 150, y: 20};
		this.dialogstates["settings"] = {x: 20, y: 20};
		this.dialogstates["travellog"] = {x: 160, y: 50};
		this.dialogstates["outfit"] = {x: 300, y: 50};
		this.dialogstates["chest"] = {x: 160, y: 370};
		this.dialogstates["corpse"] = {x: 160, y: 370};
	},

	theme: {
		/** Currently active theme ID. */
		current: "wood",

		/**
		 * Theme backgrounds indexed by ID.
		 */
		index: {
			"aubergine": "panel_aubergine_001.png",
			"brick": "panel_brick_brown_001.png",
			"honeycomb": "panel_honeycomb_001.png",
			"metal": "panelmetal003.gif",
			"parquet": "panel_parquet_brown_001.png",
			"stone": "paneldrock048.jpg",
			"tile": "panel_tile_aqua_001.png",
			"wood": "panelwood003.jpg",
			"wood2": "panelwood006.jpg"
		},

		/**
		 * Themes that should use light text.
		 */
		dark: {
			"aubergine": true,
			"brick": true,
			"metal": true,
			"parquet": true,
			"stone": true,
			"tile": true,
			"wood": true,
			"wood2": true
		},

		/**
		 * Sets the current theme.
		 *
		 * @param id
		 *     String identifier for theme.
		 */
		set: function(id) {
			if (this.index[id]) {
				this.current = id;
			}
		},

		/**
		 * Applies current theme to an element.
		 *
		 * @param element
		 *     Element to be updated.
		 * @param children
		 *     If <code>true</code>, theme will be applied to children elements
		 *     (default: <code>false</code>).
		 * @param recurse
		 *     If <code>true</code>, applies to all children recursively (default:
		 *     <code>false</code>).
		 */
		apply: function(element, children=false, recurse=false) {
			element.style.setProperty("background",
					"url(/data/gui/" + this.index[this.current] + ")");

			// make texts readable with dark & light themes
			let color = "#000000";
			if (this.dark[this.current]) {
				color = "#ffffff";
			}
			element.style.setProperty("color", color);

			if (children && element.children) {
				for (let idx = 0; idx < element.children.length; idx++) {
					this.apply(element.children[idx], recurse, recurse);
				}
			}
		}
	},
};
