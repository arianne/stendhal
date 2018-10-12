/***************************************************************************
 *                 (C) Copyright 2003-2015 - Faiumoni e.V                  *
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
import java.util.Objects;

import org.apache.log4j.Logger;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.MemoryCache;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.TransparencyMode;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;
import games.stendhal.common.constants.Nature;

/**
 * An utility for drawing the attack sprites.
 */
public final class AttackPainter {
	/* Logger instance */
	private static final Logger logger = Logger.getLogger(AttackPainter.class);

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
	private static final MemoryCache<NatureRef, Map<Direction, Sprite[]>> SWEEP_CACHE = new MemoryCache<>();
	/** Cache for the weapon sprites. */
	private static final MemoryCache<WeaponRef, Map<Direction, Sprite[]>> WEAPON_CACHE = new MemoryCache<>();
	/** Colors used for drawing distance attacks. */
	private static final Map<Nature, Color> ARROW_COLOR;

	/** Stroke used for drawing distance attacks. */
	private static final Stroke ARROW_STROKE = new BasicStroke(2);
	static {
		ARROW_COLOR = new EnumMap<>(Nature.class);
		ARROW_COLOR.put(Nature.CUT, Color.LIGHT_GRAY);
		ARROW_COLOR.put(Nature.DARK, Color.DARK_GRAY);
		ARROW_COLOR.put(Nature.LIGHT, new Color(255, 240, 140)); // light yellow
		ARROW_COLOR.put(Nature.FIRE, new Color(255, 100, 0)); // reddish orange
		ARROW_COLOR.put(Nature.ICE, new Color(140, 140, 255)); // light blue
	}

	/** Sprite sets for the painter. */
	private final Map<Direction, Sprite[]> map;

	/**
	 * Sprite sets for the weapon, or <code>null</code> if no weapon sprite is
	 * used.
	 */
	private final Map<Direction, Sprite[]> weaponMap;
	/**
	 * Ranged sprite sets for the weapon, or <code>null</code> if no weapon
	 * sprite is used for ranged attacks.
	 */
	private final Map<Direction, Sprite[]> rangedWeaponMap;

	/** Sprites used for the current attack. */
	private Sprite[] sprites;

