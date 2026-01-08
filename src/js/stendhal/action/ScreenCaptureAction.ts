/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { SlashAction } from "./SlashAction";

import { ScreenCapture } from "../util/ScreenCapture";

import { stendhal } from "../stendhal";


export class ScreenCaptureAction extends SlashAction {

	readonly minParams = 0;
	readonly maxParams = 0;

	override desc = "Start or stop capturing video of the client viewport";

	private recorder?: ScreenCapture;


	execute(type: string, params: string[], remainder: string): boolean {
		if (this.recorder && ScreenCapture.isActive()) {
			// currently recording
			this.recorder.stop();
			this.recorder = undefined;
			return true;
		}
		this.recorder = new ScreenCapture();
		this.recorder.start(stendhal.ui.viewport.getElement() as HTMLCanvasElement);
		return true;
	}
}
