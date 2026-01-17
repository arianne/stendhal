/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa";
import { stendhal } from "./stendhal";
import { Client } from "./Client";

declare var require: any;
(window as any)["Zlib"] = require("marauroa/inflate").Zlib;
let build = require("marauroa/build");
require("./data/sha3");



/**
 * Initializes "stendhal" object.
 */
function initGlobals() {
	build.version(marauroa, stendhal);
	stendhal.main = Client.get();
}

// entry point
(function() {
	initGlobals();

	stendhal.main.init();

	document.addEventListener('DOMContentLoaded', () => stendhal.main.startup());
	window.addEventListener('error', (error: ErrorEvent) => stendhal.main.onError(error));
})();
