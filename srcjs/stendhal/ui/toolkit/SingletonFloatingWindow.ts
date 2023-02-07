/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";
import { FloatingWindow } from "./FloatingWindow";

/**
 * A popup or dialog window that will disappear if another SingletonFloatingWindow is opened
 */
export class SingletonFloatingWindow extends FloatingWindow {
	private static visiblePopup?: SingletonFloatingWindow;

	/**
	 * creates and shows a SingletonFloatingWindow
	 *
	 * @param title title of the window
	 * @param contentComponent Component that is shown inside of the window
	 * @param x x-location on screen
	 * @param y y-location on screen
	 */
	constructor(title: string, contentComponent: Component, x: number, y: number) {
		super(title, contentComponent, x, y);
		if (SingletonFloatingWindow.visiblePopup) {
			SingletonFloatingWindow.visiblePopup.close();
		}
		SingletonFloatingWindow.visiblePopup = this;
	}

	override close() {
		super.close();
		if (SingletonFloatingWindow.visiblePopup === this) {
			SingletonFloatingWindow.visiblePopup = undefined;
		}
	}
}
