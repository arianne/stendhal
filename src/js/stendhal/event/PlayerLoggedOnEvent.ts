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

declare var stendhal: any;

import { RPEvent } from "./RPEvent";


export class PlayerLoggedOnEvent extends RPEvent {

	public name!: string;


	override execute(entity: any) {
		if (stendhal.players.indexOf(this.name) < 0) {
			// remember logged on players
			stendhal.players.push(this.name);
			stendhal.players = stendhal.players.sort();
		}
	}
}
