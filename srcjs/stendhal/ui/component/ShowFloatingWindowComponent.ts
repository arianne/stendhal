/***************************************************************************
 *                (C) Copyright 2022-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";
import { ui } from "../../ui/UI";
import { UIComponentEnum } from "../../ui/UIComponentEnum";
import { FloatingWindow } from "../../ui/toolkit/FloatingWindow";

/**
 * displays the player stats
 */
export class ShowFloatingWindowComponent extends Component {

	constructor(private uiComponentEnum: UIComponentEnum, private title: string, private x: number, private y: number) {
		super("showfloatingwindow-template");
		this.componentElement.innerText = title;
		this.componentElement.addEventListener("click", () => {
			this.onClick();
		});
	}

	private onClick() {
		let component = ui.get(this.uiComponentEnum);
		if (!component) {
			return;
		}

		if ((component.parentComponent as FloatingWindow | null | undefined)?.isOpen()) {
			(component.parentComponent as FloatingWindow | null | undefined)?.close();
		} else {
			new FloatingWindow(this.title, component, this.x, this.y)
		}

	}
}
