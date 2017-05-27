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

import java.io.Serializable;

/**
 * This class is used to test the reflection code.
 *
 * @author hendrik
 */
public class MockChildClass extends MockParentClass implements Serializable {


	// note this serialVersionUID is automatically created by emma
	// so we create it here anyway to simplify testing with and without
	// emma
	private static final long serialVersionUID = 550331563324952898L;

	public boolean childPublicBoolean = true;

	// this class is used by reflection
	@SuppressWarnings("unused")
	private float childPrivateFloat = 2.0f;

}
