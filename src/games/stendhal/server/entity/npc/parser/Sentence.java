package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * ConversationParser returns the parsed sentence in this class. The Sentence
 * class stores the sentence content in a list of parsed and classified words.
 * Words belonging to each other are merged into common Word objects.
 * 
 * @author Martin Fuchs
 */
public class Sentence {

	private static final Logger logger = Logger.getLogger(Sentence.class);

	public static final int ST_UNDEFINED = 0;
	public static final int ST_STATEMENT = 1;
	public static final int ST_IMPERATIVE = 2;
	public static final int ST_QUESTION = 3;

	private int sentenceType = ST_UNDEFINED;

	private String error = null;

	List<Word> words = new ArrayList<Word>();

	/**
	 * Build sentence by using the given parser object.
	 * 
	 * @param parser
	 */
	public void parse(ConversationParser parser) {
		Word prevWord = null;

		for (String ws; (ws = parser.readNextWord()) != null;) {
			// replace "and" by enumerations separated by break flags
			if (ws.equals("and")) {
				if (prevWord != null) {
					prevWord.setBreakFlag();
				}
			} else {
				PunctuationParser punct = new PunctuationParser(ws);

				// handle preceding comma characters
				if (punct.getPrecedingPunctuation().contains(",")) {
					if (prevWord != null) {
						// break sentence after the previous word
						prevWord.setBreakFlag();
					}
				}

				Word word = new Word(punct.getText());
				words.add(word);

				// handle trailing comma characters
				if (punct.getTrailingPunctuation().contains(",")) {
					// break sentence after the current word
					word.setBreakFlag();
				}

				prevWord = word;
			}
		}
	}

	/**
	 * Set sentence type as ST_STATEMENT, ST_IMPERATIVE or ST_QUESTION.
	 * 
	 * @param type
	 */
	protected void setType(int type) {
		this.sentenceType = type;
	}

	/**
	 * Return sentence type.
	 * 
	 * @return
	 */
	public int getType() {
		return sentenceType;
	}

	/**
	 * Count the number of words matching the given type string.
	 * 
	 * @param typePrefix
	 * @return
	 */
	private int countWords(String typePrefix) {
		int count = 0;

		for(Word w : words) {
			if (w.getTypeString().startsWith(typePrefix)) {
				++count;
			}
		}

		return count;
	}

	/**
	 * Return verb [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Word getWord(int i, String typePrefix) {
		for(Word w : words) {
			if (w.getTypeString().startsWith(typePrefix)) {
				if (i == 0) {
					return w;
				}

				--i;
			}
		}

		return null;
	}

	private Word triggerCache = null;

	/**
	 * Return trigger Word for the FSM engine.
	 * TODO replace by sentence matching.
	 * 
	 * @return trigger string
	 */
	public Word getTriggerWord() {
		if (triggerCache != null) {
			return triggerCache;
		}

		Word trigger = Word.emptyWord;

		Iterator<Word> it = words.iterator();

		while (it.hasNext()) {
			Word word = it.next();

			if (word.getType()==null || !word.getType().isIgnore()) {
				trigger = word;
				break;
			}
		}

		triggerCache = trigger;

		return trigger;
	}

	/**
	 * Return the number of Word objects of type "VERB" in the sentence.
	 * 
	 * @return number of subjects
	 */
	public int getVerbCount() {
		return countWords(WordType.VERB);
	}

	/**
	 * Return verb [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Word getVerb(int i) {
		return getWord(i, WordType.VERB);
	}

	/**
	 * Return verb as Word object for the special case of
	 * sentences with only one verb.
	 * 
	 * @return normalized verb string
	 */
	public Word getVerb() {
		if (getVerbCount() == 1) {
			return getVerb(0);
		} else {
			return null;
		}
	}

	/**
	 * Return verb as String for the special case of
	 * sentences with only one verb.
	 * 
	 * @return normalized verb string
	 */
	public String getVerbString() {
		if (getVerbCount() == 1) {
			return getVerb(0).getNormalized();
		} else {
			return null;
		}
	}

	/**
	 * Return the number of subjects.
	 * 
	 * @return number of subjects
	 */
	public int getSubjectCount() {
		return countWords(WordType.SUBJECT);
	}

