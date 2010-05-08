package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player carry the specified item?
 */
public class PlayerHasItemWithHimCondition implements ChatCondition {

	private final String itemName;
	private final int amount;

	/**
	 * Creates a new PlayerHasItemWithHim.
	 * 
	 * @param itemName
	 *            name of item
	 */
	public PlayerHasItemWithHimCondition(final String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new PlayerHasItemWithHim.
	 * 
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            for StackableItems
	 */
	public PlayerHasItemWithHimCondition(final String itemName, final int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerHasItemWithHimCondition.class);
	}
}
