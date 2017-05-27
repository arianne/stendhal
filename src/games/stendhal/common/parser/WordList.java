/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import marauroa.common.Log4J;
import marauroa.common.io.UnicodeSupportingInputStreamReader;

/**
 * WordList stores a list of words recognized by the ConversationParser. Words
 * are categorised by type (noun, verb, adjective, preposition) and optionally
 * sub-types (animals, food, fluids, ...).
 *
 * @author Martin Fuchs
 */

final public class WordList {

	private static final Logger logger = Logger.getLogger(WordList.class);

	// ExpressionTypes for dynamic registration
	public static final String SUBJECT_NAME_DYNAMIC = ExpressionType.SUBJECT_NAME
			+ ExpressionType.SUFFIX_DYNAMIC;

	public static final String VERB_DYNAMIC = ExpressionType.VERB
			+ ExpressionType.SUFFIX_DYNAMIC;

	public static final String WORDS_FILENAME = "words.txt";

	private Map<String, WordEntry> words = new TreeMap<String, WordEntry>();

	private Map<String, Set<CompoundName>> compoundNames = new HashMap<String, Set<CompoundName>>();

	// We keep house holding the usage of registered subject names (see registerSubjectName).
	private Map<String, Integer> subjectRefCount = new HashMap<String, Integer>();

	/** instance variable with package protection because of FindBugs hint */
	static private WordList instance;

	// Initialise the word list by reading from the
	// input file "words.txt" in the class path.
	static {
		Log4J.init();

		initInstance();
	}

	/**
	 * Initialises the WordList instance.
	 */
	private static void initInstance() {
		// read word list from "words.txt"
		instance = new WordList();

		instance.readFromResources();
	}

	/**
	 * Reads the word list from the resource file "words.txt".
	 */
	private void readFromResources() {

		final InputStream str = WordList.class.getResourceAsStream(WORDS_FILENAME);

        if (str != null) {
    		try {
    			final BufferedReader reader = new BufferedReader(new UnicodeSupportingInputStreamReader(str, "UTF-8"));

    			try {
    				read(reader, null);
    			} catch (final IOException e) {
    				logger.error("error while reading resource file '"+WORDS_FILENAME+"'", e);
    			} finally {
    				try {
    					reader.close();
    				} catch (IOException e) {
    					logger.error("error while closing reader stream for '"+WORDS_FILENAME+"'", e);
    				}
    			}
			} finally {
    			try {
    				str.close();
    			} catch (IOException e) {
    				logger.warn("exception on closing resource stream", e);
    			}
    		}
        } else {
            logger.error("unable to locate resource file '"+WORDS_FILENAME+"'");
        }
	}

	/**
	 * Returns a reference to the global word list instance.
	 *
	 * @return WordList
	 */
	public static WordList getInstance() {
		return instance;
	}

	/**
	 * Reads word list from reader object.
	 *
	 * @param reader
	 * @param comments
	 * @throws IOException
	 */
	public void read(final BufferedReader reader, final List<String> comments) throws IOException {
		while (true) {
			final String line = reader.readLine();
			if (line == null) {
				break;
			}

			if (line.startsWith("#")) {
				if (comments != null) {
					comments.add(line);
				}
			} else {
				final StringTokenizer tk = new StringTokenizer(line);

				if (!tk.hasMoreTokens()) {
					continue;
				}

				String key = tk.nextToken();

				key = trimWord(key);
				final WordEntry entry = new WordEntry();
				entry.setNormalized(key);

				readEntryLine(key, tk, entry);
				addEntry(key, entry);
			}
		}

		// calculate the hash value from all word entries
//		calculateHash();
	}

