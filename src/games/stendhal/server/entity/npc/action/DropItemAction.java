package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;

/**
 * Drops the specified item.
 */
public class DropItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropItemAction.class);
	private final String itemName;
	private final int amount;

	/**
	 * Creates a new DropItemAction.
	 * 
	 * @param itemName
	 *            name of item
	 */
	public DropItemAction(final String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new DropItemAction.
	 * 
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            for StackableItems
	 */
	public DropItemAction(final String itemName, final int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final boolean res = player.drop(itemName, amount);
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
		if (itemName == null) {
			result = PRIME * result;

		} else {
			result = PRIME * result + itemName.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
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
