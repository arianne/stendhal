/***************************************************************************
 *                   (C) Copyright 2023-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Chat } from "../util/Chat";
import { RPEvent } from "./RPEvent";

declare var marauroa: any

/**
 * shows an image for a detail view
 */
export class ChatOptionsEvent extends RPEvent {

	public npc!: string;
	public options!: string;

	public execute(entity: any): void {
		if (entity !== marauroa.me) {
			return;
		}

		let optionsList = this['options'].split("\t");
		let message = [];
		for (let optionListEntry of optionsList) {
			let option = optionListEntry.split("|~|");
			message.push(option[1]);
		}

		// update options to be included in keyword shortcuts dialog
		Chat.attending = this['npc'];
		Chat.options = message;

		let m = "Chat options for " + this['npc'] + ": " + message.join(", ");
		console.log(m);
		Chat.debug(m);
	}

};
