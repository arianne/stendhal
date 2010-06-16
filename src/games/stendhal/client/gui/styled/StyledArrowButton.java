package games.stendhal.client.gui.styled;

import java.awt.Graphics;

import javax.swing.plaf.basic.BasicArrowButton;

/**
 * An arrow button drawing according to the style.
 * <p>
 * BasicArrowButton fails to use ButtonUI even remotely properly.
 */
class StyledArrowButton extends BasicArrowButton {
	private static final int ARROW_SIZE = 5;

	/**
	 * Create a new StyledArrowButton.
	 * 
	 * @param orientation direction of the arrow
	 * @param style pixmap style
	 */
	public StyledArrowButton(int orientation, Style style) {
		/*
		 *  Only the darkShadow color is actually used, but calling the 
		 *  more complicated constructor is the only way to set the color
		 *  of the arrow.
		 */
		super(orientation, style.getForeground(), style.getForeground(),
				style.getForeground(), style.getForeground());
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