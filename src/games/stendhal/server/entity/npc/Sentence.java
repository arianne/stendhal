package games.stendhal.server.entity.npc;


/**
 * ConversationParser returns the parsed sentence in this structure.
 *
 * @author martinf
 */
public class Sentence {

	public String _verb;
    public int _amount = 1;
    public String _object;
	public boolean _error;

    public String getVerb()
    {
    	return _verb;
    }

    /**
     * return amount of objects
     * @return amount
     */
    public int getAmount()
    {
    	return _amount;
    }

	/**
	 * return the object of the parsed sentence (e.g. item to be bought)
	 * @return object name in lower case
	 */
    public String getObjectName()
    {
    	return _object;
    }

	/**
	 * return item name derived (by replacing spaces by underscores) from
	 * the object of the parsed sentence
	 * @return item name
	 */
    public String getItemName()
    {
        // concatenate user specified item names like "baby dragon"
        // with underscores to build the internal item names
       if (_object != null)
    	   return _object.replace(' ', '_');
       else
    	   return null;
    }

	/**
	 * return if some error occurred while parsing the input text
	 * @return error flag
	 */
    public boolean getError()
    {
    	return _error;
    }

    public String toString()
    {
    	StringBuilder builder = new StringBuilder(_verb);

    	if (_amount != 1) {
    		builder.append(' ');
    		builder.append(Integer.toString(_amount));
    	}

    	if (_object != null) {
    		builder.append(' ');
    		builder.append(_object);
    	}

    	return builder.toString();
    }
}
