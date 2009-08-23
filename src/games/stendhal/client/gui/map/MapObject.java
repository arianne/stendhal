package games.stendhal.client.gui.map;

import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

public abstract class MapObject {
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	public MapObject(final IEntity entity) {
		x = entity.getX();
		y = entity.getY();
		width = (int) entity.getWidth();
		height = (int) entity.getHeight();
	}

	/**
	 * Draw the entity
	 * 
	 * @param g Graphics context
	 * @param scale Scaling factor
	 */
	public abstract void draw(Graphics g, int scale);
	
	/**
	 * Scale a world coordinate to canvas coordinates
	 * 
	 * @param crd World coordinate
	 * @param scale Scaling factor
	 * @return corresponding canvas coordinate
	 */
	protected int worldToCanvas(final double crd, final int scale) {
		return (int) (crd * scale);
	}
}
