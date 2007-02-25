/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.sound;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestClass for Class DbValues
 *
 */
public class DBValuesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * TODO: check if this really what we want it to do astridemma 20.02.2007 
	 */
	@Test
	public final void testGetDBValue() {
		assertEquals(Float.NEGATIVE_INFINITY , DBValues.getDBValue(0));
		assertEquals(-20 , DBValues.getDBValue(10),0.002f);
		assertEquals(0 , DBValues.getDBValue(100),0.002f);
		
	}

}
