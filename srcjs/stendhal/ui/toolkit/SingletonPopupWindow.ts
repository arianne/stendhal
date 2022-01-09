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

import { Component } from "./Compontent";
import { PopupWindow } from "./PopupWindow";

/**
 * A popup or dialog window that will disappear if another SingletonPopupWindow is opened
 */
export class SingletonPopupWindow extends PopupWindow {
	private static visiblePopup?: SingletonPopupWindow;

	/**
	 * creates and shows a SingletonPopupWindow
	 *
	 * @param title title of the window
	 * @param contentComponent Component that is shown inside of the window
	 * @param x x-location on screen
	 * @param y y-location on screen
	 */
	constructor(title: string, contentComponent: Component, x: number, y: number) {
		super(title, contentComponent, x, y);
		if (SingletonPopupWindow.visiblePopup) {
			SingletonPopupWindow.visiblePopup.close();
		}
		SingletonPopupWindow.visiblePopup = this;
	}

	override close() {
		super.close();
		if (SingletonPopupWindow.visiblePopup === this) {
			SingletonPopupWindow.visiblePopup = this;
		}
	}
}
