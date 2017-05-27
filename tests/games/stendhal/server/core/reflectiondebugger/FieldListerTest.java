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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import marauroa.common.Pair;

/**
 * tests for the field tester.
 *
 * @author hendrik
 */
public class FieldListerTest {

	@Test
	public void testListAttributesIncludingPrivateAndParents() {
		FieldLister fl = new FieldLister(new MockChildClass());
		fl.scan();
		Map<String, Pair<String, String>> fields = fl.getResult();

		// fields created by ecl-emma to track coverage data
		fields.remove("$VRc");
		fields.remove("$jacocoData");

		assertThat(fields.size(), is(5));

	}
}
