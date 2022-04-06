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
	private readonly LEVELS = 598;
	private xp;

	constructor() {
		super("stats");

		this.xp = [this.LEVELS + 1];
		this.xp[0] = 0;
		this.xp[1] = 50;
		this.xp[2] = 100;
		this.xp[3] = 200;
		this.xp[4] = 400;
		this.xp[5] = 800;

		for (let i = 5; i < this.LEVELS; i++) {
			const exp = Math.floor((i * 16 + i * i * 5 + i * i * i * 10 + 300) / 100) * 100;
			this.xp[i + 1] = exp;
		}
	}

	update(key: string) {
		if (this.keys.indexOf(key) < -1) {
			return;
		}
		const object = marauroa.me;

		const atk = object["atk"];
		const atkXP = object["atk_xp"];
		const def = object["def"];
		const defXP = object["def_xp"];
		const atkTNL = this.getAtkDefTNL(atk, atkXP);
		const defTNL = this.getAtkDefTNL(def, defXP);
		const lvl = parseInt(object["level"], 10);
		const xp = object["xp"];
		// show dash for max level
		let xpTNL: number|string = (lvl < this.getMaxLevel()) ? this.getTNL(lvl, xp) : "-";

		this.componentElement.innerText =
			"HP: " + object["hp"] + " / " + object["base_hp"] + "\r\n"
			+ "ATK: " + atk + " x " + object["atk_item"] + "\r\n  (" + atkTNL + ")\r\n"
			+ "DEF: " + def + " x " + object["def_item"] + "\r\n  (" + defTNL + ")\r\n"
			+ "XP: " + xp + "\r\n"
			+ "Level: " + lvl + "\r\n  (" + xpTNL + ")";
	}

	/**
	 * Retrieves amount of experience required to reach a level.
	 *
	 * @param lvl
	 *     Desired level.
	 * @param xp
	 *     Current XP.
	 * @return
	 *     Remaining XP.
	 */
	private getTNL(lvl: number, xp: number): number {
		return this.getReqXP(lvl + 1) - xp;
	}

	/**
	 * Retrieves amount of experience required to reach an ATK/DEF level.
	 *
	 * @param lvl
	 *     Desired level.
	 * @param xp
	 *     Current XP.
	 * @return
	 *     Remaining XP.
	 */
	private getAtkDefTNL(lvl: number, xp: number): number {
		return this.getReqXP(lvl - 9) - xp;
	}

	/**
	 * Retrieves the amount of XP required to reach a level.
	 *
	 * @param lvl
	 *     The respective level.
	 * @return
	 *     Amount of expericence.
	 */
	private getReqXP(lvl: number): number {
		if ((lvl >= 0) && (lvl < this.xp.length)) {
			return this.xp[lvl];
		}
		return -1;
	}

	/**
	 * Retrieves highest possible player level.
	 */
	private getMaxLevel(): number {
		return this.LEVELS - 1;
	}
}
