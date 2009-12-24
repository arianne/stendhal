package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UserTest {

	/**
	 * Tests for user.
	 */
	@Test
	public final void testUser() {
		final User user = new User();

		assertEquals(0, (int) user.getModificationCount());
		assertFalse(User.isAdmin()); 
		assertFalse(user.hasSheep());
		assertFalse(user.hasPet());
		assertNull(user.getServerVersion());
	}

}