	/**
	 * Reads one line of the word list and adds the new entry.
	 *
	 * @param key
	 *
	 * @param tk
	 * @param entry
	 */
	private void readEntryLine(final String key, final StringTokenizer tk,
			final WordEntry entry) {
		if (tk.hasMoreTokens()) {
			entry.setType(new ExpressionType(tk.nextToken()));

			if (tk.hasMoreTokens()) {
				String s = tk.nextToken();

				if (s.charAt(0) == '=') {
					entry.setNormalized(trimWord(s.substring(1)));
					if (tk.hasMoreTokens()) {
						s = tk.nextToken();
					} else {
						s = null;
					}
				}

				if (s != null) {
					if (entry.isNumeral()) {
						entry.setValue(Integer.valueOf(s));
					} else {
						entry.setPlurSing(s);
					}
				}
			}

			final String normalized = entry.getNormalized();

			if (Character.isLowerCase(entry.getTypeString().charAt(0))) {
				// Type identifiers are always upper case, so a word in
				// lower case must be a plural.
				entry.setType(new ExpressionType(ExpressionType.OBJECT));
				entry.setPlurSing(trimWord(entry.getTypeString()));
			} else if ((entry.getPlurSing() == null)
					&& entry.isObject()) {
				// complete missing plural expressions using the
				// Grammar.plural() function
				final String plural = Grammar.plural(normalized);

				// only store single word plurals
				if (plural.indexOf(' ') == -1) {
					entry.setPlurSing(plural);
				}
			} else if (entry.getPlurSing() != null) {
				// check plural strings using the Grammar.plural() function
				if (!entry.isPronoun() && !entry.isObsessional() &&
					!normalized.equals("is")) {
					String plural = Grammar.plural(key);

					if ((plural.indexOf(' ') == -1)
							&& !plural.equals(entry.getPlurSing())) {
						// retry with normalized in case it differs from key
						plural = Grammar.plural(normalized);

						if ((plural.indexOf(' ') == -1)
								&& !plural.equals(entry.getPlurSing())) {
							logger.warn(String.format(
									"suspicious plural: %s -> %s (%s?)", key,
									entry.getPlurSing(), plural));
						}
					}
				}
			}

			while (tk.hasMoreTokens()) {
				logger.warn("superfluous trailing word in words.txt: "
						+ tk.nextToken());
			}
		}
	}

	/**
	 * Add an entry to the word list.
	 *
	 * @param key
	 * @param entry
	 */
	private void addEntry(final String key, final WordEntry entry) {
		words.put(trimWord(key), entry);

		// store plural and associate with singular form
		if ((entry.getPlurSing() != null)
				&& !entry.getPlurSing().equals(entry.getNormalized())) {
			final WordEntry pluralEntry = new WordEntry();

			pluralEntry.setNormalized(entry.getPlurSing());
			pluralEntry.setType(new ExpressionType(entry.getTypeString()
					+ ExpressionType.SUFFIX_PLURAL));
			pluralEntry.setPlurSing(entry.getNormalized());
			pluralEntry.setValue(entry.getValue());

			final WordEntry prev = words.put(entry.getPlurSing(), pluralEntry);

			if (prev != null) {
				logger.debug(String.format("ambiguous plural: %s/%s -> %s",
						pluralEntry.getPlurSing(), prev.getPlurSing(),
						entry.getPlurSing()));

				pluralEntry.setPlurSing(null);
				prev.setPlurSing(null);
			}
		}
	}

	/**
	 * Print all words of a given (main-)type.
	 *
	 * @param writer
	 * @param type
	 */
	public void printWordType(final PrintWriter writer, final String type) {
		for (Map.Entry<String, WordEntry> it : words.entrySet()) {
			final WordEntry entry = it.getValue();
			boolean matches;

			if (type == null) {
				// match all entries with empty type specifier
				matches = entry.getType() == null;
			} else {
				// all real (no plural) entries with matching type string
				matches = entry.getTypeString().startsWith(type)
						&& !entry.isPlural();
			}

			if (matches) {
				entry.print(writer, it.getKey());

				writer.println();
			}
		}
	}

	/**
	 * Transform the given word to lower case and trim special characters at
	 * beginning and end to use this normalized form as key in the word list.
	 *
	 * @param word
	 * @return the trimmed word
	 */
	public static String trimWord(final String word) {
		String tempword = word.toLowerCase();

		// Currently we only need to trim "'" characters.
		while (tempword.length() > 0) {
			final char c = tempword.charAt(0);

			if (c == '\'') {
				tempword = tempword.substring(1);
			} else {
				break;
			}
		}

		while (tempword.length() > 0) {
			final char c = tempword.charAt(tempword.length() - 1);

			if (c == '\'') {
				tempword = tempword.substring(0, tempword.length() - 1);
			} else {
				break;
			}
		}

		return tempword;
	}

	/**
	 * Find an entry for a given word.
	 *
	 * @param str
	 * @return WordEntry
	 */
	public WordEntry find(final String str) {
		final WordEntry entry = words.get(trimWord(str));

		return entry;
	}

