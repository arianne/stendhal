package games.stendhal.common.account;

import games.stendhal.server.core.account.MinLengthValidator;

import org.junit.Assert;
import org.junit.Test;


public class MinLengthValidatorTest {

	@Test
	public void testEmptyString() {
		MinLengthValidator validator = new MinLengthValidator("", 4);
		Assert.assertNotNull(validator.validate());
	}

	@Test
	public void testShortString() {
		MinLengthValidator validator = new MinLengthValidator("asd", 4);
		Assert.assertNotNull(validator.validate());
	}

	@Test
	public void testOKString() {
		MinLengthValidator validator = new MinLengthValidator("asdf", 4);
		Assert.assertNull(validator.validate());
	}
}
