package conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class TestRPClass {

	@Test
	public void subclass() {
		RPClass rpsuper = new RPClass("super");
		assertTrue(rpsuper.subclassOf("super"));
		RPClass sub = new RPClass("sub");
		assertFalse(sub.subclassOf(("super")));
		sub.isA("super");
		assertTrue(sub.subclassOf(("super")));
	}

}
