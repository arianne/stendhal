package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UserTest {

	@Test
	public final void testUser() {
		User user = new User();

		assertEquals(0, (int) user.getModificationCount());
		assertFalse(User.isAdmin()); 
		assertFalse(user.hasSheep());
		assertFalse(user.hasPet());
		/*
		 * TODO remove unused code assertFalse(user.hasFeature("A"));
		 * assertEquals("XXX", user.getFeature("B"));
		 */
		assertNull(user.getServerVersion());
	}

}
