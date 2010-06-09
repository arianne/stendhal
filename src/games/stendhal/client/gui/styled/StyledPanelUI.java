package games.stendhal.client.gui.styled;

import games.stendhal.client.sprite.Sprite;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/**
 * PanelUI implementation for drawing in pixmap styles.
 */
public class StyledPanelUI extends PanelUI {
	private final Style style;
	
	/**
	 * Create a new pixmap style.
	 * 
	 * @param style {@link Style} to be used for drawing the panel
	 */
	public StyledPanelUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void paint(Graphics graphics, JComponent panel) {
		Sprite image = style.getBackground();
		for (int x = 0; x < panel.getWidth(); x += image.getWidth()) {
			for (int y = 0; y < panel.getHeight(); y += image.getHeight()) {
				image.draw(graphics, x, y);
			}
		}
	}
	
	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setForeground(style.getForeground());
		button.setBorder(style.getBorder());
	}
}
