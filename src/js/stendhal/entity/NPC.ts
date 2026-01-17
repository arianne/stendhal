/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RPEntity } from "./RPEntity";

import { EntityOverlayRegistry } from "../data/EntityOverlayRegistry";

import { Color } from "../data/color/Color";
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";

export class NPC extends RPEntity {
	override minimapStyle = Color.NPC;
	override spritePath = "npc";
	override titleStyle = "#c8c8ff";

	constructor() {
		super();
		this["hp"] = 100;
		this["base_hp"] = 100;
	}

	override set(key: string, value: any) {
		super.set(key, value);

		if (key === "name") {
			// overlay animation
			this.overlay = EntityOverlayRegistry.get("NPC", this);

			if (value.startsWith("Zekiel")) {
				// Zekiel uses transparentnpc sprite but he is taller
				this.titleDrawYOffset = -32;
			}
		}
	}

	override drawTop(ctx: RenderingContext2D) {
		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		if (typeof(this["no_hpbar"]) == "undefined") {
			this.drawHealthBar(ctx, localX, localY + this.statusBarYOffset);
		}
		if (typeof(this["unnamed"]) == "undefined") {
			this.drawTitle(ctx, localX, localY + this.statusBarYOffset);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