	/**
	 * Return subject [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Word getSubject(int i) {
		return getWord(i, WordType.SUBJECT);
	}

	/**
	 * Return the subject as String for the special
	 * case of sentences with only one subject.
	 * 
	 * @return normalized subject string
	 */
	public String getSubjectName() {
		if (getSubjectCount() == 1) {
			return getSubject(0).getNormalized();
		} else {
			return null;
		}
	}

	/**
	 * Return the number of objects.
	 * 
	 * @return number of objects
	 */
	public int getObjectCount() {
		return countWords(WordType.OBJECT);
	}

	/**
	 * Return object [i] of the parsed sentence (e.g. item to be bought).
	 * 
	 * @return object
	 */
	public Word getObject(int i) {
		return getWord(i, WordType.OBJECT);
	}

	/**
	 * Return the object as String for the special
	 * case of sentences with only one object.
	 * 
	 * @return normalized object name
	 */
	public String getObjectName() {
		if (getObjectCount() == 1) {
			return getObject(0).getNormalized();
		} else {
			return null;
		}
	}

	/**
	 * Return item name derived (by replacing spaces by underscores) from the
	 * object of the parsed sentence.
	 * TODO get rid of underscore handling for item names.
	 * 
	 * @return item name
	 */
	public String getItemName(int i) {
		// concatenate user specified item names like "baby dragon"
		// with underscores to build the internal item names
		Word object = getObject(i);

		if (object != null) {
			// Here we use 'original' instead of 'normalized'
			// to handle item names concatenated by underscores.
			return object.getOriginal().toLowerCase().replace(' ', '_');
		} else {
			return null;
		}
	}

	/**
	 * Special case for sentences with only one item.
	 * 
	 * @return normalized item name
	 */
	public String getItemName() {
		if (getObjectCount() == 1) {
			return getItemName(0);
		} else {
			return null;
		}
	}

	/**
	 * Return the number of prepositions.
	 * 
	 * @return number of objects
	 */
	public int getPrepositionCount() {
		return countWords(WordType.PREPOSITION);
	}

	/**
	 * Return the preposition [i] of the parsed sentence.
	 * 
	 * @return object
	 */
	public Word getPreposition(int i) {
		return getWord(i, WordType.PREPOSITION);
	}

	/**
	 * Return if some error occurred while parsing the input text.
	 * 
	 * @return error flag
	 */
	public boolean hasError() {
		return error != null;
	}

	/**
	 * Return error message.
	 * 
	 * @return error string
	 */
	public String getError() {
		return error;
	}

	/**
	 * Return true if the sentence is empty.
	 * 
	 * @return empty flag
	 */
	public boolean isEmpty() {
		return sentenceType == ST_UNDEFINED && words.isEmpty();
	}

	protected void setError(String error) {
		this.error = error;
	}

	/**
	 * Return the complete text of the sentence with unchanged case, but with
	 * trimmed white space.
	 * 
	 * TODO There should be only as less code places as possible to rely on this
	 * method.
	 * 
	 * @return string
	 */
	public String getOriginalText() {
		SentenceBuilder builder = new SentenceBuilder();

		for (Word w : words) {
			builder.append(w.getOriginal());
		}

		return builder.toString();
	}

