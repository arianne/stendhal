package games.stendhal.server.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UnderscoreConverterTest {

	@Test
	public void testTransform() {
		assertNull(UnderscoreConverter.transform(null));
		assertEquals("", UnderscoreConverter.transform(""));
		assertEquals("abc def", UnderscoreConverter.transform("abc_def"));
		assertEquals("abc def", UnderscoreConverter.transform("abc def"));
	}

}
