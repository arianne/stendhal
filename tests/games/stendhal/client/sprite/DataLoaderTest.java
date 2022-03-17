/***************************************************************************
 *                 (C) Copyright 2012-2022 Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sprite;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DataLoaderTest {

	@Test
	public void testNormalizeFilenames() {
		assertThat(DataLoader.normalizeFilenames("data/signs/document.png"), equalTo("data/signs/document.png"));
		assertThat(DataLoader.normalizeFilenames("/data/signs/document.png"), equalTo("data/signs/document.png"));
		assertThat(DataLoader.normalizeFilenames("data/signs/../items/document.png"), equalTo("data/items/document.png"));
	}

}
