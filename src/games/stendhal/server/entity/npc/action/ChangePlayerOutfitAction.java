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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


/**
 * Modify parts or all of a players outfit.
 *
 * @author jackrabbit
 */
@Dev(category=Category.OUTFIT, label="Outfit")
public class ChangePlayerOutfitAction implements ChatAction {

	private final Outfit outfitChange;
	private final boolean addOutfit;
	private final boolean temporaryOutfit;

	/**
	 * Creates a new ChangePlayerOutfitAction
	 *
	 * @param outfit
	 *               the (part of) the outfit to be changed (i.e. removed or added)
	 * @param addOutfit
	 *               addOutfit = true: Add (part of) the outfit to the players current outfit
	 *               addOutfit = false: Remove (part of) the outfit the player currently wears
	 * @param temporaryOutfit is this a temporary outfit or a permanent change?
	 */
	public ChangePlayerOutfitAction(Outfit outfit, boolean addOutfit, boolean temporaryOutfit) {
		this.outfitChange = checkNotNull(outfit);
		this.addOutfit = addOutfit;
		this.temporaryOutfit = temporaryOutfit;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		Outfit outfit = player.getOriginalOutfit();
		if (outfit == null) {
			outfit = player.getOutfit();
		}

		// Depending on if you want to add or remove part of the outfit...
		if (addOutfit) {
			//... either put the new outfit over the old one
			// and set the players outfit to the combination
			Outfit tempOutfit = this.outfitChange.putOver(outfit);
			player.setOutfit(tempOutfit, temporaryOutfit);
		} else {
			//... or remove (parts of) the players outfit
			// and set the players outfit to this combination
			Outfit tempOutfit = outfit.removeOutfit(this.outfitChange);
			player.setOutfit(tempOutfit, temporaryOutfit);
		}
	}

	@Override
	public String toString() {
		return "Outfit used for changing: " + outfitChange.toString()
		+ " If false it should be romved, if true it should be added from/to the current outfit: "
		+ addOutfit;
	}

	@Override
	public int hashCode() {
		return 5039 * outfitChange.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ChangePlayerOutfitAction)) {
			return false;
		}
		final ChangePlayerOutfitAction other = (ChangePlayerOutfitAction) obj;
		if (!outfitChange.equals(other.outfitChange)) {
			return false;
		}
		if (addOutfit != other.addOutfit) {
			return false;
		}
		return true;
	}

}
