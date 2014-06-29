/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity.helpers;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.MemoryCache;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.TransparencyMode;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;
import games.stendhal.common.constants.Nature;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * An utility for drawing the attack sprites.
 */
public final class AttackPainter {
	/** Number of frames in attack sprites. */
	private static final int NUM_ATTACK_FRAMES = 3;
	/** Tile size for convenience. */
	private static final int TILE_SIZE = IGameScreen.SIZE_UNIT_PIXELS;
	/** Half tile size for convenience. */
	private static final int HALF_TILE = TILE_SIZE / 2;
	/**
	 * Information for positioning. Corresponds to a constant of the same name
	 * in RPEntity2DView.
	 */
	private static final int ICON_OFFSET = 8;
	
	/** Cache for previously constructed sprite sets. */
	private static final MemoryCache<AttackPainterRef, Map<Direction, Sprite[]>> cache = new MemoryCache<AttackPainterRef, Map<Direction, Sprite[]>>();
	
	/** Colors used for drawing distance attacks. */
	private static final Map<Nature, Color> arrowColor;
	/** Stroke used for drawing distance attacks. */
	private static final Stroke ARROW_STROKE = new BasicStroke(2);
	
	static {
		arrowColor = new EnumMap<Nature, Color>(Nature.class);
		arrowColor.put(Nature.CUT, Color.LIGHT_GRAY);
		arrowColor.put(Nature.DARK, Color.DARK_GRAY);
		arrowColor.put(Nature.LIGHT, new Color(255, 240, 140)); // light yellow
		arrowColor.put(Nature.FIRE, new Color(255, 100, 0)); // reddish orange
		arrowColor.put(Nature.ICE, new Color(140, 140, 255)); // light blue
	}
	
	/** Sprite sets for the painter. */ 
	private final Map<Direction, Sprite[]> map;
	/** Sprites used for the current attack. */
	private Sprite[] sprites;
	/** Frame counter for the attack. */
	private int frame;
	/** Nature of the attack. */
	private final Nature nature;
	
	/**
	 * Get a painter for attack of a given nature, and size of a creature.
	 * 
	 * @param nature attack nature
	 * @param size creature size
	 * 
	 * @return painter
	 */
	public static AttackPainter get(Nature nature, int size) {
		AttackPainterRef ref = new AttackPainterRef(nature, size);
		Map<Direction, Sprite[]> sprites = cache.get(ref);
		AttackPainter rval;
		if (sprites == null) {
			if (size == 1) {
				rval = new AttackPainter(nature);
				cache.put(ref, rval.map);
			} else {
				rval = get(nature, 1);
				rval = rval.scale(size);
				cache.put(ref, rval.map);
			}
		} else {
			rval = new AttackPainter(nature, sprites);
		}
				
		return rval;
	}
	
	/**
	 * Create a painter using a specified sprite map.
	 * 
	 * @param nature attack nature
	 * @param sprites sprite map
	 */
	private AttackPainter(Nature nature, Map<Direction, Sprite[]> sprites) {
		this.nature = nature;
		// A full clone. Use the same mapping.
		this.map = sprites;
	}
	
	/**
	 * Create a copy of a painter.
	 * 
	 * @param original painter to be copied
	 */
	private AttackPainter(AttackPainter original) {
		this.nature = original.nature;
		// This is used by scale(), which needs to modify the map
		map = new EnumMap<Direction, Sprite[]>(original.map);
	}	
	
