package games.stendhal.common.account;

import games.stendhal.server.core.account.NameCharacterValidator;

import org.junit.Assert;
import org.junit.Test;


public class NameCharacterValidatorTest {

	/**
	 * Tests for specialCharcter.
	 */
	@Test
	public void testSpecialCharcter() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf_");
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for startingWithNumber.
	 */
	@Test
	public void testStartingWithNumber() {
		final NameCharacterValidator validator = new NameCharacterValidator("1asdf");
		Assert.assertNotNull(validator.validate());
	}

	/**
	 * Tests for oKString.
	 */
	@Test
	public void testOKString() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf");
		Assert.assertNull(validator.validate());
	}
}
