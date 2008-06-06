package games.stendhal.server.actions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class NameActionTest {

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
	public void testOnAction() {
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("Please issue the old and the new name."));

	}

}
