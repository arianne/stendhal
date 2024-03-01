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

import { ui } from "./UI";


/**
 * Listens for updates to user interface.
 */
export class UIUpdateObserver {

	private vpRectObserver?: MutationObserver;
	private vpScaleObserver?: ResizeObserver;

	private static instance: UIUpdateObserver;


	/**
	 * Retrieves the singleton instance.
	 */
	public static get(): UIUpdateObserver {
		if (!UIUpdateObserver.instance) {
			UIUpdateObserver.instance = new UIUpdateObserver();
		}
		return UIUpdateObserver.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Initializes observers for display state & changes to viewport attributes.
	 */
	public init() {
		if (this.vpRectObserver) {
			console.warn("cannot re-initialize UIUpdateObserver");
			return;
		}

		this.vpRectObserver = new MutationObserver((mutations: MutationRecord[]) => {
			this.onViewPortUpdate();
		});
		this.vpRectObserver.observe(document.getElementById("viewport")!,
				{attributes: true, attributeFilter: ["left", "right", "top", "bottom", "width", "height"]});
		this.vpScaleObserver = new ResizeObserver((entries: ResizeObserverEntry[], observer: ResizeObserver) => {
			this.onViewPortUpdate();
		});
		this.vpScaleObserver.observe(document.getElementById("viewport")!);

		// listen for changes to screen orientation
		screen.orientation.addEventListener("change", (e) => {
			this.onOrientationUpdate();
		});
	}

	/**
	 * Retrieves the display style of the client.
	 *
	 * @return
	 *   String representing the client's display state.
	 */
	private getClientDisplay(): string {
		return document.getElementById("client")!.style["display"] || "none";
	}

	/**
	 * Checks if the client's display state has changed.
	 *
	 * @param oldDisplay
	 *   The previous display state of the client.
	 * @return
	 *   `true` if new state is not "none".
	 */
	private isClientDisplayChanged(oldDisplay: string): boolean {
		return this.getClientDisplay() !== oldDisplay;
	}

	/**
	 * Called when the client's "display" attribute changes.
	 */
	private onClientDisplayUpdate() {
	}

	/**
	 * Called when an attribute of the viewport has changed.
	 */
	private onViewPortUpdate() {
		ui.onDisplayUpdate();
	}

	/**
	 * Called when screen orientation changes.
	 */
	private onOrientationUpdate() {
		ui.onDisplayUpdate();
	}
}
