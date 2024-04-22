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

export class MenuItem  {
	title!: string;
	type?: string;
	action?: Function;
	index?: number;


	constructor(title: string, action?: Function) {
		this.title = title;
		this.action = action;
	}
}
