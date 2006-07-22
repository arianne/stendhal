/**
 * 
 */
package games.stendhal.test.gui;

import games.stendhal.client.Sprite;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Shape;

import javax.swing.AbstractButton;
import javax.swing.border.AbstractBorder;

/**
 * @author mtotz
 * 
 */
public class ImageBorder extends AbstractBorder {
	private static final long serialVersionUID = 1L;

	/** size of the border */
	private int borderSize;

	/** true when the border is raised */
	private RaisedState raised;

	public enum RaisedState {
		RAISED, NORMAL, LOWERED;
	}

	/** */
	public ImageBorder() {
		this(RaisedState.NORMAL);
	}

	/** */
	public ImageBorder(RaisedState raised) {
		this(raised, 2);
	}

	/**
	 * 
	 */
	public ImageBorder(RaisedState raised, int borderSize) {
		this.borderSize = borderSize;
		this.raised = raised;
	}

	/** border is opaque */
	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	/**
	 * This default implementation returns a new <code>Insets</code> instance
	 * where the <code>top</code>, <code>left</code>, <code>bottom</code>,
	 * and <code>right</code> fields are set to <code>0</code>.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @return the new <code>Insets</code> object initialized to 0
	 */
	@Override
	public Insets getBorderInsets(Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	/**
	 * Reinitializes the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 * @return the <code>insets</code> object
	 */
	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = borderSize;
		return insets;
	}

	/** paints the border */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Sprite bottomRight;
		Sprite topLeft;
		SwingUtils utils = SwingUtils.getInstance();
		if (raised == RaisedState.NORMAL) {
			bottomRight = utils.getNormalSprite();
			topLeft = utils.getNormalSprite();
		} else {
			boolean localRaised = (raised == RaisedState.RAISED);

			if (c instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) c;
				if (button.getModel().isArmed()
						|| button.getModel().isPressed()) {
					localRaised = !localRaised;
				}
			}
			bottomRight = localRaised ? utils.getDarkSprite() : utils
					.getBrightSprite();
			topLeft = localRaised ? utils.getBrightSprite() : utils
					.getDarkSprite();
		}

		int spriteWidth = topLeft.getWidth();
		int spriteHeight = topLeft.getHeight();

		// be sure to get the right clipping
		Graphics g2 = g.create(x, y, width, height);

		int xPos = x % spriteWidth;
		int yPos = y % spriteHeight;

		// temp-storage for the width
		int w;
		int h;

		// top and bottom edges
		xPos = x % spriteWidth;
		for (int i = 0; i < width; i += w) {
			w = spriteWidth - (xPos % spriteWidth);
			yPos = y % spriteHeight;
			for (int j = 0; j < borderSize; j += h) {
				h = spriteHeight - (yPos % spriteHeight);
				h = (j + h > borderSize) ? borderSize - j : h;
				topLeft.draw(g2, i, j, xPos, yPos, w, h);
				yPos = (yPos + h) % spriteHeight;
			}
			yPos = (y + height - borderSize) % spriteHeight;
			for (int j = height - borderSize; j < height; j += h) {
				h = spriteHeight - (yPos % spriteHeight);
				h = (j + h > height) ? height - j : h;
				bottomRight.draw(g2, i, j, xPos, yPos, w, h);
				yPos = (yPos + h) % spriteHeight;
			}
			xPos = (xPos + w) % spriteWidth;
		}

		Shape clip = new Polygon(new int[] { 0, borderSize, width, width,
				width - borderSize, 0 }, new int[] { height,
				height - borderSize, height - borderSize, 0, borderSize,
				borderSize }, 6);
		g2.setClip(clip);

		// left and right edges
		yPos = y % spriteHeight;
		for (int j = 0; j < height; j += h) {
			h = spriteHeight - (yPos % spriteHeight);
			xPos = x % spriteWidth;
			for (int i = 0; i < borderSize; i += w) {
				w = spriteWidth - (xPos % spriteWidth);
				w = (i + w > borderSize) ? borderSize - i : w;
				topLeft.draw(g2, i, j, xPos, yPos, w, h);
				xPos = (xPos + w) % spriteWidth;
			}
			xPos = (x + width - borderSize) % spriteWidth;
			for (int i = width - borderSize; i < width; i += w) {
				w = spriteWidth - (xPos % spriteWidth);
				w = (i + w > width) ? width - i : w;
				bottomRight.draw(g2, i, j, xPos, yPos, w, h);
				xPos = (xPos + w) % spriteWidth;
			}
			yPos = (yPos + h) % spriteHeight;
		}
	}

}
