package games.stendhal.client.gui.j2d.entity.helpers;

import java.awt.Graphics2D;

import games.stendhal.client.sprite.Sprite;
/**
 * Helper class for drawing sprites with a certain alignment in a certain area
 *
 * @author madmetzger
 */
public class DrawingHelper {

	/**
	 * Align a sprite in a defined area, which is defined by upper left corner (x,y)
	 * and width to the right and height downwards
	 *
	 * @param g2d
	 * @param sprite the sprite to draw
	 * @param horizontalAlign (left, center, right)
	 * @param verticalAlign (top, middle, bottom)
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawAlignedSprite(Graphics2D g2d, Sprite sprite,
			HorizontalAlignment horizontalAlign, VerticalAlignment verticalAlign,
			int x, int y, int width, int height) {
		/*
		 * NOTE: This has be calibrated to fit the size of an entity slot
		 * visual.
		 */
		int qx = alignHorizontal(sprite, horizontalAlign, x, width);
		int qy = alignVertical(sprite, verticalAlign, y, height);
		sprite.draw(g2d, qx, qy);
	}

	private static int alignVertical(Sprite sprite,
			VerticalAlignment verticalAlign, int y, int height) {
		int qy = y;
		switch(verticalAlign) {
		case TOP:
			qy = y - 5;
			break;
		case MIDDLE:
			qy = y + (height - sprite.getHeight()) / 2;
			break;
		case BOTTOM:
			qy = y + height + 5 - sprite.getHeight();
			break;
		}
		return qy;
	}

	private static int alignHorizontal(Sprite sprite, HorizontalAlignment a,
			int x, int width) {
		int qx = x;

		switch (a) {
		case RIGHT:
			qx = x + width + 5 - sprite.getWidth();
			break;
		case CENTER:
			qx = x + (width - sprite.getWidth()) / 2;
			break;
		case LEFT:
			qx = x + 5;
			break;
		}
		return qx;
	}

}
