/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";


export class JSONLoader {

	data: any;
	onDataReady?: Function;


	constructor(onDataReady?: Function) {
		this.onDataReady = onDataReady;
	}

	/**
	 * Loads JSON data from file.
	 *
	 * @param path
	 *     Path to JSON file.
	 */
	load(path: string) {
		fetch(path, {
				headers: {"Content-Type": "application/json"}
		}).then(resp => resp.json()).then(data => {
			this.data = data;
			if (typeof(this.onDataReady) !== "undefined") {
				this.onDataReady();
			}
		});
	}
}
