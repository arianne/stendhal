/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RPEntity } from "./RPEntity";

import { Color } from "../util/Color";

declare var marauroa: any;
declare var stendhal: any;

export class Creature extends RPEntity {

	override minimapStyle = Color.CREATURE;
	override spritePath = "monsters";
	override titleStyle = "#ffc8c8";

	override onclick(_x: number, _y: number) {
		var action = {
				"type": "attack",
				"target": "#" + this["id"]
			};
		marauroa.clientFramework.sendAction(action);
	}

	// Overrides the one in creature
	override say(text: string) {
		if (stendhal.config.getBoolean("gamescreen.speech.creature")) {
			this.addSpeechBubble(text);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/attack.png) 1 3, auto";
	}

}
