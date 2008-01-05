package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConversationParser returns the parsed sentence in this class. The Sentence
 * class stores the sentence content in a list of parsed and classified
 * expressions composed of the words forming the sentence. Words belonging
 * to each other are merged into common Expression objects.
 * 
 * @author Martin Fuchs
 */
public class Sentence {

	public static final int ST_UNDEFINED = 0;
	public static final int ST_STATEMENT = 1;
	public static final int ST_IMPERATIVE = 2;
	public static final int ST_QUESTION = 3;

	private int sentenceType = ST_UNDEFINED;

	/** Joker String used in pattern matches */
	private static final String JOKER = "*";

	private String error = null;

	List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * Build sentence by using the given parser object.
	 * 
	 * @param parser
	 */
	public void parse(ConversationParser parser) {
		Expression prevWord = null;

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

				Expression word = new Expression(punct.getText());
				expressions.add(word);

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
	private int countExpressions(String typePrefix) {
		int count = 0;

		for (Expression w : expressions) {
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
	public Expression getExpression(int i, String typePrefix) {
		for (Expression w : expressions) {
			if (w.getTypeString().startsWith(typePrefix)) {
				if (i == 0) {
					return w;
				}

				--i;
			}
		}

		return null;
	}

	private Expression triggerCache = null;

	/**
	 * Return trigger Expression for the FSM engine.
	 * TODO mf - replace by sentence matching.
	 * 
	 * @return trigger string
	 */
	public Expression getTriggerExpression() {
		if (triggerCache != null) {
			return triggerCache;
		}

		Expression trigger = Expression.emptyExpression;

		Iterator<Expression> it = expressions.iterator();

		while (it.hasNext()) {
			Expression word = it.next();

			if (word.getType() == null || !word.getType().isIgnore()) {
				trigger = word;
				break;
			}
		}

		triggerCache = trigger;

		return trigger;
	}

	/**
	 * Return the number of Expression objects of type "VER" in the sentence.
	 * 
	 * @return number of subjects
	 */
	public int getVerbCount() {
		return countExpressions(ExpressionType.VERB);
	}

	/**
	 * Return verb [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Expression getVerb(int i) {
		return getExpression(i, ExpressionType.VERB);
	}

	/**
	 * Return verb as Expression object for the special case of
	 * sentences with only one verb.
	 * 
	 * @return normalized verb string
	 */
	public Expression getVerb() {
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
		return countExpressions(ExpressionType.SUBJECT);
	}

	/**
	 * Return subject [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Expression getSubject(int i) {
		return getExpression(i, ExpressionType.SUBJECT);
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
		return countExpressions(ExpressionType.OBJECT);
	}

	/**
	 * Return object [i] of the parsed sentence (e.g. item to be bought).
	 * 
	 * @return object
	 */
	public Expression getObject(int i) {
		return getExpression(i, ExpressionType.OBJECT);
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
	 * Return object name [i] of the parsed sentence.
	 * 
	 * @return normalized object name
	 */
	public String getObjectName(int i) {
		return getObject(i).getNormalized();
	}

	/**
	 * Return item name derived (by replacing spaces by underscores) from the
	 * object of the parsed sentence.
	 *
	 * @deprecated use getObjectName() to get rid of underscore handling for item names.
	 *
	 * @return item name
	 */
	@Deprecated
	public String getItemName(int i) {
		return getObjectName(i);
//		// concatenate user specified item names like "baby dragon"
//		// with underscores to build the internal item names
//		Expression object = getObject(i);
//
//		if (object != null) {
//			// Here we use 'original' instead of 'normalized'
//			// to handle item names concatenated by underscores.
//			return object.getOriginal().toLowerCase().replace(' ', '_');
//		} else {
//			return null;
//		}
	}

	/**
	 * Special case for sentences with only one item.
	 *
	 * @deprecated use getObjectName() to get rid of underscore handling for item names.
	 * 
	 * @return normalized item name
	 */
	@Deprecated
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
		return countExpressions(ExpressionType.PREPOSITION);
	}

	/**
	 * Return the preposition [i] of the parsed sentence.
	 * 
	 * @return object
	 */
	public Expression getPreposition(int i) {
		return getExpression(i, ExpressionType.PREPOSITION);
	}

	/**
	 * Return true if the sentence is empty.
	 * 
	 * @return empty flag
	 */
	public boolean isEmpty() {
		return sentenceType == ST_UNDEFINED && expressions.isEmpty();
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
	 * Return error message string.
	 * 
	 * @return error string
	 */
	public String getErrorString() {
		return error;
	}

	/**
	 * Set error message string.
	 *
	 * @param error
	 */
	protected void setError(String error) {
		this.error = error;
	}

	/**
	 * Return the complete text of the sentence with unchanged case, but with
	 * trimmed white space.
	 * 
	 * TODO mf - There should be only as less code places as possible to rely
	 * on this method.
	 * 
	 * @return string
	 */
	public String getOriginalText() {
		SentenceBuilder builder = new SentenceBuilder();

		for (Expression w : expressions) {
			builder.append(w.getOriginal());
		}

		appendPunctation(builder);

		return builder.toString();
	}

	/**
	 * Return the sentence with all words normalized.
	 * 
	 * @return string
	 */
	public String getNormalized() {
		SentenceBuilder builder = new SentenceBuilder();

		for (Expression w : expressions) {
			if (w.getType() == null || !w.getType().isIgnore()) {
				builder.append(w.getNormalized());
			}
		}

		appendPunctation(builder);

		return builder.toString();
	}

	/**
	 * Return the full sentence as lower case string
	 * including type specifiers.
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		SentenceBuilder builder = new SentenceBuilder();

		for (Expression w : expressions) {
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

		appendPunctation(builder);

		return builder.toString();
	}

	/**
	 * Return the trailing punctuation depending on the sentence type.
	 *
	 * @return
	 */
	public void appendPunctation(SentenceBuilder builder) {
		if (sentenceType == ST_STATEMENT) {
			builder.append('.');
		} else if (sentenceType == ST_IMPERATIVE) {
			builder.append('!');
		} else if (sentenceType == ST_QUESTION) {
			builder.append('?');
		}
    }

	/**
	 * Classify word types and normalize words.
	 * 
	 * @param parser
	 */
	public void classifyWords(ConversationParser parser, boolean forMatching) {
		WordList wl = WordList.getInstance();

		for (Expression w : expressions) {
			String original = w.getOriginal();
			WordEntry entry = null;

			// If the parsed Sentence will be used for matching, look for ExpressionType specifiers. 
			if (forMatching) {
				if (ExpressionType.isTypeString(original)) {
					w.setType(new ExpressionType(original));
					w.setNormalized(JOKER);
				}
			}

			if (w.getType() == null) {
				entry = wl.find(original);
			}

			if (entry != null && entry.getType() != null) {
				w.setType(entry.getType());

				if (entry.getType().isNumeral()) {
					// evaluate numeric expressions
					w.setAmount(entry.getValue());
					w.setNormalized(Integer.toString(w.getAmount()));
				} else if (entry.getType().isPlural()) {
					// normalize to the singular form
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
				// recognize declined verbs, e.g. "swimming"
				WordEntry verb = wl.normalizeVerb(original);

				if (verb != null) {
					if (Grammar.isGerund(original)) {
						w.setType(new ExpressionType(verb.getTypeString() + ExpressionType.SUFFIX_GERUND));
					} else {
						w.setType(verb.getType());
					}

					w.setNormalized(verb.getNormalized());
				} else {
					// recognize derived adjectives, e.g. "magical" or "nomadic"
					WordEntry adjective = wl.normalizeAdjective(original);

					if (adjective != null) {
						if (Grammar.isDerivedAdjective(original)) {
							w.setType(new ExpressionType(ExpressionType.ADJECTIVE));
						} else {
							// If normalizeAdjective() changed the word, it should be a derived adjective.
							assert false;
							w.setType(adjective.getType());
						}

						w.setNormalized(adjective.getNormalized());
					} else {
    					w.setType(new ExpressionType(""));
    					w.setNormalized(original.toLowerCase());

    					if (entry == null) {
        					// add the unknown word to the word list
        					wl.addNewWord(original);
    					}
					}
				}
			}
		}
	}

	/**
	 * Standardize sentence type.
	 */
	public void standardizeSentenceType() {
		// Look for a "me" without any preceding other subject.
		Expression prevVerb = null;

		for (Expression w : expressions) {
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
							Expression you = new Expression("you", ExpressionType.SUBJECT);
							expressions.add(0, you);
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
	 * TODO mf - This grammatical aliasing is only a first step to more flexible
	 * NPC conversation. It should be integrated with the FSM engine so that quest
	 * writers can specify the conversation syntax on their own.
	 */
	public void performaAliasing() {
		Expression verb = getVerb();

		if (verb != null) {
			Expression subject1 = getSubject(0);
			Expression subject2 = getSubject(1);

			// "[you] give me(i)" -> "[I] buy"
			// Note: The second subject "me" is replaced by "i" in the WordList
			// normalization.
			if (subject1 != null 
					&& subject2 != null 
					&& subject1.getNormalized().equals("you") 
					&& verb.getNormalized().equals("give") 
					&& subject2.getNormalized().equals("i")) {
				// remove the subjects and replace the verb with "buy" as first word
				expressions.remove(subject1);
				expressions.remove(subject2);
				verb.setNormalized("buy");
				sentenceType = ST_IMPERATIVE;
				return;
			}

			// "[SUBJECT] (would like to have)" -> ""[SUBJECT] buy"
			if (verb.getNormalized().equals("have")
					&& verb.getOriginal().contains("like")
					&& ((subject1 == null && expressions.get(0) == verb) ||
						(expressions.get(0) == subject1 && expressions.get(1) == verb))) {
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
		Iterator<Expression> it = expressions.iterator();
		int type = ST_UNDEFINED;

		if (it.hasNext()) {
			Expression first = it.next();

			while (first.getType() != null && first.getType().isQuestion() && it.hasNext()) {
				first = it.next();

				if (type == ST_UNDEFINED) {
					type = ST_QUESTION;
				}
			}

			Expression second = null;
			Expression third = null;

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

					expressions.remove(first);
				}
				// statements beginning with "it is <VER-GER>"
				else if (first.getNormalized().equals("it") 
						&&	second.getNormalized().equals("is") 
						&& (third != null && third.getType() != null && third.getType().isGerund())) {
					if (type == ST_UNDEFINED) {
						type = ST_STATEMENT;
					}

					expressions.remove(first);
					expressions.remove(second);
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
			Iterator<Expression> it = expressions.iterator();

			changed = false;

			if (it.hasNext()) {
				Expression next = it.next();

				// loop over all words of the sentence starting from left
				while (it.hasNext()) {
					// Now look at two consecutive words.
					Expression curr = next;
					next = it.next();

					// don't merge if the break flag is set
					if (curr.getBreakFlag()) {
						continue;
					}

					ExpressionType curType = curr.getType();
					ExpressionType nextType = next.getType();

					if (curType != null && nextType != null) {
						// check the expression types for concrete subject or object names (no pronouns)
						boolean currIsName = curType.isObject() || (curType.isSubject() && !curType.isPronoun());
						boolean nextIsName = nextType.isObject() || (nextType.isSubject() && !nextType.isPronoun());

    					// left-merge nouns with preceding adjectives and composite nouns
    					if ((curType.isAdjective() || currIsName) && nextIsName) {
    						// special case for "ice cream" -> "ice"
    						if (curr.getNormalized().equals("ice") && next.getNormalized().equals("cream")) {
    							curr.mergeRight(next, true);
    							expressions.remove(next);
    						} else {
    							next.mergeLeft(curr, true);
    							expressions.remove(curr);
    						}
    						changed = true;
    						break;
    					}
    					// left-merge nouns with preceding amounts, dropping the numerals from the
    					// merged normalized expression
    					else if (curType.isNumeral() 
    							&& (nextType.isObject() || nextType.isSubject())) {
							next.mergeLeft(curr, false);
							expressions.remove(curr);
    						changed = true;
    						break;
    					}
    					// right-merge consecutive verbs, preserving only the main verb
    					else if (curType.isVerb() && nextType.isVerb()) {
    						// handle "would like"
    						if (curType.isConditional()) {
    							next.mergeLeft(curr, false);
    							expressions.remove(curr);
    						} else {
    							curr.mergeRight(next, false);
    							expressions.remove(next);
    						}
    						changed = true;
    						break;
    					}
    					// right-merge consecutive words of all other same main types,
    					// while merging the normalized expressions
    					else if (curType.getMainType().equals(nextType.getMainType())) {
							curr.mergeRight(next, true);
							expressions.remove(next);
    						changed = true;
    						break;
    					}
    					// left-merge question words with following verbs and adjectives,
    					// dropping question words from normalized form
    					else if (curType.isQuestion() 
    							&& (nextType.isVerb() || nextType.isAdjective())) {
    						next.mergeLeft(curr, false);
    						expressions.remove(curr);
    						changed = true;
    						break;
    					}
					}

					// left-merge words to ignore
					if (curType != null && curType.isIgnore()) {
						next.mergeLeft(curr, false);
						expressions.remove(curr);
						changed = true;
						break;
					}
				}
			}
		} while(changed);
	}

	private void mergeThreeWordExpressions() {
		boolean changed;

		// loop until no more simplification can be made
		do {
			Iterator<Expression> it = expressions.iterator();

			changed = false;

			if (it.hasNext()) {
				Expression third = it.next();

				if (it.hasNext()) {
					Expression first = null;
					Expression second = third;
					third = it.next();

					// loop over all words of the sentence starting from left
					while (it.hasNext()) {
						// Now look at three consecutive words.
						first = second;
						second = third;
						third = it.next();

						// don't merge if the break flag is set
						if (first.getBreakFlag() || second.getBreakFlag()) {
							continue;
						}

						// merge "... of ..." expressions into one expression, preserving
						// only the main word als merged normalized expression
						if (first.isObject() 
								&&	second.getNormalized().equals("of") 
								&&	third.isObject()) {
							String expr = first.getNormalized() + " of " + third.getNormalized();
							String normalizedExpr = Grammar.extractNoun(expr);

							// see if the expression has been normalized
							if (normalizedExpr != expr) {
								first.mergeRight(second, false);
								expressions.remove(second);
								third.mergeLeft(first, false);
								expressions.remove(first);
								changed = true;
								break;
							}
						}
					}
				}
			}
		} while(changed);
	}

	/**
	 * Check if two Sentences consist of identical normalized Expressions.
	 *
	 * @param other
	 * @return
	 */
	public boolean equalsNormalized(Sentence other) {
		// shortcut for sentences with differing lengths
	    if (expressions.size() != other.expressions.size()) {
	    	return false;
	    }

	    // loop over all expressions and compare both sides
	    Iterator<Expression> it1 = expressions.iterator();
	    Iterator<Expression> it2 = other.expressions.iterator();

		while (it1.hasNext() && it2.hasNext()) {
			Expression e1 = it1.next();
			Expression e2 = it2.next();

			if (!e1.matchesNormalized(e2)) {
				return false;
			}
		}

		// Now there should be no more expressions at both sides.
		if (!it1.hasNext() || it2.hasNext()) {
			return true;
		} else {
			return false;
		}
    }

	/**
	 * Check if the Sentence matches the given String.
	 * The match Sentence can contain explicit expressions, which
	 * are compared after normalizing, or ExpressionType specifiers
	 * like "VER" or "SUB*" in upper case.
	 *
	 * @param text
	 * @return
	 */
	public boolean matchesNormalized(String text) {
		return matches(ConversationParser.parseForMatching(text));
	}

	/**
	 * Check if the Sentence matches the given Sentence.
	 * The match Sentence can contain explicit expressions, which
	 * are compared after normalizing, or ExpressionType specifiers
	 * like "VER" or "SUB*" in upper case.
	 *
	 * @param other
	 * @return
	 */
	public boolean matches(Sentence other) {
		// shortcut for sentences with differing lengths
	    if (expressions.size() != other.expressions.size()) {
	    	return false;
	    }

	    // loop over all expressions and match them between both sides
	    Iterator<Expression> it1 = expressions.iterator();
	    Iterator<Expression> it2 = other.expressions.iterator();

		while (it1.hasNext() && it2.hasNext()) {
			Expression e1 = it1.next();
			Expression e2 = it2.next();
			String matchString = e2.getNormalized();

			if (matchString.contains(JOKER)) {
				if (matchString.equals(JOKER)) {
					// Type string matching is identified by a single "*" as normalized string expression.
					if (!matchesJokerString(e1.getTypeString(), e2.getTypeString())) {
						return false;
					}
				} else {
					// Look for a normalized string match towards the string containing a joker character.
					if (!matchesJokerString(e1.getNormalizedMatchString(), matchString)) {
						return false;
					}
				}
			} else if (!e1.matchesNormalized(e2)) {
				return false;
			}
		}

		// Now there should be no more expressions at both sides.
		if (!it1.hasNext() || it2.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Match the given String towards a pattern String containing JOKER characters.
	 *
	 * @param str
	 * @param matchString
	 * @return
	 */
	private boolean matchesJokerString(String str, String matchString) {
	    return str.matches(".*" + matchString.replace(JOKER, ".*") + ".*");
    }

}
