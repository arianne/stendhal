/***************************************************************************
 *                (C) Copyright 2022-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * well known user interface components
 */
export class UIComponentEnum {

	// this should be an enum, but after TypeScript and closure compiler are done, all
	// enum members resolve to undefined. That is both in JavaScript and TypeScript code

	// Panel
	public static readonly TopPanel = 0;
	public static readonly LeftPanel = 1;
	public static readonly RightPanel = 2;
	public static readonly BottomPanel = 3;

	// Dialog
	public static readonly OutfitDialog = 4;
	public static readonly TravelLogDialog = 5;
}