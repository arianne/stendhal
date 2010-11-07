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

package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * 
 * @author jackrabbit
 *
 *	Modify parts or all of a players outfit.
 */
public class ChangePlayerOutfitAction implements ChatAction {

	private final Outfit outfitChange;
	private final boolean removeOrAdd;
	
	/**
	 * Creates a new ChangePlayerOutfitAction
	 * 
	 * @param outfit
	 * 			the (part of) the outfit to be changed (i.e. removed or added)
	 * @param removeOrAdd
	 * 			removeOrAdd = true: Add (part of) the outfit to the players current outfit
	 * 			removeOrAdd = false: Remove (part of) the outfit the player currently wears
	 */
	public ChangePlayerOutfitAction(Outfit outfit, boolean removeOrAdd) {
		this.outfitChange = outfit;
		this.removeOrAdd = removeOrAdd;
	}
	
	
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final Outfit outfit = player.getOutfit();
		
		// Depending on if you want to add or remove part of the outfit...
		if (removeOrAdd == true) {
			//... either put the new outfit over the old one
			// and set the players outfit to the combination
			Outfit tempOutfit = this.outfitChange.putOver(outfit);
			player.setOutfit(tempOutfit, true);
		}
		else {
			//... or remove (parts of) the players outfit
			// and set the players outfit to this combination
			Outfit tempOutfit = outfit.removeOutfit(this.outfitChange);
			player.setOutfit(tempOutfit, true);
		}
	}
	
	public String toString() {
		return "Outfit used for changing: " + outfitChange.toString()
		+ " If false it should be romved, if true it should be added from/to the current outfit: "
		+ removeOrAdd;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChangePlayerOutfitAction other = (ChangePlayerOutfitAction) obj;
		if (outfitChange != other.outfitChange) {
			return false;
		}
		if (removeOrAdd != other.removeOrAdd) {
			return false;
		}
		return true;
	}

}
