package games.stendhal.server.entity.npc.parser;

import games.stendhal.common.ErrorDrain;
import games.stendhal.common.Grammar;

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * SentenceImplementation contains the implementation details of
 * building Sentence objects.
 * 
 * @author Martin Fuchs
 */
final class SentenceImplementation extends Sentence {

	private static final Logger logger = Logger.getLogger(SentenceImplementation.class);

	/**
	 * Create a SentenceImplementation object.
	 *
	 * @param ctx
	 */
	protected SentenceImplementation(ConversationContext ctx) {
	    super(ctx);
    }

	/**
	 * Build sentence by using the given parser object.
	 * 
	 * @param parser
	 */
	void parse(ConversationParser parser) {
		originalText = parser.getOriginalText();

		Expression prevWord = null;

		for (String ws; (ws = parser.readNextWord()) != null;) {
			// replace "and" by enumerations separated by break flags
			if (ws.equals("and")) {
				if (prevWord != null) {
					prevWord.setBreakFlag();
				}
			} else {
				PunctuationParser punct = new PunctuationParser(ws);

				String precedingPunct = punct.getPrecedingPunctuation();
				String text = punct.getText();

				// avoid to trim leading decimal points from numbers
				if (precedingPunct.length() > 0 && text.matches("[0-9.,]+")) {
					text = ws;
				}

				// handle preceding comma characters
				if (precedingPunct.contains(",")) {
					if (prevWord != null) {
						// break sentence after the previous word
						prevWord.setBreakFlag();
					}
				}

				Expression word = new Expression(text);
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
	 * Classify word types and normalize words.
	 * 
	 * @param errors
	 */
	void classifyWords(ErrorDrain errors) {
		WordList wl = WordList.getInstance();

		for (Expression w : expressions) {
			String original = w.getOriginal();
			WordEntry entry = null;

			// If the parsed Sentence will be used for matching, look for ExpressionType specifiers. 
			if (context.isForMatching()) {
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
					// If getPlurSing() is null, there is no unique singular form, so use the original string.
					if (entry.getPlurSing() != null) {
						w.setNormalized(entry.getPlurSing());
					} else {
						w.setNormalized(original);
					}
				} else {
					w.setNormalized(entry.getNormalized());
				}
			} else {
				// handle numeric expressions
				if (original.matches("^[+-]?[0-9.,]+")) {
					w.parseAmount(original, errors);
					int amount = w.getAmount();

					if (amount < 0) {
						errors.setError("negative amount: " + amount);
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
							logger.error("SentenceImplementation.classifyWords(): unexpected normalized adjective '"+adjective+"' of original '"+original+"'");
							w.setType(adjective.getType());
						}

						w.setNormalized(adjective.getNormalized());
					} else {
    					w.setType(new ExpressionType(""));
    					w.setNormalized(original.toLowerCase());

    					if (entry == null) {
    						// Don't persist expressions used for joker matching.
    						boolean persist = context.getPersistNewWords() 
    										&& (!context.isForMatching() || !original.contains(JOKER));

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
			// remove "you"
			expressions.remove(subject1);	
			// remove "for"
			expressions.remove(getPreposition(0));
			// remove "me"
			expressions.remove(subject2);	
			// replace "have" by "buy"
			verb1.setNormalized("buy");		
			sentenceType = SentenceType.IMPERATIVE;
		}
		// "[you] give me(i)" -> "[I] buy"
		else if (isYouGiveMe(subject1, verb1, subject2)) {
			// remove the subjects and replace the verb with "buy" as first word
			// remove "you"
			expressions.remove(subject1);	
			// remove "me"
			expressions.remove(subject2);	
			// replace "give" by "buy"
			getVerb().setNormalized("buy");
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
	 * @return SentenceType
	 */
	SentenceType evaluateSentenceType() {
		Iterator<Expression> it = expressions.iterator();
		SentenceType type = SentenceType.UNDEFINED;

		// As words are not yet merged together at this stage, we have to use Expression.nextValid()
		// in this function to jump over words to ignore.
		Expression first = nextValid(it);

		if (first != null) {
			while (first != null && first.isQuestion() && it.hasNext()) {
				if (type == SentenceType.UNDEFINED) {
					type = SentenceType.QUESTION;
				}

				first = nextValid(it);
			}

			Expression second = null;
			Expression third = null;

			second = nextValid(it);
			third = nextValid(it);

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
	 * @param isForMatching 
	 */
	void mergeWords() {

		//TODO mf - use WordList.compoundNames to merge compound names

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
					if (context.isForMatching()) {
						if (curr.getNormalized().contains(JOKER) 
								|| next.getNormalized().contains(JOKER)) {
    						continue;
    					}
					}

					ExpressionType curType = curr.getType();
					ExpressionType nextType = next.getType();

					if (curType != null && curType.isConditional()) {
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
					if (context.getIgnoreIgnorable()) {
    					if (curType != null && isIgnorable(curr)) {
    						next.mergeLeft(curr, false);
    						expressions.remove(curr);
    						changed = true;
    						break;
    					}
					}

					// manage precedingVerb flag to detect compound verb/noun constructs
					if (curr.getBreakFlag()) {
						precedingVerb = false;
					} else if (curType != null) {
						if (curType.isVerb()) {
    						precedingVerb = true;
    					} else if (curType.isSubject()) {
    						precedingVerb = false;
    					}
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

						// don't merge if there are joker expressions
						if (context.isForMatching()) {
							if (first.getNormalized().contains(JOKER) 
								||	second.getNormalized().contains(JOKER) 
								||  third.getNormalized().contains(JOKER)) {
	    						continue;
	    					}
						}

						// merge "... of ..." expressions into one expression, preserving
						// only the main word as merged normalized expression
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

}
