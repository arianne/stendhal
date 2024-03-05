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

/**
 * abstract base class for user actions
 */
export abstract class SlashAction {

	/** Minimum required number of parameters. */
	abstract minParams: number;
	/** Maximum allowed number of parameters. */
	abstract maxParams: number;

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

};
