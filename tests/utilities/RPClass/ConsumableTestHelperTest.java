/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import games.stendhal.server.entity.item.ConsumableItem;

public class ConsumableTestHelperTest {

	@Test
	public void testCreate() throws Exception {

		ConsumableItem eater = ConsumableTestHelper.createEater("consume");
		assertThat(eater, instanceOf(ConsumableItem.class));

		ConsumableItem createImmunizer = ConsumableTestHelper.createImmunizer("consume");
		assertThat(createImmunizer, instanceOf(ConsumableItem.class));
	}
}
