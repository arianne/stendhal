package games.stendhal.client.gui;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PasswordDialogTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheckPass() {
		PasswordDialog pwd = new PasswordDialog();
		//assertTrue(pwd.checkPass(new char[0], new char[0]));
		char[] pw1 = {'b','l','a'};
		
		assertTrue(pwd.checkPass(pw1,pw1));
		char[] pw2 = {'b','l','a'};
		assertEquals(new String(pw1),new String(pw2));
		assertTrue(pwd.checkPass(pw1,pw2));
		char[] pw3 = {'b','l','a','h'};
		assertFalse(pwd.checkPass(pw1,pw3));
		
	}


}
