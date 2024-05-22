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

import { Enum } from "../enum/Enum";


/**
 * Defines the different types of settings elements available.
 */
export class WidgetType extends Enum {
	/** Type for text input. */
	static readonly TEXT = new WidgetType("text");
	/** Type for numeric input. */
	static readonly NUMBER = new WidgetType("number");
	/** Type for dual state checkbox. */
	static readonly CHECK = new WidgetType("checkbox");
	/** Type for multi-select enumeration. */
	static readonly SELECT = new WidgetType("select");
	/** Type for slider with numeric range. */
	static readonly SLIDER = new WidgetType("range");
}
