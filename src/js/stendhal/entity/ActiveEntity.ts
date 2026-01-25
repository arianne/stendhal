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

import { Entity } from "./Entity";
import { Direction } from "../util/Direction";
import { MathUtil } from "../util/MathUtil";

import { stendhal } from "../stendhal";
import { TileMap } from "data/TileMap";

export class ActiveEntity extends Entity {

	direction: Direction;

	/** The current speed of this entity horizontally (tiles?/sec). */
	private dx: number;

	/** The current speed of this entity vertically (tiles?/sec) . */
	private dy: number;

	/** If <code>true</code>, this entity is not blocked by FlyOverArea */
	private flying = false;

	/**
	 * Create an active (moving) entity.
	 */
	constructor() {
		super();
		this.direction = Direction.DOWN;
		this.dx = 0.0;
		this.dy = 0.0;
	}

	override set(key: string, value: any) {
		if (["dir", "speed", "x", "y"].indexOf(key) > -1) {
			let numberValue = parseFloat(value);
			if (isNaN(this["_" + key])) {
				this["_" + key] = numberValue;
			}
			this.processPositioning(key, numberValue);
		}
		super.set(key, value);
	}

	override unset(key: string) {
		super.unset(key);
		if (["dir", "speed"].indexOf(key) > -1) {
			this.processPositioning(key, 0);
		}
	}

	/**
	 * Determine if this entity is not moving.
	 *
	 * @return <code>true</code> if not moving.
	 */
	public stopped() {
		return (this.dx == 0.0) && (this.dy == 0.0);
	}


	/**
	 * calculates the movement if the server an client are out of sync. for some
	 * milliseconds. (server turns are not exactly 300 ms) Most times this will
	 * slow down the client movement
	 *
	 * @param clientPos
	 *            the position the client has calculated
	 * @param serverPos
	 *            the position the server has reported
	 * @param delta
	 *            the movement based on direction
	 * @return the new delta to correct the movement error
	 */
	private calcDeltaMovement(clientPos: number, serverPos: number, delta: number) {
		let moveErr = clientPos - serverPos;
		let moveCorrection = (delta - moveErr) / delta;
		return (delta + delta * moveCorrection) / 2;
	}

	/**
	 * When entity moves, it will be called with the data.
	 *
	 * @param x new x coordinate
	 * @param y new y coordinate
	 * @param direction new direction
	 * @param speed new speed
	 */
	private onMove(x: number, y: number, direction: Direction, speed: number) {

		let oldx = x;
		let oldy = y;

		this.dx = direction.dx * speed;
		this.dy = direction.dy * speed;

		if ((Direction.LEFT == direction) || (Direction.RIGHT == direction)) {
			this["_y"] = y;
			if (MathUtil.compareDouble(this["_x"], x, 1.0)) {
				// make the movement look more nicely: + this.dx * 0.1
				this.dx = this.calcDeltaMovement(this["_x"] + this.dx * 0.1, x, direction.dx)
						* speed || 0;
			} else {
				this["_x"] = x;
			}
			this.dy = 0;
		} else if ((Direction.UP == direction) || (Direction.DOWN == direction)) {
			this["_x"] = x;
			if (MathUtil.compareDouble(this["_y"], y, 1.0)) {
				// make the movement look more nicely: + this.dy * 0.1
				this.dy = this.calcDeltaMovement(this["_y"] + this.dy * 0.1, y, direction.dy)
						* speed || 0;
			} else {
				this["_y"] = y;
			}
			this.dx = 0;
		} else {
			// placing entities
			this["_x"] = x;
			this["_y"] = y;
		}

		// Call onPosition only if the entity actually moved. Also always call
		// on partial coordinates - those are always predicted rather than real
		// and thus should always be a result of prediction. However, the
		// client collision detection does not always agree with that of the
		// server, so relying on just the coordinate change checks can miss
		// entities stopping when they collide with each other.
		if (!MathUtil.compareDouble(this["_x"], oldx)
			|| !MathUtil.compareDouble(this["_y"], oldy)
				|| !MathUtil.compareDouble(oldx, Math.floor(oldx))
				|| !MathUtil.compareDouble(oldy, Math.floor(oldy))) {
			// onPosition(x, y);
		}
	}


