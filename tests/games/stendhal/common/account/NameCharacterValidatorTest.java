package games.stendhal.common.account;

import games.stendhal.server.account.NameCharacterValidator;

import org.junit.Assert;


public class NameCharacterValidatorTest {

	public void testSpecialCharcter() {
		NameCharacterValidator validator = new NameCharacterValidator("asdf_");
		Assert.assertNotNull(validator.validate());
	}

	public void testStartingWithNumber() {
		NameCharacterValidator validator = new NameCharacterValidator("1asdf");
		Assert.assertNotNull(validator.validate());
	}

	public void testOKString() {
		NameCharacterValidator validator = new NameCharacterValidator("asdf");
		Assert.assertNull(validator.validate());
	}
}
