/**
 * 
 */
package games.stendhal.test.gui;

import games.stendhal.client.Sprite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;

/**
 * @author mtotz
 * 
 */
public class SButton extends JButton {
	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 */
	public SButton(String name) {
		super(name);
		setContentAreaFilled(false);
		setForeground(Color.WHITE);
		setBorder(new ImageBorder(ImageBorder.RaisedState.RAISED, 3));
	}

	/**
	 * paints the component
	 */
	@Override
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
