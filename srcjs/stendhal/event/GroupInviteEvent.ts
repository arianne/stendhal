/***************************************************************************
 *                 (C) Copyright 2005-2023 - Faiumoni e. V.                *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


import { RPEvent } from "./RPEvent";
import { Chat } from "../util/Chat";

declare var marauroa: any;

/**
 * handles player trade events
 */
export class GroupInviteEvent extends RPEvent {
	public expire!: string;
	public leader!: string;

	public execute(entity: any): void {
		if (entity !== marauroa.me) {
			return;
		}
		if (this["expire"]) {
			Chat.log("normal", "Your group invite by " + this["leader"] + " has expired.");
		} else {
			Chat.log("normal", "Your have been invited by " + this["leader"] + " to join a group.");
			Chat.log("normal", "To join, type: /group join " + this["leader"]);
			Chat.log("normal", "To leave the group at any time, type: /group part " + this["leader"]);
		}
	}
}
