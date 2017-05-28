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
package games.stendhal.server.core.reflectiondebugger;

/**
 * This class is used to test the reflection code.
 *
 * @author hendrik
 */
public class MockParentClass {
	public String parentPublicString = "text";

	// this class is used by reflection
	@SuppressWarnings("unused")
	private int parentPrivateInt = 1;

}
