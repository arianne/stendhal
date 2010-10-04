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


import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The 2D view of a creature.
 */
class Creature2DView extends RPEntity2DView {

	/** the patrolpath. */
	private List<Node> patrolPath;

	/** new path to the target. */
	private List<Node> targetMovedPath;

	/** the path we got. */
	private List<Node> moveToTargetPath;

	//
	// Creature2DView
	//

	public List<Node> decodePath(final String token) {
		final String[] values = token.replace(',', ' ').replace('(', ' ').replace(
				')', ' ').replace('[', ' ').replace(']', ' ').split("\\s+");
		final List<Node> list = new ArrayList<Node>();

		int x = 0;
		int pass = 1;

		for (final String value : values) {
			if (value.length() > 0) {
				final int val = Integer.parseInt(value);
				if (pass % 2 == 0) {
					list.add(new Node(x, val));
				} else {
					x = val;
				}
				pass++;
			}
		}

		return list;
	}

	protected void drawPath(final Graphics2D g2d, final List<Node> path,
			final int delta, final IGameScreen gameScreen) {
		Point p1 = gameScreen.convertWorldToScreenView(getX(), getY());

		for (final Node node : path) {
			final Point p2 = gameScreen.convertWorldToScreenView(node.x, node.y);

			g2d
					.drawLine(p1.x + delta, p1.y + delta, p2.x + delta, p2.y
							+ delta);
			p1 = p2;
		}
	}

	public List<Node> getPatrolPath() {
		return patrolPath;
	}

	public List<Node> getTargetMovedPath() {
		return targetMovedPath;
	}

	public List<Node> getMoveToTargetPath() {
		return moveToTargetPath;
	}

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 * @param tiles
	 *            The master sprite.
	 * @param width
	 *            The image width (in pixels).
	 * @param height
	 *            The image height (in pixels).
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		this.width = width;
		this.height = height;

		super.buildSprites(map, tiles, width, height);
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		String resource = ((Creature) entity).getMetamorphosis();

		if (resource == null) {
			resource = getClassResourcePath();
		}

		return SpriteStore.get().getSprite(translate(resource));
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

	}

	/**
	 * Draw the entity.
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y, final int width, final int height) {

		super.draw(g2d, x, y, width, height);

	}

	/**
	 * Reorder the actions list (if needed). Please use as last resort.
	 * 
	 * @param list
	 *            The list to reorder.
	 */
	@Override
	protected void reorderActions(final List<String> list) {
		if (list.remove(ActionType.ATTACK.getRepresentation())) {
			list.add(0, ActionType.ATTACK.getRepresentation());
		}
	}

	/**
	 * Translate a resource name into it's sprite image path.
	 * 
	 * @param name
	 *            The resource name.
	 * 
	 * @return The full resource name.
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		} else if (property == Creature.PROP_METAMORPHOSIS) {
			representationChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.ATTACK);
	}

	//
	//

	private static class Node {
		private final int x;
		private final int y;

		public Node(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.ATTACK;
	}
}
