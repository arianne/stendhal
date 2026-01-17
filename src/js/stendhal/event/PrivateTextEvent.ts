/***************************************************************************
 *                   (C) Copyright 2005-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Chat } from "util/Chat";
import { RPEvent } from "marauroa"
import { stendhal } from "stendhal";

/**
 * handles private text messages
 */
export class PrivateTextEvent extends RPEvent {

	public texttype!: string;
	public text!: string;
	public profile!: string;

	private soundTextEvents = ["privmsg", "support", "tutorial"];

	public execute(_entity: any): void {
		const ttype = this["texttype"].toLowerCase();
		const msg = this["text"].replace(/\\r\\n/g, "\n").replace(/\\r/g, "\n");

		let profile;
		if (this.hasOwnProperty("profile")) {
			profile = this["profile"];
		} else if (ttype === "tutorial") {
			profile = "floattingladynpc";
		}

		if (ttype === "server" && msg.includes("\n")) {
			Chat.log(ttype, msg.split("\n"), undefined, profile);
		} else {
			// scene settings messages should not disturb playing, just create some atmosphere
			const headed = ttype !== "scene_setting";
			Chat.log(ttype, msg, undefined, profile, headed);
		}

		// play notification sound
		const notif = stendhal.config.get("chat.private.sound");
		if (notif && this.soundTextEvents.indexOf(ttype) > -1) {
			stendhal.sound.playGlobalizedEffect(notif);
		}
	}

};