	/**
	 * Lookup the plural form of the given word from the word list.
	 *
	 * @param word
	 * @return plural string
	 */
	public String plural(final String word) {
		final WordEntry entry = words.get(trimWord(word));

		if (entry != null) {
			if ((entry.getType() != null) && !entry.getType().isPlural()) {
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
	public String singular(final String word) {
		final WordEntry entry = words.get(trimWord(word));

		if (entry != null) {
			if (entry.isPlural()) {
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
	 * Return type for normalizeVerb().
	 */
	static class Verb extends Grammar.Verb {
		public Verb(Grammar.Verb verb, WordEntry entry) {
			super(verb);

			assert entry != null;
			this.entry = entry;
		}

		public WordEntry entry; // is never null
	}

	/**
	 * Try to normalise the given word as verb.
	 *
	 * @param word
	 *
	 * @return Verb object with additional information
	 */
	Verb normalizeVerb(final String word) {
		final String trimmedWord = trimWord(word);

		final Grammar.Verb verb = Grammar.normalizeRegularVerb(trimmedWord);

		if (verb != null) {
			WordEntry entry = words.get(verb.word);

			// try and re-append "e" if it was removed by
			// normalizeRegularVerb()
			if ((entry == null) && trimmedWord.endsWith("e")
					&& !verb.word.endsWith("e")) {
				entry = words.get(verb.word + "e");
			}

			if (entry != null) {
				return new Verb(verb, entry);
			}
		}

		return null;
	}

	/**
	 * Try to find a matching word for a derived adjective.
	 *
	 * @param word
	 * @return WordEntry
	 */
	WordEntry normalizeAdjective(final String word) {
		final String trimmedWord = trimWord(word);

		final String normalized = Grammar.normalizeDerivedAdjective(trimmedWord);

		if (normalized != null) {
			final WordEntry entry = words.get(normalized);

			return entry;
		} else {
			return null;
		}
	}

	/**
	 * Register a subject name to be recognized by the conversation parser.
	 *
	 * @param name
	 */
	public void registerSubjectName(final String name) {
		registerSubjectName(name, ExpressionType.SUBJECT_NAME);
	}

	/**
	 * Register a subject name to be recognized by the conversation parser.
	 *
	 * @param name
	 * @param typeString
	 */
	public void registerSubjectName(final String name, final String typeString) {
		final String key = trimWord(name);

		Integer usageCount = subjectRefCount.get(key);
		if ((usageCount != null) && (usageCount > 0)) {
			// For already known names, we only have to increment the
			// usage counter.
			subjectRefCount.put(key, ++usageCount);
			return;
		}

		// register the new subject name
		if (usageCount == null) {
			registerName(name, typeString);
			subjectRefCount.put(key, 1);
		}
	}

	/**
	 * De-register a subject name.
	 *
	 * @param name
	 */
	public void unregisterSubjectName(final String name) {
		final String key = trimWord(name);
		final WordEntry entry = words.get(key);

		if (entry != null && entry.isName() && entry.isDynamic()) {
			Integer usageCount = subjectRefCount.get(key);

			if (usageCount != null) {
				// decrement the usage counter
				subjectRefCount.put(key, --usageCount);

				if (usageCount == 0) {
					subjectRefCount.remove(key);
					unregisterName(name);
				}
			}
		}
	}

	/**
	 * Register an item or creature name to be recognized by the conversation
	 * parser.
	 *
	 * @param name
	 * @param typeString
	 */
	public void registerName(final String name, final String typeString) {
		// parse item name without merging Expression entries
		final ConversationContext ctx = new ConversationContext();
		ctx.setMergeExpressions(false);
		final Sentence parsed = ConversationParser.parse(name, ctx);

		Expression lastExpr = null;
		boolean prepositionSeen = false;

		for (final Expression expr : parsed) {
			if ((expr.getType() == null) || expr.getType().isEmpty()) {
				// register the unknown word as new entry
				final WordEntry entry = words.get(expr.getNormalized());

				// set the type to the given one with added "DYN" suffix
				final ExpressionType type = new ExpressionType(typeString
						+ ExpressionType.SUFFIX_DYNAMIC);
				entry.setType(type);
				expr.setType(type);
			} else if (expr.isQuestion()) {
				logger.warn("name already registered with incompatible expression type while registering name '"
						+ name + "': " + expr.getNormalizedWithTypeString()
						+ " expected type: " + typeString);
			}

			if (expr.isPreposition()) {
				prepositionSeen = true;
			} else if (!prepositionSeen) {
				lastExpr = expr;
			}
		}

		if (lastExpr != null) {
			if (!isNameCompatibleLastType(lastExpr, typeString)) {
				if (typeString.startsWith(ExpressionType.SUBJECT)) {
					// ignore suspicious NPC names for now
				} else {
					logger.warn("last word of name '" + name
						+ "' has an unexpected type: "
						+ lastExpr.getNormalizedWithTypeString()
						+ " expected type: " + typeString);
				}
			}
		}

		// register compound item and subject names to use them when merging expressions
		if (parsed.getExpressions().size() > 1) {
			Expression firstExpr = parsed.expressions.get(0);
			String firstWord = firstExpr.getOriginal().toLowerCase();

			Set<CompoundName> nameSet = compoundNames.get(firstWord);

			if (nameSet == null) {
				nameSet = new HashSet<CompoundName>();
				compoundNames.put(firstWord, nameSet);
			}

			nameSet.add(new CompoundName(parsed, typeString));
		}
	}

	/**
	 * Search for compound names.
	 * @param expressions list of expressions
	 * @param idx start index of the expression list
	 * @return compound name or null
	 */
	public CompoundName searchCompoundName(AbstractList<Expression> expressions, int idx) {
        Expression first = expressions.get(idx);

    	Set<CompoundName> candidates = compoundNames.get(first.getOriginal().toLowerCase());

		if (candidates != null) {
	    	TreeSet<CompoundName> candidatesSortedFromLongestToShortest = new TreeSet<CompoundName>(new ArrayLengthDescSorter<CompoundName>());
	    	candidatesSortedFromLongestToShortest.addAll(candidates);
    		for (CompoundName compName : candidatesSortedFromLongestToShortest) {
    			if (compName.matches(expressions, idx)) {
    				return compName;
    			}
    		}
		}

		return null;
	}

	/**
	 * De-register a name after all references have been removed.
	 * @param name
	 */
	private void unregisterName(final String name) {
		// parse item name without merging Expression entries
		final ConversationContext ctx = new ConversationContext();
		ctx.setMergeExpressions(false);
		final Sentence parsed = ConversationParser.parse(name, ctx);

		// remove compound names
		if (parsed.expressions.size() > 1) {
			Expression firstExpr = parsed.expressions.get(0);
			String firstWord = firstExpr.getOriginal().toLowerCase();

			Set<CompoundName> nameSet = compoundNames.get(firstWord);

			if (nameSet != null) {
				for(CompoundName compName : nameSet) {
					if (compName.matches(parsed.expressions, 0)) {
						nameSet.remove(compName);

						if (nameSet.isEmpty()) {
							compoundNames.remove(firstWord);
						}

						break;
					}
				}
			}
		}

		for(Expression expr : parsed.expressions) {
			if (expr.isDynamic()) {
				words.remove(expr.getNormalized());
			}
		}
	}

	/**
	 * Check for compatible types.
	 *
	 * @param lastExpr last word in an expression
	 * @param typeString expected type string
	 * @return <code>true</code> if the expression is of compatible type,
	 * 	otherwise <code>false</code>
	 */
	private static boolean isNameCompatibleLastType(
			final Expression lastExpr, final String typeString) {
		final ExpressionType lastType = lastExpr.getType();

		if (lastType.getTypeString().startsWith(typeString)) {
			return true;
		}

		if (typeString.startsWith(lastType.getTypeString())) {
			return true;
		}

		if (lastType.isNumeral()) {
			return true;
		}

		if (lastType.isDynamic()) {
			return true;
		}

		// Ignore words like "chicken", "cat" and "incorporeal armor", which are
		// registered as objects, but also used as subjects.
		if (lastType.isObject() && typeString.startsWith(ExpressionType.SUBJECT)) {
			return true;
		}
		if (lastType.isSubject() && typeString.startsWith(ExpressionType.OBJECT)) {
			return true;
		}

		// handle ambiguous cases like "mill"
		if (Grammar.isAmbiguousNounVerb(lastExpr.getNormalized())) {
			if (lastType.isVerb() && typeString.equals(ExpressionType.OBJECT)) {
				return true;
			}
			if (lastType.isObject() && typeString.equals(ExpressionType.VERB)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Register a verb to be recognized by the conversation parser.
	 *
	 * @param verb
	 */
	public void registerVerb(final String verb) {
		final String key = trimWord(verb);
		final WordEntry entry = words.get(key);

		if ((entry == null) || (entry.getType() == null)
				|| entry.getType().isEmpty()) {
			final WordEntry newEntry = new WordEntry();

			newEntry.setNormalized(key);
			newEntry.setType(new ExpressionType(VERB_DYNAMIC));

			words.put(key, newEntry);
//		} else if (!checkNameCompatibleLastType(entry, ExpressionType.VERB)) {
//	 		logger.warn("verb name already registered with incompatible expression type: " +
//			entry.getNormalizedWithTypeString());
		}
	}

	/**
	 * Add a new word to the list in order to remember it later.
	 *
	 * @param str
	 * @return the added entry
	 */
	public WordEntry addNewWord(final String str) {
		final String key = trimWord(str);
		WordEntry entry = words.get(key);

		if (entry == null) {
			entry = new WordEntry();
			entry.setType(new ExpressionType(""));

			// add the new entry
			entry.setNormalized(key);
			words.put(key, entry);
		} else {
			logger.warn("word already known: " + str + " -> "
					+ entry.getNormalized());
		}

		return entry;
	}

	/**
	 * Return number of word entries.
	 * @return number of entries
	 */
	public int getWordCount() {
		return words.size();
	}
}
