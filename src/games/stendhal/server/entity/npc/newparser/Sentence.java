package games.stendhal.server.entity.npc.newparser;

import java.util.ArrayList;
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

	private int type = ST_UNDEFINED;

	private String error = null;

	List<Word> words = new ArrayList<Word>();

	protected void setType(int type) {
	    this.type = type;
    }

	public int getType() {
	    return type;
    }

	protected Word addWord(String s) {
		Word word = new Word(s);

		words.add(word);

		return word;
	}

	/**
	 * return verb of the sentence.
	 * 
	 * @return verb in lower case
	 */
	public String getVerb() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			if (w.type.isVerb()) {
				builder.append(w.normalized);
			}
		}

		return builder.toString();
	}

	/**
	 * return the first subject of the sentence.
	 * 
	 * @return subject in lower case
	 */
	public String getSubject() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			if (w.type.isNoun()) {
				builder.append(w.normalized);
			}
		}

		return builder.toString();
	}

	/**
	 * return the second subject of the sentence.
	 * 
	 * @return second subject in lower case
	 */
	public String getSubject2() {
		return null;//TODO
	}

	/**
	 * return amount of objects.
	 * 
	 * @return amount
	 */
	public int getAmount() {
		for(Word w : words) {
			if (w.type.isNumeral()) {
				return w.amount;
			}
		}

		return 1;
	}

	/**
	 * return the object of the parsed sentence (e.g. item to be bought).
	 * 
	 * @return object name in lower case
	 */
	public String getObjectName() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			if (w.type.isNoun()) {
				builder.append(w.normalized);
			}
		}

		return builder.toString();
	}

	/**
	 * return the second object name after a preposition.
	 * 
	 * @return second object name in lower case
	 */
	public String getObjectName2() {
		return null;//TODO
	}

	/**
	 * return item name derived (by replacing spaces by underscores) from the
	 * object of the parsed sentence.
	 * TODO get rid of underscore handling for item names
	 * 
	 * @return item name
	 */
	public String getItemName() {
		// concatenate user specified item names like "baby dragon"
		// with underscores to build the internal item names
		SentenceBuilder builder = new SentenceBuilder('_');

		for(Word w : words) {
			if (w.type.isNoun()) {
				builder.append(w.normalized);
			}
		}

		return builder.toString();
	}

	/**
	 * return second item name.
	 * 
	 * @return item name
	 */
	public String getItemName2() {
		return null;//TODO
	}

	/**
	 * return the preposition of the sentence if present, otherwise null.
	 * 
	 * @return preposition
	 */
	public String getPreposition() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			if (w.type.isPreposition()) {
				builder.append(w.normalized);
			}
		}

		return builder.toString();
	}

	/**
	 * return if some error occurred while parsing the input text.
	 * 
	 * @return error flag
	 */
	public boolean hasError() {
		return error != null;
	}

	/**
	 * return error message.
	 * 
	 * @return error string
	 */
	public String getError() {
		return error;
	}

	/**
	 * return true if the sentence is empty.
	 * 
	 * @return empty flag
	 */
	public boolean isEmpty() {
		return type==ST_UNDEFINED && words.isEmpty();
	}

	protected void setError(String error) {
		this.error = error;
	}

	/**
	 * return the complete text of the sentence with unchanged case, but with
	 * trimmed white space.
	 * 
	 * TODO There should be only as less code places as possible to rely on this method.
	 * 
	 * @return string
	 */
	public String getOriginalText() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			builder.append(w.original);
		}

		return builder.toString();
	}

	/**
	 * return the full sentence as lower case string.
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		SentenceBuilder builder = new SentenceBuilder();

		for(Word w : words) {
			builder.append(w.normalized + "/" + w.type.typeString);
		}

		return builder.toString();
	}

	/**
	 * classify word types and normalise words
	 * @param parser
	 */
	public void classifyWords(ConversationParser parser) {
		WordList wl = WordList.getInstance();

	    for(Word w : words) {
	    	WordEntry entry = wl.find(w.original);

	    	if (entry != null) {
	    		w.type = entry.type;

	    		if (entry.type.isNumeral()) {
	    			// evaluate numeric expressions
	    			w.amount = entry.value;
	    			w.normalized = w.amount.toString();
	    		} else if (entry.type.isPlural()) {
	    			// normalise to the singular form
	    			w.normalized = entry.plurSing;
	    		} else {
	    			w.normalized = entry.word;
	    		}
	    	} else {
	    		// handle numeric expressions
    			if (w.original.matches("^[+-]?[0-9]+")) {
    				w.parseAmount(w.original, parser);

    				if (w.amount < 0) {
    					parser.setError("negative amount: " + w.amount);
    				}
    			}
	    	}

	    	// handle unknown words
	    	if (w.type == null) {
	    		w.type = new WordType("");
	    		w.normalized = w.original.toLowerCase();
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
		if (words.size() > 2) {
			String subject1 = getSubject();
			String subject2 = getSubject2();

    		// [you] give me(i) -> [I] buy
    		// Note: The second subject "me" is replaced by "i" in ConversationParser.
    		if (subject1.equals("you") && subject2.equals("i")) {
    			if (getVerb().equals("give")) {
       			/*TODO manipulate word list
    				subject1	= "i";
    				verb		= "buy";
    				subject2	= null; */
    			}
    		}
		}
	}

}
