/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.Iterator;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Check NPC paths for problems.
 */
public class NPCPathCheck extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		final StringBuilder res = new StringBuilder();
		final NPCList npcs = SingletonRepository.getNPCList();
		boolean found = false;
		for (SpeakerNPC npc : npcs) {
			if (!checkPath(npc, res)) {
				found = true;
			}
		}
		if (found) {
			res.insert(0, "The following problems were found:\n");
		} else {
			res.insert(0, "No problems found in NPC paths.");
		}

		admin.sendPrivateText(res.toString());
	}

	/**
	 * Check the path of an NPC.
	 *
	 * @param npc
	 * @param msg place to append problem descriptions
	 * @return <code>true</code> if no problems were found, <code>false</code>
	 * 	if one or more problems were found
	 */
	private boolean checkPath(SpeakerNPC npc, StringBuilder msg) {
		FixedPath path = npc.getPath();
		if (path == null) {
			// Nothing to check
			return true;
		}
		boolean ok = true;
		if (!path.isLoop()) {
			ok = false;
			msg.append(npc.getName());
			msg.append(" has a path that is not a loop.\n");
		}
		if (!checkNodes(npc, path, msg)) {
			ok = false;
		}
		return ok;
	}

	/**
	 * Check a path for problems.
	 *
	 * @param npc npc owning the path
	 * @param path
	 * @param msg place for appending error descriptions
	 * @return <code>true</code> if no problems were found, <code>false</code>
	 * 	if one or more problems were found
	 */
	private boolean checkNodes(SpeakerNPC npc, FixedPath path, StringBuilder msg) {
		boolean ok = true;
		List<Node> nodes = path.getNodeList();
		Iterator<Node> it = nodes.iterator();
		Node previous = it.next();
		Node current = previous;
		while (it.hasNext()) {
			current = it.next();
			if (!checkTwoNodes(npc, previous, current, msg)) {
				ok = false;
			}
			previous = current;
		}
		if (path.isLoop() && !checkTwoNodes(npc, current, path.getNodeList().get(0), msg)) {
			ok = false;
		}

		return ok;
	}

	/**
	 * Check the path between two nodes for collisions.
	 *
	 * @param npc npc owning the path
	 * @param first first node
	 * @param second second node
	 * @param msg place for appending error descriptions
	 * @return <code>true</code> if no problems were found, <code>false</code>
	 * 	if one or more problems were found
	 */
	private boolean checkTwoNodes(SpeakerNPC npc, Node first, Node second, StringBuilder msg) {
		StendhalRPZone zone = npc.getZone();
		ActiveEntity entity = new ActiveEntity() {};
		entity.setPosition(first.getX(), first.getY());
		/*
		 * Simulate walking the path. The final location is not checked, but
		 * that's not a problem for looped paths, as every node will be the
		 * first for one check.
		 */
		while ((entity.getX() != second.getX()) || (entity.getY() != second.getY())) {
			if (zone.collides(entity.getX(), entity.getY())) {
				msg.append(npc.getName());
				msg.append(" will hit collision at (");
				msg.append(entity.getX());
				msg.append(",");
				msg.append(entity.getY());
				msg.append(").\n");
				return false;
			}
			entity.faceto(second.getX(), second.getY());
			Direction d = entity.getDirection();
			entity.setPosition(entity.getX() + d.getdx(), entity.getY() + d.getdy());
		}
		return true;
	}
}