	/**
	 * Return the sentence with all words normalized.
	 * 
	 * @return string
	 */
	public String getNormalized() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			if (w.getType()==null || !w.getType().isIgnore()) {
				builder.append(w.getNormalized());
			}
		}

		return builder.toString();
	}

	/**
	 * Return the full sentence as lower case string.
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		SentenceBuilder builder = new SentenceBuilder();

		for (Word w : words) {
			if (w.getType() != null) {
				if (!w.getType().isIgnore()) {
					builder.append(w.getNormalizedWithTypeString());
				}
			} else {
				builder.append(w.getOriginal());
			}

			if (w.getBreakFlag()) {
				builder.append(',');
			}
		}

		if (sentenceType == ST_STATEMENT) {
			builder.append(".");
		} else if (sentenceType == ST_IMPERATIVE) {
			builder.append("!");
		} else if (sentenceType == ST_QUESTION) {
			builder.append("?");
		}

		return builder.toString();
	}

	/**
	 * Classify word types and normalize words.
	 * 
	 * @param parser
	 */
	public void classifyWords(ConversationParser parser) {
		WordList wl = WordList.getInstance();

		for (Word w : words) {
			String original = w.getOriginal();

			WordEntry entry = wl.find(original);

			if (entry!=null && entry.getType()!=null) {
				w.setType(entry.getType());

				if (entry.getType().isNumeral()) {
					// evaluate numeric expressions
					w.setAmount(entry.getValue());
					w.setNormalized(Integer.toString(w.getAmount()));
				} else if (entry.getType().isPlural()) {
					// normalise to the singular form
					w.setNormalized(entry.getPlurSing());
				} else {
					w.setNormalized(entry.getNormalized());
				}
			} else {
				// handle numeric expressions
				if (original.matches("^[+-]?[0-9]+")) {
					w.parseAmount(original, parser);
					int amount = w.getAmount();

					if (amount < 0) {
						parser.setError("negative amount: " + amount);
					}
				}
			}

			// handle unknown words
			if (w.getType() == null) {
				// recognize declined verbs
				WordEntry verb = wl.normalizeVerb(original);

				if (verb != null) {
					if (Grammar.isGerund(original)) {
						w.setType(new WordType(verb.getTypeString()+WordType.GERUND));
					} else {
						w.setType(verb.getType());
					}

					w.setNormalized(verb.getNormalized());
				} else {
					w.setType(new WordType(""));
					w.setNormalized(original.toLowerCase());

					// add to the word list to print the warning message only once
					wl.add(original);

					logger.warn("unknown word: " + original);
				}
			}
		}
	}

	/**
	 * Standardize sentence type.
	 */
	public void standardizeSentenceType() {
		// Look for a "me" without any preceding other subject.
		Word prevVerb = null;

		for (Word w : words) {
			if (w.getBreakFlag()) {
				break;
			}

			if (w.getType() != null) {
				if (w.getType().isVerb()) {
					if (prevVerb == null) {
						prevVerb = w;
					} else {
						break;
					}
				} else if (w.getType().isSubject()) {
					if (w.getOriginal().equals("me")) {
						// If we already found a verb, we prepend "you" as
						// first subject and mark the sentence as imperative.
						if (prevVerb != null) {
							Word you = new Word("you", "you", WordType.SUBJECT);
							words.add(0, you);
							sentenceType = ST_IMPERATIVE;
						}
					}

					break;
				}
			}
		}
	}

	/**
	 * replace grammatical constructs with simpler ones with the same meaning,
	 * so that they can be understood by the FSM rules
	 * 
	 * TODO This grammatical aliasing is only a first step to more flexible NPC
	 * conversation. It should be integrated with the FSM engine so that quest
	 * writers can specify the conversation syntax on their own.
	 */
	public void performaAliasing() {
		Word verb = getVerb();

		if (verb != null) {
			Word subject1 = getSubject(0);
			Word subject2 = getSubject(1);

			// [you] give me(i) -> [I] buy
			// Note: The second subject "me" is replaced by "i" in the WordList
			// normalization.
			if (subject1 != null && subject2 != null &&
					subject1.getNormalized().equals("you") &&
					verb.getNormalized().equals("give") &&
					subject2.getNormalized().equals("i")) {
				// remove the subjects and replace the verb with "buy" as first
				// word
				words.remove(subject1);
				words.remove(subject2);
				verb.setNormalized("buy");
				sentenceType = ST_IMPERATIVE;
				return;
			}

			// [SUBJECT] (would like to have) -> [SUBJECT] buy
			if (verb.getNormalized().equals("have")
					&& verb.getOriginal().contains("like")
					&& ((subject1 == null && words.get(0) == verb) ||
						(words.get(0) == subject1 && words.get(1) == verb))) {
				// replace the verb with "buy"
				verb.setNormalized("buy");
				sentenceType = ST_IMPERATIVE;
			}
		}
	}

	/**
	 * Evaluate the sentence type from word order.
	 */
	public int evaluateSentenceType() {
		Iterator<Word> it = words.iterator();
		int type = ST_UNDEFINED;

		if (it.hasNext()) {
			Word first = it.next();

			while(first.getType()!=null && first.getType().isQuestion() &&
					it.hasNext()) {
				first = it.next();

				if (type == ST_UNDEFINED) {
					type = ST_QUESTION;
				}
			}

			Word second = null;
			Word third = null;

			if (it.hasNext()) {
				second = it.next();

				if (it.hasNext()) {
					third = it.next();
				}
			}

			if (second != null) {
				// questions beginning with "is"/"are"
				if (first.getNormalized().equals("is")) {
					if (type == ST_UNDEFINED) {
						type = ST_QUESTION;
					}
				}
				// questions beginning with "do"
				else if (first.getNormalized().equals("do")) {
					if (type == ST_UNDEFINED) {
						type = ST_QUESTION;
					}

					words.remove(first);
				}
				// statements beginning with "it is <VER-GER>"
				else if (first.getNormalized().equals("it") &&
						second.getNormalized().equals("is") &&
						(third!=null && third.getType()!=null && third.getType().isGerund())) {
					if (type == ST_UNDEFINED) {
						type = ST_STATEMENT;
					}

					words.remove(first);
					words.remove(second);
				}
			}
		}

		if (type != ST_UNDEFINED && sentenceType == ST_UNDEFINED) {
			sentenceType = type;
		}

		return type;
	}

	/**
	 * Merge words to form a simpler sentence structure.
	 */
	public void mergeWords() {

		// first merge three word expressions of the form "... of ..."
		mergeThreeWordExpressions();

		// now merge two word expressions from left to right
		mergeTwoWordExpressions();
	}

	private void mergeTwoWordExpressions() {

		/*
		 * There are two possibilities for word merges: Left-merging means to
		 * prepend the left word before the following one, removing the first
		 * one. Right-merging means to append the eight word to the preceding
		 * one, removing the second from the word list.
		 */

		boolean changed;

		// loop until no more simplification can be made
		do {
			Iterator<Word> it = words.iterator();

			changed = false;

			if (it.hasNext()) {
				Word next = it.next();

				// loop over all words of the sentence starting from left
				while(it.hasNext()) {
					// Now look at two consecutive words.
					Word word = next;
					next = it.next();

					// don't merge if the break flag is set
					if (word.getBreakFlag()) {
						continue;
					}

					WordType curType = word.getType();
					WordType nextType = next.getType();

					if (curType!=null && nextType!=null) {
    					// left-merge nouns with preceding adjectives or amounts and composite nouns
    					if ((curType.isAdjective() || curType.isNumeral() || curType.isObject()) &&
    						(nextType.isObject() || nextType.isSubject())) {
    						// special case for "ice cream" -> "ice"
    						if (word.getNormalized().equals("ice") && next.getNormalized().equals("cream")) {
    							word.mergeRight(next);
    							words.remove(next);
    						} else {
    							next.mergeLeft(word);
    							words.remove(word);
    						}
    						changed = true;
    						break;
    					}
    					// right-merge consecutive words of the same main type
    					else if (curType.getMainType().equals(nextType.getMainType())) {
    						// handle "would like"
    						if (curType.isConditional()) {
    							next.mergeLeft(word);
    							words.remove(word);
    						} else {
    							word.mergeRight(next);
    							words.remove(next);
    						}
    						changed = true;
    						break;
    					}
    					// left-merge question words with following verbs and adjectives
    					else if (curType.isQuestion() &&
    							(nextType.isVerb() || nextType.isAdjective())) {
    						next.mergeLeft(word);
    						words.remove(word);
    						changed = true;
    						break;
    					}
					}

					// left-merge words to ignore
					if (curType!=null && curType.isIgnore()) {
						next.mergeLeft(word);
						words.remove(word);
						changed = true;
						break;
					}
				}
			}
		} while (changed);
	}

	private void mergeThreeWordExpressions() {
		boolean changed;

		// loop until no more simplification can be made
		do {
			Iterator<Word> it = words.iterator();

			changed = false;

			if (it.hasNext()) {
				Word third = it.next();

				if (it.hasNext()) {
					Word first = null;
					Word second = third;
					third = it.next();

					// loop over all words of the sentence starting from left
					while(it.hasNext()) {
						// Now look at three consecutive words.
						first = second;
						second = third;
						third = it.next();

						// don't merge if the break flag is set
						if (first.getBreakFlag() || second.getBreakFlag()) {
							continue;
						}

						// merge "... of ..." expressions into one word
						if (first.isObject() &&
								second.getNormalized().equals("of") &&
								third.isObject()) {
							String expr = first.getNormalized() + " of " + third.getNormalized();
							String normalizedExpr = Grammar.extractNoun(expr);

							// see if the expression has been normalized
							if (normalizedExpr != expr) {
								first.mergeRight(second);
								words.remove(second);
								third.mergeLeft(first);
								words.remove(first);
								changed = true;
								break;
							}
						}
					}
				}
			}
		} while (changed);
	}

}
