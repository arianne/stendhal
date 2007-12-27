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
 * Words are categorised by type (noun, verb, adjective, preposition)
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

	/**
	 * read word list from reader object
	 *
	 * @param reader
	 * @throws IOException
	 */
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
        	    entry.normalized = key;

        	    readEntryLine(tk, entry);
            	addLineEntry(entry, key);
            }
		}
	}

	/**
	 * read one line of the word list and add the new entry
	 *
	 * @param tk
	 * @param entry
	 */
	private void readEntryLine(StringTokenizer tk, WordEntry entry) {
	    if (tk.hasMoreTokens()) {
	    	entry.type = new WordType(tk.nextToken());

	        if (tk.hasMoreTokens()) {
	        	String s = tk.nextToken();

	        	if (s.charAt(0) == '=') {
	        		entry.normalized = s.substring(1);
        			s = tk.hasMoreTokens()? tk.nextToken(): null;
	        	}

	        	if (s != null) {
    	        	if (entry.type.isNumeral()) {
    	        		entry.value = new Integer(s);
    	        	} else {
    	        		entry.plurSing = s;
    	        	}
	        	}
	        }

	        if (Character.isLowerCase(entry.type.typeString.charAt(0))) {
	        	entry.plurSing = entry.type.typeString;
	        	entry.type.typeString = "OBJ";
	        } else if (entry.plurSing==null &&
	        		entry.type.isObject() &&
	        		!entry.type.isName()) {
	        	String plural = Grammar.plural(entry.normalized);

	        	// only store single word plurals
	        	if (plural.indexOf(' ') == -1)
	        		entry.plurSing = plural;
	        } else if (entry.plurSing != null){
	        	String plural = Grammar.plural(entry.normalized);

	        	if (plural.indexOf(' ')==-1 &&
	        		!plural.equals(entry.plurSing) &&
	        		!Grammar.isSubject(entry.normalized) &&
	        		!entry.normalized.equals("is") && !entry.normalized.equals("me")) {
	        		logger.error(String.format("suspicious plural: %s -> %s (%s?)", entry.normalized, entry.plurSing, plural));
	        	}
	        }

	        while(tk.hasMoreTokens()) {
	        	logger.error("superflous trailing word: " + tk.nextToken());
	        }
	    }
    }

	/**
	 * add one entry to the word list
	 * 
	 * @param entry
	 * @param key
	 */
	private void addLineEntry(WordEntry entry, String key) {
	    words.put(key.toLowerCase(), entry);

	    // store plural and associate with singular form
	    if (entry.plurSing!=null && !entry.plurSing.equals(entry.normalized)) {
	    	WordEntry pluralEntry = new WordEntry();

	    	pluralEntry.normalized = entry.plurSing;
	    	pluralEntry.type = new WordType(entry.type.typeString + "-PLU");
	    	pluralEntry.plurSing = entry.normalized;
	    	pluralEntry.value = entry.value;

	    	WordEntry prev = words.put(entry.plurSing.toLowerCase(), pluralEntry);

	    	if (prev != null) {
	    		logger.debug(String.format("ambiguos plural: %s/%s -> %s", pluralEntry.plurSing, prev.plurSing, entry.plurSing));

	    		pluralEntry.plurSing = null;
	    		prev.plurSing = null;
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
		printWordType(writer, "OBJ");

   		writer.println();
		printWordType(writer, "SUB");

		writer.println();
		printWordType(writer, "ADJ");

   		writer.println();
		printWordType(writer, "NUM");

		writer.println();
		printWordType(writer, "PRE");

   		writer.println();
		printWordType(writer, "QUE");

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
	    					w.type.typeString.startsWith(type) && !w.type.isPlural();
	    	}

	    	if (matches) {
	    		w.print(writer, word);

	    		writer.println();
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
	 * Lookup the plural form of the given word from the word list
	 * @param word
	 * @return plural string
	 */
	public String plural(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (!w.type.isPlural())
				// return the associated singular from the word list
				return w.plurSing;
			else
				// The word is already in singular form.
				return w.normalized;
		} else {
			// fall back: call Grammar.plural()
			return Grammar.plural(word);
		}
    }

	/**
	 * Lookup the singular form of the given word from the word list
	 * @param word
	 * @return singular string
	 */
	public String singular(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (w.type.isPlural())
				// return the associated singular from the word list
				return w.plurSing;
			else
				// The word is already in singular form.
				return w.normalized;
		} else {
			// fall back: call Grammar.singular()
			return Grammar.singular(word);
		}
    }

	/**
	 * Normalise the given verb
	 * @param word string
	 * @return WordEntry
	 */
	public WordEntry normalizeVerb(String word) {
		word = word.toLowerCase();

		WordEntry w = words.get(word);

		if (w == null) {
			String normalized = Grammar.normalizeRegularVerb(word);

			if (normalized != null) {
				w = words.get(normalized);

				// try and re-append "e" if it was removed by normalizeRegularVerb()
				if (w==null && word.endsWith("e") && !normalized.endsWith("e")) {
					w = words.get(normalized + "e");
				}
			}
		}

		return w;
	}
}
