/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../../stendhal";

import { GeneralTab } from "./settings/GeneralTab";
import { InputTab } from "./settings/InputTab";
import { SoundTab } from "./settings/SoundTab";
import { VisualsTab } from "./settings/VisualsTab";

import { TabDialogContentComponent } from "../toolkit/TabDialogContentComponent";
import { Tooltip } from "../toolkit/Tooltip";

import { Layout } from "../../data/enum/Layout";

import { Debug } from "../../util/Debug";


export class SettingsDialog extends TabDialogContentComponent {

	public readonly storedStates: {[index: string]: string};
	private readonly initialStates: {[index: string]: string};


	constructor() {
		super("settingsdialog-template", Layout.TOP, "#settings-tabs");

		// TODO: add option to reset defaults

		this.storedStates = {
			"txtjoystickx": stendhal.config.get("joystick.center.x"),
			"txtjoysticky": stendhal.config.get("joystick.center.y")
		};

		this.initialStates = {
			"activity-indicator": stendhal.config.get("activity-indicator"),
			"activity-indicator.animate": stendhal.config.get("activity-indicator.animate"),
			"effect.blood": stendhal.config.get("effect.blood"),
			"effect.entity-overlay": stendhal.config.get("effect.entity-overlay"),
			"effect.lighting": stendhal.config.get("effect.lighting"),
			"effect.parallax": stendhal.config.get("effect.parallax"),
			"effect.weather": stendhal.config.get("effect.weather")
		};

		this.addTab("General", new GeneralTab(this, this.child("#settings-general")!));
		this.addTab("Visuals", new VisualsTab(this, this.child("#settings-visuals")!));
		this.addTab("Sound", new SoundTab(this, this.child("#settings-sound")!));
		this.addTab("Input", new InputTab(this, this.child("#settings-input")!));


		/* *** buttons *** */

		const btn_reload = this.addButton("Reload", () => {
			this.close();
			location.reload();
		}, "button-reload");
		btn_reload.title = "Reloads page if required by changes";
		btn_reload.disabled = true;

		const btn_close = this.addCloseButton();
		btn_close.title = "Close this dialog without reloading page";
	}

	public override getConfigId(): string {
		return "settings";
	}

	/**
	 * Updates state of reload button.
	 */
	public override refresh() {
		let reloadRequired = false;
		for (const key of Object.keys(this.initialStates)) {
			if (stendhal.config.get(key) !== this.initialStates[key]) {
				reloadRequired = true;
				break;
			}
		}
		(this.child("#button-reload")! as HTMLButtonElement).disabled = !reloadRequired;
	}

	/**
	 * Creates a checkbox element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLInputElement.
	 */
	private createCheckBoxSkel(id: string, tooltip?: string): HTMLInputElement {
		const checkbox = <HTMLInputElement> this.child("input[type=checkbox][id=" + id + "]")!;
		if (tooltip) {
			checkbox.title = tooltip;
		}
		if (checkbox.classList.contains("experimental")) {
			// add "experimental" denotation to label
			const label = checkbox.parentElement!;
			const text = document.createElement("span");
			text.innerText = label.innerText + " (experimental)";
			label.innerHTML = "";
			label.appendChild(checkbox);
			label.appendChild(text);
			if (!Debug.isActive("settings")) {
				// hide if settings debugging is not enabled
				checkbox.disabled = true;
				checkbox.style.setProperty("display", "none");
				label.style.setProperty("display", "none");
			}
		}
		return checkbox;
	}

	/**
	 * Creates a fully function checkbox element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param setid
	 *     Identifier of associated configuration setting.
	 * @param ttpos
	 *     Tooltip to display when setting is enabled.
	 * @param ttneg
	 *     Tooltip to display when setting is disabled.
	 * @param action
	 *     Action to execute when state changed.
	 * @param von
	 *     Optional value to set when enabled (<code>null</code> can be
	 *     used).
	 * @param voff
	 *     Optional value to set when disabled (<code>null</code> can be
	 *     used).
	 * @return
	 *     HTMLInputElement.
	 */
	createCheckBox(id: string, setid: string, ttpos: string="",
			ttneg: string="", action?: Function,
			von?: string, voff?: string): HTMLInputElement {

		const chk = this.createCheckBoxSkel(id)!;
		chk.checked = stendhal.config.getBoolean(setid);
		let tt: Tooltip;
		if (ttpos !== "") {
			let element: any = chk;
			if (chk.parentElement) {
				element = chk.parentElement;
			}
			tt = new Tooltip(element, ttpos, ttneg !== "" ? ttneg : undefined, chk.checked);
		}
		if (!chk.disabled) {
			chk.addEventListener("change", (e) => {
				if (chk.checked && typeof(von) !== "undefined") {
					stendhal.config.set(setid, von);
				} else if (!chk.checked && typeof(voff) !== "undefined") {
					stendhal.config.set(setid, voff);
				} else {
					stendhal.config.set(setid, chk.checked);
				}
				if (tt) {
					tt.setState(chk.checked);
				}
				if (action) {
					action();
				}
				this.refresh();
			});
		}
		return chk;
	}

	/**
	 * Creates a button element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLButtonElement.
	 */
	createButton(id: string, tooltip?: string): HTMLButtonElement {
		const button = <HTMLButtonElement> this.child(
			"button[id=" + id + "]")!;
		if (tooltip) {
			button.title = tooltip;
		}

		return button;
	}

