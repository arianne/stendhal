package games.stendhal.server.core.account;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import games.stendhal.server.core.account.NotEmptyValidator;

import org.junit.Test;

public class NotEmptyValidatorTest {


	@Test
	public final void testValidate() {
		NotEmptyValidator nev = new NotEmptyValidator("");
		assertNotNull(nev.validate());

		nev = new NotEmptyValidator(null);
		assertNotNull(nev.validate());
		nev = new NotEmptyValidator(" ");
		assertNull(nev.validate());
	}

}
