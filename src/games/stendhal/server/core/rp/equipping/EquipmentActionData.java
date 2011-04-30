/* $Id$
 * $Log$
 * Revision 1.1  2011/04/30 10:54:12  nhnb
 * created an object to save all the information needed during an equip action
 *
 */
package games.stendhal.server.core.rp.equipping;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;

import java.util.LinkedList;
import java.util.List;

/**
 * data use in an equipment action
 *
 * @author hendrik
 */
// TODO: This is experimental code not in use, yet.
public class EquipmentActionData {

	/** the name of an item */
	private String itemName;

	/** the requested quantity */
	private int quantity = -1;

	/** the source slots */
	private final List<EntitySlot> sourceSlots = new LinkedList<EntitySlot>();

	/** the target slots */
	private EntitySlot targetSlot;

	/** the source items */
	private final List<Entity> sourceItems = new LinkedList<Entity>();

	/** player */
	private Player player;

	/** isSwapable? */
	private boolean swapable;

	/** in-game error message */
	private String errorMessage;

	/** in-game warning message */
	private String warningMessage;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<EntitySlot> getSourceSlots() {
		return sourceSlots;
	}

	public void addSourceSlot(EntitySlot sourceSlot) {
		this.sourceSlots.add(sourceSlot);
	}

	public EntitySlot getTargetSlot() {
		return targetSlot;
	}

	public void setTargetSlot(EntitySlot targetSlot) {
		this.targetSlot = targetSlot;
	}

	public List<Entity> getSourceItems() {
		return sourceItems;
	}

	public void addSourceItem(Entity sourceItem) {
		this.sourceItems.add(sourceItem);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isSwapable() {
		return swapable;
	}

	public void setSwapable(boolean swapable) {
		this.swapable = swapable;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * stores the in-game error message to be sent to the client. "" means fail silently.
	 *
	 * @param message in-game error message
	 */
	public void setErrorMessage(String message) {
		this.errorMessage = message;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

}