	/**
	 * Creates a select element.
	 *
	 * @param id
	 *   Identifier of element to retrieve.
	 * @param opts
	 *   A {string: string} (identifier: visible text) object for available options.
	 * @param idx
	 *   The index to set as selected on construction.
	 * @param tooltip
	 *   Optional popup tooltip text.
	 * @param action
	 *   Action to execute when state changed.
	 * @return
	 *   `HTMLSelectElement`.
	 */
	createSelect(id: string, opts: {[index: string]: string}, idx: number,
			tooltip?: string, action?: Function): HTMLSelectElement {
		if (Object.keys(opts).length == 0) {
			console.warn("initializing empty values for selector ID \"" + id + "\"");
		}
		const sel = <HTMLSelectElement> this.child("select[id=" + id + "]")!;
		// FIXME: these should be set in CSS
		sel.style.setProperty("width", "9em");
		sel.parentElement!.style.setProperty("margin-right", "0");
		sel.parentElement!.style.setProperty("margin-left", "auto");
		sel.parentElement!.style.setProperty("padding-bottom", "5px");

		for (const key of Object.keys(opts)) {
			if (!opts[key]) {
				// use key as default value
				opts[key] = key;
			}
			const opt = document.createElement("option");
			opt.value = key;
			opt.textContent = opts[key];
			sel.appendChild(opt);
		}
		if (idx > -1 && idx < Object.keys(opts).length) {
			sel.selectedIndex = idx;
		}
		if (tooltip) {
			sel.title = tooltip;
		}
		if (action) {
			sel.addEventListener("change", function(e: Event) {
				action(e);
			});
		}
		return sel;
	}

	/**
	 * Creates a select element.
	 *
	 * @param {string} id
	 *   Identifier of element to retrieve.
	 * @param {string} ckey
	 *   Configuration key associated with element.
	 * @param {string=} tooltip
	 *   Optional popup tooltip text.
	 * @param {Function=} action
	 *   Action to execute when state changed.
	 * @returns {HTMLSelectElement}
	 *   Select element.
	 */
	createSelectFromConfig(id: string, ckey: string, tooltip?: string, action?: Function): HTMLSelectElement {
		const cvalue = stendhal.config.get(ckey);
		const opts = stendhal.config.getOpts(ckey);
		let idx = Object.keys(opts).indexOf(cvalue);
		if (typeof(cvalue) === "undefined" || idx < 0) {
			console.error("invalid value \"" + cvalue + "\" for configuration key \"" + ckey + "\""
					+ ". options are: " + Object.keys(opts).join(", "));
		}
		const actionTemp = function(e: Event) {
			if (e.target) {
				const select = e.target as HTMLSelectElement;
				// update configuration value
				stendhal.config.set(ckey, Object.keys(opts)[select.selectedIndex]);
			}
			if (action) {
				action(e);
			}
		};
		return this.createSelect(id, opts, idx, tooltip, actionTemp);
	}

	/**
	 * Creates a select element for registered fonts.
	 *
	 * @param id
	 *   Identifier of element to retrieve.
	 * @param idx
	 *   The index to set as selected on construction.
	 * @param tooltip
	 *   Optional popup tooltip text.
	 * @return
	 *   `HTMLSelectElement`.
	 */
	createFontSelect(id: string, idx: number, tooltip?: string): HTMLSelectElement {
		return this.createSelect(id, Object.assign({}, stendhal.config.fonts), idx, tooltip);
	}

	/**
	 * Creates a text input element.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param value
	 *     Default content.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @param type
	 *     Input type.
	 * @return
	 *     HTMLInputElement
	 */
	createTextInput(id: string, value: string="", tooltip?: string, type: string="text"): HTMLInputElement {
		const input = <HTMLInputElement> this.child("input[type=" + type + "][id=" + id + "]")!;
		input.style.setProperty("width", "9em");
		input.parentElement!.style.setProperty("margin-right", "0");
		input.parentElement!.style.setProperty("margin-left", "auto");
		input.parentElement!.style.setProperty("padding-bottom", "5px");
		input.value = value;
		if (tooltip) {
			input.title = tooltip;
		}
		return input;
	}

	/**
	 * Creates a text input element that accepts only number values.
	 *
	 * @param id
	 *     Identifier of element to retrieve.
	 * @param value
	 *     Default content.
	 * @param tooltip
	 *     Optional popup tooltip text.
	 * @return
	 *     HTMLInputElement
	 */
	createNumberInput(id: string, value: number=0, tooltip?: string): HTMLInputElement {
		const input = this.createTextInput(id, ""+value, tooltip, "number");
		// allow numbers & empty string only
		input.addEventListener("input", (e: Event) => {
			const new_char = (e as InputEvent).data;
			const new_digit = Number(new_char);
			if ((new_char != undefined && new_char.replace(/ |\t/g, "") === "") || Number.isNaN(new_digit)) {
				// disallow whitespace & non-numeric characters
				input.value = this.storedStates[input.id];
				return;
			}
			// clean up leading 0s & whitespace
			input.value = ""+parseInt(input.value.replace(/ |\t/g, ""), 10);
			this.storedStates[input.id] = input.value;
		});

		return input;
	}
}
