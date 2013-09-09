package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.area.OnePlayerArea;
import games.stendhal.server.entity.mapstuff.area.WalkBlocker;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A sokoban board
 *
 * @author hendrik
 */
public class SokobanBoard extends OnePlayerArea {
	private static Logger logger = Logger.getLogger(SokobanBoard.class);
	private String[] levelData = null;
	private static int WIDTH = 20;
	private static int HEIGHT = 16;
	private int level;
	private Player player;
	private LinkedList<Entity> entitiesToCleanup = new LinkedList<Entity>();

	/**
	 * creates a SokobanBoard
	 */
	public SokobanBoard() {
		super(WIDTH, HEIGHT);

		try {
			int cnt = 0;
			InputStream stream = this.getClass().getResourceAsStream("sokoban.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			List<String> lines = new LinkedList<String>();
			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine();
				cnt++;
			}
			levelData = lines.toArray(new String[cnt]);
			br.close();
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	/**
	 * loads a level
	 *
	 * @param level number
	 */
	public void loadLevel(int level) {
		int levelOffset = (level - 1) * (HEIGHT + 1) + 1;
		for (int y = 0; y < HEIGHT; y++) {
			String line = levelData[y + levelOffset];
			for (int x = 0; x < WIDTH; x++) {
				char chr = line.charAt(x);
				switch (chr) {
					case 'x': {
						container(x, y);
						break;
					}
					case '@': {
						wall(x, y);
						break;
					}
					case 'o': {
						box(x, y);
						break;
					}
					case '#': {
						container(x, y);
						box(x, y);
						break;
					}
					case '<': {
						player(x, y, Direction.LEFT);
						break;
					}
					case '>': {
						player(x, y, Direction.RIGHT);
						break;
					}
					case '^': {
						player(x, y, Direction.UP);
						break;
					}
					case 'v': {
						player(x, y, Direction.DOWN);
						break;
					}
					case '(': {
						container(x, y);
						player(x, y, Direction.LEFT);
						break;
					}
					case ')': {
						container(x, y);
						player(x, y, Direction.RIGHT);
						break;
					}
					case 'A': {
						container(x, y);
						player(x, y, Direction.UP);
						break;
					}
					case 'V': {
						container(x, y);
						player(x, y, Direction.DOWN);
						break;
					}
				}
				System.out.print(chr);
			}
			System.out.println();

		}
	}

	/**
	 * removes all created entities (walls, boxes, containers)
	 */
	public void clear() {
		for (Entity entity : entitiesToCleanup) {
			this.getZone().remove(entity);
		}
		entitiesToCleanup.clear();
	}

	/**
	 * creates a wall
	 *
	 * @param x x-offset
	 * @param y y-offset
	 */
	private void wall(int x, int y) {
		WalkBlocker wall = new WalkBlocker();
		wall.setPosition(this.getX() + x, this.getY() + y);
		this.getZone().add(wall);
		entitiesToCleanup.add(wall);
	}

	/**
	 * creates a box
	 *
	 * @param x x-offset
	 * @param y y-offset
	 */
	private void box(int x, int y) {
		Block block = new Block(this.getX() + x, this.getY() + y, true);
		this.getZone().add(block);
		entitiesToCleanup.add(block);
	}

	/**
	 * creates a containe 
	 *
	 * @param x x-offset
	 * @param y y-offset
	 */
	private void container(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * places the player into the level
	 *
	 * @param x x-offset
	 * @param y y-offset
	 * @param direction direction to face to
	 */
	private void player(int x, int y, Direction direction) {
		if (player != null) {
			player.setPosition(this.getX() + x, this.getY() + y);
			player.setDirection(direction);
		}
	}

	/**
	 * thes the currently playing player
	 *
	 * @param player player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
}
