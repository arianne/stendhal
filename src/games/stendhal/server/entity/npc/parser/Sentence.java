package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.Grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ConversationParser returns the parsed sentence in this class. The Sentence
 * class stores the sentence content in a list of parsed and classified
 * expressions composed of the words forming the sentence. Words belonging
 * to each other are merged into common Expression objects.
 * 
 * @author Martin Fuchs
 */
public class Sentence {

	public enum SentenceType {
		UNDEFINED,
		STATEMENT,
		IMPERATIVE,
		QUESTION
	};

	private SentenceType sentenceType = SentenceType.UNDEFINED;

	/** Joker String used in pattern matches */
	private static final String JOKER = "*";

	private String error = null;

	List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * Build sentence by using the given parser object.
	 * 
	 * @param parser
	 */
	void parse(ConversationParser parser) {
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
	 * Set sentence type as STATEMENT, IMPERATIVE or QUESTION.
	 * 
	 * @param type
	 */
	void setType(SentenceType type) {
		this.sentenceType = type;
	}

	/**
	 * Return sentence type.
	 * 
	 * @return
	 */
	public SentenceType getType() {
		return sentenceType;
	}

	/**
	 * Return an iterator over all expressions.
	 *
	 * @return Expression iterator
	 */
	public Iterator<Expression> iterator() {
		return expressions.iterator();
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
		return sentenceType == SentenceType.UNDEFINED && expressions.isEmpty();
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
	void setError(String error) {
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
	 * Append the trailing punctuation depending on the sentence type to the given SentenceBuilder.
	 *
	 * @param builder
	 */
	private void appendPunctation(SentenceBuilder builder) {
		if (sentenceType == SentenceType.STATEMENT) {
			builder.append('.');
		} else if (sentenceType == SentenceType.IMPERATIVE) {
			builder.append('!');
		} else if (sentenceType == SentenceType.QUESTION) {
			builder.append('?');
		}
    }

	/**
	 * Classify word types and normalize words.
	 * 
	 * @param parser
	 */
	void classifyWords(ConversationParser parser, boolean isForMatching) {
		WordList wl = WordList.getInstance();

		for (Expression w : expressions) {
			String original = w.getOriginal();
			WordEntry entry = null;

			// If the parsed Sentence will be used for matching, look for ExpressionType specifiers. 
			if (isForMatching) {
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
    						// Don't persist expressions used for joker matching.
    						boolean persist = !isForMatching || !original.contains(JOKER);

   	    					// Add the unknown word to the word list.
   							wl.addNewWord(original, persist);
    					}
					}
				}
			}
		}
	}