	/**
	 * Weapon sprites used for the current attack, or <code>null</code> if no
	 * weapon should be drawn.
	 */
	private Sprite[] weaponSprites;
	/** Weapon sprites used for the current ranged attack. */
	private Sprite[] rangedSprites;

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
	 * @param rangedSprites Weapon sprites used for ranged attacks.
	 */
	private AttackPainter(Nature nature, String weapon,
			Map<Direction, Sprite[]> sprites,
			Map<Direction, Sprite[]> weaponSprites,
			Map<Direction, Sprite[]> rangedSprites) {
		this.nature = nature;
		this.map = sprites;
		this.weaponMap = weaponSprites;
		this.rangedWeaponMap = rangedSprites;
		this.weapon = weapon;
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
	public static AttackPainter get(Nature nature, final String weapon, int size) {
		Map<Direction, Sprite[]> weaponSprites = null;
		Map<Direction, Sprite[]> rangedSprites = null;
		if (weapon != null) {
			final String weapon_nature = weapon + "_" + nature.toString().toLowerCase();
			WeaponRef ref = new WeaponRef(weapon_nature, size);
			if (!"ranged".equals(weapon)) {
				weaponSprites = getSpriteMap(ref, size, WEAPON_CACHE, new SpriteMaker() {
					@Override
					public Sprite getSprite() {
						Sprite weaponSprite = createWeaponImage(weapon_nature);
						if (weaponSprite == null) {
							weaponSprite = createWeaponImage(weapon);
						}
						return weaponSprite;
					}
				});
			} else {
				rangedSprites = getSpriteMap(ref, size, WEAPON_CACHE, new SpriteMaker() {
					@Override
					public Sprite getSprite() {
						SpriteStore st = SpriteStore.get();
						Sprite sprite = st.getCombatSprite(weapon + ".png");
						// Never use the fail safe sprite for attacks
						if (sprite == st.getFailsafe()) {
							return null;
						}
						return sprite;
					}
				});
			}
		}

		Map<Direction, Sprite[]> sprites = getSpriteMap(nature, size);
		return new AttackPainter(nature, weapon, sprites, weaponSprites, rangedSprites);
	}

	/**
	 * Create a weapon sprite mapping of size 1.
	 *
	 * @param weapon weapon name
	 * @return weapon sprite, or <code>null</code> if there's no image for
	 *         the weapon
	 */
	private static Sprite createWeaponImage(String weapon) {
		SpriteStore st = SpriteStore.get();
		// Avoid showing an error since not all weapon_nature sprites may be available
		if (!st.existsSprite("data/sprites/combat/" + weapon + ".png")) {
			logger.debug("Weapon sprite \"" + weapon + ".png\" not found");
			return null;
		}
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

		int partWidth = 96;
		int partHeight = 128;

		// The clippings are to prevent hard to notice translucent weapon trails
		// being drawn outside the intended area. These may be unnoticeable on
		// a test image, but sometimes visible in the game.

		// Top middle
		g.clipRect(partWidth, 0, partWidth, partHeight);
		template.draw(g, 84, 40);
		// top left
		g.setClip(0, 0, partWidth, partHeight);
		g.rotate(-Math.PI / 4);
		template.draw(g, -87, 46);
		g.dispose();
		// top right
		g = image.createGraphics();
		g.clipRect(2 * partWidth, 0, partWidth, partHeight);
		g.rotate(Math.PI / 4);
		template.draw(g, 179, -160);

		// 2. row, left
		g.setClip(null);
		template.draw(g, 105, 52);
		g.dispose();
		// 2. row, middle
		g = image.createGraphics();
		g.clipRect(partWidth, partHeight, partWidth, partHeight);
		g.rotate(Math.PI / 2);
		template.draw(g, 147, -192);
		g.dispose();
		// 2. row, right
		g = image.createGraphics();
		g.clipRect(2 * partWidth, partHeight, partWidth, partHeight);
		g.rotate(Math.PI / 4 * 3);
		template.draw(g, -70, -373);

		// 3. row, left
		g.setClip(null);
		template.draw(g, 144, -308);
		g.dispose();
		// 3. row, middle
		g = image.createGraphics();
		g.clipRect(partWidth, 2 * partHeight, partWidth, partHeight);
		g.rotate(Math.PI);
		template.draw(g, -187, -383);
		g.dispose();
		// 3. row, right
		g = image.createGraphics();
		g.clipRect(2 * partWidth, 2 * partHeight, partWidth, partHeight);
		g.rotate(-Math.PI / 4 * 3);
		template.draw(g, -457, -120);

		// 4.row, left
		g.setClip(null);
		template.draw(g, -427, -343);
		g.dispose();
		// 4. row, middle
		g = image.createGraphics();
		g.clipRect(partWidth, 3 * partHeight, partWidth, partHeight);
		g.rotate(-Math.PI / 2);
		template.draw(g, -507, 96);
		g.dispose();
		// 4. row, right
		g = image.createGraphics();
		g.clipRect(2 * partWidth, 3 * partHeight, partWidth, partHeight);
		g.rotate(-Math.PI / 4);
		template.draw(g, -204, 439);
		g.dispose();

		return new ImageSprite(image);
	}

	/**
	 * Get a mapping for nature sweep images.
	 *
	 * @param nature attack nature
	 * @param size sweep size
	 * @return image mapping
	 */
	private static Map<Direction, Sprite[]> getSpriteMap(final Nature nature, int size) {
		NatureRef ref = new NatureRef(nature, size);
		return getSpriteMap(ref, size, SWEEP_CACHE, new SpriteMaker() {
			@Override
			public Sprite getSprite() {
				SpriteStore st = SpriteStore.get();
				return st.getCombatSprite("blade_strike_"
						+ nature.toString().toLowerCase(Locale.US) + ".png");
			}
		});
	}

	/**
	 * Find or create an attack sprite map.
	 *
	 * @param <T> Type of the sprite map reference
	 * @param ref reference for the sprite map
	 * @param size size of the attack image
	 * @param cache cache used for storing created image maps
	 * @param maker sprite retriever for size 1 sprites.
	 * @return sprite map, or <code>null</code> if the needed base sprites were
	 * not found
	 */
	private static <T> Map<Direction, Sprite[]> getSpriteMap(T ref, int size,
			MemoryCache<T, Map<Direction, Sprite[]>> cache, SpriteMaker maker) {
		Map<Direction, Sprite[]> map = cache.get(ref);
		if (map == null) {
			if (size == 1) {
				Sprite template = maker.getSprite();
				if (template == null) {
					return null;
				}
				SpriteStore st = SpriteStore.get();
				map = splitTiles(st, template);
				cache.put(ref, map);
			} else {
				Map<Direction, Sprite[]> normalSized = getSpriteMap(ref, 1, cache, maker);
				if (normalSized != null) {
					map = scale(normalSized, size);
					cache.put(ref, map);
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
		Map<Direction, Sprite[]> map = new EnumMap<>(Direction.class);
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
		Map<Direction, Sprite[]> map = new EnumMap<>(Direction.class);
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
		drawAttackSprite(g2d, sprites, direction, x, y, width, height);
		drawAttackSprite(g2d, weaponSprites, direction, x, y, width, height);

		frame++;
	}

	/**
	 * Draw an attack sprite centered on the entity.
	 *
	 * @param g graphics
	 * @param spriteSet attack sprite set
	 * @param direction attack direction
	 * @param x x coordinate of the entity
	 * @param y y coordinate of the entity
	 * @param width entity width
	 * @param height entity height
	 */
	private void drawAttackSprite(Graphics2D g, Sprite[] spriteSet, Direction direction,
			int x, int y, int width, int height) {
		// Weapon sprite sets can be null
		if (spriteSet == null) {
			return;
		}
		Sprite sprite = spriteSet[frame];
		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();
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

		sprite.draw(g, sx, sy);
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
	public void drawDistanceAttack(Graphics2D g2d, RPEntity entity,
			IEntity target, int x, int y, int width, int height) {
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

		g2d.setColor(ARROW_COLOR.get(nature));
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(ARROW_STROKE);
		g2d.drawLine(startX, startY, endX, endY);
		g2d.setStroke(oldStroke);

		drawAttackSprite(g2d, rangedSprites, entity.getDirection(), x, y, width, height);

		frame++;
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
		return (this.nature == nature) && Objects.equals(this.weapon, weapon);
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
		} else {
			weaponSprites = null;
		}
		if (rangedWeaponMap != null) {
			rangedSprites = rangedWeaponMap.get(direction);
		} else {
			rangedSprites = null;
		}

		frame = 0;
	}

	/**
	 * Interface for attack image retrievers.
	 */
	private interface SpriteMaker {
		/**
		 * Get an attack sprite of size 1.
		 *
		 * @return attack sprite, or <code>null</code> if the sprite could not
		 * be found
		 */
		Sprite getSprite();
	}

	/**
	 * A reference object for caching sweep sprite mappings.
	 */
	private static final class NatureRef {
		/** Nature of the sprite mapping. */
		private final Nature nature;
		/** Size of the sprite mapping. */
		private final int size;

		/**
		 * Create a new painter reference.
		 *
		 * @param nature attack nature
		 * @param size creature size
		 */
		private NatureRef(Nature nature, int size) {
			this.nature = nature;
			this.size = size;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof NatureRef) {
				NatureRef obj = (NatureRef) o;
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
						&& Objects.equals(weapon, obj.weapon);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return weapon.hashCode() * 37 + size;
		}
	}
}
