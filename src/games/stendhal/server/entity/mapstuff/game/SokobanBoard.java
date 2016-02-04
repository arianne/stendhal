package games.stendhal.server.entity.mapstuff.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.area.NoLoginAreaBehaviour;
import games.stendhal.server.entity.mapstuff.area.NoTeleportInBehaviour;
import games.stendhal.server.entity.mapstuff.area.Wall;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.npc.condition.AvailabilityChecker;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

/**
 * A sokoban board
 *
 * @author hendrik
 */
public class SokobanBoard extends AreaEntity implements TurnListener, AvailabilityChecker {
	private static Logger logger = Logger.getLogger(SokobanBoard.class);
	private static final int EMPTY_GAMEBOARD_LEVEL = 0;
	private static int WIDTH = 20;
	private static int HEIGHT = 16;

	private String[] levelData = null;
	private int level;
	private long levelStart;
	private String playerName;
	private final LinkedList<Entity> entitiesToCleanup = new LinkedList<Entity>();
	private final Set<Pair<Integer, Integer>> containerLocations = new HashSet<Pair<Integer, Integer>>();
	private final List<Block> boxes = new LinkedList<Block>();
	private final SokobanListener sokobanListener;

	/**
	 * creates a SokobanBoard
	 *
	 * @param sokobanListener SokobanListener
	 */
	public SokobanBoard(SokobanListener sokobanListener) {
		super(WIDTH, HEIGHT);
		super.addBehaviour(new NoLoginAreaBehaviour(42, 126, "Talk to Hiro to play another game."));
		super.addBehaviour(new NoTeleportInBehaviour());

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

		this.sokobanListener = sokobanListener;
		TurnNotifier.get().notifyInTurns(0, this);
	}

