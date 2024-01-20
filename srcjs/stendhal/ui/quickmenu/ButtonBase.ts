/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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


	protected constructor(id: string) {
		this.element = document.getElementById("qm-" + id)! as HTMLImageElement;
		this.element.src = Paths.gui + "/quickmenu/" + id + ".png";
		this.element.style["cursor"] = "url(" + Paths.sprites + "/cursor/highlight.png) 1 3, auto";
		this.element.draggable = false;
		// listen for click events
		this.element.addEventListener("click", (evt: Event) => {
			this.onClick(evt);
		});
	}

	public setVisible(visible: boolean) {
		this.element.style["display"] = visible ? "block" : "none";
	}

	public setPos(x: number, y: number) {
		this.element.style["left"] = x + "px";
		this.element.style["top"] = y + "px";
	}

	protected setImageBasename(basename: string) {
		this.element.src = Paths.gui + "/quickmenu/" + basename + ".png";
	}

	protected abstract onClick(evt: Event): void;
}
