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
package games.stendhal.tools.creaturesxml;

import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.config.ItemsXMLLoader;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

/*
 * EditorXML.java
 *
 * Created on 23 de mayo de 2007, 13:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author miguel
 */
public class EditorXML {
	private boolean creaturesChange;

	private boolean itemsChange;

	private List<DefaultCreature> creaturesList;

	private List<DefaultItem> itemsList;

	public static final String itemsFile = "/data/conf/items.xml";

	public static final String creaturesFile = "data/conf/creatures.xml";

	public static final String[] slots = new String[] { "bag", "lhand",
			"rhand", "armor", "head", "legs", "cloak", "feet", "finger",
			"spells", "keyring" };

	JCreature creatureFrame;

	JItem itemFrame;

	/**
	 * Creates a new instance of EditorXML.
	 *  
	 * @throws SAXException
	 */
	public EditorXML() throws SAXException {
		creaturesChange = false;
		itemsChange = false;

		creaturesList = loadCreaturesList(EditorXML.creaturesFile);
		itemsList = loadItemsList(EditorXML.itemsFile);

		creatureFrame = new JCreature(this);
		itemFrame = new JItem(this);
	}

	public static void main(String[] args) throws SAXException {
		EditorXML xml = new EditorXML();
		xml.setVisible(true);
	}

	void sortCreatures(final List<DefaultCreature> creatures) {
		Collections.sort(creatures, new Comparator<DefaultCreature>() {

			@Override
			public int compare(DefaultCreature o1, DefaultCreature o2) {
				return o1.getLevel() - o2.getLevel();
			}
		});
	}

	public void updateFrameContents() {
		creatureFrame.setLists();
		itemFrame.setLists();
	}

	public void updateCreaturesFromFile(String ref) {
		creaturesList = loadCreaturesList(ref);
		updateFrameContents();
	}

	public void updateItemsFromFile(String ref) throws SAXException {
		itemsList = loadItemsList(ref);
		updateFrameContents();
	}

	private List<DefaultCreature> loadCreaturesList(String ref) {
		CreatureGroupsXMLLoader loader = new CreatureGroupsXMLLoader(ref);
		List<DefaultCreature> creatures = loader.load();
		sortCreatures(creatures);

		return creatures;
	}

	List<DefaultCreature> getCreatures() {
		return creaturesList;
	}

	private List<DefaultItem> loadItemsList(String ref) throws SAXException {
		ItemsXMLLoader itemsLoader = new ItemsXMLLoader();

		try {
			List<DefaultItem> items = itemsLoader.load(new URI(ref));

			sortItems(items);

			return items;
		} catch (URISyntaxException e) {
			throw new SAXException(e);
		}
	}

	void sortItems(final List<DefaultItem> items) {
		Collections.sort(items, new Comparator<DefaultItem>() {

			@Override
			public int compare(DefaultItem o1, DefaultItem o2) {
				int cmp = o1.getItemClass().compareTo(o2.getItemClass());
				if (cmp == 0) {
					return o1.getValue() - o2.getValue();
				}

				return cmp;
			}
		});
	}

	List<DefaultItem> getItems() {
		return itemsList;
	}

	public boolean hasChanges() {
		return creaturesChange || itemsChange;
	}

	public void requestFormClosing(JFrame frame) {
		if (hasChanges()) {
			int answer = JOptionPane.showConfirmDialog(frame,
					"Exit without saving?");
			if (answer == JOptionPane.YES_OPTION) {
				System.exit(0);
			} else if (answer == JOptionPane.NO_OPTION) {
				frame.setVisible(true);
			}
		} else {
			System.exit(0);
		}
	}

	void creaturesChange() {
		creaturesChange = true;
	}

	void creaturesChangeClear() {
		creaturesChange = false;
	}

	void itemsChange() {
		itemsChange = true;
	}

	void itemsChangeClear() {
		itemsChange = false;
	}

	public void setVisible(boolean b) {
		itemFrame.setVisible(true);
		creatureFrame.setVisible(true);
	}

}
