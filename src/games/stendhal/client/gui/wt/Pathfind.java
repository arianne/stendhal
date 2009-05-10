package games.stendhal.client.gui.wt;

import games.stendhal.common.CollisionDetection;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

/**
 * A* implementation. 
 * 
 * @author Kawn
 */

class Pathfind {
	private static Rectangle search_area;
	private static List<Node> closed_list = new ArrayList<Node>();
	private static LinkedList<Node> final_path = new LinkedList<Node>();
	int final_path_index = 0;

	private final HashMap<Integer, Node> nodeRegistry = new HashMap<Integer, Node>();
	private final HashMap<Integer, Node> nodeRegistryclose = new HashMap<Integer, Node>();

	private final PriorityQueue<Node> open_list = new PriorityQueue<Node>(16,
			new Comparator<Node>() {

				public int compare(final Node o1, final Node o2) {
					return (int) Math.signum(o1.F - o2.F);
				}
			});


	private Node current_node;
	

	private static int collision(final CollisionDetection collisiondetection, final int x1, final int y1) {
//		if (x1 < 0)
//			return 1;
//		if (y1 < 0)
//			return 1;
// 
//		if (x1 >= collisiondetection.getWidth())
//			return 1;
//		if (y1 >= collisiondetection.getHeight())
//			return 1;

		if (x1 < search_area.getMinX()) {
			return 1;
		}
		if (y1 < search_area.getMinY()) {
			return 1;
		}

		if (x1 >= search_area.getMaxX()) {
			return 1;
		}
		if (y1 >= search_area.getMaxY()) {
			return 1;
		}

		if (!collisiondetection.walkable(x1, y1)) {
			return 1;
		}

		return 0;
	}

	public void pathNextNode() {

		if (final_path_index != 0) {
			final_path_index--;
			current_node = final_path.get(final_path_index);
		}
	}

	public void pathJumpNode() {
		final_path_index = final_path_index - 20;

		if (final_path_index < 0) {
			final_path_index = 0;
		}

		current_node = final_path.get(final_path_index);
	}

	public void jumpToPathNode(final int destnode) {
		final_path_index = destnode;

		if (final_path_index < 0) {
			final_path_index = 0;
		}
		current_node = final_path.get(destnode);
	}

	public int nodeGetX() {
		return current_node.x;
	}

	public int nodeGetY() {
		return current_node.y;
	}

	public boolean isGoalReached() {
		return final_path_index == 0;
	}

	public void restart() {
		if (final_path != null) {
			final_path_index = final_path.size();
		}
	}

	public void clearPath() {
		open_list.clear();
		closed_list.clear();
		final_path.clear();
		final_path_index = 0;
		nodeRegistry.clear();
		nodeRegistryclose.clear();

	}

