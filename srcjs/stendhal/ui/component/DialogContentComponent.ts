/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";
import { FloatingWindow } from "../toolkit/FloatingWindow";

declare let stendhal: any;


export abstract class DialogContentComponent extends Component {

	protected frame?: FloatingWindow;
	private cid?: string;


	constructor(id: string) {
		super(id);
		this.applyTheme();
	}

	public setConfigId(cid: string) {
		this.cid = cid;
	}

	public getConfigId(): string {
		return this.cid || "";
	}

	public updateConfig(newX: number, newY: number) {
		const cid = this.getConfigId();
		if (stendhal.config.dialogstates[cid]) {
			stendhal.config.dialogstates[cid] = {x: newX, y: newY};
		}
	}

	/**
	 * Sets the closable dialog frame.
	 */
	public setFrame(frame: FloatingWindow) {
		this.frame = frame;
	}

	public getFrame(): FloatingWindow|undefined {
		return this.frame;
	}
}
