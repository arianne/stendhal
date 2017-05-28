package games.stendhal.server.entity.npc.condition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

/**
 * Tests for TriggerMatchesQuestSlotCondition
 *
 * @author hendrik
 */
public class TriggerMatchesQuestSlotConditionTest {
	private static final String QUEST_SLOT = "questslot";

	/**
	 * tests for fire()
	 */
	@Test
	public void testFire() {
		final Player player = PlayerTestHelper.createPlayer("player");
		player.setQuest(QUEST_SLOT, "The banana rests in the fireplace.");
		assertTrue(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("The banana rests in the fireplace."), null));
		assertTrue(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("The banana rests in the fireplace"), null));
		assertTrue(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("Banana rests in the fireplace"), null));
		assertTrue(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("Banana rests in  fireplace"), null));
		assertTrue(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("banana Rests in Fireplace"), null));
		assertFalse(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, -1)
			.fire(player, ConversationParser.parse("Banana rests in the building"), null));
	}

	/**
	 * tests for toString()
	 */
	@Test
	public void testToString() {
		assertThat(new TriggerMatchesQuestSlotCondition("questname", 1).toString(),
			equalTo("questtext? <questname[1]>"));
	}

	/**
	 * tests for equals()
	 */
	@Test
	public void testEqualsObject() {
		assertThat(new TriggerMatchesQuestSlotCondition("questname", 1),
				equalTo(new TriggerMatchesQuestSlotCondition("questname", 1)));
		assertThat(new TriggerMatchesQuestSlotCondition("questname", 1),
				not(equalTo(new TriggerMatchesQuestSlotCondition("questname2", 1))));
		assertThat(new TriggerMatchesQuestSlotCondition("questname", 1),
				not(equalTo(new TriggerMatchesQuestSlotCondition("questname", 0))));
		assertThat(new TriggerMatchesQuestSlotCondition("questname", 1),
				not(equalTo(null)));
	}

}
