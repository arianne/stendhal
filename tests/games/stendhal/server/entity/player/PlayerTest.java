package games.stendhal.server.entity.player;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerTest {
	String playername = "player";
	Player player;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		player = PlayerTestHelper.createPlayer(playername);

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

	@Ignore
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsObstacle() {
		Entity ent = new Entity() {
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
		int hours = player.getAge() / 60;
		int minutes = player.getAge() % 60;
		String time = hours + " hours and " + minutes + " minutes";
		assertThat(player.describe(), is("You see " + player.getTitle() + ".\n" + player.getTitle() + " is level "
				+ player.getLevel() + " and has been playing " + time + "."));

	}

	@Ignore
	@Test
	public void testIsZoneChangeAllowed() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testStop() {
		fail("Not yet implemented");
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
		player.useKarma(5.0);
		assertTrue(player.getKarma() >= 10.0);
		assertTrue(player.getKarma() <= 15.0);

	}
@Ignore
	@Test
	public void testOnAttacked() {
		fail("Not yet implemented");
	}
@Ignore
	@Test
	public void testOnDeadEntity() {
		fail("Not yet implemented");
	}
@Ignore
	@Test
	public void testDropItemsOn() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsInvisible() {
		Player player2 = PlayerTestHelper.createPlayer("player2");
		assertThat(player2.isInvisible(), not(is(true)));
		player2.setInvisible(true);
		assertThat(player2.isInvisible(), is(true));
		player2.setInvisible(false);
		assertThat(player2.isInvisible(), not(is(true)));
	}
	@Ignore
	@Test
	public void testSendPrivateTextString() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetOutfitOutfit() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testLogic() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testCreateEmptyZeroLevelPlayer() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testDestroy() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testPlayer() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testAddClientDirection() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemoveClientDirection() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testApplyClientDirection() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetAwayMessage() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetAwayMessage() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsAwayNotifyNeeded() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testResetAwayReplies() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetGrumpyMessage() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetGrumpyMessage() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testAddIgnore() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetIgnore() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemoveIgnore() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetSkill() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetSkill() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetKeyedSlotObject() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetKeyedSlot() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetKeyedSlot() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetFeature() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHasFeature() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetFeatureStringBoolean() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetFeatureStringString() {
		fail("Not yet implemented");
	}

	@Ignore	
	@Test
	public void testSendPrivateTextNotificationTypeString() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetGetLastPrivateChatter() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testSetGetAdminLevel() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRemoveSheep() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemovePet() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHasSheep() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHasPet() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetPet() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetSheep() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetSheep() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetPet() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetAge() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsNew() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetAge() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testStoreLastPVPActionTime() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetLastPVPActionTime() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testNotifyOnline() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testNotifyOffline() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsQuestCompleted() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHasQuest() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetQuest() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetQuest() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetQuests() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemoveQuest() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsQuestInState() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHasKilled() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetSoloKill() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetSharedKill() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemoveKill() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsPoisoned() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testHealPoison() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testPoison() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsFull() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testEat() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetImmune() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testRemoveImmunity() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testConsume() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testTeleport() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testDropAll() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testOnPush() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testCanPush() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetOutfitOutfitBoolean() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetOriginalOutfit() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testReturnToOriginalOutfit() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsTeleclickEnabled() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testSetTeleclickEnabled() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testGetSentence() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testIsDisconnected() {
		fail("Not yet implemented");
	}

}
