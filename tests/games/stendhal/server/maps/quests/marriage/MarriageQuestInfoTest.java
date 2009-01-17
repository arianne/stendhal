package games.stendhal.server.maps.quests.marriage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class MarriageQuestInfoTest {

	@BeforeClass
	public static void setupBeforeClass() {
		MockStendlRPWorld.get();
	}
	
	@AfterClass
	public static void teardownAfterClass() throws Exception {
		
		MockStendlRPWorld.reset();
	}
	@Test
	public void testGetQuestSlot() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		assertEquals("marriage", questinfo.getQuestSlot());
	}

	@Test
	public void testGetSpouseQuestSlot() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		assertEquals("spouse", questinfo.getSpouseQuestSlot());

	}

	@Test
	public void testIsMarried() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(questinfo.isMarried(bob));
		bob.setQuest(questinfo.getSpouseQuestSlot(), "any");
		assertTrue(questinfo.isMarried(bob));
	}

	@Test
	public void testIsEngaged() {
		MarriageQuestInfo questinfo = new MarriageQuestInfo();
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(questinfo.isEngaged(bob));
		bob.setQuest(questinfo.getQuestSlot(), "engagedany");
		assertTrue(questinfo.isEngaged(bob));
		
		bob.setQuest(questinfo.getQuestSlot(), "forging;any");
		assertTrue(questinfo.isEngaged(bob));
		
		

	}

}
