package games.stendhal.server.entity.npc.parser;

/**
 * NPC conversation context holder.
 * TODO mf - manage conversation state in the NPC conversation engine
 * 
 * @author Martin Fuchs
 */
public class ConversationContext {

	// conversation context states
	static final int CCS_NONE			 = 0;	// no current conversation context
	static final int CCS_WAIT_FOR_YES_NO = 1;	// wait for a yes/no answer
	static final int CCS_WAIT_FOR_OBJECT = 2;	// wait for a named object

	private int state = CCS_NONE;				// current state

	protected boolean forMatching = false;		// flag for sentences to be used for matching
	protected boolean mergeExpressions = true;	// flag to enable Expression merging
	protected boolean persistNewWords = true;	// flag to enable storing new words into the database
	protected boolean ignoreIgnorable = true;	// flag to enable ignoring of words marked with the type IGN

	public void setState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}

	public void setForMatching(boolean forMatching) {
	    this.forMatching = forMatching;
    }
	public boolean isForMatching() {
	    return forMatching;
    }

	public void setMergeExpressions(boolean mergeExpressions) {
	    this.mergeExpressions = mergeExpressions;
    }
	public boolean getMergeExpressions() {
	    return mergeExpressions;
    }

	public void setPersistNewWords(boolean persistNewWords) {
	    this.persistNewWords = persistNewWords;
    }
	public boolean getPersistNewWords() {
	    return persistNewWords;
    }

	public void setIgnoreIgnorable(boolean ignoreIgnorable) {
		this.ignoreIgnorable = ignoreIgnorable;
    }
	public boolean getIgnoreIgnorable() {
		return ignoreIgnorable;
    }

}
