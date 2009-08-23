package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

public class PortalMapObject extends StaticMapObject {
	public PortalMapObject(final IEntity entity) {
		super(entity);
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, Color.WHITE, Color.BLACK);
	}
}
