package games.stendhal.server.entity.npc;


/**
 * Parser for conversations with a SpeakerNPC
 *
 * @author martinf
 */
public class ConversationParser {

    private final String[] _words;
    private int _word_idx;
    private boolean _error;

    /**
     * initialise conversation parser with the complete command sentence
     * @param text
     */
	public ConversationParser(final String text)
	{
		 // Split the text line into single words separated by white space.
        _words = text!=null? text.split("\\s+"): new String[0];

		 // The first word is already recognized by the FSM, so we can skip it.
        _word_idx = _words.length>0? 1: 0;

         // start with no errors.
        _error = false;
	}

	/**
	 * read in a positive amount from the input text
	 * @return amount
	 */
	public int readAmount()
    {
        int amount = 1;

         // handle numeric expressions
        if (_word_idx<_words.length && _words[_word_idx].matches("^[+-]?[0-9]+")) {
	        try {
	        	amount = Integer.parseInt(_words[_word_idx]);

	        	if (amount < 0)
	        		_error = true;

		        ++_word_idx;
	        } catch(NumberFormatException e) {
	        	_error = true;
	        }
        }

        //TODO also handle expressions like "one", "two", "a dozen", ...

        return amount;
    }

	/**
	 * read in the object of the parsed sentence (e.g. item to be bought)
	 * @return object name in lower case
	 */
	public String readObjectName()
	{
        String name = null;

        if (_word_idx < _words.length) {
        	name = _words[_word_idx++].toLowerCase();
        }

        return name;
	}

	/**
	 * return if some error occured while parsing the input text
	 * @return error flag
	 */
	public boolean getError()
    {
	    return _error;
    }
}
