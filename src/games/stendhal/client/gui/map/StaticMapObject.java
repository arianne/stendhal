package games.stendhal.client.gui.map;

import games.stendhal.client.entity.IEntity;

import java.awt.Color;
import java.awt.Graphics;

public abstract class StaticMapObject extends MapObject {
	public StaticMapObject(final IEntity entity) {
		super(entity);
	}

	/**
	 * Draw the entity
	 * 
	 * @param g Graphics context
	 * @param scale Scaling factor
	 * @param color Drawing Color
	 * @param outline Outline color, or <code>null</code> if no outline
	 */
	protected void draw(final Graphics g, final int scale, final Color color, final Color outline) {
		final int rx = worldToCanvas(x, scale);
		final int ry = worldToCanvas(y, scale);
		final int rwidth = width * scale;
		final int rheight = height * scale;

		g.setColor(color);
		g.fillRect(rx, ry, rwidth, rheight);

		if (outline != null) {
			g.setColor(outline);
			g.drawRect(rx, ry, rwidth - 1, rheight - 1);
		}
	}
}
