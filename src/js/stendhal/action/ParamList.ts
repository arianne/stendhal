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

import { Pair } from "../util/Pair";


/**
 * Represents a list of parameter names with descriptions for actions & chat commands.
 */
export class ParamList {

	/** Parameters associated with an action/command. */
	readonly params: Pair<string, string>[];


	/**
	 * Creates a new parameter list.
	 *
	 * @param params {action.ParamList.ParamList|util.Pair.Pair|string}
	 *   Parameters to be copied to this instance (default: []).
	 */
	constructor(params: ParamList|Pair<string, string>[]|string=[]) {
		if (typeof(params) === "string") {
			const names = params.split(" ");
			params = [];
			for (const name of names) {
				if (name) {
					// string argument doesn't support including descriptions
					params.push(new Pair(name, ""));
				}
			}
		} else if (params instanceof ParamList) {
			// copy construction
			params = params.params;
		}
		this.params = [...params];
	}

	/**
	 * Adds a parameter to list.
	 *
	 * @param name {string}
	 *   Parameter name/ID such as "foo" & "&lt;foo&gt;".
	 * @param desc {string}
	 *   Parameter description info {default: empty string}.
	 */
	add(name: string, desc="") {
		this.params.push(new Pair(name, desc));
	}

	/**
	 * Formats parameter names to single string.
	 *
	 * @param namesOnly {boolean}
	 *   Format result in single line using parameter names only.
	 * @return {string}
	 *   Parameter formatted string.
	 */
	toString(namesOnly: boolean): string {
		if (namesOnly) {
			let sparams = "";
			for (const pair of this.params) {
				sparams = sparams.length == 0 ? pair.first : sparams + " " + pair.first;
			}
			return sparams;
		}
		let sparams = "";
		for (const pair of this.params) {
			if (sparams.length > 0) {
				sparams += "\n";
			}
			let tmp = pair.first;
			while (tmp.startsWith("[") || tmp.startsWith("<")) {
				tmp = tmp.substring(1);
			}
			while (tmp.endsWith("]") || tmp.endsWith(">")) {
				tmp = tmp.substring(0, tmp.length - 1);
			}
			sparams += "    " + tmp;
			if (pair.second) {
				sparams += ": " + pair.second;
			}
		}
		return sparams;
	}
}
