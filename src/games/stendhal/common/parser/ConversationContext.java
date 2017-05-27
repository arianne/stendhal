/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.parser;

/**
 * NPC conversation context holder.
 * TODO mf - manage conversation state in the NPC conversation engine
 *
 * @author Martin Fuchs
 */
public class ConversationContext {

	// conversation context states

	/** no current conversation context. */
    static final int CCS_NONE = 0;

    /** Conversation Context wait for a yes/no answer. */
    static final int CCS_WAIT_FOR_YES_NO = 1;

    /** Conversation Context wait for a named object. */
    static final int CCS_WAIT_FOR_OBJECT = 2;


    /** Flag for sentences to be used for matching. */
    protected boolean forMatching = false;

    /** Flag to enable Expression merging. */
    protected boolean mergeExpressions = true;

    /** Flag to enable ignoring of words marked with the type IGN. */
    protected boolean ignoreIgnorable = true;

    /** stores current Conversation state. */
    private int state = CCS_NONE;


    public void setState(final int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setForMatching(final boolean forMatching) {
        this.forMatching = forMatching;
    }

    public boolean isForMatching() {
        return forMatching;
    }

    public void setMergeExpressions(final boolean mergeExpressions) {
        this.mergeExpressions = mergeExpressions;
    }

    public boolean getMergeExpressions() {
        return mergeExpressions;
    }

    public void setIgnoreIgnorable(final boolean ignoreIgnorable) {
        this.ignoreIgnorable = ignoreIgnorable;
    }

    public boolean getIgnoreIgnorable() {
        return ignoreIgnorable;
    }

    /** Default implementation of hashCode() */
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (forMatching ? 1231 : 1237);
		result = prime * result + (ignoreIgnorable ? 1231 : 1237);
		result = prime * result + (mergeExpressions ? 1231 : 1237);
		result = prime * result + state;
		return result;
	}

    /** Default implementation of equals() */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConversationContext)) {
			return false;
		}
		ConversationContext other = (ConversationContext) obj;
		if (forMatching != other.forMatching) {
			return false;
		}
		if (ignoreIgnorable != other.ignoreIgnorable) {
			return false;
		}
		if (mergeExpressions != other.mergeExpressions) {
			return false;
		}
		if (state != other.state) {
			return false;
		}
		return true;
	}
}
