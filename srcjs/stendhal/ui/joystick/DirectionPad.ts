/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { DPadButton } from "./DPadButton";
import { JoystickImpl } from "./JoystickImpl";
import { Direction } from "../../util/Direction";


/**
 * On-screen movement control implementation representing a direction pad-like interface.
 *
 * Consists of four elements each representing a button that controls character's movement in a
 * single direction.
 */
export class DirectionPad extends JoystickImpl {

	/** Button components associated with this direction pad. */
	private buttons: DPadButton[];

	/** Singleton instance. */
	private static instance: DirectionPad;


	/**
	 * Retrieves singleton instance.
	 */
	public static get(): DirectionPad {
		if (!DirectionPad.instance) {
			DirectionPad.instance = new DirectionPad();
		}
		return DirectionPad.instance;
	}

	/**
	 * Hidden singleton constructor.
	 *
	 * Creates the HTML elements but does not add them to DOM.
	 */
	private constructor() {
		super();

		// create a temporary image to configure radius
		const tmp = new Image();
		tmp.onload = () => {
			// radius must be larger than button size as we cannot detect transparent pixels
			this.radius = tmp.width * 1.25;
			this.onReady();
		};
		tmp.src = DirectionPad.getResource("dpad_button");

		// initialize buttons
		this.buttons = [
			new DPadButton(Direction.UP),
			new DPadButton(Direction.DOWN),
			new DPadButton(Direction.LEFT),
			new DPadButton(Direction.RIGHT)
		];
	}

	/**
	 * Retrieves an array of the elements associated with this direction pad.
	 *
	 * @return {HTMLImageElement[]}
	 *   Image elements representing buttons.
	 */
	protected override getElements(): HTMLImageElement[] {
		const elements: HTMLImageElement[] = [];
		for (const button of this.buttons) {
			elements.push(button.element);
		}
		return elements;
	}

	/**
	 * Updates buttons positioning based on joystick's configured center and radius.
	 */
	public override update() {
		const centerX = DirectionPad.getCenterX();
		const centerY = DirectionPad.getCenterY();
		for (const button of this.buttons) {
			button.update(this.radius, centerX, centerY);
		}
	}

	/**
	 * Sets all buttons to represent a disengaged state and queues event to stop movement.
	 */
	public override reset() {
		this.disengageAll();
		if (this.engaged) {
			// stop movement
			this.queueStop();
		}
		// don't update engaged state until after check
		super.reset();
	}

	/**
	 * Sets all buttons to represent a disengaged state.
	 */
	private disengageAll() {
		for (const button of this.buttons) {
			button.onDisengaged();
		}
	}

	/**
	 * Called when a button is engaged to determine direction of movement.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 * @param button {ui.joystick.DPadButton.DPadButton}
	 *   Button component that was engaged.
	 */
	public onButtonEngaged(e: Event, button: DPadButton) {
		if (!this.onEngaged(e)) {
			return;
		}
		if (button.direction != this.direction) {
			this.onDirectionChange(button.direction);
		}
		button.onEngaged();
	}

	/**
	 * Called when a button is disengaged to stop movement.
	 *
	 * @param e {Event}
	 *   Event to be validated.
	 */
	public onButtonDisengaged(e: Event) {
		this.onDisengaged(e);
		// NOTE: calling `DPadButton.onDisengaged` not required as `DirectionPad.reset` has already been called
	}

	/**
	 * Called when drag gesture is detected to update movement and buttons.
	 *
	 * This is made public so that `ui.joystick.DPadButton.DPadButton` can access it.
	 *
	 * @param button {ui.joystick.DPadButton.DPadButton}
	 *   Button component over which the drag event was detected.
	 */
	public onDragWhileEngaged(button: DPadButton) {
		if (!this.engaged) {
			// prevent movement if direction pad is not engaged
			return;
		}
		if (button.direction != this.direction) {
			this.disengageAll();
			button.onEngaged();
			this.onDirectionChange(button.direction);
		}
	}
}
