/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.StatusID;
import games.stendhal.client.entity.TextIndicator;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.entity.helpers.AttackPainter;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.AnimatedSprite;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.sprite.TextSprite;
import games.stendhal.common.Debug;
import games.stendhal.common.Direction;
import games.stendhal.common.constants.Nature;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * The 2D view of an RP entity.
 *
 * @param <T> type of RPEntity
 */
abstract class RPEntity2DView<T extends RPEntity> extends ActiveEntity2DView<T> {

	private static final int ICON_OFFSET = 8;
	private static final int HEALTH_BAR_HEIGHT = 4;

	// Battle icons
	private static final Sprite blockedSprite;
	private static final Sprite hitSprite;
	private static final Sprite missedSprite;

	// Job icons
	private static final Sprite healerSprite;
	private static final Sprite merchantSprite;

	// Status icons
	private static final Sprite chokingSprite;
	private static final Sprite confusedSprite;
	private static final Sprite eatingSprite;
	private static final Sprite poisonedSprite;
	private static final Sprite shockedSprite;
	private static final Sprite heavySprite;

	/** Colors of the ring/circle around the player while attacking or being attacked. */
	private static final Color RING_COLOR_RED = new Color(230, 10, 10);
	private static final Color RING_COLOR_DARK_RED = new Color(74, 0, 0);
	private static final Color RING_COLOR_ORANGE = new Color(255, 200, 0);

	private static final double SQRT2 = 1.414213562;

	/** Temporary text sprites, like HP and XP changes. */
	private Map<TextIndicator, Sprite> floaters = new HashMap<TextIndicator, Sprite>();

	/**
	 * Model attributes effecting the title changed.
	 */
	private boolean titleChanged;
	/** <code>true</code> if the view should show the entity title. */
	private boolean showTitle;
	/** <code>true</code> if the view should show the HP bar. */
	private boolean showHP;

	/**
	 * The title image sprite.
	 */
	private Sprite titleSprite;

	/** The drawn height. */
	protected int height;

	/** The drawn width. */
	protected int width;
	/** Status icon managers. */
	private final List<AbstractStatusIconManager> iconManagers = new ArrayList<AbstractStatusIconManager>();
	private HealthBar healthBar;
	private int statusBarYOffset;

	/**
	 * Flag for detecting if any of the icon manager managed icons have
	 * changed.
	 */
	private volatile boolean iconsChanged;
	/**
	 * Flag for checking if the entity is attacking. Can be modified by both
	 * the EDT and the game loop.
	 */
	private volatile boolean isAttacking;
	/** <code>true</code> if the current attack is ranged. */
	private boolean rangedAttack;

	/** Object for drawing the attack. */
	private AttackPainter attackPainter;

	static {
		final SpriteStore st = SpriteStore.get();

		// Battle icons
		hitSprite = st.getCombatSprite("hitted.png");
		blockedSprite = st.getCombatSprite("blocked.png");
		missedSprite = st.getCombatSprite("missed.png");

		// Job icons
		healerSprite = st.getStatusSprite("healer.png");
		merchantSprite = st.getStatusSprite("merchant.png");

		// Status icons
		confusedSprite = st.getAnimatedSprite(st.getStatusSprite("confuse.png"), 200);
		eatingSprite = st.getSprite("data/sprites/ideas/eat.png");
		poisonedSprite = st.getAnimatedSprite(st.getStatusSprite("poison.png"), 100);
		chokingSprite = st.getSprite("data/sprites/ideas/choking.png");
		shockedSprite = st.getAnimatedSprite(st.getStatusSprite("shock.png"), 38, 200);
		heavySprite = st.getAnimatedSprite(st.getStatusSprite("heavy.png"), 200);
	}

