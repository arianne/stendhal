/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";

/**
 * a panel
 */
export class Panel extends Component {

	protected containerElement: HTMLElement;
	protected children: Component[] = [];

	/**
	 * creates a new panel
	 *
	 * @param id id of HTML element or template
	 */
	constructor(id: string) {
		super(id);
		this.containerElement = this.componentElement;
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
		if (!this.containerElement.contains(child.componentElement)) {
			this.containerElement.append(child.componentElement);
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
		if (this.containerElement.contains(child.componentElement)) {
			child.componentElement.remove();
		}
	}

	/**
	 * removes all children
	 */
	public clear() {
		this.children = [];
		this.containerElement.innerHTML = "";
	}
}
