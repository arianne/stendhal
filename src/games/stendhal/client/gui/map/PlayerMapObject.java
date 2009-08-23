package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;

public class PlayerMapObject extends RPEntityMapObject {
	/**
	 * The color of the player (blue).
	 */
	private static final Color COLOR_USER = Color.BLUE;
	/**
	 * The color of other players (white).
	 */
	private static final Color COLOR_PLAYER = Color.WHITE;
	/**
	 * The color of ghostmode players, if visible (white).
	 */
	private static final Color COLOR_GHOST = Color.GRAY;
	
	public PlayerMapObject(final IEntity entity) {
		super(entity);
		
		if (entity instanceof User) {
			drawColor = COLOR_USER;
		} else if (entity instanceof Player) {
			chooseVisibility(entity);
			
			// Follow the ghost mode changes of other players
			entity.addChangeListener(new EntityChangeListener() {
				public void entityChanged(final IEntity entity, final Object property) {
					if (property == RPEntity.PROP_GHOSTMODE) {
						chooseVisibility(entity);
					}
				}
			});
		}
	}
	
	@Override
	public void draw(final Graphics g, final int scale) {
		if ((drawColor != COLOR_GHOST) || User.isAdmin()) {
			super.draw(g, scale);
		}
	}
	
	private void chooseVisibility(final IEntity player) {
		if (!((Player) player).isGhostMode()) {
			drawColor = COLOR_PLAYER;
		} else {
			drawColor = COLOR_GHOST;
		}
	}
	
	/**
	 * Draws a player at the given position.
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
		g.drawLine(mapX, mapY - size, mapX, mapY + size);
	}
}
