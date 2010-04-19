package games.stendhal.server.core.pathfinder;

import java.util.List;

import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.player.Player;

/**
 * the guide dog of an Entity. this class takes the goals where an Entity shall
 * move to via a path. and keeps controle over the steps.
 * 
 * The guided Entity can allways change the destination. Like a blind person can
 * tell its guide dog.
 * 
 * @author astrid
 * 
 */
public class EntityGuide {

	public void guideMe(final GuidedEntity ge) {
		// empty default implememntation
	}

	/**
	 * The path.
	 */
	public FixedPath path;
	/**
	 * current position in the path.
	 */
	public int pathPosition;

	public boolean followPath(final GuidedEntity entity) {
		return Path.followPath(entity);
	}

	public void clearPath() {
		path = null;
		pathPosition = 0;

	}
	
	/**
	 * Turn the entity face the next node on the path.
	 * 
	 * @param entity the guided entity
	 */
	public void faceNext(GuidedEntity entity) {
		if (path != null && (entity instanceof Player)) {
			final List<Node> nodes = path.getNodeList();
			
			int nextPos = entity.getPathPosition() + 1;
			if (nextPos < nodes.size()) {
				Node next = nodes.get(nextPos);
				entity.faceto(next.getX(), next.getY());
			}
		}
	}
}
