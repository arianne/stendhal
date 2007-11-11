package games.stendhal.server.entity.npc.action;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;

/**
 * equips the specified item
 */
public class EquipItemAction extends SpeakerNPC.ChatAction {

	private String itemName;
	private int amount;

	/**
	 * Creates a new EquipItemAction
	 *
	 * @param itemName name of item
	 */
	public EquipItemAction(String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new EquipItemAction
	 *
	 * @param itemName name of item
	 * @param amount for StackableItems
	 */
	public EquipItemAction(String itemName, int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC npc) {
		EntityManager entityManager = StendhalRPWorld.get().getRuleManager().getEntityManager();
		Item item = entityManager.getItem(itemName);
		if (item instanceof StackableItem) {
			StackableItem stackableItem = (StackableItem) item;
			stackableItem.setQuantity(amount);
		}
		player.equip(item, true);
	}

	@Override
	public String toString() {
		return "equip item <" + amount + " " + itemName + ">";
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
		final EquipItemAction other = (EquipItemAction) obj;
		if (amount != other.amount) return false;
		if (itemName == null) {
			if (other.itemName != null) return false;
		} else if (!itemName.equals(other.itemName)) return false;
		return true;
	}
	
}