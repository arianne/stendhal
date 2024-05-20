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

import { TellAction } from "./TellAction";
import { ParamList } from "./ParamList";

import { Pair } from "../util/Pair";


export class ReTellAction extends TellAction {

	constructor() {
		super(0, 0);
	}

	override execute(_type: string, params: string[], remainder: string): boolean {
		const target = ReTellAction.getLastPlayerTell();
		if (typeof(target) !== "undefined") {
			return super.execute(_type, [target], remainder);
		}
		return true;
	}

	override getHelp(params?: ParamList|Pair<string, string>[]): string[] {
		return ["<message>", "Send a private message to the last player you sent a message to."];
	}
}
