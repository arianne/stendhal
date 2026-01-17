/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";

import { marauroa } from "marauroa"

/**
 * zone info
 */
export class ZoneInfoComponent extends Component {
	private readonly DANGER_LEVEL_DESCRIPTIONS = [
		"The area feels safe.",
		"The area feels relatively safe.",
		"The area feels somewhat dangerous.",
		"The area feels dangerous.",
		"The area feels very dangerous!",
		"The area feels extremely dangerous. Run away!"];


	constructor() {
		super("zoneinfo");
	}


	public zoneChange(zoneinfo: any) {
		document.getElementById("zonename")!.textContent = zoneinfo["readable_name"];
		if (marauroa.me) {
			let dangerLevel = Number.parseFloat(zoneinfo["danger_level"]);
			let skulls = Math.min(5, Math.round(2 * dangerLevel / (Number.parseInt(marauroa.me.level, 10) + 3)));
			let div = document.getElementById("skulls")!;
			if (skulls === 0) {
				div.style.height = "0";
			} else {
				div.style.height = "16px";
			}
			div.style.width = (skulls * 20) + "px";
			this.componentElement.title = this.DANGER_LEVEL_DESCRIPTIONS[skulls];
		}
	};

}
