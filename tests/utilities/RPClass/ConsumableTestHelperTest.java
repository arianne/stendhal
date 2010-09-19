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
package utilities.RPClass;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.item.ConsumableItem;

import org.junit.Test;

public class ConsumableTestHelperTest {

	@Test
	public void testCreate() throws Exception {

		ConsumableItem eater = ConsumableTestHelper.createEater("consume");
		assertThat(eater, is(ConsumableItem.class));

		ConsumableItem createImmunizer = ConsumableTestHelper.createImmunizer("consume");
		assertThat(createImmunizer, is(ConsumableItem.class));
	}
}
