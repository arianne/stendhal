/***************************************************************************
 *                   (C) Copyright 2021-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"

import { ParamList } from "./ParamList";
import { Pair } from "../util/Pair";


/**
 * Base slash action implementation.
 */
export abstract class SlashActionImpl {
	[key: string|symbol]: any;
	/** Minimum required number of parameters. */
	abstract minParams: number;
	/** Maximum allowed number of parameters. */
	abstract maxParams: number;
	/** Parameter definitions for help information. */
	params?: ParamList|string;
	/** Description for help information. */
	desc?: string;
	/** Alternative aliases. */
	aliases?: string[];


	/**
	 * Instructions to be executed for command.
	 *
	 * @param type {string}
	 *   Command name.
	 * @param params {string[]}
	 *   Parameters passed to command.
	 * @param remainder {string}
	 *   Any remaining data after parameters have been parsed.
	 * @return boolean
	 *   `true` to represent successful execution.
	 */
	abstract execute(type: string, params: string[], remainder: string): boolean;
}


/**
 * Abstract base class for user actions.
 */
export abstract class SlashAction extends SlashActionImpl {

	/**
	 * Retrieves help information.
	 *
	 * @param params {action.ParamList.ParamList|util.Pair.Pair[]}
	 *   Optional parameters info.
	 * @return {string[]}
	 *   Help info for this action.
	 */
	getHelp(params?: ParamList|Pair<string, string>[]): string[] {
		const p: any = params ? new ParamList(params) : this.params;
		const result = [];
		const sparams = p ? SlashAction.formatParams(p) : "";
		if (this.desc) {
			result.push(sparams);
			result.push(this.desc);
		} else if (sparams) {
			result.push(sparams);
		}
		return result;
	}

	/**
	 * Formats parameter list information.
	 *
	 * @param params {any}
	 *   Parameters info.
	 * @param namesOnly {boolean}
	 *   Format result in single line using parameter names only (default: `true`).
	 * @return {string}
	 *   Parameter formatted string.
	 */
	static formatParams(params: any, namesOnly=true): string {
		if (params instanceof SlashAction) {
			params = params.params;
		}
		if (typeof(params) === "string") {
			return params;
		}
		return new ParamList(params).toString(namesOnly);
	}

	/**
	 * Forwards action information to server.
	 *
	 * @param action {object}
	 *   Action object.
	 */
	protected send(action: object) {
		marauroa.clientFramework.sendAction(action);
	}
};
