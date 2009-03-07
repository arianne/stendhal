package games.stendhal.client.gui.bag;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import marauroa.common.game.RPObject;

public class ItemPanel extends JPanel {
	
	static BufferedImage background = new ItemImageLoader().loadFromPath("/data/gui/slot.png");
	private ItemImage itemImage;
	

	public boolean isEmpty() {
		return getComponentCount() == 0;
	}

	public void addNew(final RPObject object) {
		itemImage = new ItemImageLoader().loadItemImageFromObject(object);
		 String amount = "";	
		if (object.has("quantity")) {
			amount = object.get("quantity");	
			}
			 
			final JTextArea textArea = new JTextArea(amount);
			this.add(textArea);
		
		revalidate();
		repaint();
	}

	public void updateValues(final RPObject object) {
		JTextArea field = (JTextArea) this.getComponent(0);
		if (object.has("quantity")) {
			field.setText(object.get("quantity"));	
			}
	}

	public void removeItem(final RPObject object) {
		this.removeAll();
		revalidate();
		repaint();
		
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		if (!isEmpty()) {
			g.drawImage(itemImage.getNiceNames()[0].getCurrent(), 5, 5, this);
			itemImage.getNiceNames()[0].moveNext();
		}
	}

}
