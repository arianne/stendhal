/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "../../data/Paths";


export abstract class ButtonBase {

	private element: HTMLImageElement;
	private onClick?: EventListener;


	protected constructor(id: string) {
		this.element = document.getElementById("qm-" + id)! as HTMLImageElement;
		this.element.src = Paths.gui + "/quickmenu/" + id + ".png";
	}

	public setVisible(visible: boolean) {
		this.element.style["display"] = visible ? "block" : "none";
	}

	public setPosX(x: number) {
		this.element.style["left"] = x + "px";
	}

	protected setImageBasename(basename: string) {
		this.element.src = Paths.gui + "/quickmenu/" + basename + ".png";
	}

	protected setOnClick(eventListener: EventListener) {
		this.unsetOnClick();
		this.onClick = eventListener;
		this.element.addEventListener("click", this.onClick);
	}

	private unsetOnClick() {
		if (!this.onClick) {
			return;
		}
		this.element.removeEventListener("click", this.onClick);
		this.onClick = undefined;
	}
}
