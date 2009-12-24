package games.stendhal.client.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordDialogTest {

	/**
	 * Tests for checkPass.
	 */
	@Test
	public void testCheckPass() {
		final PasswordDialog pwd = new PasswordDialog();
		assertTrue(pwd.checkPass(new char[0], new char[0]));
		final char[] pw1 = { 'b', 'l', 'a' };

		assertTrue(pwd.checkPass(pw1, pw1));
		final char[] pw2 = { 'b', 'l', 'a' };
		assertEquals(new String(pw1), new String(pw2));
		assertTrue(pwd.checkPass(pw1, pw2));
		final char[] pw3 = { 'b', 'l', 'a', 'h' };
		assertFalse(pwd.checkPass(pw1, pw3));

	}

}
