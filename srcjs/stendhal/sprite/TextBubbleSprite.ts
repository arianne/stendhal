/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


export abstract class TextBubbleSprite {

	protected text: string;
	protected timeStamp: number;
	protected listening = false;


	constructor(text: string) {
		this.text = text;
		this.timeStamp = Date.now();
	}

	abstract draw(ctx: CanvasRenderingContext2D): boolean;

	abstract onClick(evt: MouseEvent): void;
}
