package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;

public class RPEntityMapObject extends MovingMapObject {
	private static final Color COLOR_DOMESTIC_ANIMAL = Color.ORANGE;
	private static final Color COLOR_CREATURE = Color.YELLOW;
	private static final Color COLOR_NPC = Color.BLUE;
	
	protected Color drawColor;
	
	public RPEntityMapObject(final IEntity entity) {
		super(entity);
		if (entity instanceof NPC) {
			drawColor = COLOR_NPC;
		} else if (entity instanceof DomesticAnimal) {
			drawColor = COLOR_DOMESTIC_ANIMAL;
		} else {
			drawColor = COLOR_CREATURE;
		}
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, drawColor);
	}
	
	/**
	 * Draws a cross at the given position.
	 * 
	 * @param g The graphics context
	 * @param x x coordinate of the center
	 * @param y y coordinate of the center
	 * @param color the draw color
	 */
	protected void draw(final Graphics g, final int scale,  final Color color) {
		int mapX = worldToCanvas(x, scale);
		int mapY = worldToCanvas(y, scale);
		final int scale_2 = scale / 2;

		final int size = scale_2 + 2;

		mapX += scale_2;
		mapY += scale_2;

		g.setColor(color);
		g.drawLine(mapX - size, mapY, mapX + size, mapY);
		g.drawLine(mapX, mapY + size, mapX, mapY - size);
	}
}
