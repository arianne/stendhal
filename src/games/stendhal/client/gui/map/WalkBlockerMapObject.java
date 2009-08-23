package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

public class WalkBlockerMapObject extends StaticMapObject {
	/**
	 * The colour of walk blockers (dark pink) .
	 */
    private static final Color COLOR = new Color(209, 144, 224);
    
	public WalkBlockerMapObject(final IEntity entity) {
		super(entity);
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, COLOR, null);
	}
}
