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
package games.stendhal.client.gui.login;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for Profile
 *
 * @author hendrik
 */
public class ProfileTest {

	/**
	 * Tests for createFromCommandline()
	 */
	@Test
	public void testCreateFromCommandline() {
		String[] args = new String[]{"-h", "host", "-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Profile.createFromCommandline(args).toString(), equalTo("user/char@host:1"));
	}

	/**
	 * Tests for isValid
	 */
	@Test
	public void testIsValid() {
		String[] args = new String[]{"-h", "host", "-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Boolean.valueOf(Profile.createFromCommandline(args).isValid()), equalTo(Boolean.TRUE));

		args = new String[]{"-P", "1", "-u", "user", "-c", "char", "-p", "password"};
		assertThat(Boolean.valueOf(Profile.createFromCommandline(args).isValid()), equalTo(Boolean.FALSE));
	}
}
