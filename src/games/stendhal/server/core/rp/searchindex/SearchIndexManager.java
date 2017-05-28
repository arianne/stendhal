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

import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * manages the search index
 *
 * @author hendrik
 */
public class SearchIndexManager {
	private final Set<SearchIndexEntry> index = Sets.newHashSet();

	// keep in sync with search.php
	private final ImmutableSet<String> STOP_WORDS =
			ImmutableSet.of("a", "an", "and", "is", "it", "of", "see", "the", "to", "you");

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
			if (!achievement.isActive()) {
				continue;
			}
			addName(achievement.getTitle(), SearchIndexEntryType.ACHIEVEMENT);
			addDescription(achievement.getTitle(), achievement.getDescription(), SearchIndexEntryType.ACHIEVEMENT, 1000);
		}
	}

	private void npcs() {
		for (SpeakerNPC npc : SingletonRepository.getNPCList()) {
			addName(npc.getName(), SearchIndexEntryType.NPC);
			addDescription(npc.getName(), npc.getDescription(), SearchIndexEntryType.NPC, 1000);
			addDescription(npc.getName(), npc.getJob(), SearchIndexEntryType.NPC, 1000);
		}
	}

	private void items() {
		for (DefaultItem item : SingletonRepository.getEntityManager().getDefaultItems()) {
			addName(item.getItemName(), SearchIndexEntryType.ITEM);
			addDescription(item.getItemName(), item.getDescription(), SearchIndexEntryType.ITEM, 1000);
		}
	}

	private void creatures() {
		for (DefaultCreature creature : SingletonRepository.getEntityManager().getDefaultCreatures()) {
			addName(creature.getCreatureName(), SearchIndexEntryType.CREATURE);
			addDescription(creature.getCreatureName(), creature.getDescription(), SearchIndexEntryType.CREATURE, 1000);
		}
	}

	/**
	 * adds the search index entries for a name variable
	 *
	 * @param name        name of entity
	 * @param type  type of entity
	 */
	private void addName(String name, SearchIndexEntryType type) {
		index.add(new SearchIndexEntry(name, type.getEntityType(), name, 3000 + type.getMinorScore()));

		// If the name consists of multiple words, add each word individually
		// to the index. They will get a lower score to boost exact matches.
		if (name.indexOf(" ") > -1) {
			addDescription(name, name, type, 2000);
		}
	}


	/**
	 * adds the search index entries for a name variable
	 *
	 * @param name        name of entity
	 * @param description description to add
	 * @param type        type of entity
	 * @param baseScore   base score of the match type
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
