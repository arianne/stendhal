package games.stendhal.client.entity;
/**
 * Client side representation of a pushable, solid block
 * 
 * @author madmetzger
 */
public class Block extends Entity {

	@Override
	public boolean isObstacle(IEntity entity) {
		return true;
	}

}
