/***************************************************************************
 *                     Copyright © 2003-2022 - Arianne                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DialogContentComponent } from "../component/DialogContentComponent";

declare let stendhal: any;


export class SettingsDialog extends DialogContentComponent {

	private reloadRequired = false;


	constructor() {
		super("settingsdialog-template");

		// FIXME: need a button to refresh page after adjusting settings

		const chk_blood = this.getCheckBox("chk_blood")!;
		chk_blood.setting = "blood";
		chk_blood.checked = stendhal.config.gamescreen.blood;

		const chk_shadows = this.getCheckBox("chk_shadows")!;
		chk_shadows.setting = "shadows";
		chk_shadows.checked = stendhal.config.gamescreen.shadows;

		[chk_blood, chk_shadows].forEach(chk => {
			this.addGameScreenCheckListener(chk);
		});

		const chk_dblclick = this.getCheckBox("chk_dblclick")!;
		chk_dblclick.setting = "itemDoubleClick";
		chk_dblclick.checked = stendhal.config.itemDoubleClick;
		this.addGeneralCheckListener(chk_dblclick);

		const chk_movecont = this.getCheckBox("chk_movecont")!;
		chk_movecont.setting = "moveCont";
		chk_movecont.checked = stendhal.config.moveCont;
		this.addGeneralCheckListener(chk_movecont);

		const btn_accept = this.getButton("config_accept")!;
		const btn_cancel = this.getButton("config_cancel")!;

		btn_accept.addEventListener("click", (e: Event) => {
			// TODO:

			if (this.frame != null) {
				this.frame.close();
			}
		});

		btn_cancel.addEventListener("click", (e: Event) => {
			if (this.frame != null) {
				this.frame.close();
			}
		});
	}

	public override getConfigId(): string {
		return "settings";
	}

	/**
	 * Sets up a checkbox change state listener.
	 *
	 * @param chk
	 *     CheckBox to apply listener.
	 * @param requireReload
	 *     If <code>true</code>, will trigger page reload when changes accepted.
	 */
	private addGeneralCheckListener(chk: CheckBox, requireReload: boolean=false) {
		chk.addEventListener("change", e => {
			this.onToggleGeneralCheck(chk);
		});

		if (requireReload) {
			this.reloadRequired = true;
		}
	}

	/**
	 * Sets up a checkbox change state listener.
	 *
	 * @param chk
	 *     CheckBox to apply listener.
	 * @param requireReload
	 *     If <code>true</code>, will trigger page reload when changes accepted.
	 */
	private addGameScreenCheckListener(chk: CheckBox, requireReload: boolean=false) {
		chk.addEventListener("change", e => {
			this.onToggleGameScreenCheck(chk);
		});

		if (requireReload) {
			this.reloadRequired = true;
		}
	}

	/**
	 * Updates configuration when checkbox values are changed.
	 *
	 * @param chk
	 *     The checkbox that changed state.
	 */
	private onToggleGeneralCheck(chk: CheckBox) {
		// FIXME: settings not being updated
		stendhal.config[chk.setting] = chk.checked;
	}

	/**
	 * Updates configuration when checkbox values are changed.
	 *
	 * @param chk
	 *     The checkbox that changed state.
	 */
	private onToggleGameScreenCheck(chk: CheckBox) {
		// FIXME: settings not being updated
		stendhal.config.gamescreen[chk.setting] = chk.checked;
	}

	/**
	 * Retrieves checkbox element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @return
	 *     HTMLInputElement.
	 */
	private getCheckBox(id: string): CheckBox {
		return <CheckBox> this.componentElement.querySelector(
			"input[type=checkbox][id=" + id + "]");
	}

	/**
	 * Retrieves a button element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @return
	 *     HTMLButtonElement.
	 */
	private getButton(id: string): HTMLButtonElement {
		return <HTMLButtonElement> this.componentElement.querySelector(
			"button[id=" + id + "]");
	}

	/**
	 * Updates window state for this session.
	 */
	public override onMoved() {
		/*
		const rect = this.componentElement.getBoundingClientRect();
		let newX = rect.left;
		let newY = rect.top;

		// keep dialog within view bounds
		if (newX < 0) {
			newX = 0;
			this.componentElement.style.left = "0px";
		}
		if (newY < 0) {
			newY = 0;
			this.componentElement.style.top = "0px";
		}

		// FIXME: need to check bounds of view width & height

		stendhal.config.windowstates.settings = {x: newX, y: newY};

		// DEBUG:
		console.log("SettingsDialog moved: " + newX + "," + newY);
		*/
	}
}


// XXX: this is probably unnecessary
class CheckBox extends HTMLInputElement {
	public setting: string = "";
}
