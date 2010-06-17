package games.stendhal.client.gui.styled;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * ButtonUI implementation for drawing {@link WoodStyle} style buttons. 
 */
public class StyledButtonUI extends BasicButtonUI {
	private static StyledButtonUI instance;
	
	private final Style style;
	
	// Required by UIManager, not necessarily called from EDT
	public static synchronized ComponentUI createUI(JComponent button) {
		// Button UIs can be shared
		if (instance == null) {
			instance = new StyledButtonUI(StyleUtil.getStyle());
		}
		
		return instance;
	}
	
	/**
	 * Create a new StyledButtonUI
	 * 
	 * @param style used pixmap style
	 */
	public StyledButtonUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void paint(Graphics graphics, JComponent button) {
		AbstractButton btn = (AbstractButton) button;
		paintBackground(graphics, button);
		// Restore normal look after pressing ends, if needed
		if (!btn.getModel().isPressed()) {
			// Try to avoid switching borders if the buttom has none or custom 
			// borders
			if (button.getBorder().equals(style.getBorderDown())) {
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
		// Try to avoid switching borders if the button has none, or custom
		// borders
		if (style.getBorder().equals(button.getBorder())) {
			button.setBorder(style.getBorderDown());
		}
	}
	
	@Override
	protected void paintText(Graphics graphics, AbstractButton button, 
			Rectangle textRect, String text) {
		if (button.isEnabled()) {
			super.paintText(graphics, button, textRect, text);
		} else {
			int shift = graphics.getFontMetrics().getAscent();
			
			StyleUtil.paintDisabledText(style, graphics, text, textRect.x, textRect.y + shift);
		}
	}
	
	@Override
	protected void paintFocus(Graphics graphics, AbstractButton button, 
			Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		graphics.setColor(style.getShadowColor());
		graphics.drawRect(textRect.x, textRect.y, textRect.width, textRect.height);
	}
	
	@Override
	public Dimension getPreferredSize(JComponent button) {
		/*
		 * The default styles do some weird trick with their borders that
		 * affects only the preferred size, but not the minimum size. Making
		 * a special border type that takes the size of the margin does not 
		 * work, because that increases the minimum size as well. Anyway, the
		 * effect can be simulated like this.
		 */
		Dimension dim = super.getPreferredSize(button);
		Insets margin = ((AbstractButton) button).getMargin();
		dim.width += margin.left + margin.right;
		dim.height += margin.top + margin.bottom;
		
		return dim;
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
	 * Draws the mouse focus highlighting
	 * 
	 * @param graphics
	 * @param button
	 */
	private void hilite(Graphics graphics, JComponent button) {
		graphics.setColor(style.getHighLightColor());
		Insets insets = button.getInsets();
		// -1 to avoid right and bottom lines ending under the border
		int width = button.getWidth() - insets.right - insets.left - 1;
		int height = button.getHeight() - insets.top - insets.bottom - 1;
		graphics.drawRect(insets.left, insets.top, width, height);
	}
	
	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setForeground(style.getForeground());
		button.setBorder(style.getBorder());
	}
}
