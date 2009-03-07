package games.stendhal.client.gui.bag;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import marauroa.common.game.RPObject;

public class ItemPanel extends JPanel {

	static BufferedImage background = new ItemImageLoader()
			.loadFromPath("/data/gui/slot.png");
	private ItemImage itemImage;
	private boolean isEmpty = true;

	public ItemPanel() {
		this.setOpaque(false);
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void addNew(final RPObject object) {
		isEmpty = false;
		itemImage = new ItemImageLoader().loadItemImageFromObject(object);
		String amount = "";
		if (object.has("quantity")) {
			amount = object.get("quantity");
		}
	}

	public void updateValues(final RPObject object) {
		if (object.has("quantity")) {
		}
	}

	public void removeItem(final RPObject object) {
		this.isEmpty = true;
	}

	@Override
	protected void paintComponent(final Graphics g) {

		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		if (!isEmpty()) {
			if (itemImage != null) {
				g.drawImage(itemImage.getNiceNames()[0].getCurrent(), 5, 5,
						this);
				itemImage.getNiceNames()[0].moveNext();
			}
		}
	}

}
