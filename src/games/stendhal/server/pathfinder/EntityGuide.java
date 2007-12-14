package games.stendhal.server.pathfinder;

import games.stendhal.server.entity.GuidedEntity;

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

	public void guideMe(GuidedEntity ge) {
		// empty default implememntation
	}

	/**
	 * The path.
	 */
	public FixedPath path;
	/**
	 * current position in the path
	 */
	public int pathPosition;

	public boolean followPath(GuidedEntity entity) {
		return Path.followPath(entity);
	}

	public void clearPath() {
		path = null;
		pathPosition = 0;

	}

	public int getPathsize() {
		return path.getNodes().length;
	}

}
