package games.stendhal.server.core.pathfinder;

import games.stendhal.common.CollisionDetection;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

/**
 * Server side path finder.
 */
class EntityPathfinder extends games.stendhal.server.core.pathfinder.Pathfinder {
	/**
	 * The entity searching a path.
	 */
	private final Entity entity;

	/**
	 * The zone a path is searched.
	 */
	private final StendhalRPZone zone;

	private final boolean checkEntities;

	/**
	 * Contains the collision data for entities.
	 */
	private CollisionDetection collisionMap;

	EntityPathfinder(final Entity entity, final StendhalRPZone zone, final int startX, final int startY,
			final Rectangle2D destination, final double maxDist, final boolean checkEntities) {
		super(startX, startY, destination, maxDist);
		this.entity = entity;
		this.zone = zone;
		this.checkEntities = checkEntities;
	}
	
	@Override
	protected void init() {
		super.init();
		if (checkEntities) {
			createEntityCollisionMap();
		}	
	}

	/**
	 * Creates collision data for entities.
	 * <p>The positions with entities are only
	 * considered as not valid if they: <li> are next to the start position or <li>
	 * have stopped
	 */
	private void createEntityCollisionMap() {
		Point targetPoint = new Point(goalNode.getX(), goalNode.getY());
		collisionMap = new CollisionDetection();
		collisionMap.init(zone.getWidth(), zone.getHeight());
		for (final RPObject obj : zone) {
			final Entity otherEntity = (Entity) obj;
			if (!entity.getID().equals(otherEntity.getID())
					&& otherEntity.isObstacle(entity)
					&& (otherEntity.stopped() || otherEntity.nextTo(
							startNode.getX(), startNode.getY(), 0.25))) {

				final Rectangle2D area = otherEntity.getArea(otherEntity.getX(),
						otherEntity.getY());

				// Hack: Allow players to move onto portals as destination
				if ((entity instanceof Player) && (otherEntity instanceof Portal) && area.contains(targetPoint)) {
					continue;
				}
				collisionMap.setCollide(area, true);
			}
		}
	}

	@Override
	public TreeNode createNode(int x, int y) {
		return new PathTreeNode(x, y);
	}
	
	/**
	 * Pathfinder node
	 */
	private class PathTreeNode extends TreeNode {
		protected PathTreeNode(int x, int y) {
			super(x, y);
		}

		@Override
		public TreeNode createNode(int x, int y) {
			return new PathTreeNode(x, y);
		}

		@Override
		protected int createNodeID(int x, int y) {
			return x + y * zone.getWidth();
		}

		@Override
		public boolean isValid(int x, int y) {
			boolean result = !zone.simpleCollides(entity, x, y);
			if (checkEntities && result) {
				final Rectangle2D entityArea = entity.getArea(x, y);
				result = !collisionMap.collides(entityArea);
			}

			return result;
		}		
	}
}
