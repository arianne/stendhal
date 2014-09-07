package games.stendhal.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.google.common.base.Charsets;

/**
 * flips the sokoban.txt file
 *
 * @author hendrik
 */
public class FlipSokoban {

	/**
	 * reverses each line in a sokoban board; without reversing comments.
	 *
	 * @param reader input
	 * @param out out
	 * @throws IOException in case of an input/output error
	 */
	public void flip(BufferedReader reader, PrintStream out) throws IOException {
		String line = reader.readLine();
		while (line != null) {
			if (!line.startsWith("-")) {
				line = new StringBuilder(line).reverse().toString().replace('>', 'G').replace('<', '>').replace('G', '<');
			}
			out.println(line);

			line = reader.readLine();
		}
	}

	/**
	 * reverses each line in a sokoban board; without reversing comments.
	 *
	 * @param args ignored
	 * @throws IOException in case of an input/output error
	 */
	public static void main(String[] args) throws IOException {
		InputStream is = FlipSokoban.class.getClassLoader().getResourceAsStream("games/stendhal/server/entity/mapstuff/game/sokoban.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
		try {
			new FlipSokoban().flip(reader, System.out);
		} finally {
			reader.close();
		}
	}

}
