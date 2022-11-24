/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.PlayerMapAdapter;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.util.StringUtils;


/**
 * Action to produce dialogue from an NPC other than
 * the event raiser.
 */
public class TargetNPCSayTextAction extends SayTextAction {

	private final static Logger logger = Logger.getLogger(TargetNPCSayTextAction.class);

	private final String npcName;


	public TargetNPCSayTextAction(final String npcName, final String text) {
		super(text);
		this.npcName = checkNotNull(npcName);
	}

	public TargetNPCSayTextAction(final String npcName, final Iterable<String> texts) {
		super(texts);
		this.npcName = checkNotNull(npcName);
	}

	public TargetNPCSayTextAction(final String npcName, final String[] texts) {
		super(texts);
		this.npcName = checkNotNull(npcName);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName);
		if (npc != null) {
			npc.say(StringUtils.substitute(Rand.rand(texts), new PlayerMapAdapter(player)));
		} else {
			logger.error("NPC \"" + npcName + "\" not found");
		}
	}

	@Override
	public String toString() {
		return "TargetNPCSayText";
	}

	@Override
	public int hashCode() {
		return super.hashCode() * npcName.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TargetNPCSayTextAction)) {
			return false;
		}
		final TargetNPCSayTextAction other = (TargetNPCSayTextAction) obj;
		return npcName.equals(other.npcName) && texts.equals(other.texts);
	}
}