	/**
	 * Update cycle.
	 *
	 * @param delta
	 *            The time (in ms) since last call.
	 */
	override updatePosition(delta: number) {
		// do not call super

		if (!this.stopped()) {
			let step = (delta / 300.0);

			let oldX = this["_x"];
			let oldY = this["_y"];

			// update the location of the entity based on speeds
			this["_x"] += (this.dx * step);
			this["_y"] += (this.dy * step);

			if (this.collidesMap() || this.collidesEntities()) {
				this["_x"] = oldX;
				this["_y"] = oldY;
			} else {
				// onPosition(x, y);
			}
		}
	}

	/**
	 * Process attribute changes that may affect positioning. This is needed
	 * because different entities may want to process coordinate changes more
	 * gracefully.
	 *
	 * @param base
	 *            The previous values.
	 * @param diff
	 *            The changes.
	 */
	processPositioning(key: string, value: number) {
		// Real movement case
		let oldx = this["x"];
		let oldy = this["y"];

		let newX = oldx;
		let newY = oldy;

		if (key == "x") {
			newX = value;
		}
		if (key == "y") {
			newY = value;
		}

		if (key == "dir") {
			this.direction = Direction.VALUES[value];
		}

		let speed = this["speed"];
		if (key == "speed") {
			speed =  value;
		}


		this.onMove(newX, newY, this.direction, speed);

		if ((this.direction == Direction.STOP) || (speed == 0)) {
			this.dx = 0;
			this.dy = 0;

			// Store the new position before signaling it with onPosition().
			this["_x"] = newX;
			this["_y"] = newY;
		}
	}


	/**
	 * Check if the entity collides with the collision map.
	 */
	collidesMap(): boolean {
		if (this.ignoresCollision()) {
			return false;
		}

		var startX = Math.floor(this["_x"]);
		var startY = Math.floor(this["_y"]);
		var endX = Math.ceil(this["_x"] + this["width"]);
		var endY = Math.ceil(this["_y"] + this["height"]);
		let map = TileMap.get();
		for (var y = startY; y < endY; y++) {
			for (var x = startX; x < endX; x++) {
				if (map.collision(x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if flag to ignore collision tiles or ghostmode is set.
	 */
	private ignoresCollision(): boolean {
		return typeof(this["ignore_collision"]) !== "undefined"
			|| typeof(this["ghostmode"]) !== "undefined";
	}

	/**
	 * Check if the entity with another entity;
	 */
	collidesEntities(): boolean {
		var thisStartX = Math.floor(this["_x"]);
		var thisStartY = Math.floor(this["_y"]);
		var thisEndX = Math.ceil(this["_x"] + this["width"]);
		var thisEndY = Math.ceil(this["_y"] + this["height"]);

		var i;
		for (i in stendhal.zone.entities) {
			var other = stendhal.zone.entities[i];
			if (!this.isObstacle(other)) {
				continue;
			}
			var otherStartX = Math.floor(other["_x"]);
			var otherStartY = Math.floor(other["_y"]);
			var otherEndX = Math.ceil(other["_x"] + other["width"]);
			var otherEndY = Math.ceil(other["_y"] + other["height"]);

			if (thisStartX < otherEndX && thisEndX > otherStartX
				&& thisStartY < otherEndY && thisEndY > otherStartY) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves entities direction of movement.
	 *
	 * Default is `Direction.STOP`.
	 *
	 * @returns {Direction}
	 *   Current direction entity is moving or stopped.
	 */
	getWalkDirection(): Direction {
		const val = parseInt(this["dir"], 10);
		if (Number.isNaN(val) || !Number.isFinite(val)) {
			// assume stopped if cannot determine walking direction
			return Direction.STOP;
		}
		return Direction.VALUES[Math.max(Direction.STOP.val, Math.min(Direction.LEFT.val, val))];
	}

	/**
	 * Retrieves direction entity is facing.
	 *
	 * If direction cannot be determined (e.g. `Direction.STOP`) defaults to `Direction.DOWN`.
	 *
	 * @returns {Direction}
	 *   Entity's facing direction.
	 */
	getFaceDirection(): Direction {
		const dir = this.getWalkDirection();
		if (dir === Direction.STOP) {
			// assume down if cannot determine facing direction
			return Direction.DOWN;
		}
		return dir;
	}
}
