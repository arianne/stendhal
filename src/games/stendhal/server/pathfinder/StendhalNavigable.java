package games.stendhal.server.pathfinder;

import java.awt.geom.Rectangle2D;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;

public class StendhalNavigable implements Navigable {

	/**
	 * The start x position.
	 */
	protected int x;

	/**
	 * The start y position.
	 */
	protected int y;

	/**
	 * The entity searching a path
	 */
	protected Entity entity;

	/**
	 * The zone a path is searched
	 */
	protected StendhalRPZone zone;

	/**
	 * The goal
	 */
	protected Rectangle2D goal;

	/**
	 * The maximum distance for the path.
	 * It is comared with the f value of the node
	 * The deafult is 40
	 * The minimum for working pathfinding is heuristicFromStartNode + 1
	 */
	protected double maxDistance;

	protected static final double defaultMaxDistance = 40.0;

	/**
	 * Creates a new navigation map
	 * @param entity The entity searching a path
	 * @param zone The zone a path is searched
	 * @param startX The start x position
	 * @param startY The start y position
	 * @param destination The goal
	 */
	public StendhalNavigable(Entity entity, StendhalRPZone zone, int startX, int startY, Rectangle2D destination) {

		this(entity, zone, startX, startY, destination, defaultMaxDistance);
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
	public StendhalNavigable(Entity entity, StendhalRPZone zone, int startX, int startY, Rectangle2D destination,
	        double maxDist) {

		this.entity = entity;
		this.zone = zone;
		this.x = startX;
		this.y = startY;
		this.goal = destination;

		double startF = getHeuristic(startX, startY, (int) destination.getCenterX(), (int) destination.getCenterY());

		// check the maxDistance
		if (maxDist > startF + 1) {
			this.maxDistance = maxDist;
		} else {
			this.maxDistance = startF + 1;
		}
	}

	/**
	 * checks if the entity could stand on a position
	 * @param node the position to be checked
	 * @retrun true if the the entity could stand on the position 
	 */
	public boolean isValid(Pathfinder.Node node) {
		return !zone.simpleCollides(entity, node.x, node.y);
		// return !zone.collides(entity, node.x,node.y);
	}

	/**
	 * calculates the cost for the move from the parent to the child
	 * costs are allway 1 in stendhal
	 * @param parent the parent node 
	 * @param child the cild node
	 * @retrun 1 
	 */
	public double getCost(Pathfinder.Node parent, Pathfinder.Node child) {
		return 1;
	}

	/**
	 * calculates the heuristic for the move form node1 to node2
	 * the right heuristic is very importand for A*
	 * - a over estimated heuristic will turn A* in to bsf
	 * - a under estimated heuristic will turn A* in to Dijkstra's
	 * so the manhattan distance seams to be the optimal heuristic here.
	 * But it has one disadvantage. It will expand to much. 
	 * Sevreal nodes will have the same f value
	 * It will search the area of the size 
	 * (abs(startX - goalX) + 1) * (abs(startY - goalY) + 1)
	 * So a tie-breaker is needed. 1% square distace seems to work fine.
	 * A* will prefer nodes closer to the goal.
	 * @param node1 the parent node 
	 * @param node2 the cild node
	 * @retrun the estimated cost to walk from node1 to node2
	 */
	public double getHeuristic(Pathfinder.Node node1, Pathfinder.Node node2) {
		return getHeuristic(node1.x, node1.y, node2.x, node2.y);
	}

	/**
	 * calculates the heuristic for the move form node1 to node2
	 * @param x1 x value for node 1 
	 * @param y1 y value for node 1 
	 * @param x2 x value for node 2 
	 * @param y2 y value for node 2 
	 */
	public double getHeuristic(int x1, int y1, int x2, int y2) {
		double heuristic = manhattanDistance(x1, y1, x2, y2);
		double tieBreaking = 0.01 * squareDistance(x1, y1, x2, y2);
		return heuristic + tieBreaking;
	}

	/**
	 * checks if the goal is reached 
	 * @param nodeBest the currently best node 
	 * @retrun true if the goal is reached
	 */
	public boolean reachedGoal(Pathfinder.Node nodeBest) {
		return goal.contains(nodeBest.getX(), nodeBest.getY());
	}

	/**
	 * calculates the node id
	 * @param node the node 
	 * @retrun the id of the node
	 */
	public int createNodeID(Pathfinder.Node node) {
		return node.x + node.y * zone.getWidth();
	}

	/**
	 * crates valid cild nodes, the cild nodes have to be 
	 * - a valid position
	 * - a f value less than maxDistance (checked against the given node)
	 * @param node the node 
	 */
	public void createChildren(Pathfinder path, Pathfinder.Node node) {
		int x = node.x, y = node.y;
		if (node.f < maxDistance) {
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					// If the node is this node, or invalid continue.
					if (((i == 0) && (j == 0)) || (Math.abs(i) == Math.abs(j))) {
						continue;
					} else {
						Pathfinder.Node childNode = new Pathfinder.Node(x + i, y + j);
						if (isValid(childNode)) {
							path.linkChild(node, childNode);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if the goal is unreachable.
	 * Only the outer nodes of the goal are checked. 
	 * There could be other reasons, why a goal is unreachable.
	 * 
	 * @return true checks if the goal is unreachable
	 */
	public boolean unrechable() {
		int w = (int) goal.getWidth() - 1;
		int h = (int) goal.getHeight() - 1;
		int x = (int) goal.getX();
		int y = (int) goal.getY();
		Pathfinder.Node node = new Pathfinder.Node();

		for (int i = 0; i <= w; i++) {
			for (int j = 0; j <= h; j++) {
				if ((i == 0) || (j == 0) || (i == w) || (j == h)) {
					node.x = x + i;
					node.y = y + j;
					if (isValid(node)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * calculates the manhattan distance between to positions
	 * @param x1 x value for postion 1 
	 * @param y1 y value for postion 1 
	 * @param x2 x value for postion 2 
	 * @param y2 y value for postion 2
	 * @return manhattan distance between to positions
	 */
	public static int manhattanDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	/**
	 * calculates the square distance between to positions
	 * @param x1 x value for postion 1 
	 * @param y1 y value for postion 1 
	 * @param x2 x value for postion 2 
	 * @param y2 y value for postion 2 
	 * @return square distance between to positions
	 */
	public static int squareDistance(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
}
