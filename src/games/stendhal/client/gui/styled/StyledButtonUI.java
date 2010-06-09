package games.stendhal.client.gui.styled;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * ButtonUI implementation for drawing {@link WoodStyle} style buttons. 
 */
public class StyledButtonUI extends BasicButtonUI {
	Style style = WoodStyle.getInstance();
	
	@Override
	public void paint(Graphics graphics, JComponent button) {
		AbstractButton btn = (AbstractButton) button;
		paintBackground(graphics, button);
		// Restore normal look after pressing ends, if needed
		if (!btn.getModel().isPressed()) {
			if (!button.getBorder().equals(style.getBorder())) {
				button.setBorder(style.getBorder());
			}
		}
		if (button.getMousePosition() != null) {
			hilite(graphics, button);
		}

		super.paint(graphics, button);
	}
	
	@Override
	protected void paintButtonPressed(Graphics graphics, AbstractButton button) {
		button.setBorder(style.getBorderDown());
	}
	
	@Override
	protected void paintFocus(Graphics graphics, AbstractButton button, 
			Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		graphics.setColor(Color.GRAY);
		graphics.drawRect(textRect.x, textRect.y, textRect.width, textRect.height);
	}
	
	/**
	 * Draw the background image
	 * @param graphics
	 * @param button
	 */
	private void paintBackground(Graphics graphics, JComponent button) {
		StyleUtil.fillBackground(style, graphics, 0, 0, button.getWidth(), button.getHeight());
	}
	
	/**
	 * Draws the mouse focus hilighting
	 * 
	 * @param graphics
	 * @param button
	 */
	private void hilite(Graphics graphics, JComponent button) {
		graphics.setColor(button.getForeground());
		Insets insets = button.getInsets();
		int width = button.getWidth() - insets.right - insets.left;
		int height = button.getHeight() - insets.top - insets.bottom;
		graphics.drawRect(insets.left, insets.top, width, height);
	}
	
	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setForeground(style.getForeground());
		button.setBorder(style.getBorder());
	}
}
