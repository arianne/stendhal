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

import { stendhal } from "../stendhal";

import { RPEvent } from "marauroa"


export class PlayerLoggedOutEvent extends RPEvent {

	public name!: string;


	override execute(entity: any) {
		stendhal.players.splice(stendhal.players.indexOf(this.name), 1);
	}
}
