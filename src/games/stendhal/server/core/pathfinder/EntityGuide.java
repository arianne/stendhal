package games.stendhal.server.core.pathfinder;

import games.stendhal.server.entity.GuidedEntity;

import java.util.List;

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
		if (path != null && nodeReached(entity)) {
			Node next = nextNode();
			if (next != null) {
				entity.faceto(next.getX(), next.getY());
			}
		}
	}
	
	/**
	 * Get the next node on the path.
	 * 
	 * @return The next <code>Node</code>, or <code>null</code> if there is no next node.
	 */
	private Node nextNode() {
		final List<Node> nodes = path.getNodeList();
		int nextPos = pathPosition + 1;
		Node next = null;
		
		if (nextPos < nodes.size()) {
			next = nodes.get(nextPos);
		} else if (path.isLoop()) {
			next = nodes.get(0);
		}
		
		return next;
	}

	/**
	 * Check if the entity has reached the current goal.
	 * 
	 * @param entity the guided entity
	 * @return true iff the current goal node has been reached
	 */
	private boolean nodeReached(GuidedEntity entity) {
		Node previous = path.getNodeList().get(pathPosition);

		return ((previous.getX() == entity.getX())
			&& (previous.getY() == entity.getY()));
	}
}
