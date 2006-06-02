/**
 * 
 */
package games.stendhal.test;

import games.stendhal.client.Sprite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;

/**
 * @author mtotz
 * 
 */
public class SLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SLabel() {
		this("");
	}

	/**
	 * @param image
	 * @param horizontalAlignment
	 */
	public SLabel(Icon image, int horizontalAlignment) {
		this(null, image, horizontalAlignment);
	}

	/**
	 * @param image
	 */
	public SLabel(Icon image) {
		this(null, image, CENTER);
	}

	/**
	 * @param text
	 * @param horizontalAlignment
	 */
	public SLabel(String text, int horizontalAlignment) {
		this(text, null, horizontalAlignment);
	}

	/**
	 * @param text
	 */
	public SLabel(String text) {
		this(text, null, LEADING);
	}

	/**
	 * @param text
	 * @param icon
	 * @param horizontalAlignment
	 */
	public SLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setBorder(new CompoundBorder(new ImageBorder(
				ImageBorder.RaisedState.RAISED, 3), new CompoundBorder(
				new ImageBorder(ImageBorder.RaisedState.NORMAL, 3),
				new ImageBorder(ImageBorder.RaisedState.LOWERED, 3))));
		setOpaque(false);
		setForeground(Color.WHITE);
	}

	protected void paintComponent(Graphics g) {
		Rectangle r = g.getClipBounds();

		Sprite bgSprite = SwingUtils.getInstance().getNormalSprite();
		for (int x = r.x; x < r.x + r.width; x += bgSprite.getWidth()) {
			for (int y = r.y; y < r.y + r.height; y += bgSprite.getHeight()) {
				bgSprite.draw(g, x, y);
			}
		}
		super.paintComponent(g);
	}

}
