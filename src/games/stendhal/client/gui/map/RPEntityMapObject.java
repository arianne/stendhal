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
}