	/**
	 * Create a new RPEntity2DView.
	 */
	public RPEntity2DView() {
		// Job icons
		addIconManager(new StatusIconManager(RPEntity.PROP_HEALER, healerSprite,
				HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, StatusID.HEALER));
		addIconManager(new StatusIconManager(RPEntity.PROP_MERCHANT, merchantSprite,
				HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, StatusID.MERCHANT));

		// Status icons
		/* choking status */
		addIconManager(new AbstractStatusIconManager(RPEntity.PROP_EATING, chokingSprite,
				HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM) {
			@Override
			boolean show(T rpentity) {
				return rpentity.isChoking();
			}
		});

		/* confused status */
		addIconManager(new StatusIconManager(RPEntity.PROP_CONFUSED, confusedSprite,
				HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE, StatusID.CONFUSE));

		/* eating status */
		addIconManager(new AbstractStatusIconManager(RPEntity.PROP_EATING, eatingSprite,
				HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM) {
			@Override
			boolean show(T rpentity) {
				return rpentity.isEating() && !rpentity.isChoking();
			}
		});

		/* poison status */
		StatusIconManager poisonManager = new StatusIconManager(RPEntity.PROP_POISONED, poisonedSprite,
				HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE, StatusID.POISON);
		poisonManager.setOffsets(10, -13);
		addIconManager(poisonManager);

		/* shock status */
		addIconManager(new StatusIconManager(RPEntity.PROP_SHOCK, shockedSprite,
				HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM, StatusID.SHOCK));

		/* heavy status */
		StatusIconManager heavyManager = new StatusIconManager(RPEntity.PROP_HEAVY,
				heavySprite, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE,
				StatusID.HEAVY);
		heavyManager.setOffsets(0, 32);
		addIconManager(heavyManager);

		setSpriteAlignment(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	}

	@Override
	public void initialize(final T entity) {
		super.initialize(entity);
		showTitle = entity.showTitle();
		showHP = entity.showHPBar();
		if (showTitle) {
			titleSprite = createTitleSprite();
		}
		titleChanged = false;
		iconsChanged = true;
	}

	//
	// RPEntity2DView
	//

	/**
	 * Populate keyed state sprites.
	 *
	 * @param map
	 *            The map to populate.
	 * @param tiles
	 *            The master sprite.
	 * @param width
	 *            The tile width (in pixels).
	 * @param height
	 *            The tile height (in pixels).
	 */
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		int y = 0;
		map.put(Direction.UP, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.RIGHT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.DOWN, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.LEFT, createWalkSprite(tiles, y, width, height));
	}

	/**
	 * Create the title sprite.
	 *
	 * @return The title sprite.
	 */
	private Sprite createTitleSprite() {
		final String titleType = entity.getTitleType();
		final int adminlevel = entity.getAdminLevel();
		Color nameColor = null;

		if (titleType != null) {
			if (titleType.equals("npc")) {
				nameColor = new Color(200, 200, 255);
			} else if (titleType.equals("enemy")) {
				nameColor = new Color(255, 200, 200);
			}
		}

		if (nameColor == null) {
			if (adminlevel >= 800) {
				nameColor = new Color(200, 200, 0);
			} else if (adminlevel >= 400) {
				nameColor = Color.yellow;
			} else if (adminlevel > 0) {
				nameColor = new Color(255, 255, 172);
			} else {
				nameColor = Color.white;
			}
		}

		return TextSprite.createTextSprite(entity.getTitle(), nameColor);
	}

	/**
	 * Extract a walking animation for a specific row. The source sprite
	 * contains 3 animation tiles, but this is converted to 4 frames.
	 *
	 * @param tiles
	 *            The tile image.
	 * @param y
	 *            The base Y coordinate.
	 * @param width
	 *            The frame width.
	 * @param height
	 *            The frame height.
	 *
	 * @return A sprite.
	 */
	protected Sprite createWalkSprite(final Sprite tiles, final int y,
			final int width, final int height) {
		final SpriteStore store = SpriteStore.get();

		final Sprite[] frames = new Sprite[4];

		int x = 0;
		frames[0] = store.getTile(tiles, x, y, width, height);

		x += width;
		frames[1] = store.getTile(tiles, x, y, width, height);

		x += width;
		frames[2] = store.getTile(tiles, x, y, width, height);

		frames[3] = frames[1];

		return new AnimatedSprite(frames, 100, false);
	}

	/**
	 * Add a new status icon manager.
	 *
	 * @param manager
	 */
	final void addIconManager(AbstractStatusIconManager manager) {
		iconManagers.add(manager);
	}

