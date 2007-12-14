package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * equipts the specified item
 */
public class DropItemAction extends SpeakerNPC.ChatAction {
	private static Logger logger = Logger.getLogger(DropItemAction.class);
	private String itemName;
	private int amount;

	/**
	 * Creates a new EquipItemAction
	 * 
	 * @param itemName
	 *            name of item
	 */
	public DropItemAction(String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new EquipItemAction
	 * 
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            for StackableItems
	 */
	public DropItemAction(String itemName, int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		boolean res = player.drop(itemName, amount);
		if (!res) {
			logger.error("Cannot drop " + amount + " " + itemName,
					new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + amount;
		result = PRIME * result
				+ ((itemName == null) ? 0 : itemName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DropItemAction other = (DropItemAction) obj;
		if (amount != other.amount) {
			return false;
		}
		if (itemName == null) {
			if (other.itemName != null) {
				return false;
			}
		} else if (!itemName.equals(other.itemName)) {
			return false;
		}
		return true;
	}

}