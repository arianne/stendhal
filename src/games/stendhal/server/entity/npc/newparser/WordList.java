package games.stendhal.server.entity.npc.newparser;

import games.stendhal.common.Grammar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * Word list manager
 * Words are categorized by type (noun, verb, adjective, preposition)
 * and optionally sub-types (animals, food, fluids, ...).
 * 
 * @author Martin Fuchs
 */
public class WordList {
	private static final Logger logger = Logger.getLogger(WordList.class);

	private final Map<String, WordEntry> words = new TreeMap<String, WordEntry>();
	private final List<String> comments = new ArrayList<String>();

	private static final WordList instance = new WordList();

	// initialise word list from the input file "words.txt"
	static {
		Log4J.init();

		InputStream str = WordList.class.getResourceAsStream("words.txt");

		BufferedReader reader = new BufferedReader(new InputStreamReader(str));

		try {
			instance.read(reader);

	        reader.close();
        } catch(IOException e) {
	        e.printStackTrace();
        }
	}

	public static WordList getInstance() {
		return instance;
	}

	public void read(BufferedReader reader) throws IOException	{
		for(;;) {
            String line = reader.readLine();
            if (line == null)
            	break;

            StringTokenizer tk = new StringTokenizer(line);

            if (!tk.hasMoreTokens())
            	continue;

            String key = tk.nextToken();

            if (key.startsWith("#")) {
            	comments.add(line);
            } else {
        	    WordEntry entry = new WordEntry();
        	    entry.word = key;

        	    readEntryLine(tk, entry);
            	addLineEntry(entry, key);
            }
		}
	}

	private void readEntryLine(StringTokenizer tk, WordEntry entry) {
	    if (tk.hasMoreTokens()) {
	    	entry.type = tk.nextToken();

	        if (tk.hasMoreTokens()) {
	        	String s = tk.nextToken();

	        	if (entry.type.startsWith("NUM"))
	        		entry.value = new Integer(s);
	        	else
	        		entry.plural = s;
	        }

	        if (Character.isLowerCase(entry.type.charAt(0))) {
	        	entry.plural = entry.type;
	        	entry.type = "NOU";
	        } else if (entry.plural==null &&
	        		entry.type.startsWith("NOU") &&
	        		!entry.type.endsWith("NAM")) {
	        	String plural = Grammar.plural(entry.word);

	        	// only store single word plurals
	        	if (plural.indexOf(' ') == -1)
	        		entry.plural = plural;
	        } else if (entry.plural != null){
	        	String plural = Grammar.plural(entry.word);

	        	if (plural.indexOf(' ')==-1 &&
	        		!plural.equals(entry.plural) &&
	        		!Grammar.isSubject(entry.word) &&
	        		!entry.word.equals("is")) {
	        		logger.error(String.format("suspicious plural: %s -> %s (%s?)", entry.word, entry.plural, plural));
	        	}
	        }

	        while(tk.hasMoreTokens()) {
	        	logger.error("superflous trailing word: " + tk.nextToken());
	        }
	    }
    }

	private void addLineEntry(WordEntry entry, String key) {
	    words.put(key.toLowerCase(), entry);

	    // store plural and associate with singular form
	    if (entry.plural!=null && !entry.plural.equals(entry.word)) {
	    	WordEntry pluralEntry = new WordEntry();

	    	pluralEntry.word = entry.plural;
	    	pluralEntry.type = entry.type + "-PLU";
	    	pluralEntry.plural = entry.word;
	    	pluralEntry.value = entry.value;

	    	WordEntry prev = words.put(entry.plural.toLowerCase(), pluralEntry);

	    	if (prev != null) {
	    		logger.debug(String.format("ambiguos plural: %s/%s -> %s", pluralEntry.plural, prev.plural, entry.plural));

	    		pluralEntry.plural = null;
	    		prev.plural = null;
	    	}
	    }
    }

	/**
	 * find an entry for a given word
	 * @param s
	 * @return Word
	 */
	public WordEntry find(String s) {
		WordEntry w = words.get(s.toLowerCase());

		return w;
	}

	/**
	 * print all words sorted by known types
	 * @param writer
	 */
	public void write(PrintWriter writer) {
		for(String c : comments) {
			writer.println(c);
		}

   		writer.println();
		printWordType(writer, "VER");

   		writer.println();
		printWordType(writer, "NOU");

		writer.println();
		printWordType(writer, "ADJ");

   		writer.println();
		printWordType(writer, "NUM");

		writer.println();
		printWordType(writer, "PRE");

		writer.println();
		printWordType(writer, "IGN");

		writer.println();
		printWordType(writer, null);
	}

	/**
	 * print all words of a given (main-)type
	 * 
	 * @param writer
	 * @param type
	 */
	private void printWordType(PrintWriter writer, String type) {
	    for(String word : words.keySet()) {
	    	WordEntry w = words.get(word);
	    	boolean matches;

	    	if (type == null) {
	    		matches = w.type==null;
	    	} else {
	    		matches = w.type!=null &&
	    					w.type.startsWith(type) && !w.isPlural();
	    	}

	    	if (matches) {
	    		w.print(writer);
	    		writer.println();
	    	}
	    }
    }

	/**
	 * main() function for WordList to read word list
	 * and print out in a sorted, formated way
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		PrintWriter writer = new PrintWriter(System.out);

		instance.write(writer);

		writer.close();
	}

	/**
	 * Lookup the singular form of the given word from the word list
	 * @param word
	 * @return
	 */
	public String plural(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (!w.isPlural())
				// return the associated singular from the word list
				return w.plural;
			else
				// The word is already in singular form.
				return w.word;
		} else {
			// Fallback: call Grammar.plural()
			return Grammar.plural(word);
		}
    }

	/**
	 * Lookup the singular form of the given word from the word list
	 * @param word
	 * @return
	 */
	public String singular(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (w.isPlural())
				// return the associated singular from the word list
				return w.plural;
			else
				// The word is already in singular form.
				return w.word;
		} else {
			// Fallback: call Grammar.singular()
			return Grammar.singular(word);
		}
    }
}
