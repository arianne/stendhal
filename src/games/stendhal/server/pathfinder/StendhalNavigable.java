package games.stendhal.server.pathfinder;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;

public class StendhalNavigable implements Navigable {
	int x;

	int y;

	Entity entity;

	StendhalRPZone zone;

	public StendhalNavigable(Entity entity, int x, int y) {
		this.entity = entity;
		this.zone = (StendhalRPZone) Entity.getWorld()
				.getRPZone(entity.getID());
		this.x = x;
		this.y = y;
	}

	public boolean isValid(Pathfinder.Node node) {
		return !zone.simpleCollides(entity, node.x, node.y);
		// return !zone.collides(entity, node.x,node.y);
	}

	public double getCost(Pathfinder.Node parent, Pathfinder.Node child) {
		int dx = parent.getX() - child.getX();
		int dy = parent.getY() - child.getY();

		return (dx * dx) + (dy * dy);
	}

	public double getHeuristic(Pathfinder.Node parent, Pathfinder.Node child) {
		int dx = parent.getX() - child.getX();
		int dy = parent.getY() - child.getY();

		return (dx * dx) + (dy * dy);
	}

	public boolean reachedGoal(Pathfinder.Node nodeBest) {
		// return Math.abs(nodeBest.x-x)<=1 && Math.abs(nodeBest.y-y)<=1;
		return nodeBest.getX() == x && nodeBest.getY() == y;
	}

	public int createNodeID(Pathfinder.Node node) {
		return node.x + node.y * zone.getWidth();
	}

	public void createChildren(Pathfinder path, Pathfinder.Node node) {
		int x = node.x, y = node.y;
		Pathfinder.Node tempNode = new Pathfinder.Node();

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				tempNode.x = x + i;
				tempNode.y = y + j;
				// If the node is this node, or invalid continue.
				if ((i == 0 && j == 0) || (Math.abs(i) == Math.abs(j))
						|| isValid(tempNode) == false) {
					continue;
				}

				path.linkChild(node, x + i, y + j);
			}
		}
	}
}
