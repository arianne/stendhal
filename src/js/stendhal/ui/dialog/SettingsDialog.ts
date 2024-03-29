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

import { ChatPanel } from "../ChatPanel";
import { ui } from "../UI";
import { ChatLogComponent } from "../component/ChatLogComponent";
import { PlayerEquipmentComponent } from "../component/PlayerEquipmentComponent";
import { PlayerStatsComponent } from "../component/PlayerStatsComponent";
import { ItemInventoryComponent } from "../component/ItemInventoryComponent";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { TravelLogDialog } from "./TravelLogDialog";
import { UIComponentEnum } from "../UIComponentEnum";
import { singletons } from "../../SingletonRepo";

declare var marauroa: any;
declare var stendhal: any;


export class SettingsDialog extends DialogContentComponent {

	public static debugging = false;

	private storedStates: {[index: string]: string};
	private initialStates: {[index: string]: string};
	private btn_reload: HTMLButtonElement;


	constructor() {
		super("settingsdialog-template");

		// TODO: add option to reset defaults

		const clog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		this.storedStates = {
			"txtjoystickx": stendhal.config.get("joystick.center.x"),
			"txtjoysticky": stendhal.config.get("joystick.center.y")
		};

		this.initialStates = {
			"effect.blood": stendhal.config.get("effect.blood"),
			"effect.weather": stendhal.config.get("effect.weather")
		};


		/* *** left panel *** */

		this.createCheckBox("chk_light", "effect.lighting",
				"Lighting effects are enabled", "Lighting effects are disabled");

		this.createCheckBox("chk_weather", "effect.weather",
				"Weather is enabled", "Weather is disabled", function() {
					if (clog) {
						clog.addLine("client", "Weather changes will take effect after you change maps.");
					}
				})!;

		const sd = this;
		this.createCheckBox("chk_blood", "effect.blood",
				"Gory images are enabled", "Gory images are disabled");

		this.createCheckBox("chk_nonude", "effect.no-nude",
				"Naked entities have undergarments", "Naked entities are not covered");

		this.createCheckBox("chk_shadows", "effect.shadows",
				"Shadows are enabled", "Shadows are disabled");

		this.createCheckBox("chk_speechcr", "speech.creature",
				"Creature speech bubbles are enabled", "Creature speech bubbles are disabled");

		const player_stats = ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent;

		const chk_charname = this.createCheckBox("chk_charname", "panel.stats.charname",
				undefined, undefined,
				function() {
					player_stats.enableCharName(chk_charname.checked);
				})!;

		const chk_hpbar = this.createCheckBox("chk_hpbar", "panel.stats.hpbar",
				undefined, undefined,
				function() {
					player_stats.enableBar("hp", chk_hpbar.checked);
				})!;

		this.createCheckBox("chk_activityindicator", "activity-indicator",
				"Indicator will be drawn", "Indicator will not be drawn");

		const chk_floatchat = this.createCheckBox("chk_floatchat", "chat.float",
				undefined, undefined,
				function() {
					(ui.get(UIComponentEnum.BottomPanel) as ChatPanel).setFloating(chk_floatchat.checked);
				});

		this.createCheckBox("chk_hidechat", "chat.autohide",
				"Chat panel will be hidden after sending text", "Chat panel will remain on-screen");

		// FIXME: are there unique properties for pinch & tap zooming?
		this.createCheckBox("chk_zoom", "zoom.touch",
				"Touch zooming enabled (may not work with all browsers)",
				"Touch zooming disabled (may not work with all browsers)",
				function() {
					stendhal.session.update();
				});


		/* *** center panel *** */

		this.createCheckBox("chk_dblclick", "inventory.double-click",
				"Items are used/consumed with double click/touch",
				"Items are used/consumed with single click/touch",
				function() {
					// update cursors
					(ui.get(UIComponentEnum.PlayerEquipment) as PlayerEquipmentComponent).markDirty();
					for (const cid of [UIComponentEnum.Bag, UIComponentEnum.Keyring]) {
						(ui.get(cid) as ItemInventoryComponent).markDirty();
					}
				});

		// FIXME: open chest windows are not refreshed
		this.createCheckBox("chk_chestqp", "inventory.quick-pickup",
				"Click tranfers items from chests and corpses to player inventory",
				"Click executes default action on items in chests and corpses");

		const chk_movecont = this.createCheckBox("chk_movecont", "move.cont",
				"Player will continue to walk after changing areas",
				"Player will stop after changing areas",
				function() {
					const action = {"type": "move.continuous"} as {[index: string]: string;};
					if (chk_movecont.checked) {
						action["move.continuous"] = "";
					}
					marauroa.clientFramework.sendAction(action);
				})!;

		// TODO: make this multiple choice
		const chk_pvtsnd = this.createCheckBox("chk_pvtsnd", "chat.private.sound",
				"Private message audio notifications enabled",
				"Private message audio notifications disabled",
				undefined, "ui/notify_up", "null");
		chk_pvtsnd.checked = stendhal.config.get("chat.private.sound") === "ui/notify_up";

		this.createCheckBox("chk_clickindicator", "click-indicator",
				"Displaying clicks", "Not displaying clicks");

		this.createCheckBox("chk_pathfinding", "pathfinding",
				"Click/Tap ground to walk", "Ground pathfinding disabled");

		this.createCheckBox("chk_pathfindingmm", "pathfinding.minimap",
				"Click/Tap minimap to walk", "Minimap pathfinding disabled");

		this.createCheckBox("chk_nativeemojis", "emojis.native",
				"Using native emojis", "Using built-in emojis",
				function() {
					singletons.getChatInput().refresh();
				});


		/* *** right panel *** */

		const themes = {} as {[index: string]: string};
		for (const t of Object.keys(stendhal.config.themes.map)) {
			if (t === "wood") {
				themes[t] = t + " (default)";
			} else {
				themes[t] = t;
			}
		}

		const sel_theme = this.createSelect("selecttheme", themes,
				Object.keys(themes).indexOf(stendhal.config.getTheme()));

		sel_theme.addEventListener("change", (o) => {
			stendhal.config.setTheme(Object.keys(themes)[sel_theme.selectedIndex]);
			stendhal.config.refreshTheme();
		});

		/* TODO:
		 *   - create components to change font size, weight, style, etc.
		 */

		const fonts = Object.keys(stendhal.config.fonts);

		const sel_fontbody = this.createFontSelect("selfontbody",
				fonts.indexOf(stendhal.config.get("font.body")));
		sel_fontbody.addEventListener("change", (e) => {
			const new_font = fonts[sel_fontbody.selectedIndex];
			stendhal.config.set("font.body", new_font);
			document.body.style.setProperty("font-family", new_font);
		});

		const sel_fontchat = this.createFontSelect("selfontchat",
				fonts.indexOf(stendhal.config.get("font.chat")));
		sel_fontchat.addEventListener("change", (e) => {
			stendhal.config.set("font.chat", fonts[sel_fontchat.selectedIndex]);
			// make sure component is open before trying to refresh
			if (clog) {
				clog.refresh();
			}
		});

		const sel_fonttlog = this.createFontSelect("selfonttlog",
				fonts.indexOf(stendhal.config.get("font.travel-log")))
		sel_fonttlog.addEventListener("change", (e) => {
			stendhal.config.set("font.travel-log", fonts[sel_fonttlog.selectedIndex]);
			const tlog = (ui.get(UIComponentEnum.TravelLogDialog) as TravelLogDialog);
			// make sure component is open before trying to refresh
			if (tlog) {
				tlog.refresh();
			}
		});

		this.createSelectFromConfig("selmenustyle", "menu.style",
				undefined,
				function(e: Event) {
					ui.onMenuUpdate();
				});

		// common chat keyword options
		const txt_chatopts = this.createTextInput("txtchatopts", stendhal.config.get("chat-opts.custom"),
				"Comma-separated list accessible from the chat options dialog");
		txt_chatopts.addEventListener("change", (e) => {
			stendhal.config.set("chat-opts.custom", txt_chatopts.value);
		});

		// on-screen joystick
		const sel_joystick = this.createSelectFromConfig("seljoystick", "joystick.style",
				undefined,
				function(e: Event) {
					stendhal.ui.gamewindow.updateJoystick();
				});
		const chk_joystick = this.createCheckBox("chk_joystick", "joystick",
				undefined, undefined,
				function(e: Event) {
					sel_joystick.disabled = !chk_joystick.checked;
					stendhal.ui.gamewindow.updateJoystick();
				});
		sel_joystick.disabled = !chk_joystick.checked;

		// joystck positioning
		for (const o of ["x", "y"]) {
			const orienter = this.createNumberInput("txtjoystick" + o,
					parseInt(this.storedStates["txtjoystick" + o], 10),
					"Joystick position on " + o.toUpperCase() + " axis");
			orienter.addEventListener("input", (e) => {
				// update configuration
				stendhal.config.set("joystick.center." + o, orienter.value || 0);
				// update on-screen joystick position
				stendhal.ui.gamewindow.updateJoystick();
			});
		}


		/* *** buttons *** */

		this.btn_reload = this.createButton("btn_config_reload",
				"Reloads page if required by changes");
		this.btn_reload.disabled = true;
		this.btn_reload.addEventListener("click", (e: Event) => {
			this.close();
			location.reload();
		});

		const btn_close = this.createButton("btn_config_close",
				"Close this dialog without reloading page");
		btn_close.addEventListener("click", (e: Event) => {
			this.close();
		});

		const button_layout = this.btn_reload.parentElement!;
		button_layout.style.setProperty("padding-top", "15px");
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

		this.btn_reload.disabled = !reloadRequired;
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
			if (!SettingsDialog.debugging) {
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
	private createCheckBox(id: string, setid: string, ttpos: string="",
			ttneg: string="", action?: Function,
			von?: string, voff?: string): HTMLInputElement {

		const chk = this.createCheckBoxSkel(id)!;
		chk.checked = stendhal.config.getBoolean(setid);
		const tt = new CheckTooltip(ttpos, ttneg);
		if (chk.parentElement) {
			chk.parentElement.title = tt.getValue(chk.checked);
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
				chk.parentElement!.title = tt.getValue(chk.checked);
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
	private createButton(id: string, tooltip?: string): HTMLButtonElement {
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
	private createSelect(id: string, opts: {[index: string]: string}, idx: number,
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
	 * @param id
	 *   Identifier of element to retrieve.
	 * @param cid
	 *   Configuration key associated with element.
	 * @param tooltip
	 *   Optional popup tooltip text.
	 * @param action
	 *   Action to execute when state changed.
	 * @return
	 *   `HTMLSelectElement`.
	 */
	private createSelectFromConfig(id: string, ckey: string, tooltip?: string, action?: Function): HTMLSelectElement {
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
	private createFontSelect(id: string, idx: number, tooltip?: string): HTMLSelectElement {
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
	private createTextInput(id: string, value: string="", tooltip?: string, type: string="text"): HTMLInputElement {
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
	private createNumberInput(id: string, value: number=0, tooltip?: string): HTMLInputElement {
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


class CheckTooltip {
	private valueEnabled: string;
	private valueDisabled: string;
	constructor(e: string, d: string) {
		this.valueEnabled = e;
		this.valueDisabled = d;
	}
	public getValue(enabled: boolean): string {
		if (enabled) {
			return this.valueEnabled;
		}
		return this.valueDisabled;
	}
}
