/***************************************************************************
 *                 (C) Copyright 2003-2014 - Faiumoni e.V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.config.ItemGroupsXMLLoader;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;

/**
 * Print creature and item data in a format usable for translations.
 * The included strings are:
 * - names
 * - descriptions
 * - creature sentences
 */
public class GenerateDescriptions {
	private static void creatureDescriptions() throws Exception {
		final CreatureGroupsXMLLoader loader = new CreatureGroupsXMLLoader("/data/conf/creatures.xml");
		final List<DefaultCreature> creatures = loader.load();

		for (final DefaultCreature creature : creatures) {
			System.out.println(creature.getCreatureName() + "=" + creature.getCreatureName());
			System.out.println(describe(creature.getDescription(), creature.getCreatureName()));
			if (!creature.getNoiseLines().isEmpty()) {
				Set<String> says = new HashSet<String>();
				for (List<String> lines : creature.getNoiseLines().values()) {
					says.addAll(lines);
				}
				for (String line : says) {
					System.out.println(line + "=" + line);
				}
			}
		}
	}

	private static void itemDescriptions() throws Exception {
		final ItemGroupsXMLLoader loader = new ItemGroupsXMLLoader(new URI(
				"/data/conf/items.xml"));
		final List<DefaultItem> items = loader.load();

		for (final DefaultItem item : items) {
			System.out.println(item.getItemName() + "=" + item.getItemName());
			System.out.println(describe(item.getDescription(), item.getItemName()));
		}
	}

	private static String describe(String desc, String name) {
		if (desc == null || desc.trim().isEmpty()) {
			// The default description
			desc = "You see " + Grammar.a_noun(name) + ".";
		}
		return desc + "=" + desc;
	}

	public static void main(final String[] args) throws Exception {
		itemDescriptions();
		creatureDescriptions();
	}
}
