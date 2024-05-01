/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { NumericEnumeration } from "./NumericEnumeration";


/**
 * Enumeration type representing layout of elements.
 */
export class Layout extends NumericEnumeration {
	public static readonly TOP = new Layout(1);
	public static readonly BOTTOM = new Layout();
	public static readonly LEFT = new Layout();
	public static readonly RIGHT = new Layout();
}
