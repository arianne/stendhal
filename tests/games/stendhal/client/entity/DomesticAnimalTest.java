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
package games.stendhal.client.entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DomesticAnimalTest {

	private final class DomesticAnimalExtension extends DomesticAnimal {
		private int chanceset;

		public int getChanceset() {
			return chanceset;
		}

		public void setChanceset(final int chanceset) {
			this.chanceset = chanceset;
		}

		@Override
		protected void probableChat(final int chance) {
			chanceset = chance;
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for onIdea.
	 */
	@Test
	public void testOnIdea() {
		
		DomesticAnimalExtension animal = new DomesticAnimalExtension();
		assertThat(animal.getChanceset(), is(0));
		animal.onIdea(null);	
		assertThat(animal.getChanceset(), is(0));
		
		animal.setChanceset(0);
		animal.onIdea("eat");
		assertThat(animal.getChanceset(), is(15));
		
		animal.setChanceset(0);
		animal.onIdea("food");
		assertThat(animal.getChanceset(), is(20));
		
		animal.setChanceset(0);
		animal.onIdea("walk");
		assertThat(animal.getChanceset(), is(20));
		
		animal.setChanceset(0);
		animal.onIdea("follow");
		assertThat(animal.getChanceset(), is(20));
		
		animal.setChanceset(0);
		animal.onIdea("stop");
		assertThat(animal.getChanceset(), is(0));
		
	}

	
}
