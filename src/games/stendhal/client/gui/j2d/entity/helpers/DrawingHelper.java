package games.stendhal.client.gui.j2d.entity.helpers;

import games.stendhal.client.sprite.Sprite;

import java.awt.Graphics2D;
/**
 * Helper class for drawing sprites with a certain alignment in a certain area
 * 
 * @author madmetzger
 */
public class DrawingHelper {

	public static void drawAlignedSprite(Graphics2D g2d, Sprite sprite, HorizontalAlignment a, int x, int y, int width) {
		/*
		 * NOTE: This has be calibrated to fit the size of an entity slot
		 * visual.
		 */
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
		sprite.draw(g2d, qx, y - 5);
	}

}
