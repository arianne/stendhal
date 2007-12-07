package games.stendhal.server.entity.npc;

import games.stendhal.common.Grammar;

import java.util.StringTokenizer;


/**
 * Parser for conversations with a SpeakerNPC
 * This class parses strings in english language and returns them
 * as Sentence objects. All sentence constituents are in lower case.
 *
 * @author martinf
 */
public class ConversationParser {

	private StringTokenizer _tokenizer;
	private String	_next_word;
	private String _original;
    private boolean	_error;

    /**
     * create a new conversation parser and initialise with the given text string
     */
	public ConversationParser(final String text)
	{
		 // initialise a new tokenizer with the given text
		_tokenizer = new StringTokenizer(text!=null? text: "");

		 // get first word
		_next_word = _tokenizer.hasMoreTokens()? _tokenizer.nextToken(): null;
		_original = _next_word;

         // start with no errors.
        _error = false;
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
		sentence._verb = parser.nextWord();
		sentence._amount = parser.readAmount();
		sentence._object = parser.readObjectName();

         // Optionally there may be following a preposition and a second object.
		sentence._preposition = parser.nextWord();
		sentence._object2 = parser.readObjectName();

		sentence._error = parser.getError();
		sentence._original = parser._original;
/*TODO
		 // derive the singular from the item name if the amount is greater than one
		if (sentence._amount != 1) {
			sentence._object = Grammar.singular(sentence._object);
		}
*/
        return sentence;
	}

	private String nextWord()
    {
		String word = _next_word;

		if (word != null) {
			if (_tokenizer.hasMoreTokens()) {
				_next_word = _tokenizer.nextToken();
				_original += ' ' + _next_word;
			} else {
				_next_word = null;
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
        	if (_next_word.matches("^[+-]?[0-9]+")) {
    	        try {
    	        	amount = Integer.parseInt(_next_word);
    
    	        	if (amount < 0)
    	        		_error = true;

    		        nextWord();
    	        } catch(NumberFormatException e) {
    	        	_error = true;
    	        }
            } else {
            	 // handle expressions like "one", "two", ...
            	Integer number = Grammar.number(_next_word);

            	if (number != null) {
            		amount = number.intValue();
            		nextWord();
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
        	if (_next_word == null)
        		break;

        	 // stop if the next word is a preposition
        	if (Grammar.isPreposition(_next_word)) 
        		break;

        	String word = nextWord();

             // concatenate user specified item names like "baby dragon"
             // with spaces to build the internal item names
            if (name == null) {
            	name = word;
            } else {
            	name += " " + word;
            }
        }

        return name;
	}

	/**
	 * return if some error occurred while parsing the input text
	 * @return error flag
	 */
	public boolean getError()
    {
	    return _error;
    }
}
