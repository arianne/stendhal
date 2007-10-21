package games.stendhal.common.account;

import games.stendhal.server.account.MinLengthValidator;

import org.junit.Assert;


public class MinLengthValidatorTest {

	public void testEmptyString() {
		MinLengthValidator validator = new MinLengthValidator("", 4);
		Assert.assertNotNull(validator.validate());
	}

	public void testShortString() {
		MinLengthValidator validator = new MinLengthValidator("asd", 4);
		Assert.assertNotNull(validator.validate());
	}

	public void testOKString() {
		MinLengthValidator validator = new MinLengthValidator("asdf", 4);
		Assert.assertNull(validator.validate());
	}
}
