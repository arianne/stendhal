/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

export class Component {

	readonly componentElement!: HTMLElement;

	constructor(id: string) {
		let element = document.getElementById(id);
		if (!element) {
			throw new Error("Cannot create component because there is no HTML element with id " + id);
		}

		if (element instanceof HTMLTemplateElement) {
			element = element.content.cloneNode(true) as HTMLElement
		}
		this.componentElement = element;
	}
}
