package games.stendhal.server.entity.npc;

import games.stendhal.common.Grammar;


/**
 * Parser for conversations with a SpeakerNPC
 *
 * @author martinf
 */
public class ConversationParser {

    private final String[] _words;
    private int		_word_idx;
    private boolean	_error;

    /**
     * create a new conversation parser and initialise with the given text string
     */
	public ConversationParser(final String text)
	{
		 // Split the text line into single words separated by white space.
        _words = text!=null? text.trim().split("\\s+"): new String[0];

		 // start with the first word
        _word_idx = 0;

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

		 // parse the text as simple "verb - amount - object" construct
		sentence._verb = parser.readWord();
        sentence._amount = parser.readAmount();
        sentence._object = parser.readObjectName();
        sentence._error = parser.getError();

        return sentence;
	}

	private String readWord()
    {
        String word = null;

        if (_word_idx < _words.length) {
        	word = _words[_word_idx++].toLowerCase();
        }

        return word;
    }

	/**
	 * read in a positive amount from the input text
	 * @return amount
	 */
	private int readAmount()
    {
        int amount = 1;

         // handle numeric expressions
        if (_word_idx < _words.length) {
        	if (_words[_word_idx].matches("^[+-]?[0-9]+")) {
    	        try {
    	        	amount = Integer.parseInt(_words[_word_idx]);
    
    	        	if (amount < 0)
    	        		_error = true;
    
    		        ++_word_idx;
    	        } catch(NumberFormatException e) {
    	        	_error = true;
    	        }
            } else {
            	 // handle expressions like "one", "two", ...
            	Integer number = Grammar.number(_words[_word_idx]);

            	if (number != null) {
            		amount = number.intValue();
            		++_word_idx;
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
        String name = readWord();

        //TODO handle object names consisting of more than one word

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
