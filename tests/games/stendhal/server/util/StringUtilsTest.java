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
package games.stendhal.server.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for StringUtils
 *
 * @author hendrik
 */
public class StringUtilsTest {

	/**
	 * Tests for countUpperCase.
	 */
	@Test
	public void testCountUpperCase() {
		assertThat(StringUtils.countUpperCase(""), equalTo(0));
		assertThat(StringUtils.countUpperCase("1"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**"), equalTo(0));
		assertThat(StringUtils.countUpperCase("a"), equalTo(0));
		assertThat(StringUtils.countUpperCase("**A*B*"), equalTo(2));
	}

	/**
	 * Tests for countLowerCase.
	 */
	@Test
	public void testCountLowerCase() {
		assertThat(StringUtils.countLowerCase(""), equalTo(0));
		assertThat(StringUtils.countLowerCase("1"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**"), equalTo(0));
		assertThat(StringUtils.countLowerCase("A"), equalTo(0));
		assertThat(StringUtils.countLowerCase("**a*b*"), equalTo(2));
	}

	/**
	 * Tests for subst
	 */
	@Test
	public void testSubstMap()  {
		Map<String, String> params = new HashMap<String, String>();
		params.put("o", "0");
		params.put("p", "0, 1");
		params.put("x", "0, y, 1");
		params.put("quplnounhash(amount,name)", "25 coins");

		assertThat(StringUtils.substitute("", (Map<String, ?>)null), equalTo(""));
		assertThat(StringUtils.substitute("Hallo", (Map<String, ?>)null), equalTo("Hallo"));
		assertThat(StringUtils.substitute("Hall[o", params), equalTo("Hall0"));
		assertThat(StringUtils.substitute("Hall[o]", params), equalTo("Hall0"));
		assertThat(StringUtils.substitute("[o]Hall[o]", params), equalTo("0Hall0"));
		assertThat(StringUtils.substitute("Hal[l]o", params), equalTo("Halo"));
		assertThat(StringUtils.substitute("id IN ([o])", params), equalTo("id IN (0)"));
		assertThat(StringUtils.substitute("id IN ([p])", params), equalTo("id IN (0, 1)"));

		assertThat(StringUtils.substitute("Hallo, i need [quplnounhash(amount,name)].", params), equalTo("Hallo, i need 25 coins."));
	}

	/**
	 * Tests for subst
	 */
	@Test
	public void testSubstParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("o", "0");
		params.put("p", "0, 1");
		params.put("x", "0, y, 1");

		assertThat(StringUtils.substitute(""), equalTo(""));
		assertThat(StringUtils.substitute("Hallo"), equalTo("Hallo"));
		assertThat(StringUtils.substitute("Hallo", "o"), equalTo("Hallo"));
		assertThat(StringUtils.substitute("Hallo", "o", "0"), equalTo("Hallo"));
		assertThat(StringUtils.substitute("Hall[o", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("Hall0"));
		assertThat(StringUtils.substitute("Hall[o]", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("Hall0"));
		assertThat(StringUtils.substitute("[o]Hall[o]", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("0Hall0"));
		assertThat(StringUtils.substitute("Hal[l]o", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("Halo"));
		assertThat(StringUtils.substitute("id IN ([o])", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("id IN (0)"));
		assertThat(StringUtils.substitute("id IN ([p])", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("id IN (0, 1)"));

		assertThat(StringUtils.substitute("id = '[x]'", "o", "0", "p", "0, 1", "x", "0, y, 1"), equalTo("id = '0, y, 1'"));
	}
}
