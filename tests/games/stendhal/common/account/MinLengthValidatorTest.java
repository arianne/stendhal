package games.stendhal.common.account;

import games.stendhal.server.core.account.MinLengthValidator;

import org.junit.Assert;
import org.junit.Test;


public class MinLengthValidatorTest {

	/**
	 * Tests for emptyString.
	 */
	@Test
	public void testEmptyString() {
		final MinLengthValidator validator = new MinLengthValidator("", 4);
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for shortString.
	 */
	@Test
	public void testShortString() {
		final MinLengthValidator validator = new MinLengthValidator("asd", 4);
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for oKString.
	 */
	@Test
	public void testOKString() {
		final MinLengthValidator validator = new MinLengthValidator("asdf", 4);
		Assert.assertNull(validator.validate());
	}
}
