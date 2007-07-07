/*
 * @(#) games/stendhal/client/entity/Creature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import marauroa.common.Log4J;
import marauroa.common.Logger;

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The 2D view of a creature.
 */
public class Creature2DView extends RPEntity2DView {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(Creature2DView.class);

	/**
	 * The entity this view is for.
	 */
	private Creature	creature;

	/**
	 * Should the path be hidden for this creature?
	 */
	public boolean hidePath = false;

	/**
	 * Whether to display all debug messages for this creature in the
	 * game log.
	 */
	public boolean watch = false;

	/** the patrolpath */
	private List<Node> patrolPath;

	/** new path to the target */
	private List<Node> targetMovedPath;

	/** the path we got */
	private List<Node> moveToTargetPath;

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

	public List<Node> decodePath(final String token) {
		String[] values = token.replace(',', ' ').replace('(', ' ').replace(')', ' ').replace('[', ' ').replace(']',
		        ' ').split(" ");
		List<Node> list = new ArrayList<Node>();

		int x = 0;
		int pass = 1;

		for (String value : values) {
			if (value.trim().length() > 0) {
				int val = Integer.parseInt(value.trim());
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


	protected void drawPath(final GameScreen screen, final List<Node> path, final int delta) {
		Graphics g2d = screen.expose();
		Point p1 = screen.convertWorldToScreen(getX(), getY());

		for (Node node : path) {
			Point p2 = screen.convertWorldToScreen(node.x, node.y);

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
	 * Get the width.
	 *
	 * @return	The width in tile units.
	 */
	public double getWidth() {
		return width;
	}


	protected void handleDebug(String debug) {
		if (!Debug.CREATURES_DEBUG_CLIENT) {
			return;
		}

		patrolPath = null;
		targetMovedPath = null;
		moveToTargetPath = null;

		if (watch) {
			StendhalUI.get().addEventLine(creature.getID() + " - " + debug);
		}

		String[] actions = debug.split("\\|");
		// parse all actions
		for (String action : actions) {
			if (action.length() > 0) {
				StringTokenizer tokenizer = new StringTokenizer(action, ";");

				try {
					String token = tokenizer.nextToken();
					logger.debug("- creature action: " + token);
					if (token.equals("sleep")) {
						break;
					} else if (token.equals("patrol")) {
						patrolPath = decodePath(tokenizer.nextToken());
					} else if (token.equals("targetmoved")) {
						targetMovedPath = decodePath(tokenizer.nextToken());
					} else if (token.equals("movetotarget")) {
						moveToTargetPath = null;
						String nextToken = tokenizer.nextToken();

						if (nextToken.equals("blocked")) {
							nextToken = tokenizer.nextToken();
						}

						if (nextToken.equals("waiting")) {
							nextToken = tokenizer.nextToken();
						}

						if (nextToken.equals("newpath")) {
							moveToTargetPath = null;
							nextToken = tokenizer.nextToken();
							if (nextToken.equals("blocked")) {
								moveToTargetPath = null;
							} else {
								moveToTargetPath = decodePath(nextToken);
							}
						}
					}
				} catch (Exception e) {
					logger.warn("error parsing debug string '" + debug + "' actions [" + Arrays.asList(actions)
					        + "] action '" + action + "'", e);
				}
			}
		}
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
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		buildSprites(map, getWidth(), getHeight());
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		if (Debug.CREATURES_DEBUG_CLIENT) {
			if (hidePath) {
				list.add(ActionType.DEBUG_SHOW_PATH.getRepresentation());
			} else {
				list.add(ActionType.DEBUG_HIDE_PATH.getRepresentation());
			}

			if (watch) {
				list.add(ActionType.DEBUG_DISABLE_WATCH.getRepresentation());
			} else {
				list.add(ActionType.DEBUG_ENABLE_WATCH.getRepresentation());
			}
		}
	}


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
		List<Node>	path;


		super.draw(screen, g2d, x, y, width, height);

		if (Debug.CREATURES_DEBUG_CLIENT && !hidePath) {
			if ((path = getTargetMovedPath()) != null) {
				int delta = GameScreen.SIZE_UNIT_PIXELS / 2;
				g2d.setColor(Color.red);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2);
			}

			if ((path = getPatrolPath()) != null) {
				g2d.setColor(Color.green);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2 + 1);
			}

			if ((path = getMoveToTargetPath()) != null) {
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
	 * Reorder the actions list (if needed). Please use as last resort.
	 *
	 * @param	list		The list to reorder.
	 */
	protected void reorderActions(final List<String> list) {
		if(list.remove(ActionType.ATTACK.getRepresentation())) {
			list.add(0, ActionType.ATTACK.getRepresentation());
		}
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
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if(property == Creature.PROP_DEBUG) {
			handleDebug(creature.getDebug());
		} else if(property == Creature.PROP_METAMORPHOSIS) {
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


	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case DEBUG_SHOW_PATH:
				hidePath = false;
				break;

			case DEBUG_HIDE_PATH:
				hidePath = true;
				break;

			case DEBUG_ENABLE_WATCH:
				watch = true;
				break;

			case DEBUG_DISABLE_WATCH:
				watch = false;
				break;

			default:
				super.onAction(at);
				break;
		}
	}

	//
	//

	protected static class Node {
		public int	x;
		public int	y;


		public Node(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

}
