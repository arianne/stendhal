package games.stendhal.client.gui.styled;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class StyledScrollBarUI extends MetalScrollBarUI {
	private final Style style;
	
	public StyledScrollBarUI(Style style) {
		this.style = style;
	}
	
	/**
	 * Paints the background flat area of the scroll bar.
	 * 
	 * @param g graphics
	 * @param bar the scroll bar to be painted
	 * @param trackBounds bounds of the painted area
	 */
	@Override
	protected void paintTrack(Graphics g, JComponent bar, Rectangle trackBounds) {
		g.setColor(style.getHighLightColor());
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
		g.setColor(style.getShadowColor());
		g.drawRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}
	
	/**
	 * Draws the handle of the scroll bar.
	 * 
	 * @param g graphics
	 * @param bar the scroll bar to be painted
	 * @param bounds bounds of the scroll bar handle
	 */
	@Override
	protected void paintThumb(Graphics g, JComponent bar, Rectangle bounds) {
		StyleUtil.fillBackground(style, g, bounds.x, bounds.y, bounds.width, bounds.height);
		style.getBorder().paintBorder(bar, g, bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new StyledArrowButton(orientation, style);
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new StyledArrowButton(orientation, style);
	}
	
	@Override
	public void installUI(JComponent scrollBar) {
		super.installUI(scrollBar);
		scrollBar.setForeground(style.getForeground());
	}
	
	/**
	 * An arrow button drawing according to the style.
	 * <p>
	 * BasicArrowButton fails to use ButtonUI even remotely properly.
	 */
	private static class StyledArrowButton extends BasicArrowButton {
		private static final int ARROW_SIZE = 5;
		
		public StyledArrowButton(int orientation, Style style) {
			/*
			 *  Only the darkShadow color is actually used, but calling the 
			 *  more complicated constructor is the only way to set the color
			 *  of the arrow.
			 */
			super(orientation, style.getForeground(), style.getForeground(),
					style.getForeground(), style.getForeground());
			setUI(new StyledButtonUI());
		}

		@Override
		public void paint(Graphics g) {
			paintComponent(g);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			getUI().paint(g, this);
			getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
			
			/*
			 * The coordinates are found by trial and error. The method is not 
			 * properly documented anywhere.
			 */
			paintTriangle(g, (getWidth() - ARROW_SIZE) / 2 + 1, 
					(getHeight() - ARROW_SIZE) / 2 + 1, ARROW_SIZE, 
					getDirection(), true);
		}
	}
}