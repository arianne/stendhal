package games.stendhal.server.entity.npc;

/**
 * ConversationParser returns the parsed sentence in this class, all
 * returned words are in lower case.
 * 
 * @author Martin Fuchs
 */
public class Sentence {

	private String subject1 = "i";
	private String verb = null;
	private String subject2 = null;

	private int objectAmount = 1;
	private String object1 = null;

	private String preposition = null;

	private String object2 = null;

	private String error = null;
	private String original;

	/**
	 * return the main subject of the sentence.
	 * 
	 * @return subject in lower case
	 */
	public String getSubject() {
		return subject1;
	}

	/**
	 * return verb of the sentence.
	 * 
	 * @return verb in lower case
	 */
	public String getVerb() {
		return verb;
	}

	/**
	 * return the second subject of the sentence.
	 * 
	 * @return second subject in lower case
	 */
	public String getSubject2() {
		return subject2;
	}

	/**
	 * return amount of objects.
	 * 
	 * @return amount
	 */
	public int getAmount() {
		return objectAmount;
	}

	/**
	 * return the object of the parsed sentence (e.g. item to be bought).
	 * 
	 * @return object name in lower case
	 */
	public String getObjectName() {
		return object1;
	}

	/**
	 * return the second object name after a preposition.
	 * 
	 * @return second object name in lower case
	 */
	public String getObjectName2() {
		return object2;
	}

	/**
	 * return item name derived (by replacing spaces by underscores) from the
	 * object of the parsed sentence.
	 * 
	 * @return item name
	 */
	public String getItemName() {
		// concatenate user specified item names like "baby dragon"
		// with underscores to build the internal item names
		if (object1 != null) {
			return object1.replace(' ', '_');
		} else {
			return null;
		}
	}

	/**
	 * return second item name.
	 * 
	 * @return item name
	 */
	public String getItemName2() {
		if (object2 != null) {
			return object2.replace(' ', '_');
		} else {
			return null;
		}
	}

	/**
	 * return the preposition of the sentence if present, otherwise null.
	 * 
	 * @return preposition
	 */
	public String getPreposition() {
		return preposition;
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
		return verb == null;
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
		return original;
	}

	/**
	 * return the full sentence as lower case string.
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(subject1);

		builder.append(' ');
		builder.append(verb);

		if (subject2 != null) {
			builder.append(' ');
			builder.append(subject2);
		}

		if (objectAmount != 1) {
			builder.append(' ');
			builder.append(Integer.toString(objectAmount));
		}

		if (object1 != null) {
			builder.append(' ');
			builder.append(object1);
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

	protected void setSubject(String subject) {
		this.subject1 = subject;
	}

	protected void setVerb(String verb) {
		this.verb = verb;
	}

	protected void setSubject2(String subject2) {
		this.subject2 = subject2;
	}

	protected void setAmount(int amount) {
		this.objectAmount = amount;
	}

	protected void setObject(String object) {
		this.object1 = object;
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

	/**
	 * replace grammatical constructs with simpler ones with the same meaning,
	 * so that they can be understood by the FSM rules
	 * 
	 * TODO This grammatical aliasing is only a first step to more flexible
	 * NPC conversation. It should be integrated with the FSM engine so that
	 * quest writers can specify the conversation syntax on their own.
	 */
	public void performaAliasing() {
		if (verb!=null && subject2!=null) {
    		// [you] give me(i) -> [I] bye
    		// Note: The second subject "me" is replaced by "i" in ConversationParser.
    		if (subject1.equals("you") && subject2.equals("i")) {
    			if (verb.equals("give")) {
    				subject1	= "i";
    				verb		= "buy";
    				subject2	= null;
    			}
    		}
		}
	}
}
