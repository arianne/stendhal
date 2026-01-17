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

import { marauroa } from "marauroa"

import { Direction } from "./Direction";


/**
 * Representation of either clockwise or counterclockwise rotation.
 */
class Rotation {
	public static readonly CLOCKWISE = new Rotation(1);
	public static readonly COUNTERCLOCKWISE = new Rotation(-1);

	private constructor(public readonly val: number) {}
}

/**
 * Manages user's facing direction.
 */
export class FacingHandler {

	public static UP = Direction.UP.val;
	public static DOWN = Direction.DOWN.val;
	public static LEFT = Direction.LEFT.val;
	public static RIGHT = Direction.RIGHT.val;

	public static LOWEST = FacingHandler.UP;
	public static HIGHEST = FacingHandler.LEFT;

	/** Singleton instance. */
	private static instance: FacingHandler;


	/**
	 * Retrieves singleton instance.
	 */
	public static get(): FacingHandler {
		if (!FacingHandler.instance) {
			FacingHandler.instance = new FacingHandler();
		}
		return FacingHandler.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Retrieves user's direction.
	 *
	 * @return
	 *   Current facing `Direction`.
	 */
	public getDirection(): Direction {
		// default is down
		let dir = FacingHandler.DOWN;
		if (marauroa.me) {
			dir = marauroa.me.getFaceDirection().val;
			dir = dir >= FacingHandler.LOWEST && dir <= FacingHandler.HIGHEST ? dir : FacingHandler.DOWN;
		}
		return Direction.VALUES[dir];
	}

	/**
	 * Determines next direction user should face.
	 *
	 * @param rot
	 *   Whether user should turn clockwise or counterclockwise.
	 * @return
	 *   Next facing `Direction`.
	 */
	public getNextDir(rot: Rotation): Direction {
		let nextDir = this.getDirection().val + rot.val;
		if (nextDir < FacingHandler.LOWEST) {
			nextDir = FacingHandler.HIGHEST;
		} else if (nextDir > FacingHandler.HIGHEST) {
			nextDir = FacingHandler.LOWEST;
		}
		return Direction.VALUES[nextDir];
	}

	/**
	 * Sets user's facing direction.
	 *
	 * @param dir
	 *   `Direction` to face towards.
	 */
	public faceTo(dir: Direction) {
		marauroa.clientFramework.sendAction({
			"type": "face",
			"dir": ""+dir.val
		});
	}

	/**
	 * Turns user clockwise.
	 */
	public turnClockwise() {
		if (!marauroa.me) {
			// wait until user is ready
			return;
		}
		this.faceTo(this.getNextDir(Rotation.CLOCKWISE));
	}

	/**
	 * Turns user counterclockwise.
	 */
	public turnCounterClockwise() {
		if (!marauroa.me) {
			// wait until user is ready
			return;
		}
		this.faceTo(this.getNextDir(Rotation.COUNTERCLOCKWISE));
	}
}
