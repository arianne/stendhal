/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { SubEvent } from "./SubEvent";

import { SoundLayer } from "../../data/enum/SoundLayer";

import { SoundManager } from "../../ui/SoundManager";


export class KikaPunishmentEvent extends SubEvent {

	override execute(entity: any, flags: string[]) {
		// DEBUG:
		console.log("Kika punishment!");

		SoundManager.get().playGlobalizedEffect("event/thunderclap", SoundLayer.SFX.value);

		// TODO:
		// - lightning flash
		// - maybe make thunderclap louder
	}
}
