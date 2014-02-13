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
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * manages the search index
 *
 * @author hendrik
 */
public class SearchIndexManager {
	private final Set<SearchIndexEntry> index = Sets.newHashSet();
	private final ImmutableSet<String> STOP_WORDS =
		ImmutableSet.of("you", "see", "a", "an", "to", "the", "and");

	/**
	 * generates the search index
	 *
	 * @return searchIndex
	 */
	public Set<SearchIndexEntry> generateIndex() {
		achievements();
		creatures();
		items();
		npcs();
		return index;
	}

	private void achievements() {
		for (Achievement achievement : AchievementNotifier.get().getAchievements()) {
			addName(achievement.getTitle(), SearchIndexEntryType.ACHIEVEMENT);
			addDescription(achievement.getTitle(), achievement.getDescription(), SearchIndexEntryType.ACHIEVEMENT, 10);
		}
	}

	private void npcs() {
		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			addName(npc.getName(), SearchIndexEntryType.NPC);
			addDescription(npc.getName(), npc.getDescription(), SearchIndexEntryType.NPC, 10);
			addDescription(npc.getName(), npc.getJob(), SearchIndexEntryType.NPC, 10);
		}
	}

	private void items() {
		for (Item item : SingletonRepository.getEntityManager().getItems()) {
			System.out.print("'" + item.getName() + "' ");
			addName(item.getName(), SearchIndexEntryType.ITEM);
			addDescription(item.getName(), item.getDescription(), SearchIndexEntryType.ITEM, 10);
		}
	}

	private void creatures() {
		for (Creature creature : SingletonRepository.getEntityManager().getCreatures()) {
			addName(creature.getName(), SearchIndexEntryType.CREATURE);
			addDescription(creature.getName(), creature.getDescription(), SearchIndexEntryType.CREATURE, 10);
		}
	}

	/**
	 * adds the search index entries for a name variable
	 *
	 * @param name        name of entity
	 * @param entityType  type of entity
	 */
	private void addName(String name, SearchIndexEntryType type) {
		index.add(new SearchIndexEntry(name, type.getEntityType(), name, 30 + type.getMinorScore()));

		// If the name consists of multiple words, add each word individually
		// to the index. They will get a lower score to boost exact matches.
		if (name.indexOf(" ") > -1) {
			addDescription(name, name, type, 20);
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
		String lowerCaseName = name.toLowerCase(Locale.ENGLISH);

		// add each word individually. it is okay to add the same word multiple
		// times because index is a hashset
		StringTokenizer st = new StringTokenizer(description.toLowerCase(Locale.ENGLISH), " #;:,.-!\"");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.equals(lowerCaseName)) {
				continue;
			}

			if (STOP_WORDS.contains(token)) {
				continue;
			}

			index.add(new SearchIndexEntry(token, type.getEntityType(), name, baseScore + type.getMinorScore()));
		}
	}
}
