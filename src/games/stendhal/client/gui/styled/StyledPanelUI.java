package games.stendhal.client.gui.styled;

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
		StyleUtil.fillBackground(style, graphics, 0, 0, panel.getWidth(), panel.getHeight());
	}
	
	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setForeground(style.getForeground());
		button.setBorder(style.getBorder());
	}
}
