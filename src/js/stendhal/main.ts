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


import { Client } from "./Client";

/*
declare var require: any;
require("marauroa/marauroa");
require("marauroa/client-framework");
require("marauroa/message-factory");
require("marauroa/perception");
require("marauroa/rpfactory");
require("marauroa/inflate");
require("marauroa/deserializer");
require("marauroa/build"); // TODO
require("./data/sha3");
*/

declare var stendhal: any;


/**
 * Initializes "stendhal" object.
 */
function initGlobals() {
	const win = window as any;
	//win.marauroa = win.marauroa || {}; // marauroa object should already be intialized
	win.stendhal = win.stendhal || {};
	stendhal.main = Client.get();
}

// entry point
(function() {
	initGlobals();

	stendhal.main.init();

	document.addEventListener('DOMContentLoaded', () => stendhal.main.startup());
	window.addEventListener('error', (error: ErrorEvent) => stendhal.main.onError(error));
})();