	/**
	 * Draw the floating text indicators (floaters).
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	private void drawFloaters(final Graphics2D g2d, final int x, final int y,
			final int width) {
		for (Map.Entry<TextIndicator, Sprite> floater : floaters.entrySet()) {
			final TextIndicator indicator = floater.getKey();
			final Sprite sprite = floater.getValue();
			final int age = indicator.getAge();

			final int tx = x + (width - sprite.getWidth()) / 2;
			final int ty = y - (int) (age * 5L / 300L);
			sprite.draw(g2d, tx, ty);
		}
	}

	/**
	 * Draw the entity HP bar.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	protected void drawHPbar(final Graphics2D g2d, final int x, final int y,
			final int width) {
		int dx = (width - healthBar.getWidth()) / 2;
		healthBar.draw(g2d, x + dx, y - healthBar.getHeight());
	}

	/**
	 * Draw the entity status bar. The status bar show the title and HP bar.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	protected void drawStatusBar(Graphics2D g2d, int x, int y, int width) {
		if (showTitle) {
			drawTitle(g2d, x, y, width);
		}
		if (showHP) {
			drawHPbar(g2d, x, y, width);
		}
	}

	private int getStatusBarHeight() {
		if (titleSprite != null) {
			return 3 + titleSprite.getHeight();
		} else if (healthBar != null) {
			return healthBar.getHeight();
		}
		return 0;
	}

	/**
	 * Draw the entity title.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	protected void drawTitle(final Graphics2D g2d, final int x, final int y, final int width) {
		if (titleSprite != null) {
			int tx = x + ((width - titleSprite.getWidth()) / 2);
			int ty = y - getStatusBarHeight();

			titleSprite.draw(g2d, tx, ty);
		}
	}

	/**
	 * Draw the combat indicators.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	private void drawCombat(final Graphics2D g2d, final int x,
							  final int y, final int width, final int height) {
		Rectangle2D wrect = entity.getArea();
		final Rectangle srect = new Rectangle(
				(int) (wrect.getX() * IGameScreen.SIZE_UNIT_PIXELS),
				(int) (wrect.getY() * IGameScreen.SIZE_UNIT_PIXELS),
				(int) (wrect.getWidth() * IGameScreen.SIZE_UNIT_PIXELS),
				(int) (wrect.getHeight() * IGameScreen.SIZE_UNIT_PIXELS)
		);

		// Calculating the circle's height
		int circleHeight = (int) ((srect.height - 2) / SQRT2);
		circleHeight = Math.max(circleHeight, srect.height - IGameScreen.SIZE_UNIT_PIXELS / 2);

		// When the entity is attacking the user give him a orange ring
		if (entity.isAttacking(User.get())) {
			g2d.setColor(RING_COLOR_ORANGE);
			g2d.drawOval(srect.x - 1, srect.y + srect.height - circleHeight, srect.width, circleHeight);
			g2d.drawOval(srect.x, srect.y + srect.height - circleHeight, srect.width, circleHeight);
			g2d.drawOval(srect.x + 1, srect.y + srect.height - circleHeight, srect.width, circleHeight);
			drawShadedOval(g2d, srect.x + 1, srect.y + srect.height - circleHeight + 1, srect.width - 2, circleHeight - 2, RING_COLOR_ORANGE, true, false);
		}

		// When the entity is attacked by another entity
		if (entity.isBeingAttacked()) {
			Color lineColor;
			g2d.setColor(RING_COLOR_RED);

			// When it is also attacking the user give him only a red outline
			if (entity.isAttacking(User.get())) {
				lineColor = RING_COLOR_RED;
				drawShadedOval(g2d, srect.x - 1, srect.y + srect.height - circleHeight - 1, srect.width + 2, circleHeight + 2, RING_COLOR_RED, false, true);
			} else {
				// Otherwise make his complete ring red
				lineColor = RING_COLOR_DARK_RED;
				g2d.drawOval(srect.x - 1, srect.y + srect.height - circleHeight, srect.width, circleHeight);
				g2d.drawOval(srect.x, srect.y + srect.height - circleHeight, srect.width, circleHeight);
				g2d.drawOval(srect.x + 1, srect.y + srect.height - circleHeight, srect.width, circleHeight);
				drawShadedOval(g2d, srect.x + 1, srect.y + srect.height - circleHeight + 1, srect.width - 2, circleHeight - 2, RING_COLOR_RED, true, false);
				drawShadedOval(g2d, srect.x - 1, srect.y + srect.height - circleHeight - 1, srect.width + 2, circleHeight + 2, RING_COLOR_ORANGE, false, false);
			}

			// Get the direction of his opponents and draw an arrow to those
			EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
			for (Entity attacker : entity.getAttackers()) {
				directions.add(Direction.getAreaDirectionTowardsArea(entity.getArea(), attacker.getArea()));
			}
			drawArrows(g2d, srect.x - 1, srect.y + srect.height - circleHeight - 1, srect.width + 2, circleHeight + 2, directions, lineColor);

		// When the entity is attacked by the user, but still is attacking the user, give him a dark orange outline
		} else if (entity.isAttacking(User.get())) {
			drawShadedOval(g2d, srect.x - 1, srect.y + srect.height - circleHeight - 1, srect.width + 2, circleHeight + 2, RING_COLOR_ORANGE, false, false);
		}

		drawAttack(g2d, x, y, width, height);

		if (entity.isDefending()) {
			// Draw bottom right combat icon
			final int sx = srect.x + srect.width - ICON_OFFSET;
			final int sy = y + height - 2 * ICON_OFFSET;

			switch (entity.getResolution()) {
			case BLOCKED:
				blockedSprite.draw(g2d, sx, sy);
				break;

			case MISSED:
				missedSprite.draw(g2d, sx, sy);
				break;

			case HIT:
				hitSprite.draw(g2d, sx, sy);
				break;
			default:
				// cannot happen we are switching on enum
			}
		}
	}

	/**
	 * Function to draw the arrows on the attack/being attacked ring.
	 *
	 * @param g2d The graphic context
	 * @param x The x-center of the arrows
	 * @param y The y-center of the arrows
	 * @param width ring width
	 * @param height ring height
	 * @param directions The directions an arrow should be drawn
	 * @param lineColor The color of the outline of the arrow
	 */
	private void drawArrows(final Graphics2D g2d, final int x, final int y, final int width, final int height, final EnumSet<Direction> directions, final Color lineColor) {
		int arrowHeight = 6 + 2 * (height / 23 - 1);
		int arrowWidth = 3 + (width / 34 - 1);
		if (directions.contains(Direction.LEFT)) {
			g2d.setColor(Color.RED);
			g2d.fillPolygon(
					new int[] {x+1, x-arrowWidth, x+1},
					new int[]{y+(height/2)-(arrowHeight/2), y+(height/2),y+(height/2)+(arrowHeight/2)},
					3);
			g2d.setColor(lineColor);
			g2d.drawPolyline(
					new int[]{x, x-arrowWidth, x},
					new int[]{y+(height/2)-(arrowHeight/2), y+(height/2), y+(height/2)+(arrowHeight/2)},
					3);
		}
		if (directions.contains(Direction.RIGHT)) {
			g2d.setColor(Color.RED);
			g2d.fillPolygon(
					new int[]{x+width, x+width+arrowWidth, x+width},
					new int[]{y+(height/2)-(arrowHeight/2), y+(height/2), y+(height/2)+(arrowHeight/2)},
					3);
			g2d.setColor(lineColor);
			g2d.drawPolyline(
					new int[]{x+width, x+width+arrowWidth, x+width},
					new int[]{y+(height/2)-(arrowHeight/2), y+(height/2), y+(height/2)+(arrowHeight/2)},
					3);
		}
		if (directions.contains(Direction.UP)) {
			g2d.setColor(Color.RED);
			g2d.fillPolygon(
					new int[]{x+(width/2)-(arrowHeight/2), x+(width/2), x+(width/2)+(arrowHeight/2)},
					new int[]{y+1, y-arrowWidth, y+1},
					3);
			g2d.setColor(lineColor);
			g2d.drawPolyline(
					new int[]{x+(width/2)-(arrowHeight/2), x+(width/2), x+(width/2)+(arrowHeight/2)},
					new int[]{y, y-arrowWidth, y},
					3);
		}
		if (directions.contains(Direction.DOWN)) {
			g2d.setColor(Color.RED);
			g2d.fillPolygon(
					new int[]{x+(width/2)-(arrowHeight/2), x+(width/2), x+(width/2)+(arrowHeight/2)},
					new int[]{y+height, y+height+arrowWidth, y+height},
					3);
			g2d.setColor(lineColor);
			g2d.drawPolyline(
					new int[]{x+(width/2)-(arrowHeight/2), x+(width/2), x+(width/2)+(arrowHeight/2)},
					new int[]{y+height, y+height+arrowWidth, y+height},
					3);
		}
	}

