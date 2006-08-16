package games.stendhal.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a list of tiles and the maps where they are used
 * 
 * @author hendrik
 */
public class InversedMapAnalyser {
	
	private Map<String, HashMap<String, Integer>> tiles = new HashMap<String, HashMap<String, Integer>>();
	private String PATH = "/home/hendrik/stendhal/cache/";

	private void readMap(String filename) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(PATH + filename));
		String text;

		file.readLine(); // size

		while ((text = file.readLine()) != null) {
			if (text.trim().equals("")) {
				break;
			}
			String[] items = text.split(",");
			for (String item : items) {
				HashMap<String, Integer> temp = tiles.get(item);
				if (temp == null)  {
					temp = new HashMap<String, Integer>();
					tiles.put(item, temp);
				}
				Integer count = temp.get(filename);
				if (count == null) {
					count = new Integer(0);
				}
				count = new Integer(count.intValue() + 1);
				temp.put(filename, count);
				
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
