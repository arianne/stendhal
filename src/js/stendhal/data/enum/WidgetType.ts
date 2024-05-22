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


/**
 * Defines the different types of settings elements available.
 */
export const enum WidgetType {
	/** Type for text input. */
	TEXT = "text",
	/** Type for numeric input. */
	NUMBER = "number",
	/** Type for dual state checkbox. */
	CHECK = "checkbox",
	/** Type for multi-select enumeration. */
	SELECT = "select",
	/** Type for slider with numeric range. */
	SLIDER = "range"
}
