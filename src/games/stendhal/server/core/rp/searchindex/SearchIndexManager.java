/***************************************************************************
 *                    (C) Copyright 2014 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.searchindex;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.HashSet;
import java.util.StringTokenizer;

import com.google.common.collect.Sets;

/**
 * manages the search index
 *
 * @author hendrik
 */
public class SearchIndexManager {
	private final HashSet<SearchIndexEntry> index = Sets.newHashSet();

	/**
	 * stores the search index
	 */
	public void store() {
		
		npcs();
		// TODO: invoke DBCommand
	}

	private void npcs() {
		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			addName(npc.getName(), SearchIndexEntryType.NPC);
			addDescription(npc.getName(), npc.getDescription(), SearchIndexEntryType.NPC, 10);
			addDescription(npc.getName(), npc.getJob(), SearchIndexEntryType.NPC, 10);
		}
	}

	/**
	 * adds the search index entries for a name variable
	 *
	 * @param name        name of entity
	 * @param entityType  type of entity
	 */
	private void addName(String name, SearchIndexEntryType type) {
		index.add(new SearchIndexEntry(name, type.getEntityType(), name, 30));

		// If the name consists of multiple words, add each word individually
		// to the index. They will get a lower score to boost exact matches.
		if (name.indexOf(" ") > -1) {
			addDescription(name, name, type, 20 + type.getMinorScore());
		}
	}


	/**
	 * adds the search index entries for a name variable
	 *
	 * @param name        name of entity
	 * @param entityType  type of entity
	 * @param minorScore  base score of the match type
	 */
	private void addDescription(String name, String description, SearchIndexEntryType type, int baseScore) {
		if (description == null) {
			return;
		}

		// add each word individually. it is okay to add the same word multiple
		// times because index is a hashset
		StringTokenizer st = new StringTokenizer(description);
		while (st.hasMoreTokens()) {
			index.add(new SearchIndexEntry(st.nextToken(), type.getEntityType(), name, baseScore +  + type.getMinorScore()));
		}
	}
}
