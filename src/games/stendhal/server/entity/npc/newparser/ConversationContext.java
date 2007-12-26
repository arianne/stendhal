package games.stendhal.server.entity.npc.newparser;

/**
 * NPC conversation context holder
 * 
 * @author Martin Fuchs
 */
public class ConversationContext
{
	// converation context states
	static final int CCS_NONE			 = 0;	// no current conversation context
	static final int CCS_WAIT_FOR_YES_NO = 1;	// wait for a yes/no answer
	static final int CCS_WAIT_FOR_OBJECT = 2;	// wait for a named object

	private int state = CCS_NONE;

	public void setState(int state) {
	    this.state = state;
    }

	public int getState() {
	    return state;
    }
}
