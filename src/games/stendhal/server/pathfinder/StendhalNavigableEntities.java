package games.stendhal.server.pathfinder;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import games.stendhal.common.CollisionDetection;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;

import marauroa.common.game.RPObject;

/**
 * This class extens the StendhalNavigable with the check for entities
 * positions with entities are only considered as not valid if they  
 * - are next to the start position or
 * - have stopped
 * That have the following reasons:
 * - The acutall entity positions are only a snapshoot, 
 *   they will have moved after some turn
 * - A* expands a lost if the straight line is blocked  
 */
public class StendhalNavigableEntities extends StendhalNavigable {
	
	/**
	 * contains the collision data for entities
	 */
	private CollisionDetection collisionMap;
	
	/**
	 * Creates a new navigation map
	 * @param entity The entity searching a path
	 * @param zone The zone a path is searched
	 * @param startX The start x position
	 * @param startY The start y position
	 * @param destination The goal
	 */
	public StendhalNavigableEntities(Entity entity, StendhalRPZone zone,
			int startX, int startY, Rectangle2D destination) {

		super(entity, zone, startX, startY, destination);
		createEntityCollisionMap();
	}

	/**
	 * Creates a new navigation map
	 * @param entity The entity searching a path
	 * @param zone The zone a path is searched
	 * @param startX The start x position
	 * @param startY The start y position
	 * @param destination The goal
	 * @param maxDist The maximum distance for the path
	 */
	public StendhalNavigableEntities(Entity entity, StendhalRPZone zone,
			int startX, int startY, Rectangle2D destination, double maxDist) {

		super(entity, zone, startX, startY, destination, maxDist);
		createEntityCollisionMap();
	}

	/**
	 * checks if the entity could stand on a position
	 * @param node the position to be checked
	 * @retrun true if the the entity could stand on the position 
	 */
	@Override
	public boolean isValid(Pathfinder.Node node) {
		if (super.isValid(node)) {
			Rectangle2D entityArea = entity.getArea(node.x, node.y);
			return !collisionMap.collides(entityArea);
		}
		return false;
	}
	
	/**
	 * cerates collision data for entities
	 * the positions with entities are only considered as not valid if they  
	 * - are next to the start position or
	 * - have stopped
	 */
	public void createEntityCollisionMap() {
		collisionMap = new CollisionDetection();
		collisionMap.init(zone.getWidth(), zone.getHeight());
		for (Iterator<RPObject> it = zone.iterator(); it.hasNext();) {
			Entity otherEntity = (Entity) it.next();
			if (!entity.getID().equals(otherEntity.getID())
					&& otherEntity.isObstacle()
					&& (otherEntity.stopped() 
							|| otherEntity.nextTo(x, y , 0.25))) {

				Rectangle2D area = otherEntity.getArea(otherEntity.getX(),
						otherEntity.getY());
				collisionMap.setCollide(area, true);
			}
		}
	}
}
	  	 
