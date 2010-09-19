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
package games.stendhal.server.entity.npc.parser;

/**
 * NPC conversation context holder. TODO mf - manage conversation state in the NPC conversation engine
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
    
    /** Flag to enable storing new words into the database. */
    protected boolean persistNewWords = false; 
    
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

    public void setPersistNewWords(final boolean persistNewWords) {
        this.persistNewWords = persistNewWords;
    }

    public boolean getPersistNewWords() {
        return persistNewWords;
    }

    public void setIgnoreIgnorable(final boolean ignoreIgnorable) {
        this.ignoreIgnorable = ignoreIgnorable;
    }

    public boolean getIgnoreIgnorable() {
        return ignoreIgnorable;
    }

}
