package games.stendhal.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates a list of tiles and the maps where they are used
 * 
 * @author hendrik
 */
public class InversedMapAnalyser {
	
	private Map<Integer, TreeMap<String, Integer>> tiles = new TreeMap<Integer, TreeMap<String, Integer>>();
	private static final String PATH = "/home/hendrik/stendhal/cache/";

	private void readMap(String filename) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(PATH + filename));
		String text;

		file.readLine(); // size

		while ((text = file.readLine()) != null) {
			if (text.trim().equals("")) {
				break;
			}
			String[] items = text.split(",");
			for (String itemStr : items) {
				Integer item = new Integer(Integer.parseInt(itemStr));
				TreeMap<String, Integer> temp = tiles.get(item);
				if (temp == null)  {
					temp = new TreeMap<String, Integer>();
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

	private void out() {
		System.out.println(tiles);
		System.out.println("-----------------------------");

		Map<Integer, Integer> out = new TreeMap<Integer, Integer>(); 
		for (Integer tile : tiles.keySet()) {
			Map<String, Integer> temp = tiles.get(tile);
			int counter = 0;
			for (Integer count : temp.values()) {
				counter += count.intValue();
			}
			out.put(tile, new Integer(counter));
		}
		System.out.println(out);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		InversedMapAnalyser ima = new InversedMapAnalyser();
		File dir = new File(InversedMapAnalyser.PATH);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.getName().equals("stendhal.cache")) {
				ima.readMap(file.getName());
			}
		}
		ima.out();
	}

}
