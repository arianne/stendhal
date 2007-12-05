package games.stendhal.server.entity.npc;


/**
 * ConversationParser returns the parsed sentence in this structure.
 *
 * @author martinf
 */
public class Sentence {

	String	_verb = null;
    int		_amount = 1;
    String	_object = null;
    String	_preposition = null;
    String	_object2 = null;
	boolean _error = false;

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
	 * return the second object name after a preposition 
	 * @return second object name in lower case
	 */
    public String getObjectName2()
    {
    	return _object2;
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
	 * return second item name
	 * @return item name
	 */
	public String getItemName2()
    {
       if (_object2 != null)
    	   return _object2.replace(' ', '_');
       else
    	   return null;
    }

	/**
	 * return the preposition of the sentence if present,
	 * otherwise null
	 * @return preposition
	 */
	public String getPreposition()
    {
	    return _preposition;
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

    	if (_preposition != null) {
    		builder.append(' ');
    		builder.append(_preposition);
    	}

    	if (_object2 != null) {
    		builder.append(' ');
    		builder.append(_object2);
    	}

    	return builder.toString();
    }
}
