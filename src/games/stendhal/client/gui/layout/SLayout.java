/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.layout;

/**
 * Layout constraints.
 * <p>
 * Use <code>constraints(SLayout ... c)</code> of the appropriate
 * layout manager to create a constraints object.
 */
public enum SLayout {
	/** The component should expand in the direction of the layout */
	EXPAND_AXIAL,
	/** The component should expand perpendicular to the direction of the layout */
	EXPAND_PERPENDICULAR,
	/** The component should expand in horizontal direction */
	EXPAND_X,
	/** The component should expand in vertical direction */
	EXPAND_Y;
}
