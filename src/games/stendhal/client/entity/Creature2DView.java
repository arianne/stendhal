/*
 * @(#) games/stendhal/client/entity/Creature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.common.Debug;

/**
 * The 2D view of a creature.
 */
public class Creature2DView extends RPEntity2DView {
	/**
	 * The entity this view is for.
	 */
	private Creature	creature;

	/*
	 * The drawn height.
	 */
	protected double	height;

	/*
	 * The drawn width.
	 */
	protected double	width;


	/**
	 * Create a 2D view of a creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public Creature2DView(final Creature creature) {
		super(creature);

		this.creature = creature;

		updateSize();
	}


	//
	// Creature2DView
	//

	protected void drawPath(final GameScreen screen, final List<Creature.Node> path, final int delta) {
		Graphics g2d = screen.expose();
		Point p1 = screen.convertWorldToScreen(getX(), getY());

		for (Creature.Node node : path) {
			Point p2 = screen.convertWorldToScreen(node.nodeX, node.nodeY);

			g2d.drawLine(p1.x + delta, p1.y + delta, p2.x + delta, p2.y + delta);
			p1 = p2;
		}
	}


	/**
	 * Get the height.
	 *
	 * @return	The height in tile units.
	 */
	public double getHeight() {
		return height;
	}


	/**
	 * Get the width.
	 *
	 * @return	The width in tile units.
	 */
	public double getWidth() {
		return width;
	}


	/**
	 * Set the appropriete drawn size based on the creature.
	 * <strong>NOTE: This is called from the constructor.</strong>
	 */
	protected void updateSize() {
		width = entity.getWidth();
		height = entity.getHeight();

		// Hack for human like creatures
		if ((Math.abs(width - 1.0) < 0.1) && (Math.abs(height - 2.0) < 0.1)) {
			width = 1.5;
			height = 2.0;
		}
	}


	//
	// RPEntity2DView
	//

	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		String resource = creature.getMetamorphosis();

		if(resource == null) {
			resource = getClassResourcePath();
		}

		return SpriteStore.get().getSprite(translate(resource));
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<Object, Sprite> map) {
		buildSprites(map, getWidth(), getHeight());
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 * This builds all the animation sprites and sets the default frame.
	 */
	@Override
	protected void buildRepresentation() {
		updateSize();
		super.buildRepresentation();
	}


	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	protected void draw(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		List<Creature.Node>	path;


		super.draw(screen, g2d, x, y, width, height);

		if (Debug.CREATURES_DEBUG_CLIENT && !creature.isPathHidden()) {
			if ((path = creature.getTargetMovedPath()) != null) {
				int delta = GameScreen.SIZE_UNIT_PIXELS / 2;
				g2d.setColor(Color.red);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2);
			}

			if ((path = creature.getPatrolPath()) != null) {
				g2d.setColor(Color.green);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2 + 1);
			}

			if ((path = creature.getMoveToTargetPath()) != null) {
				g2d.setColor(Color.blue);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2 + 2);
			}
		}
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
	}


	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param	name		The resource name.
	 *
	 * @return	The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/monsters/" + name + ".png";
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(Entity entity, Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if(property == Creature.PROP_METAMORPHOSIS) {
			representationChanged = true;
		}
	}
}
