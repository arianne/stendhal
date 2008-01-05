package games.stendhal.server.entity.npc.parser;

/**
 * NPC conversation context holder.
 * 
 * @author Martin Fuchs
 */
public class ConversationContext {
	
	// conversation context states
	static final int CCS_NONE			 = 0;	// no current conversation context
	static final int CCS_WAIT_FOR_YES_NO = 1;	// wait for a yes/no answer
	static final int CCS_WAIT_FOR_OBJECT = 2;	// wait for a named object

	private int state = CCS_NONE;			// current state

	private boolean forMatching = false;	// flag for sentences to be used for matching

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

}
