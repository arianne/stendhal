/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import java.util.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Modify all or part of a player's outfit while preserving temporary outfits.
 */
@Dev(category=Category.OUTFIT, label="Outfit")
public class ChangePlayerOutfitAndPreserveTempAction implements ChatAction {

	private final Outfit outfitChange;
	private final boolean addOutfit;

	public ChangePlayerOutfitAndPreserveTempAction(final Outfit outfitChange, final boolean addOutfit) {
		this.outfitChange = checkNotNull(outfitChange);
		this.addOutfit = addOutfit;
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		final boolean isTemp = player.has("outfit_org");
		if (!isTemp) {
			(new ChangePlayerOutfitAction(outfitChange, addOutfit, false)).fire(player, sentence, npc);
			return;
		}

		Outfit currentOutfit = player.getOutfit();

		if (addOutfit) {
			// Add to current outfit
			player.setOutfit(outfitChange.putOver(currentOutfit), isTemp);
		} else {
			// Remove from current outfit
			player.setOutfit(currentOutfit.removeOutfit(this.outfitChange), isTemp);
		}
	}

	@Override
	public String toString() {
		return "Outfit used for changing: " + outfitChange.toString()
		+ " If false it should be removed, if true it should be added to the current outfit: "
		+ addOutfit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(outfitChange);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ChangePlayerOutfitAndPreserveTempAction)) {
			return false;
		}
		ChangePlayerOutfitAndPreserveTempAction other = (ChangePlayerOutfitAndPreserveTempAction) obj;
		return outfitChange.equals(other.outfitChange)
			&& addOutfit == other.addOutfit;
	}
}
