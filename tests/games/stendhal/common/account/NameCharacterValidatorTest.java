package games.stendhal.common.account;

import games.stendhal.server.core.account.NameCharacterValidator;

import org.junit.Assert;
import org.junit.Test;


public class NameCharacterValidatorTest {

	@Test
	public void testSpecialCharcter() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf_");
		Assert.assertNotNull(validator.validate());
	}

	@Test
	public void testStartingWithNumber() {
		final NameCharacterValidator validator = new NameCharacterValidator("1asdf");
		Assert.assertNotNull(validator.validate());
	}

	@Test
	public void testOKString() {
		final NameCharacterValidator validator = new NameCharacterValidator("asdf");
		Assert.assertNull(validator.validate());
	}
}