	/**
	 * Standardize sentence type.
	 */
	void standardizeSentenceType() {
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
					if (w.getOriginal().equalsIgnoreCase("me")) {
						// If we already found a verb, we prepend "you" as
						// first subject and mark the sentence as imperative.
						if (prevVerb != null) {
							Expression you = new Expression("you", ExpressionType.SUBJECT);
							expressions.add(0, you);
							sentenceType = SentenceType.IMPERATIVE;
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
	void performaAliasing() {
		Expression verb1 = getVerb(0);
		Expression verb2 = getVerb(1);
		Expression subject1 = getSubject(0);
		Expression subject2 = getSubject(1);

		// Does the Sentence start with a "will/would SUBJECT VERB" construct?
		if (matchesNormalizedStart("will SUB VER")) {
			// merge the second verb with "will/would", removing the first one 
			verb2.mergeLeft(verb1, false);
			expressions.remove(verb1);
			sentenceType = SentenceType.QUESTION;
		}
		// Does the Sentence match "do you have OBJ for me?"?
		else if (matchesNormalized("you have OBJ for me")) {
			expressions.remove(subject1);	// remove "you"
			expressions.remove(getPreposition(0));	// remove "for"
			expressions.remove(subject2);	// remove "me"
			verb1.setNormalized("buy");		// replace "have" by "buy"
			sentenceType = SentenceType.IMPERATIVE;
		}
		// "[you] give me(i)" -> "[I] buy"
		else if (isYouGiveMe(subject1, verb1, subject2)) {
			// remove the subjects and replace the verb with "buy" as first word
			expressions.remove(subject1);	// remove "you"
			expressions.remove(subject2);	// remove "me"
			getVerb().setNormalized("buy");	// replace "give" by "buy"
			sentenceType = SentenceType.IMPERATIVE;
		}

		// "[SUBJECT] (would like to have)" -> "[SUBJECT] buy"
		if (isLikeToHave()) {
			// replace the verb with "buy"
			getVerb().setNormalized("buy");
			sentenceType = SentenceType.IMPERATIVE;
		}
	}

	/**
	 * Is the sentence in the form "[you] give me(i)" ?
	 *
	 * @param subject1
	 * @param verb
	 * @param subject2
	 * @return true for match
	 */
	private static boolean isYouGiveMe(Expression subject1, Expression verb, Expression subject2) {
		if (verb != null
				&& subject1 != null 
				&& subject2 != null) {
			// Note: The second subject "me" is replaced by "i" in the WordList normalization.
			if (subject1.getNormalized().equals("you") 
					&& verb.getNormalized().equals("give") 
					&& subject2.getNormalized().equals("i")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Is the sentence in the form "[SUBJECT] (would like to have)"?
	 *
	 * @return true for match
	 */
	private boolean isLikeToHave() {
		Expression verb = getVerb();

		if (verb != null) {
			if (verb.getNormalized().equals("have") && verb.getOriginal().contains("like")) {
				Expression subject1 = getSubject(0);
				Expression firstExpression = expressions.get(0);
				Expression secondExpression = expressions.get(1);

				// "(would like to have)" ?
				if (subject1 == null && verb == firstExpression) {
					return true;
				}

				// "SUBJECT (would like to have)" ?
				if (subject1 == firstExpression && verb == secondExpression) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Evaluate the sentence type from word order.
	 */
	SentenceType evaluateSentenceType() {
		Iterator<Expression> it = expressions.iterator();
		SentenceType type = SentenceType.UNDEFINED;

		// As words are not yet merged together at this stage, we have to use Expression.nextValid()
		// in this function to jump over words to ignore.
		Expression first = Expression.nextValid(it);

		if (first != null) {
			while (first.getType().isQuestion() && it.hasNext()) {
				if (type == SentenceType.UNDEFINED) {
					type = SentenceType.QUESTION;
				}

				first = Expression.nextValid(it);
			}

			Expression second = null;
			Expression third = null;

			second = Expression.nextValid(it);
			third = Expression.nextValid(it);

			if (second != null) {
				// questions beginning with "is"/"are"
				if (first.getNormalized().equals("is")) {
					if (type == SentenceType.UNDEFINED) {
						type = SentenceType.QUESTION;
					}
				} else if (first.getNormalized().equals("do") 
						&& (second == null || !second.getOriginal().equalsIgnoreCase("me"))) {
					// question begins with "do", but no "do me" sentence
					if (type == SentenceType.UNDEFINED) {
						type = SentenceType.QUESTION;
					}

					expressions.remove(first);
				} else if (first.getNormalized().equals("it") 
						&&	second.getNormalized().equals("is") 
						&& (third != null && third.getType() != null && third.getType().isGerund())) {
					// statement begins with "it is <VER-GER>"
					if (type == SentenceType.UNDEFINED) {
						type = SentenceType.STATEMENT;
					}

					expressions.remove(first);
					expressions.remove(second);
				}
			}
		}

		if (type != SentenceType.UNDEFINED && sentenceType == SentenceType.UNDEFINED) {
			sentenceType = type;
		}

		return type;
	}

	/**
	 * Merge words to form a simpler sentence structure.
	 */
	void mergeWords(boolean isForMatching) {

		// first merge three word expressions of the form "... of ..."
		mergeThreeWordExpressions(isForMatching);

		// now merge two word expressions from left to right
		mergeTwoWordExpressions(isForMatching);
	}

	private void mergeTwoWordExpressions(boolean isForMatching) {

		/*
		 * There are two possibilities for word merges: Left-merging means to
		 * prepend the left word before the following one, removing the first
		 * one. Right-merging means to append the eight word to the preceding
		 * one, removing the second from the word list.
		 */

		boolean changed;

		// loop until no more simplification can be made
		do {
			changed = false;

			Iterator<Expression> it = expressions.iterator();

			boolean prevConditional = false;
			boolean precedingVerb = false;

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

					// don't merge if there are joker expressions
					if (isForMatching) {
						if (curr.getNormalized().contains(JOKER) 
								|| next.getNormalized().contains(JOKER)) {
    						continue;
    					}
					}

					ExpressionType curType = curr.getType();
					ExpressionType nextType = next.getType();

					if (curType.isConditional()) {
						prevConditional = true;
					}

					if (curType != null && nextType != null) {
						// left-merge composite nouns and nouns with preceding adjectives or verbs
						if (isCompoundNoun(curType, nextType, precedingVerb)) {
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
						// left-merge "would like", preserving only the main verb
						else if (curType.isVerb() && nextType.isVerb()) {
							if (prevConditional) {
    							next.mergeLeft(curr, false);
    							expressions.remove(curr);
        						changed = true;
        						break;
							}
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

					// manage precedingVerb flag to detect compound verb/noun constructs
					if (curr.getBreakFlag()) {
						precedingVerb = false;
					} else if (curType.isVerb()) {
						precedingVerb = true;
					} else if (curType.isSubject()) {
						precedingVerb = false;
					}
				}
			}
		} while(changed);
	}

	/**
	 * Decide if the given two expressions form a compound noun.
	 * 
	 * @param curType
	 * @param nextType
	 * @param precedingVerb
	 * @return
	 */
	private static boolean isCompoundNoun(ExpressionType curType, ExpressionType nextType, boolean precedingVerb) {
		// check the next expression type for concrete subject or object names (no pronouns)
		boolean nextIsName = nextType.isObject() || (nextType.isSubject() && !nextType.isPronoun());

		// left-merge composite nouns and nouns with preceding adjectives or verbs
		if (nextIsName) {
			// check the current expression type for concrete subject or object names (no pronouns)
			boolean currIsName = curType.isObject() || (curType.isSubject() && !curType.isPronoun());

			// handle compound words like "fire sword"
			if (currIsName) {
				return true;
			}

			// handle compound words like "golden sword"
			if (curType.isAdjective()) {
				return true;
			}

			// handle compound words like "summon scroll"
			if (curType.isVerb() && precedingVerb) {
				return true;
			}
		}

		return false;
    }

	private void mergeThreeWordExpressions(boolean isForMatching) {
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

						// don't merge if there are joker expressions
						if (isForMatching) {
							if (first.getNormalized().contains(JOKER) 
								||	second.getNormalized().contains(JOKER) 
								||  third.getNormalized().contains(JOKER)) {
	    						continue;
	    					}
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
		if (other == null) {
			return false;
		}

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
		if (!it1.hasNext() && !it2.hasNext()) {
			return true;
		} else {
			return false;
		}
    }

	/**
	 * Compare two sentences and return the difference as String.
	 *
	 * @param other
	 * @return difference String
	 */
	public String diffNormalized(Sentence other) {
		SentenceBuilder ret = new SentenceBuilder();

	    // loop over all expressions and match them between both sides
	    Iterator<Expression> it1 = expressions.iterator();
	    Iterator<Expression> it2 = other.expressions.iterator();

		while (true) {
			Expression e1 = Expression.nextValid(it1);
			Expression e2 = Expression.nextValid(it2);

			if (e1 == null && e2 == null) {
				break;
			} else if (e1 != null && e2 != null) {
    			if (!e1.matchesNormalized(e2)) {
    				ret.append("-[" + e1.getNormalized() + "]");
    				ret.append("+[" + e2.getNormalized() + "]");
    			}
			} else if (e1 != null) {
				ret.append("-[" + e1.getNormalized() + "]");
			} else {
				ret.append("+[" + e2.getNormalized() + "]");
			}
		}

		return ret.toString();
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
		return matchesFull(ConversationParser.parseForMatching(text));
	}

	/**
	 * Check if the Sentence beginning matches the given String.
	 * The match Sentence can contain explicit expressions, which
	 * are compared after normalizing, or ExpressionType specifiers
	 * like "VER" or "SUB*" in upper case.
	 *
	 * @param text
	 * @return
	 */
	public boolean matchesNormalizedStart(String text) {
		return matchesStart(ConversationParser.parseForMatching(text));
	}

	/**
	 * Check if the Sentence completely matches the given Sentence.
	 * The match Sentence can contain explicit expressions, which
	 * are compared after normalizing, or ExpressionType specifiers
	 * like "VER" or "SUB*" in upper case.
	 *
	 * @param other
	 * @return
	 */
	public boolean matchesFull(Sentence other) {
		return matches(other, false);
	}

	/**
	 * Check if the Sentence start matches the given Sentence.
	 * The match Sentence can contain explicit expressions, which
	 * are compared after normalizing, or ExpressionType specifiers
	 * like "VER" or "SUB*" in upper case.
	 *
	 * @param other
	 * @return
	 */
	public boolean matchesStart(Sentence other) {
		return matches(other, true);
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
	private boolean matches(Sentence other, boolean matchStart) {
		if (other == null) {
			return false;
		}

	    // loop over all expressions and match them between both sides
	    Iterator<Expression> it1 = expressions.iterator();
	    Iterator<Expression> it2 = other.expressions.iterator();
	    Expression e1, e2;

		while (true) {
			e1 = Expression.nextValid(it1);
			e2 = Expression.nextValid(it2);

			if (e1 == null || e2 == null) {
				break;
			}

			String matchString = e2.getNormalized();

			if (matchString.contains(JOKER)) {
				if (matchString.equals(JOKER)) {
					// Type string matching is identified by a single "*" as normalized string expression.
					if (!matchesJokerString(e1.getTypeString(), e2.getTypeString()+"*")) {
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

		// If we look for a full match, there should be no more expressions at both sides.
		if (e1 == null && e2 == null) {
			return true;
		}
		// If we look for a match at Sentence start, there must be no more epxressions at the right side.
		else if (matchStart && e2 == null) {
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
		if (str.equals(JOKER)) {
			// Empty strings do not match the "*" joker.
			return str.length() > 0;
		} else { 
			// Convert the joker string into a regular expression and let the Pattern class do the work.
			return Pattern.compile(matchString.replace(JOKER, ".*")).matcher(str).find();
		}
    }

}
