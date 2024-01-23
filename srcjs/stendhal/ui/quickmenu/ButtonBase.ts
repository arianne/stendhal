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

import { Component } from "../toolkit/Component";
import { Paths } from "../../data/Paths";


export abstract class ButtonBase extends Component {

	protected constructor(id: string) {
		super("qm-" + id);
		(this.componentElement as HTMLImageElement).src = Paths.gui + "/quickmenu/" + id + ".png";
		this.componentElement.style["cursor"] = "url(" + Paths.sprites + "/cursor/highlight.png) 1 3, auto";
		this.componentElement.draggable = false;
		// listen for click events
		this.componentElement.addEventListener("click", (evt: Event) => {
			this.onClick(evt);
		});
		this.update();
	}

	public setPos(x: number, y: number) {
		this.componentElement.style["left"] = x + "px";
		this.componentElement.style["top"] = y + "px";
	}

	protected setImageBasename(basename: string) {
		(this.componentElement as HTMLImageElement).src = Paths.gui + "/quickmenu/" + basename + ".png";
	}

	protected abstract onClick(evt: Event): void;

	public update() {
		// implementing classes can override
	}
}
