package games.stendhal.client.gui.bag;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import marauroa.common.game.RPObject;

public class ItemPanel extends JPanel {
	
	static BufferedImage background = new ItemImageLoader().loadFromPath("/data/gui/slot.png");
	private Image itemImage;

	public boolean isEmpty() {
		return getComponentCount() == 0;
	}

	public void addNew(final RPObject object) {
		itemImage = new ItemImageLoader().loadFromObject(object);
		this.add(new JTextArea(object.get("id") + object.get("name")));
		revalidate();
		repaint();
	}

	public void updateValues(final RPObject object) {
		
		try {
			JTextArea field = (JTextArea) this.getComponent(0);
					for (final String text : object) {
						field.setText(field.getText() + '\n' + text + ":" + object.get(text));
					
					}
		} catch (Exception e) {
			System.out.println(e);
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
			g.drawImage(itemImage, 5, 5, this);
		}
	}

}
