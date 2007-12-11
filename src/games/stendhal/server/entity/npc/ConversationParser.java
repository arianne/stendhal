package games.stendhal.server.entity.npc;

import games.stendhal.common.Grammar;

import java.util.StringTokenizer;


/**
 * Parser for conversations with a SpeakerNPC
 * This class parses strings in english language and returns them
 * as Sentence objects. All sentence constituents are in lower case.
 *
 * @author Martin Fuchs
 */
public class ConversationParser {

	private StringTokenizer _tokenizer;
	private StringBuilder _original;
	private String	_nextWord;
    private String _error;

    /**
     * create a new conversation parser and initialise with the given text string
     */
	public ConversationParser(final String text)
	{
		 // initialise a new tokenizer with the given text
		_tokenizer = new StringTokenizer(text!=null? text: "");

		 // get first word
		_nextWord = _tokenizer.hasMoreTokens()? _tokenizer.nextToken(): null;
		_original = _nextWord!=null? new StringBuilder(_nextWord): new StringBuilder();

         // start with no errors.
        _error = null;
	}

    /**
     * parse the given command sentence
     * @param text
     * @return sentence
     */
	public static Sentence parse(final String text)
	{
		ConversationParser parser = new ConversationParser(text);
		Sentence sentence = new Sentence();

		 // Parse the text as simple "verb - amount - object" construct.
		sentence._verb = parser._nextWord();
		sentence._amount = parser.readAmount();
		String object = parser.readObjectName();

         // Optionally there may be following a preposition and a second object.
		sentence._preposition = parser._nextWord();
		sentence._object2 = parser.readObjectName();

		sentence._error = parser.getError();
		sentence._original = parser._original.toString();

		 // derive the singular from the item name if the amount is greater than one
		if (sentence._amount != 1) {
			object = Grammar.singular(object);
		}

		sentence._object = object;

        return sentence;
	}

	private String _nextWord()
    {
		String word = _nextWord;

		if (word != null) {
			if (_tokenizer.hasMoreTokens()) {
				_nextWord = _tokenizer.nextToken();
				_original.append(' ');
				_original.append(_nextWord);
			} else {
				_nextWord = null;
			}

	        return word.toLowerCase();
		} else {
			return null;
		}
    }

	/**
	 * read in a positive amount from the input text
	 * @return amount
	 */
	private int readAmount()
    {
        int amount = 1;

         // handle numeric expressions
        if (_tokenizer.hasMoreTokens()) {
        	if (_nextWord.matches("^[+-]?[0-9]+")) {
    	        try {
    	        	amount = Integer.parseInt(_nextWord);
    
    	        	if (amount < 0)
    	        		setError("negative amount: " + amount);

    		        _nextWord();
    	        } catch(NumberFormatException e) {
    	        	setError("illegal number format: '" + _nextWord + "'");
    	        }
            } else {
            	 // handle expressions like "one", "two", ...
            	Integer number = Grammar.number(_nextWord);

            	if (number != null) {
            		amount = number.intValue();
            		_nextWord();
            	}
            }
        }

        return amount;
    }

	/**
	 * read in the object of the parsed sentence (e.g. item to be bought)
	 * @return object name in lower case
	 */
	private String readObjectName()
	{
        String name = null;

         // handle object names consisting of more than one word
        for(;;) {
        	if (_nextWord == null)
        		break;

        	 // stop if the next word is a preposition
        	if (Grammar.isPreposition(_nextWord) &&
        		!_nextWord.equals("of")) //TODO directly integrate Grammar.extractNoun() here
        		break;

        	String word = _nextWord();

             // concatenate user specified item names like "baby dragon"
             // with spaces to build the internal item names
            if (name == null) {
            	name = word;
            } else {
            	name += " " + word;
            }
        }

        return Grammar.extractNoun(name);
	}

	/**
	 * set error flag on parsing problems
	 */
	private void setError(String error)
    {
		if (_error == null) {
			_error = error;
    	} else {
    		_error += "\n" + error;
    	}
    }

	/**
	 * return whether some error occurred while parsing the input text
	 * @return error flag
	 */
	public String getError()
    {
	    return _error;
    }
}
