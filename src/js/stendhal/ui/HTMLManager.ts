/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * HTML code manipulation.
 */
export class HTMLManager {

	/** Singleton instance. */
	private static instance: HTMLManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): HTMLManager {
		if (!HTMLManager.instance) {
			HTMLManager.instance = new HTMLManager();
		}
		return HTMLManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	esc(msg: string, filter=[]) {
		msg = msg.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/\n/g, "<br>");
		// restore filtered tags
		for (const tag of filter) {
			msg = msg.replace("&lt;" + tag + "&gt;", "<" + tag + ">")
					.replace("&lt;/" + tag + "&gt;", "</" + tag + ">");
		}

		return msg;
	}

	niceName(s: string): string {
		if (!s) {
			return "";
		}
		let temp = s.replace(/_/g, " ").trim();
		return temp.charAt(0).toUpperCase() + temp.slice(1);
	}

	/**
	 * Retrieves target element from event.
	 *
	 * @param event {any}
	 *   Executed event.
	 * @return {EventTarget}
	 *   Translated event target.
	 */
	extractTarget(event: any): EventTarget {
		if (event.changedTouches) {
			// FIXME: Always uses last index. Any way to detect which touch index was engaged?
			const tidx = event.changedTouches.length - 1;
			if (["touchmove", "touchend"].indexOf(event.type) > -1) {
				// touch events target source element
				for (const el of document.elementsFromPoint(event.changedTouches[tidx].pageX, event.changedTouches[tidx].pageY)) {
					if (!el.classList.contains("notarget")) {
						return el;
					}
				}
			}
			return event.changedTouches[tidx].target;
		}
		return event.target;
	}

	/**
	 * Normalizes an event object.
	 *
	 * @param event {any}
	 *   Executed event.
	 * @return {any}
	 *   Normalized event.
	 */
	extractPosition(event: any): any {
		let pos = event;
		const target = this.extractTarget(event);
		const canvas = target as HTMLCanvasElement;
		if (event.changedTouches) {
			// FIXME: Always uses last index. Any way to detect which touch index was engaged?
			const tidx = event.changedTouches.length - 1;
			pos = {
				pageX: Math.round(event.changedTouches[tidx].pageX),
				pageY: Math.round(event.changedTouches[tidx].pageY),
				clientX: Math.round(event.changedTouches[tidx].clientX),
				clientY: Math.round(event.changedTouches[tidx].clientY),
				target: target
			}
			if (["touchmove", "touchend"].indexOf(event.type) > -1) {
				// touch events target source element
				const rect = canvas.getBoundingClientRect();
				pos.offsetX = pos.pageX - rect.left;
				pos.offsetY = pos.pageY - rect.top;
			} else {
				pos.offsetX = pos.pageX - canvas.offsetLeft;
				pos.offsetY = pos.pageY - canvas.offsetTop;
			}
		}
		pos.canvasRelativeX = Math.round(pos.offsetX * canvas.width / canvas.clientWidth);
		pos.canvasRelativeY = Math.round(pos.offsetY * canvas.height / canvas.clientHeight);
		return pos;
	}

	formatTallyMarks(line: string): any {
		let tmp = line.split("<tally>");
		const pre = tmp[0];
		tmp = tmp[1].split("</tally>");
		const post = tmp[1];
		const count = parseInt(tmp[0].trim(), 10);

		let tallyString = "";
		if (count > 0) {
			let t = 0
			for (let idx = 0; idx < count; idx++) {
				t++
				if (t == 5) {
					tallyString += "5";
					t = 0;
				}
			}

			if (t > 0) {
				tallyString += t;
			}
		} else {
			tallyString = "0";
		}

		const tally = document.createElement("span");
		tally.className = "tally";
		tally.textContent = tallyString;

		return [pre, tally, post];
	}

	/**
	 * Extracts slot name from element ID.
	 *
	 * @param id {string}
	 *   Element's ID string.
	 * @return {string}
	 *   Slot name.
	 */
	parseSlotName(id: string): string {
		if (id.includes("-")) {
			return id.split("-")[0];
		}
		return id.replace(/[0-9]$/, "");
	}
}
