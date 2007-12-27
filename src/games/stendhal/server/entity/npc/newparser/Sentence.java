package games.stendhal.server.entity.npc.newparser;

import games.stendhal.common.Grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConversationParser returns the parsed sentence in this class
 * with all delivered words in lower case.
 * 
 * @author Martin Fuchs
 */
public class Sentence {

	public final static int ST_UNDEFINED	= 0;
	public final static int ST_STATEMENT	= 1;
	public final static int ST_IMPERATIVE	= 2;
	public final static int ST_QUESTION		= 3;

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

		for(String ws; (ws=parser.readNextWord())!=null; ) {
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
    					prevWord.setBreakFlag();
    				}
    			}

    			Word word = new Word(punct.getText());
    			words.add(word);

    			// handle trailing comma characters
    			if (punct.getTrailingPunctuation().contains(",")) {
    				word.setBreakFlag();
    			}

    			prevWord = word;
			}
		}
    }

	/**
	 * set sentence type as ST_STATEMENT, ST_IMPERATIVE or ST_QUESTION
	 * @param type
	 */
	protected void setType(int type) {
	    this.sentenceType = type;
    }

	/**
	 * return sentence type
	 * @return
	 */
	public int getType() {
	    return sentenceType;
    }

	/**
	 * count the number of words matching the given type string
	 * 
	 * @param typePrefix
	 * @return
	 */
	private int countWords(String typePrefix) {
		int count = 0;

		for(Word w : words) {
			if (w.getType().getTypeString().startsWith(typePrefix)) {
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
			if (w.getType().getTypeString().startsWith(typePrefix)) {
				if (i == 0) {
					return w;
				}

				--i;
			}
		}

		return null;
	}

	/**
	 * return trigger string for the FSM engine
	 * 
	 * @return trigger string
	 */
	public String getTrigger() {
		Iterator<Word> it = words.iterator();

		while(it.hasNext()) {
			Word word = it.next();

			if (!word.getType().isIgnore()) {
				return word.getNormalized();
			}
		}

		return "";
    }

	/**
	 * Return the number of "VER" Word objects in the sentence.
	 * 
	 * @return number of subjects
	 */
	public int getVerbCount() {
		return countWords("VER");
	}

	/**
	 * Return verb [i] of the sentence.
	 * 
	 * @return subject
	 */
	public Word getVerb(int i) {
		return getWord(i, "VER");
	}

	/**
	 * special case for sentences with only one verb
	 * 
	 * @return normalised verb string
	 */
	public Word getVerb() {
		if (getVerbCount() == 1) {
			return getVerb(0);
		} else {
			return null;
		}
    }

	/**
	 * special case for sentences with only one verb
	 * 
	 * @return normalised verb string
	 */
	public String getVerbString() {
		if (getVerbCount() == 1) {
			return getVerb(0).getNormalized();
		} else {
			return null;
		}
    }

	/**
	 * return the number of subjects
	 * 
	 * @return number of subjects
	 */
	public int getSubjectCount() {
		return countWords("SUB");
	}

	/**
	 * return subject [i] of the sentence
	 * 
	 * @return subject
	 */
	public Word getSubject(int i) {
		return getWord(i, "SUB");
	}

	/**
	 * special case for sentences with only one subject
	 * 
	 * @return normalised subject string
	 */
	public String getSubjectName() {
		if (getSubjectCount() == 1) {
			return getSubject(0).getNormalized();
		} else {
			return null;
		}
    }

	/**
	 * return the number of objects
	 * 
	 * @return number of objects
	 */
	public int getObjectCount() {
		return countWords("OBJ");
	}

	/**
	 * return the object [i] of the parsed sentence (e.g. item to be bought)
	 * 
	 * @return object
	 */
	public Word getObject(int i) {
		return getWord(i, "OBJ");
	}

	/**
	 * special case for sentences with only one object
	 * 
	 * @return normalised subject string
	 */
	public String getObjectName() {
		if (getObjectCount() == 1) {
			return getObject(0).getNormalized();
		} else {
			return null;
		}
    }

	/**
	 * return item name derived (by replacing spaces by underscores) from the
	 * object of the parsed sentence.
	 * TODO get rid of underscore handling for item names
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
	 * special case for sentences with only one item
	 * 
	 * @return normalised subject string
	 */
	public String getItemName() {
		if (getObjectCount() == 1) {
			return getItemName(0);
		} else {
			return null;
		}
    }

	/**
	 * return the number of prepositions
	 * 
	 * @return number of objects
	 */
	public int getPrepositionCount() {
		return countWords("PRE");
	}

	/**
	 * return the preposition [i] of the parsed sentence
	 * 
	 * @return object
	 */
	public Word getPreposition(int i) {
		return getWord(i, "PRE");
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
		return sentenceType==ST_UNDEFINED && words.isEmpty();
	}

	protected void setError(String error) {
		this.error = error;
	}

	/**
	 * Return the complete text of the sentence with unchanged case, but with
	 * trimmed white space.
	 * 
	 * TODO There should be only as less code places as possible to rely on this method.
	 * 
	 * @return string
	 */
	public String getOriginalText() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			builder.append(w.getOriginal());
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

		for(Word w : words) {
			if (!w.getType().isIgnore()) {
				builder.append(w.getNormalized() + "/" + w.getType().getTypeString());
			}

			if (w.getBreakFlag()) {
				builder.append(',');
			}
		}

		if (sentenceType == ST_STATEMENT)
			builder.append(".");
		else if (sentenceType == ST_IMPERATIVE)
			builder.append("!");
		else if (sentenceType == ST_QUESTION)
			builder.append("?");

		return builder.toString();
	}

	/**
	 * classify word types and normalise words
	 * @param parser
	 */
	public void classifyWords(ConversationParser parser) {
		WordList wl = WordList.getInstance();

	    for(Word w : words) {
	    	String original = w.getOriginal();

	    	WordEntry entry = wl.find(original);

	    	if (entry != null) {
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
	    		// recognise declined verbs
	    		WordEntry verb = wl.normalizeVerb(original);

	    		if (verb != null) {
	    			WordType type = verb.getType();

	    			if (original.endsWith("ing")) {
	    				w.setType(new WordType(type.getTypeString()+"-GER"));
	    			} else {
	    				w.setType(type);
	    			}

	    			w.setNormalized(verb.getNormalized());
	    		} else {
    	    		parser.setError("unknown word: " + original);

    	    		w.setType(new WordType(""));
    	    		w.setNormalized(original.toLowerCase());
	    		}
	    	}
	    }
    }

	/**
	 * replace grammatical constructs with simpler ones with the same meaning,
	 * so that they can be understood by the FSM rules
	 * 
	 * TODO This grammatical aliasing is only a first step to more flexible
	 * NPC conversation. It should be integrated with the FSM engine so that
	 * quest writers can specify the conversation syntax on their own.
	 */
	public void performaAliasing() {
		if (sentenceType==ST_IMPERATIVE && getSubjectCount()>=2) {
			Word subject1 = getSubject(0);
			Word subject2 = getSubject(1);
			Word verb = getVerb();

    		// [you] give me(i) -> [I] buy
    		// Note: The second subject "me" is replaced by "i" in the WordList normalisation.
    		if (subject1.getNormalized().equals("you") && subject2.getNormalized().equals("i")) {
    			if (verb!=null && verb.getNormalized().equals("give")) {
    				// remove the previous subjects and replace the verb with "buy" as first word
    				words.remove(subject1);
    				words.remove(subject2);
    				words.remove(verb);
    				words.add(0, new Word("buy"));
    			}
    		}
		}
	}

	/**
	 * evaluate sentence type from word order
	 */
	public int evaluateSentenceType() {
		Iterator<Word> it = words.iterator();
		int type = ST_UNDEFINED;

		if (it.hasNext()) {
			Word first = it.next();

			while(first.getType().isQuestion() && it.hasNext()) {
				first = it.next();

				if (type == ST_UNDEFINED)
					type = ST_QUESTION;
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
    				if (type == ST_UNDEFINED)
    					type = ST_QUESTION;
    			}
    			// questions beginning with "do"
    			else if (first.getNormalized().equals("do")) {
    				if (type == ST_UNDEFINED)
    					type = ST_QUESTION;

    				words.remove(first);
    			}
    			// statements beginning with "it is <VER-GER>"
    			else if (first.getNormalized().equals("it") &&
    					second.getNormalized().equals("is") &&
    					(third!=null && third.getType().isGerund())) {
    				if (type == ST_UNDEFINED)
    					type = ST_STATEMENT;

    				words.remove(first);
    				words.remove(second);
    			}
			}
		}

		if (type!=ST_UNDEFINED && sentenceType==ST_UNDEFINED) {
			sentenceType = type;
		}

		return type;
    }

	/**
	 * merge words to form a simpler sentence structure
	 */
	public void mergeWords() {

		// first merge three word expressions of the form "... of ..."
		mergeThreeWordExpressions();

		// now merge two word expressions from left to right
		mergeTwoWordExpressions();
	}

	private void mergeTwoWordExpressions() {

		/* There are two possibilities for word merges:
		 Left-merging means to prepend the left word before the following one, removing the first one.
		 Right-merging means to append the eight word to the preceding one, removing the second from
		 the word list. */

		boolean changed;

		// loop until no more simplification can be made
		do {
			Iterator<Word> it = words.iterator();

			changed = false;

			if (it.hasNext()) {
    			Word next = it.next();

    			// loop over all words of the sentence starting from left
    			while(it.hasNext()) {
    				// Now look at two neighbour words.
        			Word word = next;
        			next = it.next();

        			// don't merge if the break flag is set
        			if (word.getBreakFlag()) {
        				continue;
        			}

        			// left-merge nouns with preceding adjectives or amounts and composite nouns
        			if ((word.getType().isAdjective() || word.getType().isNumeral() || word.getType().isObject()) &&
        					(next.getType().isObject() || next.getType().isSubject())) {
        				next.mergeLeft(word);
        				words.remove(word);
        				changed = true;
        				break;
        			}
        			// right-merge consecutive words of the same main type
        			else if (word.getType().getMainType().equals(next.getType().getMainType())) {
        				word.mergeRight(next);
        				words.remove(next);
        				changed = true;
        				break;
        			}
        			// left-merge question words with following verbs and adjectives
        			else if (word.getType().isQuestion() &&
        					(next.getType().isVerb() || next.getType().isAdjective())) {
        				next.mergeLeft(word);
        				words.remove(word);
        				changed = true;
        				break;
        			}
        			// left-merge words to ignore
        			else if (word.getType().isIgnore()) {
        				next.mergeLeft(word);
        				words.remove(word);
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
        				// Now look at three neighbour words.
        				first = second;
            			second = third;
            			third = it.next();

            			// don't merge if the break flag is set
            			if (first.getBreakFlag() || second.getBreakFlag()) {
            				continue;
            			}

            			// merge "... of ..." expressions into one word
            			if (first.getType().isObject() && second.getNormalized().equals("of") &&
            					third.getType().isObject()) {
            				String expr = first.getNormalized() + " of " + third.getNormalized();
            				String normalizedExpr = Grammar.extractNoun(expr);

            				// see if the expression has been normalised
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
		} while(changed);
	}

}
