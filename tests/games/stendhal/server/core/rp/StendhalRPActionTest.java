package games.stendhal.server.core.rp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPEvent;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class StendhalRPActionTest {
	private static StendhalRPZone zone;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Before
	public void setUp() throws Exception {
		zone = new StendhalRPZone("zone", 20, 20);
		zone.protectionMap.init(1, 1);
		MockStendlRPWorld.get().addRPZone(zone);
	}

	@After
	public void tearDown() throws Exception {
		MockStendlRPWorld.get().removeZone(zone);
		
	}
	
	// Sets the square below the victim protected 
	private void protectMap() {
		// this seems to be enough for now
		zone.protectionMap.setCollide(0, 0);
	}
	
	/**
	 * Get whatever private text message a player got.
	 * 
	 * @param player the player to be examined
	 * @return the text message
	 */
	private String getTextMessage(Player player) {
		for (RPEvent e : player.events()) {
			if (e.getName().equals("private_text")) {
				return e.get("text");
			}
		}
		
		return null;
	}
	
	@Test
	public void startAttackingOneself() {
		final Player player = PlayerTestHelper.createPlayer("lunatic");
		zone.add(player);
		// players can't attack themselves. should always fail
		StendhalRPAction.startAttack(player, player);
		assertNull("don't allow targeting oneself", player.getAttackTarget());
	}
	
	// Trying to attack creatures should always succeed
	@Test
	public void startAttackingCreature() {
		final Player player = PlayerTestHelper.createPlayer("hyde");
		
		Creature victim = SingletonRepository.getEntityManager().getCreature("mouse");
		zone.add(victim);
		
		// both at level 0
		StendhalRPAction.startAttack(player, victim);
		assertSame(player.getAttackTarget(), victim);
		player.stopAttack(victim);
		assertNull(player.getAttackTarget());
		
		// protecting the mouse should not matter
		protectMap();
		StendhalRPAction.startAttack(player, victim);
		assertSame(player.getAttackTarget(), victim);
		player.stopAttack(victim);
		
		// strong player, weak monster
		player.setLevel(100);
		StendhalRPAction.startAttack(player, victim);
		assertSame(player.getAttackTarget(), victim);
		
		// weak player, strong monster
		player.setLevel(0);
		victim = SingletonRepository.getEntityManager().getCreature("dark angel");
		zone.add(victim);
		StendhalRPAction.startAttack(player, victim);
		assertSame(player.getAttackTarget(), victim);
		
		// both are somewhat strong
		player.setLevel(100);
		StendhalRPAction.startAttack(player, victim);
		assertSame(player.getAttackTarget(), victim);
	}
	
	@Test
	public void startAttackingEqualPlayer() {
		final Player jekyll = PlayerTestHelper.createPlayer("jekyll");
		final Player hyde = PlayerTestHelper.createPlayer("hyde");
		
		zone.add(jekyll);
		zone.add(hyde);
		
		for (int level = 0; level < 200; level++) {
			hyde.setLevel(level);
			jekyll.setLevel(level);
			StendhalRPAction.startAttack(hyde, jekyll);
			// equal level. should always succeed
			assertSame("Attacking player at unprotected area", hyde.getAttackTarget(), jekyll);
			hyde.stopAttack(jekyll);
		}
	
		hyde.clearEvents();
		// protect jekyll; the attack should fail
		protectMap();
		for (int level = 0; level < 200; level++) {
			hyde.setLevel(level);
			jekyll.setLevel(level);
			StendhalRPAction.startAttack(hyde, jekyll);
			assertNull("Attacking player at protected area", hyde.getAttackTarget());
			assertEquals("message at attacking at protected area", 
					"The powerful protective aura in this place prevents you from attacking jekyll.", getTextMessage(hyde));
			hyde.clearEvents();
		}
	}
	
	@Test
	public void startAttackingStrongerPlayer() {
		final Player jekyll = PlayerTestHelper.createPlayer("jekyll");
		final Player hyde = PlayerTestHelper.createPlayer("hyde");
		
		zone.add(jekyll);
		zone.add(hyde);
		
		for (int level = 0; level < 200; level++) {
			jekyll.setLevel(level);
			StendhalRPAction.startAttack(hyde, jekyll);
			// jekyll is stronger than hyde, so attacking should succeed
			assertSame("Attacking player at unprotected area", hyde.getAttackTarget(), jekyll);
			hyde.stopAttack(jekyll);
		}
		
		// protect jekyll; the attack should fail
		protectMap();
		for (int level = 0; level < 200; level++) {
			jekyll.setLevel(level);
			StendhalRPAction.startAttack(hyde, jekyll);
			assertNull("Attacking player at protected area", hyde.getAttackTarget());
			assertEquals("message at attacking at protected area", 
					"The powerful protective aura in this place prevents you from attacking jekyll.", getTextMessage(hyde));
			hyde.clearEvents();
		}
	}
	
	@Test
	public void startAttackingWeakPlayer() {
		final Player jekyll = PlayerTestHelper.createPlayer("jekyll");
		final Player hyde = PlayerTestHelper.createPlayer("hyde");
		
		zone.add(jekyll);
		zone.add(hyde);
		
		for (int defenderLevel = 0; defenderLevel < 200; defenderLevel += 10) {
			jekyll.setLevel(defenderLevel);
			for (int attackerLevel = (int) (1.3 * defenderLevel); 0.74 * attackerLevel  <= defenderLevel + 2; attackerLevel++) {
				hyde.setLevel(attackerLevel);
				StendhalRPAction.startAttack(hyde, jekyll);
				if ((jekyll.getLevel() + 2.0) < hyde.getLevel() * 0.75) {
					assertNull("Attacking a too weak player. Level " + hyde.getLevel() + " vs Level " + jekyll.getLevel(), 
							hyde.getAttackTarget());
					
					assertEquals("message at attacking at protected area", 
							"Your conscience would trouble you if you carried out this attack.", getTextMessage(hyde));
					hyde.clearEvents();
					
					// check that self defence works
					StendhalRPAction.startAttack(jekyll, hyde);
					StendhalRPAction.startAttack(hyde, jekyll);
					assertSame("Self defence against a weak enemy", hyde.getAttackTarget(), jekyll);
					jekyll.stopAttack(hyde);
				} else {
					// the victim is not too weak
					assertSame("Attacking only a bit weaker victim", hyde.getAttackTarget(), jekyll);
				}
				hyde.stopAttack(jekyll);
			}
		}
	}
	
	@Test
	public void startAttackingPet() {
		final Player jekyll = PlayerTestHelper.createPlayer("jekyll");
		final Player hyde = PlayerTestHelper.createPlayer("hyde");
		final Sheep sheep = new Sheep();
		
		zone.add(hyde);
		zone.add(sheep);
		// attacking wild sheep should be ok
		StendhalRPAction.startAttack(hyde, sheep);
		assertSame("Attacking a sheep in unprotected area", hyde.getAttackTarget(), sheep);
		hyde.stopAttack(sheep);
		// also if you are the owner
		sheep.setOwner(hyde);
		StendhalRPAction.startAttack(hyde, sheep);
		assertSame("Attacking a sheep in unprotected area", hyde.getAttackTarget(), sheep);
		hyde.stopAttack(sheep);
		// but attacking someone elses pet is a no-no
		sheep.setOwner(jekyll);
		StendhalRPAction.startAttack(hyde, sheep);
		assertNull("Attacking someone else's sheep", hyde.getAttackTarget());
		assertEquals("message at attacking someone else's sheep", 
				"You pity jekyll's sheep too much to kill it.", 
				getTextMessage(hyde));
		hyde.stopAttack(sheep);
		hyde.clearEvents();
		sheep.setOwner(null);
		
		// Protected. should fail
		protectMap();
		StendhalRPAction.startAttack(hyde, sheep);
		assertNull("Attacking a sheep in protected area ", hyde.getAttackTarget());
		assertEquals("message at attacking a sheep in protected area", 
				"The powerful protective aura in this place prevents you from attacking that sheep.", 
				hyde.events().get(0).get("text"));
		hyde.stopAttack(sheep);
		hyde.clearEvents();
		
		// the same with an owned sheep
		sheep.setOwner(hyde);
		StendhalRPAction.startAttack(hyde, sheep);
		assertNull("Attacking a sheep in protected area ", hyde.getAttackTarget());
		assertEquals("message at attacking a sheep in protected area", 
				"The powerful protective aura in this place prevents you from attacking hyde's sheep.", 
				hyde.events().get(0).get("text"));
		hyde.clearEvents();
		
		// ...regarless of the owner
		sheep.setOwner(jekyll);
		StendhalRPAction.startAttack(hyde, sheep);
		assertNull("Attacking a sheep in protected area ", hyde.getAttackTarget());
		assertEquals("message at attacking a sheep in protected area", 
				"The powerful protective aura in this place prevents you from attacking jekyll's sheep.", 
				hyde.events().get(0).get("text"));
	}
}
