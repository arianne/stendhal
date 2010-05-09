package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player carry the specified item?
 */
public class PlayerHasRecordedItemWithHimCondition implements ChatCondition {

	private final String questName;
	private final int index;

	/**
	 * Creates a new PlayerHasItemWithHim.
	 * 
	 * @param itemName
	 *            name of item
	 */
	public PlayerHasRecordedItemWithHimCondition(final String questName) {
		this.questName = questName;
		this.index = -1;
	}

	/**
	 * Creates a new PlayerHasItemWithHim.
	 * 
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            for StackableItems
	 */
	public PlayerHasRecordedItemWithHimCondition(final String questName, final int index) {
		this.questName = questName;
		this.index = index;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final String questSubString = player.getQuest(questName, index);
		final String[] elements = questSubString.split("=");
		String itemName=elements[0];
		int amount = 1;
		if(elements.length > 1) {
			amount=MathHelper.parseIntDefault(elements[1], 1);
		} 
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has recorded item from questslot <" + questName + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasRecordedItemWithHimCondition.class);
	}
}
