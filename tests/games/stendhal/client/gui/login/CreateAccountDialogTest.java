/*
 * CreateAccountDialogTest.java
 *
 * Created on Oct 21, 2007, 2:08:15 PM
 */

package games.stendhal.client.gui.login;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import marauroa.common.Log4J;

/**
 * Tests the CreateAccountDialog.
 * @author timothyb89
 */
public class CreateAccountDialogTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
	}

	/**
	 * Tests for inValidPass.
	 */
    @Test
    public void testInValidPass() {

        final CreateAccountDialog cad = new CreateAccountDialog();
        assertFalse(cad.validatePassword("", ""));

        final String user1 = "qwerty";
        final String pass1 = "qwerty";
        assertFalse(cad.validatePassword(user1, pass1));

        final String user2 = "qwertyuiop";
        final String pass2 = "qwerty";
        assertFalse(cad.validatePassword(user2, pass2));

        final String pass3 = "qwertyu";
        assertFalse(cad.validatePassword(user2, pass3));

        final String pass4 = "tyuiop";
        assertFalse(cad.validatePassword(user2, pass4));

        final String pass5 = "rtyuiop";
        assertFalse(cad.validatePassword(user2, pass5));
    }
	/**
	 * Tests for validPass.
	 */
    @Test
    public void testValidPass() {
    	 final CreateAccountDialog cad = new CreateAccountDialog();
    	 assertTrue(cad.validatePassword("timothy", "verygood"));
    }
}
