package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class AbstractQuestTest {
	private final class Mockquest extends AbstractQuest {
		@Override
		public String getSlotName() {
			return QUESTSlotSTRING;
		}
	}

	private static String QUESTSlotSTRING = "TESTQUEST";

	private static String QUESTNAMESTRING = "test quest name";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInitString() {
		final Mockquest quest = new Mockquest();
		quest.init(QUESTNAMESTRING);
		assertEquals(QUESTSlotSTRING, quest.getSlotName());
		assertEquals(QUESTNAMESTRING, quest.getName());
	}

	@Test
	public final void testGetHintGetHistory() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTSlotSTRING, null);
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}
		};
		assertTrue(quest.getHint(pl).isEmpty());
		assertTrue(quest.getHistory(pl).isEmpty());
	}

	@Test
	public final void testIsCompleted() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTSlotSTRING, null);
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return QUESTSlotSTRING;
			}
		};

		quest.init(QUESTNAMESTRING);

		assertFalse(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "done");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertTrue(pl.isQuestCompleted(QUESTSlotSTRING));
		assertTrue(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "rejected");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertFalse(pl.isQuestCompleted(QUESTSlotSTRING));
		assertTrue(quest.isCompleted(pl));

		pl.setQuest(QUESTSlotSTRING, "failed");
		assertTrue(pl.hasQuest(QUESTSlotSTRING));
		assertFalse(pl.isQuestCompleted(QUESTSlotSTRING));
		assertTrue(quest.isCompleted(pl));

	}

	@Test
	public final void testIsRepeatable() {
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}
		};
		assertFalse("abstract quests are not repeatable by default",
				quest.isRepeatable(null));
	}

	@Test
	public final void testIsStarted() {
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}
		};
		final Player pl = PlayerTestHelper.createPlayer("player");
		assertFalse(quest.isStarted(pl));
		pl.setQuest(QUESTSlotSTRING, "whatever");
	}

	@Test(expected = NullPointerException.class)
	public final void testIsStartedthrowsNPEwithnullArgument() {
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}
		};
		assertFalse(quest.isStarted(null));
	}

	@Test
	public final void testGetName() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setQuest(QUESTNAMESTRING, null);
		final AbstractQuest quest = new AbstractQuest() {

			@Override
			public String getSlotName() {
				return null;
			}
		};

		quest.init(QUESTNAMESTRING);
		assertEquals(QUESTNAMESTRING, quest.getName());
	}

}
