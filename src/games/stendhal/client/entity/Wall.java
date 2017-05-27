package games.stendhal.client.entity;

/**
 * Client side representation of a wall
 *
 * @author hendrik
 */
public class Wall extends Entity {

	@Override
	public boolean isObstacle(IEntity entity) {
		return true;
	}

}
