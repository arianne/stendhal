/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { StatBarComponent } from "./StatBarComponent";


export class KarmaBarComponent extends StatBarComponent {

	private karma = 0;
	private lastChange = 0;
	// value represented in bar
	private repValue = 0.5;

	// prevents bar from lighting up when first created
	private initialized = false;


	constructor() {
		super("karmabar");
	}

	override draw(newKarma: number) {
		const cycleTime = Date.now();

		if (this.initialized) {
			if (newKarma != this.karma) {
				this.canvas.style.setProperty("outline", "1px solid #ffffff");
				this.karma = newKarma;
				this.canvas.title = this.describeKarma(newKarma);
				this.calculateRepresentation(newKarma);
				this.lastChange = cycleTime;
			} else if (cycleTime - this.lastChange >= 1000) {
				this.canvas.style.setProperty("outline", "1px solid #000000");
			}
		} else {
			this.karma = newKarma;
			this.canvas.title = this.describeKarma(newKarma);
			this.calculateRepresentation(newKarma);
			this.initialized = true;
		}

		// black background
		this.ctx.beginPath();
		this.ctx.fillStyle = "#000000";
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

		// gradient foreground
		const grad = this.ctx.createLinearGradient(0, 0, this.canvas.width, 0);
		grad.addColorStop(0, "#ff0000");
		grad.addColorStop(0.5, "#ffffff");
		grad.addColorStop(1, "#0000ff");
		this.ctx.fillStyle = grad;
		this.ctx.fillRect(0, 0, this.repValue, this.canvas.height);
	}

	/**
	 * Get textual description of karma value.
	 *
	 * @param karma
	 *     karma value.
	 * @return
	 *     Karma description.
	 */
	private describeKarma(karma: number): string {
		if (karma > 499) {
			return "You have unusually good karma";
		} else if (karma > 99) {
			return "You have great karma";
		} else if (karma > 5) {
			return "You have good karma";
		} else if (karma > -5) {
			return "You have average karma";
		} else if (karma > -99) {
			return "You have bad karma";
		} else if (karma > -499) {
			return "You have terrible karma";
		}
		return "You have disastrously bad karma";
	}

	/**
	 * Calculates the value to be represented in bar.
	 */
	private calculateRepresentation(karma: number) {
		const normalized = 0.5 + Math.atan(0.02 * karma) / Math.PI;
		this.repValue = this.canvas.width * Math.max(Math.min(normalized, 1), 0);
	}
}
