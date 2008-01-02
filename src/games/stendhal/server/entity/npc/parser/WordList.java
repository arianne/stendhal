package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import marauroa.common.Log4J;
import marauroa.server.game.db.Accessor;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;

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

	private static final String WORDS_FILENAME = "words.txt";

	private final Map<String, WordEntry> words = new TreeMap<String, WordEntry>();

	private static final WordList instance = new WordList();

	// initialize the word list by querying the database or reading from the
	// input file "words.txt" in the class path
	static {
		Log4J.init();

		int ret = instance.readFromDB();

		if (ret <= 0) {
    		InputStream str = WordList.class.getResourceAsStream(WORDS_FILENAME);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(str));

    		try {
    			instance.read(reader, null);
    			reader.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		// If the database is still empty, store the default entries into it.
    		if (ret == 0) {
    			instance.writeToDB();
    		}
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
				key = trimWord(key);
				WordEntry entry = new WordEntry();
				entry.setNormalized(key);

				readEntryLine(tk, entry);
				addEntry(key, entry);
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
					entry.setNormalized(trimWord(s.substring(1)));
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

			// Type identifiers are always upper case, so a word in lower case
			// must be a plural.
			if (Character.isLowerCase(entry.getTypeString().charAt(0))) {
				entry.setType(new ExpressionType(ExpressionType.OBJECT));
				entry.setPlurSing(trimWord(entry.getTypeString()));
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
					logger.warn(String.format("suspicious plural: %s -> %s (%s?)", entry.getNormalized(),
							entry.getPlurSing(), plural));
				}
			}

			while (tk.hasMoreTokens()) {
				logger.warn("superflous trailing word in words.txt: " + tk.nextToken());
			}
		}
	}

	/**
	 * Add one entry to the word list.
	 * @param key
	 * @param entry
	 */
	private void addEntry(String key, WordEntry entry) {
		words.put(trimWord(key), entry);

		// store plural and associate with singular form
		if (entry.getPlurSing() != null && !entry.getPlurSing().equals(entry.getNormalized())) {
			WordEntry pluralEntry = new WordEntry();

			pluralEntry.setNormalized(entry.getPlurSing());
			pluralEntry.setType(new ExpressionType(entry.getTypeString() + ExpressionType.SUFFIX_PLURAL));
			pluralEntry.setPlurSing(entry.getNormalized());
			pluralEntry.setValue(entry.getValue());

			WordEntry prev = words.put(entry.getPlurSing(), pluralEntry);

			if (prev != null) {
				logger.debug(String.format("ambiguous plural: %s/%s -> %s", pluralEntry.getPlurSing(),
						prev.getPlurSing(), entry.getPlurSing()));

				pluralEntry.setPlurSing(null);
				prev.setPlurSing(null);
			}
		}
	}

	/**
	 * The main() function WordList reads the current word list, writes a new
	 * updated, pretty formatted list in the file "words.txt" and updates
	 * the database table "words".
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        try {
        	// read in the current word list including comment lines
    		InputStream str = WordList.class.getResourceAsStream(WORDS_FILENAME);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(str));

    		instance.words.clear();

        	List<String> comments = new ArrayList<String>();
			instance.read(reader, comments);
			reader.close();

	    	// see if we can find the word list source file in the file system
	    	String outputPath = "src/games/stendhal/server/entity/npc/parser/words.txt";

			File file = new File(outputPath);
			if (!file.exists()) {
				// Otherwise just write the output file into the current directory.
				outputPath = WORDS_FILENAME;
			}

        	PrintWriter writer = new PrintWriter(new FileWriter(outputPath));
			instance.write(writer, comments);
			writer.close();

			System.out.println("The updated word list has been written to the file '" + outputPath  +"'.");

			// update database entries
			instance.writeToDB();
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
		for (String key : words.keySet()) {
			WordEntry entry = words.get(key);
			boolean matches;

			if (type == null) {
				// match all entries with empty type specifier
				matches = entry.getType() == null;
			} else {
				// all real (no plural) entries with matching type string
				matches = entry.getTypeString().startsWith(type) && !entry.isPlural();
			}

			if (matches) {
				entry.print(writer, key);

				writer.println();
			}
		}
	}

	/**
	 * Transform the given word to lower case and trim special characters at
	 * beginning and end to use this normalized form as key in the word list.
	 *  
	 * @param word
	 * @return
	 */
	public static String trimWord(String word) {
		word = word.toLowerCase();

		// Currently we only need to trim "'" characters. 
		while(word.length() > 0) {
			char c = word.charAt(0);

			if (c == '\'') {
				word = word.substring(1);
			} else {
				break;
			}
		}

		while(word.length() > 0) {
			char c = word.charAt(word.length()-1);

			if (c == '\'') {
				word = word.substring(0, word.length()-1);
			} else {
				break;
			}
		}

	    return word;
    }

	/**
	 * Find an entry for a given word.
	 * 
	 * @param str
	 * @return Word
	 */
	public WordEntry find(String str) {
		WordEntry entry = words.get(trimWord(str));

		return entry;
	}

	/**
	 * Lookup the plural form of the given word from the word list.
	 * 
	 * @param word
	 * @return plural string
	 */
	public String plural(String word) {
		WordEntry entry = words.get(trimWord(word));

		if (entry != null) {
			if (entry.getType()!=null && !entry.getType().isPlural()) {
				// return the associated singular from the word list
				return entry.getPlurSing();
			} else {
				// The word is already in singular form.
				return entry.getNormalized();
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
		WordEntry entry = words.get(trimWord(word));

		if (entry != null) {
			if (entry.getType()!=null && entry.getType().isPlural()) {
				// return the associated singular from the word list
				return entry.getPlurSing();
			} else {
				// The word is already in singular form.
				return entry.getNormalized();
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
		word = trimWord(word);

		String normalized = Grammar.normalizeRegularVerb(word);

		if (normalized != null) {
			WordEntry entry = words.get(normalized);

			// try and re-append "e" if it was removed by
			// normalizeRegularVerb()
			if (entry == null && word.endsWith("e") && !normalized.endsWith("e")) {
				entry = words.get(normalized + "e");
			}

			return entry;
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
		word = trimWord(word);

		String normalized = Grammar.normalizeDerivedAdjective(word);

		if (normalized != null) {
			WordEntry entry = words.get(normalized);

			return entry;
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
		String key = trimWord(name);
		WordEntry entry = words.get(key);

		if (entry==null || entry.getType()==null) {
			WordEntry newEntry = new WordEntry();

			newEntry.setNormalized(key);
			newEntry.setType(new ExpressionType(SUBJECT_NAME_DYNAMIC));

			words.put(key, newEntry);
		} else if (!entry.getType().isSubject()) {
			logger.warn("subject name already registered with incompatible expression type: " + entry.getNormalizedWithTypeString());
		}
	}

	/**
	 * Remove a name from the conversation parser word list.
	 * 
	 * @param name
	 */
	public void unregisterSubjectName(String name) {
		String key = trimWord(name);
		WordEntry entry = words.get(key);

		if (entry!=null && entry.getTypeString().equals(SUBJECT_NAME_DYNAMIC)) {
			words.remove(key);
		}
	}

	/**
	 * Add a new word to the list in order to remember it later. 
	 *
	 * @param str
	 */
	public WordEntry addNewWord(String str) {
		String key = trimWord(str);
		WordEntry entry = words.get(key);

		if (entry == null) {
			entry = new WordEntry();

			entry.setNormalized(key);
			words.put(key, entry);

			// save the new word into the database
			Set<String> keys = new HashSet<String>();
			keys.add(key);
			insertIntoDB(keys);
		} else {
			logger.warn("word already known: " + str + " -> " + entry.getNormalized());
		}

		return entry;
	}

	/**
	 * Write current word list into the database table "words".
	 */
	public void writeToDB() {
		JDBCDatabase db = JDBCDatabase.getDatabase();
		Transaction trans = db.getTransaction();
		boolean success;

		// empty table "words" 
		Accessor acc = trans.getAccessor();

		try {
			try {
        		acc.execute("truncate table words");
    		} finally {
        		acc.close();
            }

    		success = true;
        } catch(SQLException e) {
    		success = false;
            logger.error("error emptying DB table words", e);
        }

        if (success) {
        	insertIntoDB(words.keySet());
        }
    }

	/**
	 * Store a number of word entries into the database.
	 * 
	 * @param keys
	 * @return success flag
	 */
	private boolean insertIntoDB(Set<String> keys) {
		JDBCDatabase db = JDBCDatabase.getDatabase();
		Transaction trans = db.getTransaction();
		boolean success;

		try {
			success = insertIntoDB(trans, keys);

    		if (success) {
    			trans.commit();
    		}
        } catch(SQLException e) {
	        logger.error("error while inserting new word into DB", e);
	        success = false;
        }

		if (!success) {
			try {
	            trans.rollback();
            } catch(SQLException e) {
    	        logger.error("error while rolling back transaction", e);
            }
		}

        return success;
    }

	/**
	 * Store a number of word entries into the database, using the given
	 * Transaction object.
	 * 
	 * @param trans
	 * @param keys
	 * @return success flag
	 * @throws SQLException
	 */
	private boolean insertIntoDB(Transaction trans, Set<String> keys) throws SQLException {
		Connection conn = trans.getConnection();

		PreparedStatement stmt = conn.prepareStatement(
			"insert into words(normalized, type, plural, value)\n"+
			"values(?, ?, ?, ?)"
		);

		int count = 0;

		try {
			for (String key : keys) {
				WordEntry entry = words.get(key);

				// We ignore all plural entries, they are already present as attribute of the singular form.
				if (entry.getType()==null || !entry.getType().isPlural()) {
        			stmt.setString(1, key);
        			stmt.setString(2, entry.getTypeString());
        			stmt.setString(3, entry.getPlurSing());

        			Integer value = entry.getValue();
        			if (value != null) {
        				stmt.setInt(4, value);
        			} else {
        				stmt.setNull(4, Types.INTEGER);
        			}

        			stmt.execute();

        			ResultSet idRes = stmt.getGeneratedKeys();
        			if (idRes.next()) {
        				entry.setId(idRes.getInt(1));
            			++count;
        			} else {
        				logger.error("missing auto-generated ID for word: " + key);
        			}
        			idRes.close();
				}
			}
		} finally {
			stmt.close();
		}

		stmt = conn.prepareStatement(
			"update words\n"+
			"set alias_id = ?\n"+
			"where id = ?"
		);

		try {
			for (String key : keys) {
				WordEntry entry = words.get(key);
				String normalized = entry.getNormalized();

				// Now we store the alias_id for alias entries.
				if (!normalized.equals(key)) {
					WordEntry alias = words.get(normalized);

					if (alias != null) {
	        			stmt.setInt(1, alias.getId());
	        			stmt.setInt(2, entry.getId());

	        			stmt.execute();
					} else {
						logger.error("word alias not found: " + key + " -> " + normalized);
						return false;
					}
				}
			}

			logger.debug("wrote " + count + " words into database");

			return true;
		} finally {
			stmt.close();
		}
	}

	/**
	 * Read word entries from the database.
	 */
	private int readFromDB() {
		JDBCDatabase db = JDBCDatabase.getDatabase();

		Transaction trans = db.getTransaction();
		Accessor acc = trans.getAccessor();

		try {
	        ResultSet res = acc.query(
        		"select	w.id, w.normalized, w.type, w.plural, w.value,\n"+
        			"	s.normalized\n"+
        		"from	words w\n"+
        		"left outer join words s on s.id = w.alias_id"
	        );

			int count = 0;

	        while(res.next()) {
	        	WordEntry entry = new WordEntry();

	        	entry.setId(res.getInt(1));

	        	String key = res.getString(2);
	        	entry.setNormalized(key);

	        	entry.setType(new ExpressionType(res.getString(3)));

	        	entry.setPlurSing(res.getString(4));

	        	int value = res.getInt(5);
	        	if (!res.wasNull()) {
	        		entry.setValue(value);
	        	}

	        	String singular = res.getString(6);
	        	if (singular != null) {
	        		entry.setNormalized(singular);
	        	}

	        	addEntry(key, entry);
	        	++count;
	        }

			trans.commit();

			logger.debug("read " + count + " word entries from database");

			return count;
        } catch(SQLException e) {
	        logger.error("error while reading from DB table words", e);
	        try {
	            trans.rollback();
            } catch(SQLException e1) {
    	        logger.error("error while rolling back transaction", e);
            }
	        return -1;
        }
	}

}
