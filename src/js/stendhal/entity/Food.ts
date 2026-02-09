/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";
import { Paths } from "../data/Paths";

import { marauroa } from "marauroa"
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";


export class Food extends Entity {

	override zIndex = 5000;

	override init() {
		this.imageSprite = new ImageSprite(
			images.load(Paths.sprites + "/food.png"),
			0, this["amount"] * 32, 32, 32);
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "amount") {
			if (this.imageSprite) {
				this.imageSprite.offsetY = this["amount"] * 32;
			}
		}
		// TODO: play sound effect
	}

	override onclick(_x: number, _y: number) {
		var action = {
				"type": "look",
				"target": "#" + this["id"]
			};
		marauroa.clientFramework.sendAction(action);
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
