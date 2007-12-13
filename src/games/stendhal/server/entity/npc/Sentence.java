package games.stendhal.server.entity.npc;


/**
 * ConversationParser returns the parsed sentence in this structure,
 * all returned words are in lower case.
 *
 * @author Martin Fuchs
 */
public class Sentence {

	private String verb = null;
	private int    amount = 1;
	private String object = null;
	private String preposition = null;
	private String object2 = null;
	private String error = null;
	private String original;

	/**
	 * return verb of the sentence
	 * @return verb in lower case
	 */
    public String getVerb()
    {
    	return verb;
    }

    /**
	 * return amount of objects
	 * @return amount
	 */
	public int getAmount()
	{
    	return amount;
	}

	/**
	 * return the object of the parsed sentence (e.g. item to be bought)
	 * @return object name in lower case
	 */
	public String getObjectName()
	{
		return object;
	}

	/**
	 * return the second object name after a preposition 
	 * @return second object name in lower case
	 */
    public String getObjectName2()
    {
    	return object2;
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
       if (object != null)
    	   return object.replace(' ', '_');
       else
    	   return null;
    }

	/**
	 * return second item name
	 * @return item name
	 */
	public String getItemName2()
    {
       if (object2 != null)
    	   return object2.replace(' ', '_');
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
	    return preposition;
    }

	/**
	 * return if some error occurred while parsing the input text
	 * @return error flag
	 */
    public boolean hasError()
    {
    	return error != null;
    }

    /**
     * return error message
     * @return error string
     */
	public String getError()
    {
	    return error;
    }

    /**
     * return true if the sentence is empty
     * @return empty flag
     */
    public boolean isEmpty()
    {
    	return verb == null;
    }

	/**
	 * return the complete text of the sentence with
	 * unchanged case, nut with trimmed white space
	 * @return string
	 */
	public String getOriginalText()
    {
    	return original;
    }

	/**
	 * return the full sentence as lower case string
	 * @return string
	 */
    @Override
	public String toString()
    {
    	StringBuilder builder = new StringBuilder(verb);

    	if (amount != 1) {
    		builder.append(' ');
    		builder.append(Integer.toString(amount));
    	}

    	if (object != null) {
    		builder.append(' ');
    		builder.append(object);
    	}

    	if (preposition != null) {
    		builder.append(' ');
    		builder.append(preposition);
    	}

    	if (object2 != null) {
    		builder.append(' ');
    		builder.append(object2);
    	}

    	return builder.toString();
    }

	protected void setError(String error) {
		this.error = error;
	}

	protected void setVerb(String verb) {
		this.verb = verb;
	}

	protected void setAmount(int amount) {
		this.amount = amount;
	}

	protected void setObject(String object) {
		this.object = object;
	}

	protected void setPreposition(String preposition) {
		this.preposition = preposition;
	}

	protected void setObject2(String object2) {
		this.object2 = object2;
	}

	protected void setOriginal(String original) {
		this.original = original;
	}

}
