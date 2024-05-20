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

	override readonly minParams;
	override readonly maxParams;
	override readonly aliases = ["msg"];

	private static lastPlayerTell?: string;


	/**
	 * Creates a new tell action.
	 *
	 * NOTE: only inheriting classes should call constructor with min/max params
	 *
	 * @param {number=} minParams
	 *   Minimum required number of parameters.
	 * @param {number=} maxParams
	 *   Maximum allowed number of parameters.
	 */
	constructor(minParams?: number, maxParams?: number) {
		super();
		this.minParams = typeof(minParams) !== "undefined" ? minParams : 1;
		this.maxParams = typeof(maxParams) !== "undefined" ? maxParams : 1;
	}

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