	/**
	 * loads a level
	 *
	 * @param lvl level number
	 */
	public void loadLevel(int lvl) {
		String temp = playerName;
		clear();
		playerName = temp;

		levelStart = System.currentTimeMillis();
		this.level = lvl;
		int levelOffset = level * (HEIGHT + 1) + 1;
		for (int y = 0; y < HEIGHT; y++) {
			String line = levelData[y + levelOffset];
			if (line.startsWith("--")) {
				logger.error("Found level seperator within level " + lvl);
			}
			for (int x = 0; x < WIDTH; x++) {
				char chr = line.charAt(x);
				switch (chr) {
					case 'x': {
						container(x, y);
						break;
					}
					case '@': {
						int xStart = x;
						while (true) {
							if ((x >= WIDTH) || (line.charAt(x) != '@')) {
								x--;
								break;
							}
							x++;
						}
						wall(xStart, x - xStart + 1, y);
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
			}
		}
	}

	/**
	 * removes all created entities (walls, boxes, containers)
	 */
	public void clear() {
		for (Entity entity : entitiesToCleanup) {
			this.getZone().remove(entity);
		}
		boxes.clear();
		containerLocations.clear();
		entitiesToCleanup.clear();
		playerName = null;
	}

	/**
	 * creates a wall
	 *
	 * @param x x-offset
	 * @param width width
	 * @param y y-offset
	 */
	private void wall(int x, int width, int y) {
		Wall wall = new Wall(width, 1);
		wall.setPosition(this.getX() + x, this.getY() + y);
		wall.setEntityClass("block/mine_cart_empty");
		wall.setDescription("You see a wall.");
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
		Block block = new Block(true, "pumpkin_halloween");
		block.setPosition(this.getX() + x, this.getY() + y);
		block.setResetBlock(false);
		block.setDescription("You see a pumpkin, move it to a basket.");
		this.getZone().add(block);
		entitiesToCleanup.add(block);
		boxes.add(block);
	}

	/**
	 * creates a container
	 *
	 * @param x x-offset
	 * @param y y-offset
	 */
	private void container(int x, int y) {
		TargetMarker container = new TargetMarker(1, 1);
		container.setPosition(this.getX() + x, this.getY() + y);
		this.getZone().add(container);
		entitiesToCleanup.add(container);
		containerLocations.add(new Pair<Integer, Integer>(
								Integer.valueOf(this.getX() + x),
								Integer.valueOf(this.getY() + y)));
	}

	/**
	 * places the player into the level
	 *
	 * @param x x-offset
	 * @param y y-offset
	 * @param direction direction to face to
	 */
	private void player(int x, int y, Direction direction) {
		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player != null) {
			player.setPosition(this.getX() + x, this.getY() + y);
			player.setDirection(direction);
		}
	}

	/**
	 * sets the currently playing player
	 *
	 * @param player player
	 */
	public void setPlayer(Player player) {
		this.playerName = player.getName();
	}

	/**
	 * gets the number of levels
	 *
	 * @return number of levels
	 */
	public int getLevelCount() {
		return levelData.length / (HEIGHT + 1) - 1;
	}

	/**
	 * checks whether a game is active.
	 *
	 * @return true, if a game is active, false otherwise
	 */
	public boolean isGameActive() {
		return playerName != null;
	}

	/**
	 * checks whether the player is present
	 *
	 * @return true, if the player is inside the game field; false otherwise
	 */
	public boolean isPlayerPresent() {
		if (playerName == null) {
			return false;
		}

		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		if (player == null) {
			return false;
		}

		if (player.getZone() != this.getZone()) {
			return false;
		}

		int x = player.getX();
		if (x < this.getX() || x > this.getX() + WIDTH) {
			return false;
		}

		int y = player.getY();
		if (y < this.getY() || y > this.getY() + HEIGHT) {
			return false;
		}

		return true;
	}

	/**
	 * is the timeout reached
	 *
	 * @return true, if the timeout was reached, false otherwise
	 */
	private boolean isTimeout() {
		int allowedMin = 4  + level * 2;
		if (level <= 2) {
			allowedMin = 2;
		}

		long diff = System.currentTimeMillis() - levelStart;
		return diff / 1000 > allowedMin * 60;
	}

	/**
	 * checks whether the level was completed successfully.
	 *
	 * @return true, if the level was completed; false otherwise.
	 */
	private boolean checkLevelCompleted() {
		for (Block entity : boxes) {
			Pair<Integer, Integer> point = new Pair<Integer, Integer>(
				Integer.valueOf(entity.getX()), Integer.valueOf(entity.getY()));

			// if this block is not on a container position, the level is not completed
			if (!containerLocations.contains(point)) {
				return false;
			}
		}
		// All blocks are on container positions. Since there cannot be
		// two blocks on the same tile and since the number of blocks
		// and containers is equal, call containers are filled
		return true;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		TurnNotifier.get().notifyInTurns(0, this);

		if (!isGameActive()) {
			return;
		}

		if (!isPlayerPresent()) {
			if (level != EMPTY_GAMEBOARD_LEVEL) {
				loadLevel(EMPTY_GAMEBOARD_LEVEL);
			}
			playerName = null;
 			return;
		}

		if (isTimeout()) {
			sokobanListener.onTimeout(playerName, level);
			loadLevel(EMPTY_GAMEBOARD_LEVEL);
			playerName = null;
			return;
		}

		Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
		String text = player.get("text");
		if ((text != null) && (text.equals("exit") || text.equals("leave") || text.equals("reset") || text.equals("retry"))) {
			sokobanListener.onLeave(playerName, level);
			loadLevel(EMPTY_GAMEBOARD_LEVEL);
			playerName = null;
			return;
		}

		// level completed?
		if (checkLevelCompleted()) {
			sokobanListener.onSuccess(playerName, level);
			loadLevel(EMPTY_GAMEBOARD_LEVEL);
			playerName = null;
			return;
		}
	}

	@Override
	public boolean isAvailable() {
		return !isPlayerPresent();
	}
}
