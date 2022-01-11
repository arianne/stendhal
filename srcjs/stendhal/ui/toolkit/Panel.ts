/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Compontent";

/**
 * a panel
 */
export class Panel extends Component {

	private children: Component[] = [];

	/**
	 * creates a new panel
	 *
	 * @param id id of HTML element or template
	 */
	constructor(id: string) {
		super(id);
	}

	/**
	 * adds a component to this panel
	 *
	 * @param child Component to add to this panel
	 */
	public add(child: Component) {
		if (this.children.indexOf(child) > -1) {
			return;
		}
		this.children.push(child);
		if (!this.componentElement.contains(child.componentElement)) {
			this.componentElement.append(child.componentElement);
		}
	}

	/**
	 * removes a component from this panel
	 *
	 * @param child Component to remove from this panel
	 */
	public remove(child: Component) {
		let index = this.children.indexOf(child)
		if (index < 0) {
			return;
		}
		this.children.splice(index, 1);
		if (this.componentElement.contains(child.componentElement)) {
			child.componentElement.remove();
		}
	}

}