	/**
	 * @param g2d The graphic context
	 * @param x The x-position of the upperleft of the oval
	 * @param y The y-position of the upperleft of the oval
	 * @param width The widht of the oval
	 * @param height The height of the oval
	 * @param color The base color of the oval, shadow still needs to be applied
	 * @param reversed Whether the bottom part, or the upper part should be dark (true is upper part)
	 * @param light
	 */
	private void drawShadedOval(final Graphics2D g2d, final int x, final int y, final int width, final int height, final Color color, final boolean reversed, final boolean light) {

		// Calculate how much darker the ring must be made (depends on the boolean 'light')
		float multi1;
		float multi2;
		if (light) {
			multi1 = reversed ? 1f : 0.8f;
			multi2 = reversed ? 0.8f : 1f;
		} else {
			multi1 = reversed ? 0.24f : 0.39f;
			multi2 = reversed ? 0.39f : 0.24f;
		}

		// Darken the colors by the given multiplier
		Color color1 = new Color((int) (color.getRed() * multi1), (int) (color.getGreen() * multi1), (int) (color.getBlue() * multi1));
		Color color2 = new Color((int) (color.getRed() * multi2), (int) (color.getGreen() * multi2), (int) (color.getBlue() * multi2));

		// Draw with two arcs a oval
		g2d.setColor(color1);
		g2d.drawArc(x, y, width, height, 0, 180);
		g2d.setColor(color2);
		g2d.drawArc(x, y, width, height, 180, 180);
	}

