package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Does the player carry the specified item?
 */
public class PlayerHasItemWithHim extends SpeakerNPC.ChatCondition {

	private String itemName;
	private int amount;

	/**
	 * Creates a new PlayerHasItemWithHim
	 *
	 * @param itemName name of item
	 */
	public PlayerHasItemWithHim(String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new PlayerHasItemWithHim
	 *
	 * @param itemName name of item
	 * @param amount for StackableItems
	 */
	public PlayerHasItemWithHim(String itemName, int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}


	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + amount;
		result = PRIME * result + ((itemName == null) ? 0 : itemName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PlayerHasItemWithHim other = (PlayerHasItemWithHim) obj;
		if (amount != other.amount) return false;
		if (itemName == null) {
			if (other.itemName != null) return false;
		} else if (!itemName.equals(other.itemName)) return false;
		return true;
	}


}