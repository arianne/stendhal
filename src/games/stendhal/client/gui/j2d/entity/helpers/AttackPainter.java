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
	/**
	 * A reference object for caching sweep sprite mappings.
	 */
	private static final class AttackPainterRef {
		/** Nature of the sprite mapping. */
		private final Nature nature;
		/** Size of the sprite mapping. */
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
			return nature.hashCode() * 37 + size;
		}
	}

	/**
	 * Reference object for weapon sprite mappings.
	 */
	private static final class WeaponRef {
		/** Weapon name. */
		private final String weapon;
		/** Size of the sprite mapping. */
		private final int size;

		/**
		 * Create a new WeaponRef.
		 * 
		 * @param weapon weapon name
		 * @param size size of the sprite mapping
		 */
		WeaponRef(String weapon, int size) {
			this.weapon = weapon;
			this.size = size;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof WeaponRef) {
				WeaponRef obj = (WeaponRef) o;
				return (size == obj.size)
						&& ((weapon == null && obj.weapon == null) || (weapon != null && weapon
								.equals(obj.weapon)));
			}
			return false;
		}

		@Override
		public int hashCode() {
			return weapon.hashCode() * 37 + size;
		}
	}

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
	/** Cache for the weapon sprites. */
	private static final MemoryCache<WeaponRef, Map<Direction, Sprite[]>> weaponCache = new MemoryCache<WeaponRef, Map<Direction, Sprite[]>>();
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

	private static Map<Direction, Sprite[]> createSweepImage(Nature nature) {
		SpriteStore st = SpriteStore.get();
		Sprite tiles = st.getCombatSprite("blade_strike_"
				+ nature.toString().toLowerCase(Locale.US) + ".png");
		return splitTiles(st, tiles);
	}

	/**
	 * Create a weapon sprite mapping of size 1.
	 * 
	 * @param weapon weapon name
	 * @return weapon sprite map, or <code>null</code> if there's no image for
	 *         the weapon
	 */
	private static Map<Direction, Sprite[]> createWeaponImage(String weapon) {
		SpriteStore st = SpriteStore.get();
		Sprite template = st.getCombatSprite(weapon + ".png");
		// Never use the fail safe sprite for attacks
		if (template == st.getFailsafe()) {
			return null;
		}
		final GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		BufferedImage image = gc.createCompatibleImage(288, 512,
				TransparencyMode.TRANSPARENCY);
		Graphics2D g = image.createGraphics();

		// Top middle
		template.draw(g, 84, 40);
		// top left
		g.rotate(-Math.PI / 4);
		template.draw(g, -87, 46);
		g.dispose();
		// top right
		g = image.createGraphics();
		g.rotate(Math.PI / 4);
		template.draw(g, 179, -160);

		// 2. row, left
		template.draw(g, 105, 52);
		g.dispose();
		// 2. row, middle
		g = image.createGraphics();
		g.rotate(Math.PI / 2);
		template.draw(g, 147, -192);
		g.dispose();
		// 2. row, right
		g = image.createGraphics();
		g.rotate(Math.PI / 4 * 3);
		template.draw(g, -70, -373);

		// 3. row, left
		template.draw(g, 144, -308);
		g.dispose();
		// 3. row, middle
		g = image.createGraphics();
		g.rotate(Math.PI);
		template.draw(g, -187, -383);
		g.dispose();
		// 3. row, right
		g = image.createGraphics();
		g.rotate(-Math.PI / 4 * 3);
		template.draw(g, -457, -120);

		// 4.row, left
		template.draw(g, -427, -343);
		g.dispose();
		// 4. row, middle
		g = image.createGraphics();
		g.rotate(-Math.PI / 2);
		template.draw(g, -507, 96);
		g.dispose();
		// 4. row, right
		g = image.createGraphics();
		g.rotate(-Math.PI / 4);
		template.draw(g, -204, 439);
		g.dispose();

		return splitTiles(SpriteStore.get(), new ImageSprite(image));
	}

	/**
	 * Get a painter for attack of a given nature, and size of a creature.
	 * 
	 * @param nature attack nature
	 * @param weapon weapon, or <code>null</code> if not specified
	 * @param size creature size
	 * 
	 * @return painter
	 */
	public static AttackPainter get(Nature nature, String weapon, int size) {
		Map<Direction, Sprite[]> sprites = getSpriteMap(nature, size);
		Map<Direction, Sprite[]> weaponSprites = getWeaponMap(weapon, size);

		return new AttackPainter(nature, weapon, sprites, weaponSprites);
	}

	private static Map<Direction, Sprite[]> getSpriteMap(Nature nature, int size) {
		AttackPainterRef ref = new AttackPainterRef(nature, size);
		Map<Direction, Sprite[]> map = cache.get(ref);
		if (map == null) {
			if (size == 1) {
				map = createSweepImage(nature);
				cache.put(ref, map);
			} else {
				Map<Direction, Sprite[]> normalSized = getSpriteMap(nature, 1);
				if (normalSized != null) {
					map = scale(normalSized, size);
					cache.put(ref, map);
				}
			}
		}
		return map;
	}

	/**
	 * Get a weapon sprite map.
	 * 
	 * @param weapon name of the weapon
	 * @param size size of the attack sprites
	 * @return weapin sprite mapping of the wanted size, or <code>null</code> if
	 *         there is no image for the weapon
	 */
	private static Map<Direction, Sprite[]> getWeaponMap(String weapon, int size) {
		if (weapon == null) {
			return null;
		}
		WeaponRef ref = new WeaponRef(weapon, size);
		Map<Direction, Sprite[]> map = weaponCache.get(ref);
		if (map == null) {
			if (size == 1) {
				map = createWeaponImage(weapon);
				weaponCache.put(ref, map);
			} else {
				Map<Direction, Sprite[]> normalSized = getWeaponMap(weapon, 1);
				if (normalSized != null) {
					map = scale(normalSized, size);
					weaponCache.put(ref, map);
				}
			}
		}
		return map;
	}

	/**
	 * Scale all sprites in a attack sprite map, and create a new mapping with
	 * the scaled sprites.
	 * 
	 * @param origMap original sprite mapping. This should be size 1.
	 * @param size the size of the new map
	 * @return map of scaled sprites
	 */
	private static Map<Direction, Sprite[]> scale(
			Map<Direction, Sprite[]> origMap, int size) {
		Map<Direction, Sprite[]> map = new EnumMap<Direction, Sprite[]>(
				Direction.class);
		for (Direction d : Direction.values()) {
			Sprite[] sprites = origMap.get(d);
			if (sprites != null) {
				// The sprite arrays are the same as in the original. Avoid
				// overwriting those.
				Sprite[] scaled = new Sprite[sprites.length];
				for (int i = 0; i < NUM_ATTACK_FRAMES; i++) {
					Sprite orig = sprites[i];

					final GraphicsConfiguration gc = GraphicsEnvironment
							.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration();
					int newWidth = orig.getWidth() + (size - 1) * TILE_SIZE;
					double scaling = newWidth / (double) orig.getWidth();

					BufferedImage image = gc.createCompatibleImage(newWidth,
							(int) (orig.getHeight() * scaling),
							TransparencyMode.TRANSPARENCY);
					Graphics2D g = image.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.scale(scaling, scaling);
					orig.draw(g, 0, 0);

					g.dispose();
					scaled[i] = new ImageSprite(image);
				}
				map.put(d, scaled);
			}
		}
		return map;
	}

	/**
	 * Split a sprite to a set of attack images.
	 * 
	 * @param st sprite store
	 * @param orig sprite to be split
	 * 
	 * @return a map of attack sprites
	 */
	private static Map<Direction, Sprite[]> splitTiles(SpriteStore st,
			Sprite orig) {
		int twidth = NUM_ATTACK_FRAMES * TILE_SIZE;
		int theight = 4 * TILE_SIZE;
		Map<Direction, Sprite[]> map = new EnumMap<Direction, Sprite[]>(
				Direction.class);
		int y = 0;
		map.put(Direction.UP,
				st.getTiles(orig, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.RIGHT,
				st.getTiles(orig, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.DOWN,
				st.getTiles(orig, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		y += theight;
		map.put(Direction.LEFT,
				st.getTiles(orig, 0, y, NUM_ATTACK_FRAMES, twidth, theight));
		return map;
	}

	/** Sprite sets for the painter. */
	private final Map<Direction, Sprite[]> map;

	/**
	 * Sprite sets for the weapon, or <code>null</code> if no weapon sprite is
	 * used.
	 */
	private final Map<Direction, Sprite[]> weaponMap;

	/** Sprites used for the current attack. */
	private Sprite[] sprites;

	/**
	 * Weapon sprites used for the current attack, or <code>null</code> if no
	 * weapon should be drawn.
	 */
	private Sprite[] weaponSprites;

	/** Frame counter for the attack. */
	private int frame;

	/** Nature of the attack. */
	private final Nature nature;

	/** Weapon used in the attack, or <code>null</code>. */
	private final String weapon;

	/**
	 * Create a painter using a specified sprite map.
	 * 
	 * @param nature attack nature
	 * @param weapon weapon or <code>null</code> if no weapon should be drawn
	 * @param sprites sprite map
	 * @param weaponSprites weapon sprite map, or <code>null</code> if no weapon
	 *        should be drawn.
	 */
	private AttackPainter(Nature nature, String weapon,
			Map<Direction, Sprite[]> sprites,
			Map<Direction, Sprite[]> weaponSprites) {
		this.nature = nature;
		this.map = sprites;
		this.weaponMap = weaponSprites;
		this.weapon = weapon;
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
	public void draw(Graphics2D g2d, Direction direction, int x, int y,
			int width, int height) {
		final Sprite sprite = sprites[frame];

		final int spriteWidth = sprite.getWidth();
		final int spriteHeight = sprite.getHeight();

		int sx;
		int sy;

		/*
		 * Align swipe image to be 16 px past the facing edge, centering in
		 * other axis.
		 * 
		 * Swipe image is 3x4 tiles, but really only uses partial areas. Adjust
		 * positions to match (or fix images to be uniform/centered).
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
		/*
		 * Weapon sprite set can be null, and technically it can *become* null.
		 * That is done in the game loop thread, so grab a local reference first
		 * before checking it.
		 */
		Sprite[] wSet = weaponSprites;
		if (wSet != null) {
			wSet[frame].draw(g2d, sx, sy);
		}

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
	public void drawDistanceAttack(final Graphics2D g2d, final RPEntity entity,
			final RPEntity target, final int x, final int y, final int width,
			final int height) {
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
	 * Check if this painter has the given nature an weapon.
	 * 
	 * @param nature nature to compare to
	 * @param weapon weapon to compare to
	 * @return <code>true</code> if the painter has the given nature, otherwise
	 *         <code>false</code>
	 */
	public boolean hasNatureAndWeapon(Nature nature, String weapon) {
		return this.nature == nature
				&& ((weapon == null && this.weapon == null) || (weapon != null && weapon
						.equals(this.weapon)));
	}

	/**
	 * Check if the current attack has been completely drawn.
	 * 
	 * @return <code>true</code> if drawing the attack has been completed,
	 *         otherwise <code>false</code>
	 */
	public boolean isDoneAttacking() {
		if (frame < NUM_ATTACK_FRAMES) {
			return false;
		}
		frame = 0;
		return true;
	}

	/**
	 * Prepare for an attack to a given direction.
	 * 
	 * @param direction attack direction
	 */
	public void prepare(Direction direction) {
		sprites = map.get(direction);
		if (weaponMap != null) {
			weaponSprites = weaponMap.get(direction);
		}
		frame = 0;
	}

	/**
	 * Advance a frame.
	 */
	private void skipFrame() {
		frame++;
	}
}
