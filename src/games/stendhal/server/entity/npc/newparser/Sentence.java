package games.stendhal.server.entity.npc.newparser;

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

	protected void setType(int type) {
	    this.sentenceType = type;
    }

	public int getType() {
	    return sentenceType;
    }

	/**
	 * Build sentence by using the given parser object.
	 * 
	 * @param parser
	 */
	public void parse(ConversationParser parser) {
		Word prevWord = null;

		for(String ws; (ws=parser.readNextWord())!=null; ) {
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
	public String getVerb() {
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
	    			w.setType(verb.getType());
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
		if (getSubjectCount() >= 2) {
			Word subject1 = getSubject(0);
			Word subject2 = getSubject(1);

    		// [you] give me(i) -> [I] buy
    		// Note: The second subject "me" is replaced by "i" in WordList.
    		if (subject1.getNormalized().equals("you") && subject2.getNormalized().equals("i")) {
    			if (getVerb().equals("give")) {
       			/*TODO manipulate word list
    				subject1	= "i";
    				verb		= "buy";
    				subject2	= null; */
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
			Word word = it.next();

			while(word.getType().isQuestion() && it.hasNext()) {
				word = it.next();

				if (type == ST_UNDEFINED)
					type = ST_QUESTION;
			}

			if (it.hasNext()) {
    			// handle questions beginning with "is"/"are"
    			if (word.getNormalized().equals("is")) {
    				if (type == ST_UNDEFINED)
    					type = ST_QUESTION;
    			}
    			// handle questions beginning with "do"
    			else if (word.getNormalized().equals("do")) {
    				if (type == ST_UNDEFINED)
    					type = ST_QUESTION;

    				words.remove(word);
    			}
			}
		}

		if (sentenceType == ST_UNDEFINED) {
			sentenceType = type;
		}

		return type;
    }

	/**
	 * merge words to form a simpler sentence structure
	 */
	public void mergeWords() {
		boolean changed;

		/* There are two possibilities for word merges:
		 Left-merging means to prepend the left word before the following one, removing the first one.
		 Right-merging means to append the eight word to the preceding one, removing the second from
		 the word list. */

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

}