	public boolean newPath(final CollisionDetection collisiondetection,
			final int initial_x, final int initial_y, final int final_x, final int final_y,
			final Rectangle search_area2) {

		// System.out.println("PATHFIND: " + initial_x + " " + initial_y + " "
		// +final_x + " "+ final_y + " " +collisiondetection.toString());

		search_area = search_area2;

		// System.out.println("AREA: " + search_area.getMinX() + " " +
		// search_area.getMinY() + " " +search_area.getMaxX() + " " +
		// search_area.getMaxY());

//		if (colision(collisiondetection,final_x,final_y) != 0) {
//			System.out.println("NON-WALKABLE DESTINATION: " + initial_x + " " + initial_y + " " +final_x + " "+ final_y + " " );
//			return false; 
//		}

		// long computation_time = System.currentTimeMillis();
		final Node ini_node = new Node(initial_x, initial_y, initial_x, initial_y);

		clearPath();

		// 1) Add the starting square (or node) to the open list.
		ini_node.parent = new Node();

		open_list.offer(ini_node);
		nodeRegistry.put(ini_node.x + ini_node.y
				* collisiondetection.getWidth(), ini_node);

		do {
			// THERE IS NO PATH
			if (open_list.size() == 0) {
				// computation_time = System.currentTimeMillis() -
				// computation_time;
				// System.out.println("THERE IS NO PATH!! "+
				// closed_list.size());// elapsed time: " + computation_time);
				return false;
			}

			/*
			 * a) Look for the lowest F cost square on the open list. We refer
			 * to this as the current square.
			 */

			// b) Switch it to the closed list.
			final Node node_Fm = open_list.poll();
			closed_list.add(node_Fm);
			nodeRegistryclose.put(node_Fm.x + node_Fm.y
					* collisiondetection.getWidth(), node_Fm);

			// System.out.println("Elements:"+open_list.size()+":"+closed_list.size());

			// The end has been reached
			if ((node_Fm.x == final_x) && (node_Fm.y == final_y)) {
				break;
			}

			// c) For each of the 8 squares adjacent to this current square ...
			int x_tmp;
			int y_tmp;

			for (y_tmp = node_Fm.y - 1; y_tmp <= node_Fm.y + 1; y_tmp++) {
				for (x_tmp = node_Fm.x - 1; x_tmp <= node_Fm.x + 1; x_tmp++) {

					if (y_tmp == node_Fm.y) {
						if (x_tmp == node_Fm.x) {
							continue;
						}
					}
					if ((y_tmp != node_Fm.y) && (x_tmp != node_Fm.x)) {
						continue;
					}

					// //If it is not walkable or if it is on the closed list,
					// ignore it.
					// Otherwise do the following.

					if (nodeRegistryclose.get(x_tmp + y_tmp
							* collisiondetection.getWidth()) != null) {
						continue;
					}

					if (collision(collisiondetection, x_tmp, y_tmp) == 0) {
						final int manhattan = 10 * (Math.abs(x_tmp - final_x) + Math.abs(y_tmp
								- final_y));

						Node node_UP;
						if ((Math.abs(x_tmp - node_Fm.x) == 1)
								&& (Math.abs(y_tmp - node_Fm.y) == 1)) {
							node_UP = new Node(x_tmp, y_tmp, node_Fm.G + 14,
									manhattan);
						} else {
							int potato = 0;

							// --- Bonus if it doesn't change the direction
							final int incx = (node_Fm.parent.x - node_Fm.x);
							final int incy = (node_Fm.parent.y - node_Fm.y);

							final int incx2 = (node_Fm.x - x_tmp);
							final int incy2 = (node_Fm.y - y_tmp);

							if ((incx == incx2) && (incy == incy2)) {
								potato = 1;
								// System.out.println("HOPLA: " +incx +" "+
								// incy);
							}

							node_UP = new Node(x_tmp, y_tmp, node_Fm.G + 10
									- potato, manhattan);

						}
						node_UP.parent = node_Fm;
						// System.out.println("ADJACENT:"+x_tmp+":"+y_tmp + " G
						// " +node_UP.G + " H " + node_UP.H + " H " +
						// node_UP.F);

						final Node temp = nodeRegistry.get(node_UP.x + node_UP.y
								* collisiondetection.getWidth());

						if (temp != null) {
							// //If it is on the open list already, check to see
							// if this path to that
							// square is better, using G cost as the measure. A
							// lower G cost means
							// that this is a better path. If so, change the
							// parent of the square
							// to the current square, and recalculate the G and
							// F scores of the square.
							// If you are keeping your open list sorted by F
							// score,
							// you may need to resort the list to account for
							// the change.
							if (node_UP.G < temp.G) {
								temp.G = node_UP.G;
								temp.F = node_UP.F;
								temp.H = node_UP.H;
								temp.parent = node_UP.parent;
							}

						} else {
							// //If it is not in the open list, add it to the
							// open list.
							// Make the current square the parent of this
							// square. Record the
							// F, G, and H costs of the square.
							// open_list.add(node_UP);
							open_list.offer(node_UP);
							nodeRegistry.put(node_UP.x + node_UP.y
									* collisiondetection.getWidth(), node_UP);
						}
					}
				}
			}
		} while (true);

		// System.out.println("-----------------");
		final ListIterator<Node> i = closed_list.listIterator();
		Node temp;
		while (i.hasNext()) {
			temp =  i.next();
			// System.out.println("E:"+temp.x+":"+temp.y + " G " +temp.G + " H "
			// + temp.H + " "+ temp.parent.x +":" + temp.parent.y);
		}

		int petiX = final_x;
		int petiY = final_y;

		final_path.clear();

		for (int j = closed_list.size() - 1; j >= 0; j--) {

			temp =  closed_list.get(j);
			if ((petiX == temp.x) && (petiY == temp.y)) {

				// System.out.println("S:"+temp.x+":"+temp.y + " G " +temp.G + "
				// H " + temp.H + " "+ temp.parent.x +":" + temp.parent.y);
				petiX = temp.parent.x;
				petiY = temp.parent.y;
				final_path.addLast(temp);
			}
		}

		final_path_index = final_path.size();

		// computation_time = System.currentTimeMillis() - computation_time;

		// System.out.println("PATH FOUND!! " + closed_list.size());//elapsed
		// time: " + computation_time);

		return final_path.size() > 0;
	}

//	public static void main(String[] args) {
//		Pathfind Path = new Pathfind();
//		Path.NewPath(1,2,5,2);
//		while (!Path.ReachedGoal()) {
//			Path.PathNextNode();
//			System.out.println("PEPITO:"+Path.NodeGetX()+":"+Path.NodeGetY());
//		}
//	}

	private class Node {
		private final int x;
		private final int y;
		private int G;
		private int H;
		private int F;
		private Node parent;

		public Node(final int x, final int y, final int g, final int h) {
			super();
			this.x = x;
			this.y = y;
			G = g;
			H = h;
			F = G + H;
		}

		public Node() {
			x = 0;
			y = 0;
			G = 0;
			H = 0;
			F = 0;
		}

	}
}
