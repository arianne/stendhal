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

import { ParamList } from "./ParamList";
import { SlashAction } from "./SlashAction";

import { Pair } from "../util/Pair";


/**
 * Requests progress status info from server.
 */
export class ProgressStatusAction extends SlashAction {

	readonly minParams = 0;
	readonly maxParams = 0;


	override execute(_type: string, _params: string[], _remainder: string): boolean {
		const action: any = {"type": _type};
		if (_remainder.length > 0) {
			if (_remainder.indexOf("Open Quests") > -1) {
				action["progress_type"] = "Open Quests";
				_remainder = _remainder.substring(12);
			} else if (_remainder.indexOf("Completed Quests") > -1) {
				action["progress_type"] = "Completed Quests";
				_remainder = _remainder.substring(17);
			} else if (_remainder.indexOf("Production") > -1) {
				action["progress_type"] = "Production";
				_remainder = _remainder.substring(11);
			} else {

			}
			if (_remainder) {
				action["item"] = _remainder;
			}
		}
		this.send(action);
		return true;
	}

	override getHelp(params?: ParamList|Pair<string, string>[]): string[] {
		// FIXME: including parameter in chat command input should set visible tab
		return ["", "Open travel log dialog window."];
	}
}