	/**
	 * Draw the attacking effect.
	 *
	 * @param g2d The graphics context
	 * @param x x coordinate of the attacker
	 * @param y y coordinate of the attacker
	 * @param width width of the attacker
	 * @param height height of the attacker
	 */
	private void drawAttack(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
		if (isAttacking) {
			if (!attackPainter.isDoneAttacking()) {
				RPEntity target = entity.getAttackTarget();

				if (target != null) {
					if (rangedAttack) {
						attackPainter.drawDistanceAttack(g2d, entity, target, x, y, width, height);
					} else {
						attackPainter.draw(g2d, entity.getDirection(), x, y, width, height);
					}
				}
			} else {
				isAttacking = false;
			}
		}
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return A tile sprite containing all animation images.
	 */
	protected abstract Sprite getAnimationSprite();

	/**
	 * Draws a shadow under image if shadows are enabled in the client.
	 *
	 * @param sprite
	 * 		The sprite to manipulate.
	 * @return
	 * 		Sprite
	 */
	protected Sprite addShadow(final Sprite sprite) {
		final boolean draw_shadows = WtWindowManager.getInstance().getProperty("gamescreen.shadows", "true").equals("true");

		if (draw_shadows && entity.castsShadow()) {
			/* XXX: would it be better to use a single shadow file & scale it?
			 * XXX: would it be better to use an opaque image & set transparency here?
			 */

			// custom shadows are created with attribute set in .xml config
			final String custom_shadow = entity.getShadowStyle();
			final ImageSprite shadowed;
			final Graphics g;

			// check if custom shadow image exists
			if (custom_shadow != null && DataLoader.getResource(custom_shadow) != null) {
				// draw shadow under the image
				shadowed = new ImageSprite(SpriteStore.get().getSprite(custom_shadow));
				g = shadowed.getGraphics();

				sprite.draw(g, 0, 0);
				return shadowed;
			}

			final int w_sprite = sprite.getWidth() / 3;
			final int h_sprite = sprite.getHeight() / 4;
			final String standard_shadow = "data/sprites/shadow/" + Integer.toString(w_sprite) + "x" + Integer.toString(h_sprite) + ".png";

			// check if corresponding standard shadow image exists
			if (DataLoader.getResource(standard_shadow) != null) {
				// draw a shadow under the image
				shadowed = new ImageSprite(SpriteStore.get().getSprite(standard_shadow));
				g = shadowed.getGraphics();

				sprite.draw(g, 0, 0);
				return shadowed;
			}
		}

		return sprite;
	}

	/**
	 * Get the number of tiles in the X axis of the base sprite.
	 *
	 * @return The number of tiles.
	 */
	protected int getTilesX() {
		return 3;
	}

	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 *
	 * @return The number of tiles.
	 */
	protected int getTilesY() {
		return 4;
	}

	/**
	 * Determine is the user can see this entity while in ghostmode.
	 *
	 * @return <code>true</code> if the client user can see this entity while in
	 *         ghostmode.
	 */
	protected boolean isVisibleGhost() {
		return false;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate keyed state sprites.
	 *
	 * @param entity the entity to build sprites for
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(T entity, final Map<Object, Sprite> map) {
		final Sprite tiles = getAnimationSprite();

		width = tiles.getWidth() / getTilesX();
		height = tiles.getHeight() / getTilesY();

		buildSprites(map, tiles, width, height);
		calculateOffset(entity, width, height);

		/*
		 * Set icons for a newly created entity.
		 */
		checkIcons();

		// Prepare the health bar
		int barWidth = Math.max(width * 2 / 3, IGameScreen.SIZE_UNIT_PIXELS);
		healthBar = new HealthBar(barWidth, HEALTH_BAR_HEIGHT);
		healthBar.setHPRatio(entity.getHpRatio());
	}

	/**
	 * Check if icon states have changed.
	 */
	private void checkIcons() {
		for (AbstractStatusIconManager handler : iconManagers) {
			if (handler.check(entity)) {
				iconsChanged = true;
			}
		}
	}

	/**
	 * Check if icon states have changed.
	 *
	 * @param property the changed property
	 */
	private void checkIcons(Object property) {
		for (AbstractStatusIconManager handler : iconManagers) {
			if (handler.check(property, entity)) {
				iconsChanged = true;
			}
		}
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		final RPObject obj = entity.getRPObject();
		if (!obj.has("no_attack")) {
			/* FIXME: SilentNPC no longer has "Attack" option in menu. Should this
			 *        code be changed?
			 *
			 * Menu is used to provide an alternate action for some entities (like
			 * puppies - and they should not be attackable).
			 *
			 * For now normally attackable entities get a menu only in Capture The
			 * Flag, and then they don't need attack. If that changes, this code
			 * will need to be adjusted.
			 */
			if (!entity.getRPObject().has("menu")) {
				if (entity.isAttackedBy(User.get())) {
					list.add(ActionType.STOP_ATTACK.getRepresentation());
				} else {
					list.add(ActionType.ATTACK.getRepresentation());
				}
			}
		}

		list.add(ActionType.PUSH.getRepresentation());
	}

	/**
	 * Draw the entity.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		drawCombat(g2d, x, y, width, height);
		super.draw(g2d, x, y, width, height);

		if (Debug.SHOW_ENTITY_VIEW_AREA) {
			g2d.setColor(Color.cyan);
			g2d.drawRect(x, y, width, height);
		}
	}

	/**
	 * Draw the top layer parts of an entity. This will be on down after all
	 * other game layers are rendered.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	@Override
	protected void drawTop(final Graphics2D g2d, final int x, final int y,
			final int width, final int height) {
		drawFloaters(g2d, x, y, width);
		drawStatusBar(g2d, x, y + statusBarYOffset, width);
	}

	/**
	 * Get the height.
	 *
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the entity's visibility.
	 *
	 * @return The visibility value (0-100).
	 */
	@Override
	protected int getVisibility() {
		/*
		 * Hide while in ghostmode.
		 */
		if (entity.isGhostMode()) {
			if (isVisibleGhost()) {
				return super.getVisibility() / 2;
			} else {
				return 0;
			}
		} else {
			return super.getVisibility();
		}
	}

	/**
	 * Get the width.
	 *
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 *
	 * Also, players can only interact with the topmost entity.
	 *
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 8000;
	}

	@Override
	public void setVisibleScreenArea(Rectangle area) {
		Rectangle drawingArea = getDrawingArea();
		int drawTop = drawingArea.y - getStatusBarHeight();
		int visibleTop = area.y;
		statusBarYOffset = Math.max(0, visibleTop - drawTop);
	}

	@Override
	protected void update() {
		super.update();

		if (titleChanged) {
			titleChanged = false;
			showTitle = entity.showTitle();
			if (showTitle) {
				titleSprite = createTitleSprite();
			} else {
				titleSprite = null;
			}
		}

		if (iconsChanged) {
			iconsChanged = false;
			for (AbstractStatusIconManager handler : iconManagers) {
				handler.apply();
			}
		}
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == RPEntity.PROP_ADMIN_LEVEL) {
			titleChanged = true;
			visibilityChanged = true;
		} else if (property == RPEntity.PROP_GHOSTMODE) {
			visibilityChanged = true;
		} else if (property == RPEntity.PROP_OUTFIT
				|| property == RPEntity.PROP_ZOMBIE) {
			representationChanged = true;
		} else if (property == IEntity.PROP_TITLE
				|| property == RPEntity.PROP_TITLE_TYPE) {
			titleChanged = true;
		} else if (property == RPEntity.PROP_TEXT_INDICATORS) {
			onFloatersChanged();
		} else if (property == RPEntity.PROP_HP_RATIO) {
			if (healthBar != null) {
				healthBar.setHPRatio(entity.getHpRatio());
			}
		} else if (property == RPEntity.PROP_HP_DISPLAY) {
			showHP = entity.showHPBar();
		} else if (property == RPEntity.PROP_ATTACK) {
			Nature nature = entity.getShownDamageType();
			String weapon = entity.getShownWeapon();
			if (nature == null) {
				isAttacking = false;
			} else {
				rangedAttack = entity.isDoingRangedAttack();
				if (attackPainter == null || !attackPainter.hasNatureAndWeapon(nature, weapon)) {
					attackPainter = AttackPainter.get(nature, weapon, (int) Math.min(entity.getWidth(), entity.getHeight()));
				}
				attackPainter.prepare(getState(entity));
				isAttacking = true;
			}
		}

		checkIcons(property);
	}

	/**
	 * Called when the floating text indicators change.
	 */
	private void onFloatersChanged() {
		Iterator<TextIndicator> it = entity.getTextIndicators();
		Map<TextIndicator, Sprite> newFloaters = new HashMap<TextIndicator, Sprite>();

		while (it.hasNext()) {
			TextIndicator floater = it.next();
			Sprite sprite = floaters.get(floater);
			if (sprite == null) {
				sprite = TextSprite.createTextSprite(floater.getText(), floater.getType().getColor());
			}

			newFloaters.put(floater, sprite);
		}

		floaters = newFloaters;
	}

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		if (entity.getRPObject().has("menu")) {
			onAction(ActionType.USE);
		} else {
			super.onAction();
		}
	}

	/**
	 * Perform an action.
	 *
	 * @param action
	 *            The action.
	 */
	@Override
	public void onAction(ActionType action) {
		ActionType at = action;
		if (at == null) {
			at = ActionType.USE;
		}
		if (isReleased()) {
			return;
		}
		RPAction rpaction;

		switch (at) {
		case ATTACK:
		case PUSH:
		case USE:
			at.send(at.fillTargetInfo(entity));
			break;

		case STOP_ATTACK:
			rpaction = new RPAction();

			rpaction.put("type", at.toString());
			rpaction.put("attack", "");

			at.send(rpaction);
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	/**
	 * An icon manager whose visibility check is just checking a status of the
	 * entity.
	 */
	private class StatusIconManager extends AbstractStatusIconManager {
		/** The followed status. */
		private final StatusID status;

		/**
		 * Create a StatusIconManager.
		 *
 		 * @param property observed property
		 * @param sprite icon sprite
		 * @param xAlign horizontal alignment of the sprite
		 * @param yAlign vertical alignment of the sprite
		 * @param status status corresponding to the visibility of the icon
		 */
		StatusIconManager(Object property, Sprite sprite, HorizontalAlignment xAlign,
				VerticalAlignment yAlign, StatusID status) {
			super(property, sprite, xAlign, yAlign);
			this.status = status;
		}

		@Override
		boolean show(T entity) {
			return entity.hasStatus(status);
		}
	}

	/**
	 * A manager for a status icon. Observes property changes and shows and
	 * hides the icon as needed.
	 */
	abstract class AbstractStatusIconManager {
		/** Observed property. */
		private final Object property;
		/** Icon sprite. */
		private final Sprite sprite;
		/** Horizontal alignment of the sprite. */
		private final HorizontalAlignment xAlign;
		/** Vertical alignment of the sprite. */
		private final VerticalAlignment yAlign;
		private int xOffset, yOffset;
		/**
		 * For tracking changes that can have multiple "true" values (such
		 * as away messages).
		 */
		private boolean wasVisible;
		/**
		 * Property for telling if the icon should be visible.
		 */
		private boolean shouldBeVisible;
		/**
		 * Flag for detecting that the visibility status has changed.
		 */
		private volatile boolean changed;

		/**
		 * Create a new StatusIconManager.
		 *
		 * @param property observed property
		 * @param sprite icon sprite
		 * @param xAlign Horizontal alignment of the sprite related to the
		 * 	entity view
		 * @param yAlign Vertical alignment of the sprite related to the
		 * 	entity view
		 */
		AbstractStatusIconManager(Object property, Sprite sprite,
				HorizontalAlignment xAlign, VerticalAlignment yAlign) {
			this.property = property;
			this.sprite = sprite;
			this.xAlign = xAlign;
			this.yAlign = yAlign;
		}

		/**
		 * Check if the icon should be shown.
		 *
		 * @param entity checked entity
		 * @return <code>true</code> if the icon should be shown,
		 * 	<code>false</code> otherwise
		 */
		abstract boolean show(T entity);

		/**
		 * Check the entity at a property change. Show or hide the icon if
		 * needed.
		 *
		 * @param changedProperty property that changed
		 * @param entity changed entity
		 * @return <code>true</code> if the visibility status changed, otherwise
		 * 	<code>false</code>
		 */
		boolean check(Object changedProperty, T entity) {
			if (property == changedProperty) {
				return check(entity);
			}

			return false;
		}

		/**
		 * Apply visibility changes if needed.
		 */
		void apply() {
			if (changed) {
				changed = false;
				setVisible(shouldBeVisible);
			}
		}

		/**
		 * Set the icon offsets compared to the normal position determined by
		 * the alignment. <b>The horizontal offset will be ignored, unless
		 * the icon is center aligned.</b>
		 * @param xOffset
		 * @param yOffset
		 */
		void setOffsets(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		/**
		 * Check the status of an entity, and show or hide the icon if
		 * needed.
		 *
		 * @param entity checked entity
		 * @return <code>true</code> if the visibility status changed, otherwise
		 * 	<code>false</code>
		 */
		private boolean check(T entity) {
			boolean old = shouldBeVisible;
			shouldBeVisible = show(entity);
			boolean tmp = old != shouldBeVisible;
			if (tmp) {
				changed = true;
			}

			return tmp;
		}

		/**
		 * Find the correct location for the icon when it has been set visible.
		 */
		private void position() {
			if (xAlign == HorizontalAlignment.CENTER) {
				return;
			}
			xOffset = 0;
			for (int i = 0; i < iconManagers.size(); i++) {
				AbstractStatusIconManager manager = iconManagers.get(i);
				if (manager != this) {
					if (sharesPosition(manager)) {
						if (xAlign == HorizontalAlignment.LEFT) {
							xOffset += manager.sprite.getWidth();
						} else {
							xOffset -= manager.sprite.getWidth();
						}
					}
				} else {
					// Reposition any icons in the same position after this
					reposition(i + 1);
					break;
				}
			}
		}

		/**
		 * Reposition any visible icons following this when the visibility has
		 * changed.
		 *
		 * @param startIndex the position of this manager in the iconManagers
		 * 	list
		 */
		private void reposition(int startIndex) {
			for (int j = startIndex; j < iconManagers.size(); j++) {
				AbstractStatusIconManager follower = iconManagers.get(j);
				if (sharesPosition(follower)) {
					follower.setVisible(false);
					follower.position();
					follower.setVisible(true);
					// position() above will trigger repositioning of
					// any icons after follower, so avoid processing any
					// further.
					break;
				}
			}
		}

		/**
		 * Check if a manager shares position with this, and is in visible
		 * state.
		 *
		 * @param manager manager to be checked
		 * @return <code>true</code> if the manages shares alignment properties
		 * with this, and its icon is visible.
		 */
		private boolean sharesPosition(AbstractStatusIconManager manager) {
			return manager.xAlign == xAlign && manager.yAlign == yAlign
					&& manager.shouldBeVisible;
		}

		/**
		 * Attach or detach the icon sprite, depending on visibility.
		 *
		 * @param visible new visibility status
		 */
		private void setVisible(boolean visible) {
			if (visible) {
				// Avoid attaching the sprite more than once
				if (!wasVisible) {
					position();
					attachSprite(sprite, xAlign, yAlign, xOffset, yOffset);
					wasVisible = true;
				}
			} else {
				wasVisible = false;
				detachSprite(sprite);
				reposition(iconManagers.indexOf(this) + 1);
			}
		}
	}
}
