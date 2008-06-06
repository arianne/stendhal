package games.stendhal.server.actions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;
import utilities.RPClass.SheepTestHelper;

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
	public void testOnActionNoArgs() {
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("Please issue the old and the new name."));

	}

	@Test
	public void testOnActiondoesnotownoldname() {
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		action.put("target", "oldname");
		action.put("args", "newname");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("You don't own any oldname"));

	}

	@Test
	public void testOnActionName() {
		StendhalRPZone zone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(zone);
		
		SheepTestHelper.generateRPClasses();
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		action.put("target", "sheep");
		action.put("args", "newname");
		Sheep pet = new Sheep();
		
		zone.add(pet);
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		zone.add(bob);
		
		bob.setSheep(pet);
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("You changed the name of 'sheep' to 'newname'"));

	}
	
	@Test
	public void testOnActionRename() {
		StendhalRPZone zone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(zone);
		
		SheepTestHelper.generateRPClasses();
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		action.put("target", "oldname");
		action.put("args", "newname");
		Sheep pet = new Sheep();
		pet.setTitle("oldname");
		zone.add(pet);
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		zone.add(bob);
		
		bob.setSheep(pet);
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("You changed the name of 'oldname' to 'newname'"));

	}
	@Test
	public void testOnActionLongestName() {
		StendhalRPZone zone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(zone);
		
		SheepTestHelper.generateRPClasses();
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		action.put("target", "oldname");
		action.put("args", "01234567890123456789");
		Sheep pet = new Sheep();
		pet.setTitle("oldname");
		zone.add(pet);
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		zone.add(bob);
		
		bob.setSheep(pet);
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("You changed the name of 'oldname' to '01234567890123456789'"));
		
		

	}
	@Test
	public void testOnActiontooLongName() {
		StendhalRPZone zone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(zone);
		
		SheepTestHelper.generateRPClasses();
		RPAction action = new RPAction();
		NameAction nameAction = new NameAction();
		action.put("target", "oldname");
		action.put("args", "012345678901234567890");
		
		Sheep pet = new Sheep();
		pet.setTitle("oldname");
		zone.add(pet);
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		zone.add(bob);
		
		bob.setSheep(pet);
		nameAction.onAction(bob, action);
		assertThat(bob.getPrivateTextString(), is("The new name of your pet must not be longer than 20."));
		
		

	}
}
