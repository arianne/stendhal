/***************************************************************************
 *                      (C) Copyright 2010 - Stendhal                      *
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for StringHelper
 *
 * @author hendrik
 */
public class StringHelperTest {

	@Test
	public void testUnquote() {
		assertThat(StringHelper.unquote(null), nullValue());
		assertThat(StringHelper.unquote("a"), equalTo("a"));
		assertThat(StringHelper.unquote("''"), equalTo(""));
		assertThat(StringHelper.unquote("abcd"), equalTo("abcd"));
		assertThat(StringHelper.unquote("'abcd'"), equalTo("abcd"));
		assertThat(StringHelper.unquote("\"abcd\""), equalTo("abcd"));
	}

}
