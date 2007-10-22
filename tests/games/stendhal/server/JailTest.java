package games.stendhal.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JailTest {

	static String ZONE_NAME = "test";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCriminalNotInworld() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();
		Player policeman = new Player(new RPObject());
		Player bob = new Player(new RPObject());
		bob.setName("bob");
		Jail.get().imprison("bob", policeman, 1, "test");
		assertEquals("Player bob not found", policeman.get("private_text"));

	}

	@Test
	public final void testCriminalNofreeCell() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();
		Player policeman = new Player(new RPObject());
		Player bob = new Player(new RPObject());
		bob.setName("bob");
		Jail.jailzone = new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE);
		Jail.get().imprison(bob, policeman, 1, "test");
		assertTrue((policeman.get("private_text")).contains("Could not find a cell forbob"));

	}

	@Test
	public final void testCriminalimprison() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();
		Player policeman = new Player(new RPObject());
		Player bob = new Player(new RPObject());
		bob.setName("bob");
		Jail.jailzone = new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE);

		Jail.get().imprison(bob, policeman, 1, "test");
		assertTrue("fail because durkham does not know how to load the data",
				Jail.isInJail(bob));

	}

	@Test
	@Ignore
	public final void testRelease() {
		fail("Not yet implemented");
	}

	@Test
	public final void testIsInJail() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();
		Player bob = new Player(new RPObject());
		StendhalRPZone zone = new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE);
		zone.add(bob);
		Jail.jailzone = zone;
		Jail.get().imprison("bob", bob, 1, "test");
		assertFalse(Jail.isInJail(bob));

		bob.setPosition(1, 1);
		assertTrue(Jail.isInJail(bob));
		Player nobob = new Player(new RPObject());
		StendhalRPZone noJail = new StendhalRPZone("noJail");
		noJail.add(nobob);
		nobob.setPosition(0, 0);
		Jail.get().imprison("nobob", nobob, 1, "test");
		assertFalse(Jail.isInJail(nobob));

		bob.setPosition(1, 1);
		assertFalse(Jail.isInJail(nobob));

	}

	@Test
	@Ignore
	public final void testOnLoggedIn() {
		fail("Not yet implemented");
	}

}
