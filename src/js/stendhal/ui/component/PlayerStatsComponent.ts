/***************************************************************************
 *                (C) Copyright 2003-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { StatusesListComponent } from "./StatusesListComponent";
import { StatBarComponent } from "./StatBarComponent";
import { KarmaBarComponent } from "./KarmaBarComponent";

import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";

import { singletons } from "../../SingletonRepo";

import { Item } from "../../entity/Item";

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";


/**
 * displays the player stats
 */
export class PlayerStatsComponent extends Component {

	private readonly keys = ["hp", "base_hp", "atk", "atk_item", "atk_xp", "def", "def_item", "def_xp", "xp", "level"];
	private readonly LEVELS = 598;
	private MONEY_SLOTS = ["pouch", "bag", "lhand", "rhand"];
	private xp;

	private hpText: HTMLElement;
	private otherText: HTMLElement;

	private bars: any = {};


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

		const statuses = new StatusesListComponent();
		ui.registerComponent(UIComponentEnum.StatusesList, statuses);

		this.enableCharName(stendhal.config.getBoolean("panel.stats.charname"));

		this.hpText = this.child("#hptext")!;
		this.otherText = this.child("#otherstats")!;

		this.bars["karma"] = new KarmaBarComponent();
		// hide karma bar by default
		this.enableBar("karma", false);

		this.bars["hp"] = new StatBarComponent("hpbar");
		// use config to determine if HP bar should be visible
		this.enableBar("hp", singletons.getConfigManager()
				.getBoolean("panel.stats.hpbar"));
	}

	update(key: string) {
		if (this.keys.indexOf(key) < -1) {
			return;
		}

		const object = marauroa.me;
		this.updateKarma(object["karma"]);
		this.updateHp(object["hp"], object["base_hp"]);
		this.updateOther(object);

		if (!this.isBarEnabled("karma") && object) {
			const features = object["features"];
			if (features && features["karma_indicator"] != null) {
				this.enableBar("karma");
			}
		}
	}

	/**
	 * Updates karma bar.
	 *
	 * @param karma
	 *     New karma value.
	 */
	private updateKarma(karma: number) {
		if (this.isBarEnabled("karma")) {
			this.bars["karma"].draw(karma);
		}
	}

	/**
	 * Updates HP value & draws bar.
	 *
	 * @param hp
	 *     Player's actual HP.
	 * @param base_hp
	 *     Player's potential max HP.
	 */
	private updateHp(hp: number, base_hp: number) {
		this.hpText.innerText = "HP: " + hp + " / " + base_hp;
		if (this.isBarEnabled("hp")) {
			this.bars["hp"].draw(hp / base_hp);
		}
	}

	/**
	 * Updates all other stat values.
	 *
	 * @param object
	 *     Owner of stats.
	 */
	private updateOther(object: any) {
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

		this.otherText.innerText =
			"ATK: " + atk + " x " + object["atk_item"] + "\r\n  (" + atkTNL + ")\r\n"
			+ "DEF: " + def + " x " + object["def_item"] + "\r\n  (" + defTNL + ")\r\n"
			+ "XP: " + xp + "\r\n"
			+ "Level: " + lvl + "\r\n  (" + xpTNL + ")\r\n"
			+ "Money: " + this.calculateMoney();
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

	/**
	 * Retrieves amount of money being carried by player.
	 *
	 * @return
	 *     Total money from relevant slots.
	 */
	private calculateMoney(): number {
		let mo = 0;
		if (marauroa.me) {
			for (const sname of this.MONEY_SLOTS) {
				const slot = marauroa.me[sname];
				if (slot) {
					for (let idx = 0; idx < slot.count(); idx++) {
						const o = slot.getByIndex(idx);
						if (o instanceof Item) {
							const i = <Item> o;
							if (i["name"] === "money") {
								mo += parseInt(i["quantity"]);
							}
						}
					}
				}
			}
		}
		return mo;
	}

	/**
	 * Sets visibility of character name in status panel.
	 */
	enableCharName(visible=true) {
		const charname = document.getElementById("charname")! as HTMLDivElement;
		if (visible) {
			charname.style["display"] = "block";
		} else {
			charname.style["display"] = "none";
		}
	}

	/**
	 * Enables or disables drawing of stat bars.
	 *
	 * @param
	 *     Bar identifier string.
	 * @param visible
	 *     If true, bar will be drawn.
	 */
	enableBar(id: string, visible=true) {
		const bar = this.bars[id];
		if (bar) {
			bar.setVisible(visible);
		}
	}

	isBarEnabled(id: string): boolean {
		const bar = this.bars[id];
		if (bar) {
			return bar.isVisible();
		}
		return false;
	}
}
