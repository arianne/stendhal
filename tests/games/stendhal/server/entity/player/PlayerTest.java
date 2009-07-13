package games.stendhal.server.entity.player;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerTest {
	private String playername = "player";
	private Player player;
	private Player killer;
	private StendhalRPZone zone;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		zone = new StendhalRPZone("the zone where the corpse shall be slain");
				player = PlayerTestHelper.createPlayer(playername);
				zone.add(player);
				killer = PlayerTestHelper.createPlayer("killer");	
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		assertThat(player.hashCode(), is(playername.hashCode()));
	}

	@Test
	public void testEqualsObject() {
		assertThat(player, equalTo(player));
		assertThat(player, equalTo(PlayerTestHelper.createPlayer(playername)));
		assertThat(player, not(equalTo(PlayerTestHelper.createPlayer(playername + 's'))));
	}

	@Test
	public void testToString() {
		assertThat(player.toString(), is("Player [" + playername + ", " + playername.hashCode() + ']'));
	}

	@Test
	public void testIsObstacle() {
		final Entity ent = new Entity() {
		};
		ent.setResistance(100);
		assertTrue(player.isObstacle(ent));
		ent.setResistance(95);

		assertFalse(player.isObstacle(ent));
		assertThat(player.getResistance(ent), is(95));

	}

	@Test
	public void testOnAdded() {
		
		player.onAdded(new StendhalRPZone("playertest"));
		RPObject object = Player.getKeyedSlotObject(player, "!visited");
		if (object == null) {
			fail("slot not found");

		}
		assertTrue(object.has("playertest"));
		assertThat(player.get("visibility"), is("100"));
		player.onAdded(new StendhalRPZone(PlayerDieer.DEFAULT_DEAD_AREA));
		object = Player.getKeyedSlotObject(player, "!visited");
		if (object == null) {
			fail("slot not found");

		}
		assertTrue(object.has(PlayerDieer.DEFAULT_DEAD_AREA));
		assertThat(player.get("visibility"), is("50"));
		player.onRemoved(new StendhalRPZone(PlayerDieer.DEFAULT_DEAD_AREA));
		assertThat(player.get("visibility"), is("100"));
	}

	@Test
	public void testDescribe() {
		final int hours = player.getAge() / 60;
		final int minutes = player.getAge() % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		assertThat(player.describe(), is("You see " + player.getTitle() + ".\n" + player.getTitle() + " is level "
				+ player.getLevel() + " and has been playing " + time + "."));
	}
	
	@Test
	public void testDescribeOfPlayerWithAwayMessage() {
		final int hours = player.getAge() / 60;
		final int minutes = player.getAge() % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		player.setAwayMessage("I am away.");
		String description = player.describe();
		String expectedDescription = "You see " + player.getTitle() + ".\n"
				+ player.getTitle() + " is level " + player.getLevel()
				+ " and has been playing " + time + "."
				+ "\nplayer is away and has left a message: "
				+ player.getAwayMessage();
		assertThat(description, is(expectedDescription));
	}
	
	@Test
	public void testDescribeOfPlayerWithGrumpyMessage() {
		final int hours = player.getAge() / 60;
		final int minutes = player.getAge() % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		player.setGrumpyMessage("I am grumpy.");
		String description = player.describe();
		String expectedDescription = "You see " + player.getTitle() + ".\n"
				+ player.getTitle() + " is level " + player.getLevel()
				+ " and has been playing " + time + "."
				+ "\nplayer is grumpy and has left a message: "
				+ player.getGrumpyMessage();
		assertThat(description, is(expectedDescription));
	}

	@Test
	public void testDescribeOfPlayerWithAwayAndGrumpyMessage() {
		final int hours = player.getAge() / 60;
		final int minutes = player.getAge() % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		player.setAwayMessage("I am away.");
		player.setGrumpyMessage("I am grumpy.");
		String description = player.describe();
		String expectedDescription = "You see " + player.getTitle() + ".\n"
				+ player.getTitle() + " is level " + player.getLevel()
				+ " and has been playing " + time + "."
				+ "\nplayer is away and has left a message: "
				+ player.getAwayMessage()
				+ "\nplayer is grumpy and has left a message: "
				+ player.getGrumpyMessage();
		assertThat(description, is(expectedDescription));
	}
	
	@Test
	public void testIsGhost() {
		assertFalse(player.isGhost());
		player.setGhost(true);
		assertTrue(player.isGhost());
		player.setGhost(false);
		assertFalse(player.isGhost());

	}

	@Test
	public void testAddGetUseKarma() {

		assertThat(player.getKarma(), is(10.0));
		player.addKarma(5.0);
		assertThat(player.getKarma(), is(15.0));
		assertThat(player.getDouble("karma"), is(player.getKarma()));
		player.useKarma(5.0);
		assertTrue(player.getKarma() >= 10.0);
		assertTrue(player.getKarma() <= 15.0);

	}

	@Test
	public void testIsInvisible() {
		final Player player2 = PlayerTestHelper.createPlayer("player2");
		assertThat(player2.isInvisibleToCreatures(), not(is(true)));
		player2.setInvisible(true);
		assertThat(player2.isInvisibleToCreatures(), is(true));
		player2.setInvisible(false);
		assertThat(player2.isInvisibleToCreatures(), not(is(true)));
	}

	@Test
	public void testSetImmune() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(bob.isImmune());
		bob.setImmune();
		assertTrue(bob.isImmune());
	}

	
	@Test
	public void testRemoveImmunity() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(bob.isImmune());
		bob.setImmune();
		assertTrue(bob.isImmune());
		bob.removeImmunity();
		assertFalse(bob.isImmune());
		
	}
	
	@Test
	public void testIsBadBoy() throws Exception {
		assertFalse(player.isBadBoy());
		assertFalse(killer.isBadBoy());

		player.onDead(killer);
		assertTrue(killer.isBadBoy());
		assertFalse(player.isBadBoy());
	}
	
	@Test
	public void testRehabilitate() throws Exception {
		assertFalse(player.isBadBoy());
		assertFalse(killer.isBadBoy());

		player.onDead(killer);
		assertTrue(killer.isBadBoy());
		assertFalse(player.isBadBoy());
		killer.rehabilitate();
		assertFalse(player.isBadBoy());
		assertFalse(killer.isBadBoy());

	}
	
	@Test
	public void testgetWidth() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertThat(bob.getWidth(), is(1.0));
		assertThat(bob.get("width"), is("1"));
		
		assertThat(bob.getHeight(), is(1.0));
		assertThat(bob.get("height"), is("1"));
		
		Player george = Player.createEmptyZeroLevelPlayer("george");
		assertThat(george.getWidth(), is(1.0));
		assertThat(george.get("width"), is("1"));
		
		assertThat(george.getHeight(), is(1.0));
		assertThat(george.get("height"), is("1"));
	}
}
