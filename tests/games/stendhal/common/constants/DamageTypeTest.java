package games.stendhal.common.constants;

import static org.junit.Assert.assertEquals;
import games.stendhal.common.constants.DamageType;

import org.junit.Test;

public class DamageTypeTest {
	/**
	 * Test parsing various strings give expected results
	 */
	@Test
	public void checkParsing() {
		assertEquals(DamageType.CUT, DamageType.parse("cut"));
		assertEquals(DamageType.ICE, DamageType.parse("ice"));
		assertEquals(DamageType.FIRE, DamageType.parse("fire"));
		assertEquals(DamageType.LIGHT, DamageType.parse("light"));
		assertEquals(DamageType.DARK, DamageType.parse("dark"));
		// Default damage; do something even if someone has made a typo
		assertEquals(DamageType.CUT, DamageType.parse("cuddle"));
		assertEquals(DamageType.CUT, DamageType.parse(null));
	}
}
