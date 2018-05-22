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
package games.stendhal.common;



/**
 * class to store the max outfit numbers for player available outfits.
 * @author kymara
 */
public class Outfits {

	/*
	 * Edit these fields to add new outfits.
	 * Note: Outfits are numbered starting at 0 and these
	 * variables are the total number of outfits.
	 */

	/** number of player selectable heads */
	public static final int HEAD_OUTFITS = 21;
	// TODO: Remove when testing outfits is finished
	public static final int HEAD_OUTFITS_TESTING = 7;

	/** number of player selectable dresses */
	public static final int CLOTHES_OUTFITS = 62;

	/** number of player selectable hair styles */
	public static final int HAIR_OUTFITS = 45;

	/** number of player selectable body shapes */
	public static final int BODY_OUTFITS = 15;
	// TODO: Remove when testing outfits is finished
	public static final int BODY_OUTFITS_TESTING = 4;

	/** number of player selectable eyes */
	public static final int EYES_OUTFITS = 10;

	/** number of player selectable mouths */
	public static final int MOUTH_OUTFITS = 4;
}
