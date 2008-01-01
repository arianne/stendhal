package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
 * WordList stores a list of words recognized by the ConversationParser. Words are
 * categorized by type (noun, verb, adjective, preposition) and optionally sub-types
 * (animals, food, fluids, ...).
 * 
 * @author Martin Fuchs
 */
public class WordList {

	private static final Logger logger = Logger.getLogger(WordList.class);

	public static final String SUBJECT_NAME_DYNAMIC = ExpressionType.SUBJECT_NAME + ExpressionType.SUFFIX + "DYN";

	private final Map<String, WordEntry> words = new TreeMap<String, WordEntry>();

	private static final WordList instance = new WordList();

	// initialize word list from the input file "words.txt"
	static {
		Log4J.init();

		InputStream str = WordList.class.getResourceAsStream("words.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(str));

		try {
			instance.read(reader, null);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static WordList getInstance() {
		return instance;
	}

	/**
	 * Read word list from reader object.
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public void read(BufferedReader reader, List<String> comments) throws IOException {
		for (;;) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}

			StringTokenizer tk = new StringTokenizer(line);

			if (!tk.hasMoreTokens()) {
				continue;
			}

			String key = tk.nextToken();

			if (key.startsWith("#")) {
				if (comments != null) {
					comments.add(line);
				}
			} else {
				WordEntry entry = new WordEntry();
				entry.setNormalized(key);

				readEntryLine(tk, entry);
				addLineEntry(entry, key);
			}
		}
	}

	/**
	 * Read one line of the word list and add the new entry.
	 * 
	 * @param tk
	 * @param entry
	 */
	private void readEntryLine(StringTokenizer tk, WordEntry entry) {
		if (tk.hasMoreTokens()) {
			entry.setType(new ExpressionType(tk.nextToken()));

			if (tk.hasMoreTokens()) {
				String s = tk.nextToken();

				if (s.charAt(0) == '=') {
					entry.setNormalized(s.substring(1));
					s = tk.hasMoreTokens() ? tk.nextToken() : null;
				}

				if (s != null) {
					if (entry.getType().isNumeral()) {
						entry.setValue(new Integer(s));
					} else {
						entry.setPlurSing(s);
					}
				}
			}

			// Type identifiers are always upper case, so a lower case word must
			// be a plural.
			if (Character.isLowerCase(entry.getTypeString().charAt(0))) {
				entry.setType(new ExpressionType(ExpressionType.OBJECT));
				entry.setPlurSing(entry.getTypeString());
			}
			// complete missing plural expressions using the Grammar.plural()
			// function
			else if (entry.getPlurSing() == null && entry.getType().isObject()) {
				String plural = Grammar.plural(entry.getNormalized());

				// only store single word plurals
				if (plural.indexOf(' ') == -1) {
					entry.setPlurSing(plural);
				}
			}
			// check for sensible plural strings using the Grammar.plural()
			// function
			else if (entry.getPlurSing() != null) {
				String plural = Grammar.plural(entry.getNormalized());

				if (plural.indexOf(' ') == -1 && !plural.equals(entry.getPlurSing())
						&& !Grammar.isSubject(entry.getNormalized()) && !entry.getNormalized().equals("is")
						&& !entry.getNormalized().equals("me")) {
					logger.error(String.format("suspicious plural: %s -> %s (%s?)", entry.getNormalized(),
							entry.getPlurSing(), plural));
				}
			}

			while (tk.hasMoreTokens()) {
				logger.error("superflous trailing word: " + tk.nextToken());
			}
		}
	}

	/**
	 * Add one entry to the word list.
	 * 
	 * @param entry
	 * @param key
	 */
	private void addLineEntry(WordEntry entry, String key) {
		words.put(key.toLowerCase(), entry);

		// store plural and associate with singular form
		if (entry.getPlurSing() != null && !entry.getPlurSing().equals(entry.getNormalized())) {
			WordEntry pluralEntry = new WordEntry();

			pluralEntry.setNormalized(entry.getPlurSing());
			pluralEntry.setType(new ExpressionType(entry.getTypeString() + ExpressionType.SUFFIX_PLURAL));
			pluralEntry.setPlurSing(entry.getNormalized());
			pluralEntry.setValue(entry.getValue());

			WordEntry prev = words.put(entry.getPlurSing().toLowerCase(), pluralEntry);

			if (prev != null) {
				logger.debug(String.format("ambiguos plural: %s/%s -> %s", pluralEntry.getPlurSing(),
						prev.getPlurSing(), entry.getPlurSing()));

				pluralEntry.setPlurSing(null);
				prev.setPlurSing(null);
			}
		}
	}

	/**
	 * The main() function WordList reads the current word list writes a new updated,
	 * pretty formatted list in the file "words.txt".
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        try {
        	// read in the current word list including comment lines
    		InputStream str = WordList.class.getResourceAsStream("words.txt");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(str));

        	List<String> comments = new ArrayList<String>();
			instance.read(reader, comments);
			reader.close();

	    	// see if we can find the word list source file in the file system
	    	String outputPath = "src/games/stendhal/server/entity/npc/parser/words.txt";

			File file = new File(outputPath);
			if (!file.exists()) {
				// Otherwise just write the output file into the current directory.
				outputPath = "words.txt";
			}

        	PrintWriter writer = new PrintWriter(new FileWriter(outputPath));
			instance.write(writer, comments);
			writer.close();

			System.out.println("The updated word list has been written to the file '" + outputPath  +"'.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print all words sorted by known types.
	 * 
	 * @param writer
	 */
	public void write(PrintWriter writer, final List<String> comments) {
		for (String c : comments) {
			writer.println(c);
		}

		writer.println();
		printWordType(writer, ExpressionType.VERB);

		writer.println();
		printWordType(writer, ExpressionType.OBJECT);

		writer.println();
		printWordType(writer, ExpressionType.SUBJECT);

		writer.println();
		printWordType(writer, ExpressionType.ADJECTIVE);

		writer.println();
		printWordType(writer, ExpressionType.NUMERAL);

		writer.println();
		printWordType(writer, ExpressionType.PREPOSITION);

		writer.println();
		printWordType(writer, ExpressionType.QUESTION);

		writer.println();
		printWordType(writer, ExpressionType.IGNORE);

		writer.println();
		printWordType(writer, null);
	}

	/**
	 * Print all words of a given (main-)type.
	 * 
	 * @param writer
	 * @param type
	 */
	private void printWordType(PrintWriter writer, String type) {
		for (String word : words.keySet()) {
			WordEntry w = words.get(word);
			boolean matches;

			if (type == null) {
				matches = w.getType() == null;
			} else {
				matches = w.getTypeString().startsWith(type) && !w.isPlural();
			}

			if (matches) {
				w.print(writer, word);

				writer.println();
			}
		}
	}

	/**
	 * Find an entry for a given word.
	 * 
	 * @param s
	 * @return Word
	 */
	public WordEntry find(String s) {
		WordEntry w = words.get(s.toLowerCase());

		return w;
	}

	/**
	 * Lookup the plural form of the given word from the word list.
	 * 
	 * @param word
	 * @return plural string
	 */
	public String plural(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (w.getType()!=null && !w.getType().isPlural()) {
				// return the associated singular from the word list
				return w.getPlurSing();
			} else {
				// The word is already in singular form.
				return w.getNormalized();
			}
		} else {
			// fall back: call Grammar.plural()
			return Grammar.plural(word);
		}
	}

	/**
	 * Lookup the singular form of the given word from the word list.
	 * 
	 * @param word
	 * @return singular string
	 */
	public String singular(String word) {
		WordEntry w = words.get(word.toLowerCase());

		if (w != null) {
			if (w.getType()!=null && w.getType().isPlural()) {
				// return the associated singular from the word list
				return w.getPlurSing();
			} else {
				// The word is already in singular form.
				return w.getNormalized();
			}
		} else {
			// fall back: call Grammar.singular()
			return Grammar.singular(word);
		}
	}

	/**
	 * Try to normalize the given verb.
	 * 
	 * @param word
	 * @return WordEntry
	 */
	protected WordEntry normalizeVerb(String word) {
		word = word.toLowerCase();

		String normalized = Grammar.normalizeRegularVerb(word);

		if (normalized != null) {
			WordEntry w = words.get(normalized);

			// try and re-append "e" if it was removed by
			// normalizeRegularVerb()
			if (w == null && word.endsWith("e") && !normalized.endsWith("e")) {
				w = words.get(normalized + "e");
			}

			return w;
		} else {
			return null;
		}
	}

	/**
	 * Try to find a matching word for a derived adjective.
	 * 
	 * @param word
	 * @return WordEntry
	 */
	protected WordEntry normalizeAdjective(String word) {
		word = word.toLowerCase();

		String normalized = Grammar.normalizeDerivedAdjective(word);

		if (normalized != null) {
			WordEntry w = words.get(normalized);

			return w;
		} else {
			return null;
		}
	}

	/**
	 * Register a name to be recognized by the conversation parser.
	 * 
	 * @param name
	 */
	public void registerSubjectName(String name) {
		String key = name.toLowerCase();
		WordEntry w = words.get(key);

		if (w==null || w.getType()==null) {
			WordEntry entry = new WordEntry();

			entry.setNormalized(key);
			entry.setType(new ExpressionType(SUBJECT_NAME_DYNAMIC));

			words.put(key, entry);
		} else if (!w.getType().isSubject()) {
			logger.warn("subject name already registered with incompatible expression type: " + w.getNormalizedWithTypeString());
		}
	}

	/**
	 * Remove a name from the conversation parser word list.
	 * 
	 * @param name
	 */
	public void unregisterSubjectName(String name) {
		String key = name.toLowerCase();
		WordEntry w = words.get(key);

		if (w!=null && w.getTypeString().equals(SUBJECT_NAME_DYNAMIC)) {
			words.remove(key);
		}
	}

	/**
	 * Add a new word to the list in order to remember it later. 
	 *
	 * @param s
	 */
	public WordEntry add(String s) {
		String key = s.toLowerCase();
		WordEntry entry = words.get(key);

		if (entry == null) {
			entry = new WordEntry();

			entry.setNormalized(key);
			words.put(key, entry);
		} else {
			logger.warn("word already known: " + s + " -> " + entry.getNormalized());
		}

		return entry;
	}

}
