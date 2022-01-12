/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

declare var marauroa: any;

/**
 * displays the player stats
 */
export class PlayerStatsComponent extends Component {

	private readonly keys = ["hp", "base_hp", "atk", "atk_item", "atk_xp", "def", "def_item", "def_xp", "xp", "level"];

	constructor() {
		super("stats");
	}

	update(key: string) {
		if (this.keys.indexOf(key) < -1) {
			return;
		}
		let object = marauroa.me;
		this.componentElement.innerText =
			"HP: " + object["hp"] + " / " + object["base_hp"] + "\r\n"
			+ "ATK: " + object["atk"] + " x " + object["atk_item"] + "\r\n"
			+ "DEF: " + object["def"] + " x " + object["def_item"] + "\r\n"
			+ "XP: " + object["xp"] + "\r\n"
			+ "Level: " + object["level"];
	}

}
