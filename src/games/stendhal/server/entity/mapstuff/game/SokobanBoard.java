package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.entity.mapstuff.area.OnePlayerArea;

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

	public void loadLevel(int level) {
		int levelOffset = (level - 1) * (HEIGHT + 1) + 1;
		for (int y = 0; y < HEIGHT; y++) {
			String line = levelData[y + levelOffset];
			for (int x = 0; x < WIDTH; x++) {
				char chr = line.charAt(x);
				System.out.print(chr);
			}
			System.out.println();
		}
	}
}
