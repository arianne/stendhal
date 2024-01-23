/***************************************************************************
 *                    Copyright © 2023-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../UI";
import { Direction } from "../../util/Direction";

declare var marauroa: any;
declare var stendhal: any;


/**
 * Joystick implementation that does nothing. Used when joystick should not be drawn on-screen.
 */
export class JoystickBase {

	protected radius = 0;
	// last executed direction
	protected direction = Direction.STOP;

	private stopTimeoutId = 0;


	public reset() {
		// do nothing
	}

	public onRemoved() {
		// do nothing
	}

	public static getResource(res: string) {
		return stendhal.paths.gui + "/joystick/" + res + ".png";
	}

	protected static getCenterX(): number {
		return stendhal.config.getInt("client.joystick.center.x", 224);
	}

	protected static getCenterY(): number {
		return stendhal.config.getInt("client.joystick.center.y", 384);
	}

	protected static checkActionEvent(e: Event): boolean {
		if (e instanceof MouseEvent && e.button == 0) {
			return true;
		}
		if (e instanceof TouchEvent) {
			return true;
		}
		return false;
	}

	protected onDirectionChange(dir: Direction) {
		if (!ui.isDisplayReady()) {
			console.debug("not executing direction change before display is ready");
			return;
		}
		this.direction = dir;
		if (this.direction == Direction.STOP) {
			marauroa.clientFramework.sendAction({type: "stop"});
		} else {
			if (this.stopTimeoutId) {
				clearTimeout(this.stopTimeoutId);
				this.stopTimeoutId = 0;
			}
			marauroa.clientFramework.sendAction({type: "move", dir: ""+this.direction.val});
		}
	}

	protected queueStop() {
		this.direction = Direction.STOP;
		this.stopTimeoutId = setTimeout(() => {
			// new direction not pressed before timeout expired
			marauroa.clientFramework.sendAction({type: "stop"});
			this.stopTimeoutId = 0;
		}, 300);
	}
}
