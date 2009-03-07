package games.stendhal.client.gui.bag;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import marauroa.common.game.RPObject;

public class ItemPanel extends JPanel {

	static BufferedImage background = new ItemImageLoader()
			.loadFromPath("/data/gui/slot.png");
	private ItemImage itemImage;
	
	public ItemPanel() {
		this.setOpaque(false);
	}

	@Override
	protected void paintComponent(final Graphics g) {

		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		
		if (itemImage != null) {
			g.drawImage(itemImage.getCircledCollectionArray()[0].getCurrent(), 5, 5,
					this);
			itemImage.getCircledCollectionArray()[0].moveNext();
		}
	
	}

	public void setImage(ItemImage image) {
		itemImage = image;
		
	}

}