	/**
	 * Create a painter of a given nature, and size 1.
	 * 
	 * @param nature nature of the painter
	 */
	private AttackPainter(Nature nature) {
		this.nature = nature;
		this.map = new EnumMap<Direction, Sprite[]>(Direction.class);
		
		int twidth = NUM_ATTACK_FRAMES * TILE_SIZE;
		int theight = 4 * TILE_SIZE;
		SpriteStore st = SpriteStore.get();
		final Sprite tiles = st.getSprite("data/sprites/combat/blade_strike_" 
				+ nature.toString().toLowerCase(Locale.US) + ".png");
		
		int y = 0;
		map.put(Direction.UP, st.getTiles(tiles, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.RIGHT, st.getTiles(tiles, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.DOWN, st.getTiles(tiles, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.LEFT, st.getTiles(tiles, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
	}
	
	/**
	 * Create a painter of a new size, but with the same nature as this painter.
	 * 
	 * @param size new size
	 * @return scaled painter instance
	 */
	private AttackPainter scale(int size) {
		AttackPainter copy = new AttackPainter(this);
		
		for (Direction d : Direction.values()) {
			Sprite[] sprites = copy.map.get(d);
			if (sprites != null) {
				// The sprite arrays are the same as in the original. Avoid
				// overwriting those.
				Sprite[] scaled = new Sprite[sprites.length];
				for (int i = 0; i < NUM_ATTACK_FRAMES; i++) {
					Sprite orig = sprites[i];
					
					final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
					int newWidth = orig.getWidth() + (size - 1) * TILE_SIZE;
					double scaling = newWidth / (double) orig.getWidth();

					BufferedImage image = gc.createCompatibleImage(newWidth,
							(int) (orig.getHeight() * scaling), TransparencyMode.TRANSPARENCY);
					Graphics2D g = image.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.scale(scaling, scaling);
					orig.draw(g, 0, 0);
					
					g.dispose();
					scaled[i] = new ImageSprite(image);
				}
				copy.map.put(d, scaled);
			}
		}
		
		return copy;
	}
	
	/**
	 * Check if this painter has the given nature.
	 * 
	 * @param nature nature to compare to
	 * @return <code>true</code> if the painter has the given nature, otherwise
	 * 	<code>false</code>
	 */
	public boolean hasNature(Nature nature) {
		return this.nature == nature;
	}
	
	/**
	 * Check if the current attack has been completely drawn.
	 * 
	 * @return <code>true</code> if drawing the attack has been completed,
	 * 	otherwise <code>false</code> 
	 */
	public boolean isDoneAttacking() {
		if (frame < NUM_ATTACK_FRAMES) {
			return false;
		}
		frame = 0;
		return true;
	}
	
	/**
	 * Advance a frame.
	 */
	private void skipFrame() {
		frame++;
	}
	
	/**
	 * Prepare for an attack to a given direction.
	 * 
	 * @param direction attack direction
	 */
	public void prepare(Direction direction) {
		sprites = map.get(direction);
		frame = 0;
	}
	
	/**
	 * Draw a melee attack.
	 * 
	 * @param g2d graphics
	 * @param direction attack direction
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param width entity width
	 * @param height entity height
	 */
	public void draw(Graphics2D g2d, Direction direction, int x, int y, int width, int height) {
		final Sprite sprite = sprites[frame];

		final int spriteWidth = sprite.getWidth();
		final int spriteHeight = sprite.getHeight();

		int sx;
		int sy;

		/*
		 * Align swipe image to be 16 px past the facing edge, centering
		 * in other axis.
		 * 
		 * Swipe image is 3x4 tiles, but really only uses partial areas.
		 * Adjust positions to match (or fix images to be
		 * uniform/centered).
		 */
		switch (direction) {
		case UP:
			sx = x + ((width - spriteWidth) / 2) + HALF_TILE;
			sy = y - HALF_TILE - TILE_SIZE;
			break;

		case DOWN:
			sx = x + ((width - spriteWidth) / 2);
			sy = y + height - spriteHeight + HALF_TILE;
			break;

		case LEFT:
			sx = x - HALF_TILE;
			sy = y + ((height - spriteHeight) / 2) - HALF_TILE;
			break;

		case RIGHT:
			sx = x + width - spriteWidth + HALF_TILE;
			sy = y + ((height - spriteHeight) / 2) - ICON_OFFSET;
			break;

		default:
			sx = x + ((width - spriteWidth) / 2);
			sy = y + ((height - spriteHeight) / 2);
		}

		sprite.draw(g2d, sx, sy);

		skipFrame();
	}
	
	/**
	 * Draw a distance attack line.
	 * 
	 * @param g2d graphics
	 * @param entity attacking entity
	 * @param target attack target
	 * @param x attacker x coordinate
	 * @param y attacker y coordinate
	 * @param width attacker width
	 * @param height attacker height
	 */
	public void drawDistanceAttack(final Graphics2D g2d, final RPEntity entity, final RPEntity target,
			final int x, final int y, final int width, final int height) {
		Nature nature = entity.getShownDamageType(); 

		int startX = x + width / 2;
		int startY = y + height / 2;
		int endX = (int) (TILE_SIZE * (target.getX() + target.getWidth() / 2));
		// Target at the upper edge of the occupied area.
		// Getting the EntityView from an entity is tedious, and
		// still does not work reliable for everything (rats)
		int endY = (int) (TILE_SIZE * target.getY()); 

		int yLength = (endY - startY) / NUM_ATTACK_FRAMES;
		int xLength = (endX - startX) / NUM_ATTACK_FRAMES;

		startY += frame * yLength;
		endY = startY + yLength;

		startX += frame * xLength;
		endX = startX + xLength;

		g2d.setColor(arrowColor.get(nature));
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(ARROW_STROKE);
		g2d.drawLine(startX, startY, endX, endY);
		g2d.setStroke(oldStroke);
		
		skipFrame();
	}
	
	/**
	 * A reference object for caching sprite sets.
	 */
	private static final class AttackPainterRef {
		private final Nature nature;
		private final int size;
		
		/**
		 * Create a new painter reference.
		 * 
		 * @param nature attack nature
		 * @param size creaure size
		 */
		private AttackPainterRef(Nature nature, int size) {
			this.nature = nature;
			this.size = size;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof AttackPainterRef) {
				AttackPainterRef obj = (AttackPainterRef) o;
				return (size == obj.size) && (nature == obj.nature);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return nature.hashCode() ^ size;
		}
	}
}
