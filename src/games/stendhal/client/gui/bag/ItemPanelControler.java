package games.stendhal.client.gui.bag;

import java.awt.Component;
import java.awt.Dimension;

import marauroa.common.game.RPObject;

public class ItemPanelControler {

	private static final Dimension PREFERRED_SIZE = new Dimension(40, 40);
	private boolean isEmpty = true;
	private final ItemPanel ITEM_PANEL = new ItemPanel();
	
	public ItemPanelControler() {
		ITEM_PANEL.setPreferredSize(PREFERRED_SIZE);
		ITEM_PANEL.addMouseListener(new PopupMenuListener() {
			
		});
	}
	
	public Component getComponent() {
		return ITEM_PANEL;
	}

	public void removeItem(final RPObject object) {
		ITEM_PANEL.setImage(null);
		this.isEmpty = true;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public void addNew(final RPObject object) {
		isEmpty = false;
		ITEM_PANEL.setImage(new ItemImageLoader().loadItemImageFromObject(object));
		updateValues(object);
	}

	public void updateValues(final RPObject object) {
		if (object.has("quantity")) {
			ITEM_PANEL.setQuantity(object.getInt("quantity"));
		}
	}
}
