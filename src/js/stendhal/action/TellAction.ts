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


export class TellAction extends SlashAction {

	override readonly minParams = 1;
	override readonly maxParams = 1;
	override readonly aliases = ["msg"];

	private static lastPlayerTell?: string;


	override execute(_type: string, params: string[], remainder: string): boolean {
		const target = params[0];
		const action = {
			"type": "tell",
			"target": target,
			"text": remainder
		};
		this.send(action);
		TellAction.lastPlayerTell = target;
		return true;
	}

	override getHelp(params?: ParamList|Pair<string, string>[]): string[] {
		return ["<player> <message>", "Send a private message to #player."];
	}

	/**
	 * Retrieves name of player most recently messaged.
	 *
	 * @return {string|undefined}
	 *   Player name or `undefined` if never private messaged.
	 */
	static getLastPlayerTell(): string|undefined {
		return TellAction.lastPlayerTell;
	}
}
