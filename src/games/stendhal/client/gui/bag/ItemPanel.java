package games.stendhal.client.gui.bag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ItemPanel extends JPanel {

	static BufferedImage background = new ItemImageLoader()
			.loadFromPath("data/gui/slot.png");
	private ItemImage itemImage;
	private final JTextField field = new JTextField();
	private CircledCollection<BufferedImage> circledColl;
	public ItemPanel() {
		
		field.setOpaque(false);
		field.setBorder(null);
		field.setForeground(Color.WHITE);
		this.setOpaque(false);
		this.add(field);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		
		if (itemImage != null) {
			g.drawImage(circledColl.getCurrent(), 5, 5,
					this);
			circledColl.moveNext();
		}
	
	}

	public void setImage(final ItemImage image) {
		itemImage = image;
		if (image != null) {
			
			circledColl = image.getCircledCollectionArray()[0];
		} else {
			field.setText(null);
		}
		
	}

	public void setQuantity(final int amount) {
		
		final String text = format(amount);
		field.setText(text);
		
		revalidate();
	}

	private String format(final long value) {
		if (value > 1000000) {
			return String.valueOf(value / 1000000) + "M";
		}
		if (value > 1000) {
			return String.valueOf(value / 1000) + "K";
		}
		return String.valueOf(value);
	}

}
