/**
 * 
 */
package games.stendhal.tools.loganalyser.laganalyser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author hendrik
 *
 */
public class LagReader {
	private BufferedReader br;

	/**
	 * creates a new LagReader
	 *
	 * @param inputFileName name of log file
	 * @throws FileNotFoundException in case the file is not found
	 */
	public LagReader(String inputFileName) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(inputFileName));
	}

	/**
	 * returns the relative times uesed in each step
	 *
	 * @return relative times
	 */
	public int[] readTurnOverflowRelative() {
		int[] absolute = readTurnOverflowAbsolute();
		if (absolute != null) {
			return calculateRelative(absolute);
		}
		return null;
	}

	/**
	 * reads the absolute times from the file.
	 *
	 * @return array of ints
	 */
	public int[] readTurnOverflowAbsolute() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * calcuates the relative offset
	 *
	 * @param absolute absolute times
	 * @return relative times
	 */
	int[] calculateRelative(int[] absolute) {
		int[] res = new int[absolute.length];
		res[0] = absolute[0];
		for (int i = 1; i < res.length; i++) {
			res [i] = absolute[i] - absolute[i-1];
		}
		return res;
	}

	/**
	 * closes the input stream
	 *
	 * @throws IOException in case of an I/O error
	 */
	public void close() throws IOException {
		br.close();
	}

}
