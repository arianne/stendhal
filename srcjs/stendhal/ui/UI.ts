/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./toolkit/Compontent";
import { SingletonFloatingWindow } from "./toolkit/SingletonFloatingWindow";

class UI {
	createSingletonFloatingWindow(title: string, contentComponent: Component, x: number, y: number) {
		new SingletonFloatingWindow(title, contentComponent, x, y);
	}
}

export let ui = new UI();
