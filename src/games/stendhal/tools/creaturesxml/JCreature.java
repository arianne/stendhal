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

/*
 * JCreature.java
 *
 * Created on 6 de mayo de 2007, 22:52
 */

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.creature.impl.EquipItem;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author miguel
 */
@SuppressWarnings("serial")
public class JCreature extends javax.swing.JFrame {

	boolean justUpdateCreature;

	private List<DefaultCreature> filteredCreatures;

	private EditorXML xml;

	/**
	 * Creates new form JCreature.
	 *  
	 * @param xml
	 */
	public JCreature(EditorXML xml) {
		this.xml = xml;
		initComponents();
		loadData();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		justUpdateCreature = false;
	}

	private void loadData() {
		filteredCreatures = xml.getCreatures();
		setLists();
		creatureList.setSelectedIndex(0);
		refresh();
	}

	void setLists() {
		creatureClass.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "animal", "beholder", "ent", "dark_elf",
						"demon", "dwarf", "elemental", "elf", "gargoyle",
						"giant", "giant_animal", "giant_human", "gnome",
						"goblin", "golem", "huge_animal", "human", "kobold",
						"minotaur", "mummy", "mutant", "mythical_animal",
						"orc", "ratfolk", "small_animal", "troll", "undead",
						"vampire" }));

		updateItemLists();
		updateCreatureList(null, filter.getText());
	}

	private void updateItemLists() {
		itemList.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				return xml.getItems().get(i).getItemName();
			}

			@Override
			public int getSize() {
				return xml.getItems().size();
			}
		});
	}

	private void updateCreatureList(String field, String filter) {
		filteredCreatures = new LinkedList<DefaultCreature>();

		if (filter.length() > 0) {
			for (DefaultCreature c : xml.getCreatures()) {
				boolean add = false;

				if (field == null || field.equals("Name")) {
					add = c.getCreatureName().contains(filter);
				} else if (field.contains(">")) {
					add = c.getLevel() > Integer.parseInt(filter);
				} else if (field.contains("<")) {
					add = c.getLevel() < Integer.parseInt(filter);
				}

				if (add) {
					filteredCreatures.add(c);
				}
			}
		} else {
			filteredCreatures = xml.getCreatures();
		}

		creatureList.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				DefaultCreature creature = filteredCreatures.get(i);
				return "(" + creature.getLevel() + ") "
						+ creature.getCreatureName();
			}

			@Override
			public int getSize() {
				return filteredCreatures.size();
			}
		});

		amountOfCreatures.setText(Integer.toString(xml.getCreatures().size()));
	}

//	private List<DefaultItem> loadItemsList(String ref) throws SAXException {
//		ItemsXMLLoader itemsLoader = new ItemsXMLLoader();
//
//		try {
//			List<DefaultItem> items = itemsLoader.load(new URI(ref));
//
//			return items;
//		} catch (URISyntaxException e) {
//			throw new SAXException(e);
//		}
//	}

	private void clean(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(2, 2, creatureImage.getWidth() - 2,
				creatureImage.getHeight() - 2);
	}

	private void drawSinglePart(Sprite sprite, double w, double h, Graphics g) {
		clean(g);

		// TODO: Draw bounding box of logical size passed (to show relative
		// position/extents). Center left-right, Bottom align.

		/*
		 * Calculate sizes based on a 3x4 template layout.
		 */
		w = sprite.getWidth() / (3.0 * 32.0);
		h = sprite.getHeight() / (4.0 * 32.0);

		if (w == 1.0D && h == 2D) {
			w = 1.5D;
		}
		int offset = (int) (h * 2D * 32D);
		if (sprite.getHeight() < offset) {
			offset = 0;
		}
		int x = creatureImage.getWidth() / 2 - (int) ((w * 32D) / 2D);
		int y = creatureImage.getHeight() / 2 - (int) ((h * 32D) / 2D);
		sprite.draw(g, x, y, 0, offset, (int) (w * 32D), (int) (h * 32D));
	}

	private void refresh() {
		int pos = creatureList.getSelectedIndex();

		if (pos < 0) {
			return;
		}

		DefaultCreature actual = filteredCreatures.get(pos);

		if (actual.getCreatureName() == null) {
			return;
		}
		creatureName.setText(actual.getCreatureName());
		creatureClass.setSelectedItem(actual.getCreatureClass());
		creatureSubclass.setText(actual.getCreatureSubclass());
		creatureSize.setText((int) actual.getWidth() + ","
				+ (int) actual.getHeight());
		creatureTileid.setText(actual.getTileId().replace(
				"../../tileset/logic/creature/", ""));
		String gfxLocation = "/" + actual.getCreatureClass() + "/"
				+ actual.getCreatureSubclass() + ".png";
		Sprite spr = SpriteStore.get().getSprite(
				"stendhal/data/sprites/monsters" + gfxLocation);

		drawSinglePart(spr, actual.getWidth(), actual.getHeight(),
				creatureImage.getGraphics());
		creatureGFXLocation.setText(gfxLocation);
		creatureDescription.setText(actual.getDescription());

		creatureATK.setText(Integer.toString(actual.getAtk()));
		creatureDEF.setText(Integer.toString(actual.getDef()));
		creatureHP.setText(Integer.toString(actual.getHP()));
		creatureSpeed.setText(Double.toString(actual.getSpeed()));

		creatureLevel.setText(Integer.toString(actual.getLevel()));
		creatureXP.setText(Integer.toString(actual.getXP() / 20));
		creatureRespawn.setText(Integer.toString(actual.getRespawnTime()));

		SuggestAttributeButtonActionPerformed(null);

		StringBuilder os = new StringBuilder("");

		double value = 0;
		for (DropItem item : actual.getDropItems()) {
			os.append(item.name + "; [" + item.min + "," + item.max + "]; "
					+ item.probability + "\n");
			value += (getItemValue(item.name) * (item.probability)
					* (item.max + item.min) / 2.0);
		}

		creatureValue.setText(Integer.toString((int) value));

		if (actual.getCreatureName() != null) {
			creatureDrops.setText(os.toString());
		}

		os = new StringBuilder("");
		for (EquipItem item : actual.getEquipedItems()) {
			os.append(item.name + "; " + item.slot + "; " + item.quantity
					+ "\n");
		}

		if (actual.getCreatureName() != null) {
			creatureEquips.setText(os.toString());
		}

		os = new StringBuilder("");
		
		HashMap<String, LinkedList<String>> noises = actual.getNoiseLines();
		for (Map.Entry<String, LinkedList<String>> it : noises.entrySet()) {
			for (String line : it.getValue()) {
				os.append("says[" + it.getKey() + "]: " + line + "\n");
			}
		}

		Map<String, String> aiList = actual.getAIProfiles();
		for (Map.Entry<String, String> profile : aiList.entrySet()) {
			os.append(profile.getKey());
			if (profile.getValue() != null) {
				os.append(" = " + profile.getValue());
			}
			os.append("\n");
		}

		if (actual.getCreatureName() != null) {
			creatureAI.setText(os.toString());
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		creatureRespawn1 = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		creatureList = new javax.swing.JList();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		creatureSubclass = new javax.swing.JTextField();
		creatureSize = new javax.swing.JTextField();
		creatureGFXLocation = new javax.swing.JTextField();
		creatureImage = new javax.swing.JPanel();
		jLabel6 = new javax.swing.JLabel();
		creatureTileid = new javax.swing.JTextField();
		creatureClass = new javax.swing.JComboBox();
		updateGFXButton = new javax.swing.JButton();
		creatureValue = new javax.swing.JTextField();
		jLabel18 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		creatureName = new javax.swing.JTextField();
		jLabel17 = new javax.swing.JLabel();
		amountOfCreatures = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		creatureLevel = new javax.swing.JTextField();
		creatureXP = new javax.swing.JTextField();
		jLabel10 = new javax.swing.JLabel();
		creatureRespawn = new javax.swing.JTextField();
		SuggestAttributeButton = new javax.swing.JButton();
		jLabel12 = new javax.swing.JLabel();
		creatureATK = new javax.swing.JTextField();
		jLabel13 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		creatureDEF = new javax.swing.JTextField();
		creatureHP = new javax.swing.JTextField();
		creatureSpeed = new javax.swing.JTextField();
		jLabel16 = new javax.swing.JLabel();
		suggestedATK = new javax.swing.JTextField();
		suggestedDEF = new javax.swing.JTextField();
		suggestedHP = new javax.swing.JTextField();
		suggestedRespawn = new javax.swing.JTextField();
		suggestedXP = new javax.swing.JTextField();
		justEditCreature = new javax.swing.JCheckBox();
		data = new javax.swing.JTabbedPane();
		jPanel2 = new javax.swing.JPanel();
		creatureDescription = new javax.swing.JTextArea();
		jPanel5 = new javax.swing.JPanel();
		creatureDrops = new javax.swing.JTextArea();
		jPanel6 = new javax.swing.JPanel();
		creatureEquips = new javax.swing.JTextArea();
		jPanel7 = new javax.swing.JPanel();
		creatureAI = new javax.swing.JTextArea();
		jPanel8 = new javax.swing.JPanel();
		jLabel11 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		itemList = new javax.swing.JList();
		pushIntoArea = new javax.swing.JButton();
		addButton = new javax.swing.JButton();
		setButton = new javax.swing.JButton();
		filter = new javax.swing.JTextField();
		FilterButton = new javax.swing.JButton();
		ClearButton = new javax.swing.JButton();
		filteredField = new javax.swing.JComboBox();
		jMenuBar1 = new javax.swing.JMenuBar();
		jLoad = new javax.swing.JMenu();
		jLoadFromFile = new javax.swing.JMenuItem();
		jSave = new javax.swing.JMenu();
		jSaveToFile = new javax.swing.JMenuItem();

		creatureRespawn1.setText("jTextField8");

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Stendhal Creature Editor 2.30");
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		creatureList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			@Override
			public int getSize() {
				return strings.length;
			}

			@Override
			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		creatureList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		creatureList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
				creatureListValueChanged(evt);
			}
		});

		jScrollPane1.setViewportView(creatureList);

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel2.setFont(new java.awt.Font("Arial", 1, 11));
		jLabel2.setText("Class:");

		jLabel3.setText("Subclass:");

		jLabel4.setText("GFX Location:");

		jLabel5.setText("Size:");

		creatureSubclass.setText("jTextField3");

		creatureSize.setText("jTextField4");

		creatureGFXLocation.setEditable(false);
		creatureGFXLocation.setText("jTextField5");

		creatureImage.setBackground(new java.awt.Color(255, 255, 255));
		creatureImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(
				0, 0, 0)));
		org.jdesktop.layout.GroupLayout creatureImageLayout = new org.jdesktop.layout.GroupLayout(
				creatureImage);
		creatureImage.setLayout(creatureImageLayout);
		creatureImageLayout.setHorizontalGroup(creatureImageLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 178,
				Short.MAX_VALUE));
		creatureImageLayout.setVerticalGroup(creatureImageLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 198,
				Short.MAX_VALUE));

		jLabel6.setText("Tiled ID");

		creatureTileid.setText("jTextField6");

		creatureClass.setEditable(true);
		creatureClass.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		updateGFXButton.setFont(new java.awt.Font("Tahoma", 0, 10));
		updateGFXButton.setText("Update GFX");
		updateGFXButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateGFXButtonActionPerformed(evt);
			}
		});

		creatureValue.setEditable(false);
		creatureValue.setText("jTextField1");

		jLabel18.setText("Value");

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout.createSequentialGroup().addContainerGap().add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								jPanel1Layout.createSequentialGroup().add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jLabel2).add(jLabel5).add(
												jLabel4).add(jLabel6).add(
												jLabel3).add(jLabel18)).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING,
												false).add(creatureTileid).add(
												creatureGFXLocation).add(
												creatureSize).add(
												creatureSubclass).add(
												creatureClass, 0, 256,
												Short.MAX_VALUE).add(
												creatureValue,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												148,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).add(
								updateGFXButton)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED, 86,
						Short.MAX_VALUE).add(creatureImage,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(49,
						49, 49)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout.createSequentialGroup().addContainerGap().add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								jPanel1Layout.createSequentialGroup().add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel2).add(
												creatureClass,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel3).add(
												creatureSubclass,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel5).add(
												creatureSize,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel4).add(
												creatureGFXLocation,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel6).add(
												creatureTileid,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												creatureValue,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												28, Short.MAX_VALUE).add(
												jLabel18)).add(19, 19, 19).add(
										updateGFXButton)).add(creatureImage,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addContainerGap()));

		jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel1.setFont(new java.awt.Font("Arial", 1, 11));
		jLabel1.setText("Name");

		creatureName.setText("jTextField1");

		jLabel17.setText("creatures available");

		amountOfCreatures.setText("jLabel18");

		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout.createSequentialGroup().addContainerGap().add(
						jLabel1).add(52, 52, 52).add(creatureName,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED, 187,
						Short.MAX_VALUE).add(amountOfCreatures).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(jLabel17).addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel3Layout.createSequentialGroup().addContainerGap().add(
						jPanel3Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jPanel3Layout.createParallelGroup(
										org.jdesktop.layout.GroupLayout.BASELINE).add(
										creatureName,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
										jLabel17).add(amountOfCreatures)).add(
								jLabel1)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)));

		jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel7.setText("Attributes");

		jLabel8.setText("Level:");

		jLabel9.setText("XP:");

		creatureLevel.setText("jTextField2");

		creatureXP.setText("jTextField7");

		jLabel10.setText("Respawn:");

		creatureRespawn.setText("jTextField8");

		SuggestAttributeButton.setText("Suggest Attributes");
		SuggestAttributeButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SuggestAttributeButtonActionPerformed(evt);
			}
		});

		jLabel12.setText("ATK:");

		creatureATK.setText("jTextField1");

		jLabel13.setText("DEF:");

		jLabel14.setText("HP:");

		jLabel15.setText("Speed:");

		creatureDEF.setText("jTextField2");

		creatureHP.setText("jTextField3");

		creatureSpeed.setText("jTextField4");

		jLabel16.setText("turns");

		suggestedATK.setBackground(new java.awt.Color(204, 204, 204));
		suggestedATK.setEditable(false);

		suggestedDEF.setBackground(new java.awt.Color(204, 204, 204));
		suggestedDEF.setEditable(false);

		suggestedHP.setBackground(new java.awt.Color(204, 204, 204));
		suggestedHP.setEditable(false);

		suggestedRespawn.setBackground(new java.awt.Color(204, 204, 204));
		suggestedRespawn.setEditable(false);

		suggestedXP.setBackground(new java.awt.Color(204, 204, 204));
		suggestedXP.setEditable(false);

		justEditCreature.setText("I am sure");
		justEditCreature.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		justEditCreature.setMargin(new java.awt.Insets(0, 0, 0, 0));
		justEditCreature.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				justEditCreatureActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel4Layout.createSequentialGroup().addContainerGap().add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jPanel4Layout.createSequentialGroup().add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jLabel12).add(jLabel14).add(
												jLabel13).add(jLabel15)).add(
										22, 22, 22).add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING,
												false).add(creatureSpeed).add(
												creatureHP).add(creatureDEF).add(
												creatureATK,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												134,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING,
												false).add(suggestedDEF).add(
												suggestedHP).add(
												suggestedATK,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												105,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
										20, 20, 20).add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING).add(
												jPanel4Layout.createSequentialGroup().add(
														jPanel4Layout.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING).add(
																jLabel9).add(
																jLabel10).add(
																jLabel8)).addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED).add(
														jPanel4Layout.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING).add(
																jPanel4Layout.createSequentialGroup().add(
																		jPanel4Layout.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING,
																				false).add(
																				creatureRespawn).add(
																				creatureXP,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				95,
																				Short.MAX_VALUE)).addPreferredGap(
																		org.jdesktop.layout.LayoutStyle.RELATED).add(
																		jPanel4Layout.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING,
																				false).add(
																				org.jdesktop.layout.GroupLayout.TRAILING,
																				suggestedXP,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				82,
																				Short.MAX_VALUE).add(
																				org.jdesktop.layout.GroupLayout.TRAILING,
																				suggestedRespawn)).addPreferredGap(
																		org.jdesktop.layout.LayoutStyle.RELATED).add(
																		jLabel16).addPreferredGap(
																		org.jdesktop.layout.LayoutStyle.RELATED,
																		17,
																		Short.MAX_VALUE)).add(
																creatureLevel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																245,
																Short.MAX_VALUE))).add(
												jPanel4Layout.createSequentialGroup().add(
														justEditCreature).addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED,
														90, Short.MAX_VALUE).add(
														SuggestAttributeButton)))).add(
								jLabel7)).addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel4Layout.createSequentialGroup().addContainerGap().add(
						jLabel7,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel12).add(creatureATK,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								suggestedATK,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								creatureDEF,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								jLabel13).add(suggestedDEF,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel14).add(creatureHP,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								suggestedHP,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								creatureSpeed,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								jLabel15).add(SuggestAttributeButton).add(
								justEditCreature)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel4Layout.createSequentialGroup().addContainerGap(24,
						Short.MAX_VALUE).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel8).add(creatureLevel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel9).add(creatureXP,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								suggestedXP,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel10).add(jLabel16).add(creatureRespawn,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								suggestedRespawn,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
						53, 53, 53)));

		creatureDescription.setColumns(20);
		creatureDescription.setLineWrap(true);
		creatureDescription.setRows(5);

		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				creatureDescription,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				creatureDescription,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		data.addTab("Description", jPanel2);

		creatureDrops.setColumns(20);
		creatureDrops.setLineWrap(true);
		creatureDrops.setRows(5);

		org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(creatureDrops,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279,
				Short.MAX_VALUE));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING, creatureDrops,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 172,
				Short.MAX_VALUE));
		data.addTab("Drops", jPanel5);

		creatureEquips.setColumns(20);
		creatureEquips.setLineWrap(true);
		creatureEquips.setRows(5);

		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(creatureEquips,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(creatureEquips,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		data.addTab("Equips", jPanel6);

		creatureAI.setColumns(20);
		creatureAI.setLineWrap(true);
		creatureAI.setRows(5);

		org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(
				jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(creatureAI,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(creatureAI,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		data.addTab("AI Profile", jPanel7);

		jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel11.setText("Items:");

		itemList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			@Override
			public int getSize() {
				return strings.length;
			}

			@Override
			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane2.setViewportView(itemList);

		pushIntoArea.setText("<<");
		pushIntoArea.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				pushIntoAreaActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(
				jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel8Layout.createSequentialGroup().addContainerGap().add(
						jPanel8Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jScrollPane2,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								365, Short.MAX_VALUE).add(
								jPanel8Layout.createSequentialGroup().add(
										jLabel11).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										261, Short.MAX_VALUE).add(pushIntoArea).addContainerGap()))));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel8Layout.createSequentialGroup().addContainerGap().add(
						jPanel8Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel11).add(pushIntoArea)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jScrollPane2,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170,
						Short.MAX_VALUE)));

		addButton.setText("Add");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		setButton.setFont(new java.awt.Font("Arial", 1, 12));
		setButton.setText("Set");
		setButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setButtonActionPerformed(evt);
			}
		});

		filter.setBackground(new java.awt.Color(204, 204, 204));

		FilterButton.setText("Filter");
		FilterButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				FilterButtonActionPerformed(evt);
			}
		});

		ClearButton.setText("Clear");
		ClearButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ClearButtonActionPerformed(evt);
			}
		});

		filteredField.setBackground(new java.awt.Color(204, 204, 204));
		filteredField.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Name", "Level >", "Level <" }));

		jLoad.setText("Load");
		jLoadFromFile.setText("From file");
		jLoadFromFile.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jLoadFromFileActionPerformed(evt);
			}
		});

		jLoad.add(jLoadFromFile);

		jMenuBar1.add(jLoad);

		jSave.setText("Save");
		jSaveToFile.setText("To File");
		jSaveToFile.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jSaveToFileActionPerformed(evt);
			}
		});

		jSave.add(jSaveToFile);

		jMenuBar1.add(jSave);

		setJMenuBar(jMenuBar1);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING, false).add(
								layout.createSequentialGroup().addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										data,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										284,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel8,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).add(jPanel4,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(jPanel3,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								layout.createSequentialGroup().add(
										filteredField,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										9, Short.MAX_VALUE).add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												org.jdesktop.layout.GroupLayout.TRAILING,
												layout.createSequentialGroup().add(
														filter,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														84,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED).add(
														FilterButton)).add(
												org.jdesktop.layout.GroupLayout.TRAILING,
												ClearButton))).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								layout.createSequentialGroup().add(addButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										127, Short.MAX_VALUE).add(setButton)).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								jScrollPane1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								239, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jPanel3,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								layout.createSequentialGroup().add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												FilterButton).add(
												filter,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										ClearButton)).add(filteredField,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup().add(
										jPanel1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel4,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jPanel8,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
												data, 0, 0, Short.MAX_VALUE))).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup().add(
										jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										576, Short.MAX_VALUE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												addButton).add(setButton)))).addContainerGap()));
		pack();
	} // </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_formWindowClosing
		xml.requestFormClosing(this);
	} // GEN-LAST:event_formWindowClosing

	private void FilterButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_FilterButtonActionPerformed
		updateCreatureList((String) filteredField.getSelectedItem(),
				filter.getText());
	} // GEN-LAST:event_FilterButtonActionPerformed

	private void ClearButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_ClearButtonActionPerformed
		filter.setText("");
		updateCreatureList(null, filter.getText());
	} // GEN-LAST:event_ClearButtonActionPerformed

	private void justEditCreatureActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_justEditCreatureActionPerformed
		justUpdateCreature = !justUpdateCreature;
	} // GEN-LAST:event_justEditCreatureActionPerformed

	private void pushIntoAreaActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_pushIntoAreaActionPerformed
		int i = data.getSelectedIndex();

		if (itemList.getSelectedIndex() < 0) {
			return;
		}
		List<DefaultItem> items = xml.getItems();
		switch (i) {
		case 0:
			creatureDescription.insert(
					items.get(itemList.getSelectedIndex()).getItemName(),
					creatureDescription.getCaretPosition());
			break;
		case 1:
			creatureDrops.insert(
					items.get(itemList.getSelectedIndex()).getItemName(),
					creatureDrops.getCaretPosition());
			break;
		case 2:
			creatureEquips.insert(
					items.get(itemList.getSelectedIndex()).getItemName(),
					creatureEquips.getCaretPosition());
			break;
		}
	} // GEN-LAST:event_pushIntoAreaActionPerformed

	private void jLoadFromFileActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jLoadFromFileActionPerformed
		String cdata = null;

		JFileChooser fc = new JFileChooser(".");
		fc.setSelectedFile(new File(EditorXML.creaturesFile));
		fc.setDialogTitle("Choose creatures XML file");
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == 0) {
			java.io.File file = fc.getSelectedFile();
			cdata = file.getAbsolutePath();
		} else {
			return;
		}

		xml.updateCreaturesFromFile(cdata);
		xml.creaturesChangeClear();
	} // GEN-LAST:event_jLoadFromFileActionPerformed

	private void updateGFXButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_updateGFXButtonActionPerformed
		try {
			String clazz = (String) creatureClass.getSelectedItem();
			String subclass = creatureSubclass.getText();

			String gfxLocation = "/" + clazz + "/" + subclass + ".png";
			Sprite spr = SpriteStore.get().getSprite(
					"data/sprites/monsters" + gfxLocation);
			String[] sizes = creatureSize.getText().split(",");

			drawSinglePart(spr, Integer.parseInt(sizes[0]),
					Integer.parseInt(sizes[1]), creatureImage.getGraphics());
			creatureGFXLocation.setText(gfxLocation);
		} catch (NumberFormatException e) {
		}
	} // GEN-LAST:event_updateGFXButtonActionPerformed

//	private int suggestedXPValue(int level) {
//		int base = (int) Math.pow(10, level / 100 + 1);
//		int xp = ((level * level * level / 100 + level * 10) / base) * base;
//		return xp;
//	}
//
//	private int suggestedRespawnValue(int level) {
//		int base = (int) Math.pow(10, level / 100 + 1);
//		int respawn = ((900 + (level * level) / 10 + (level * level * level) / 400) / base)
//				* base;
//		return respawn;
//	}

	private void SuggestAttributeButtonActionPerformed(
			java.awt.event.ActionEvent evt) { // GEN-FIRST:event_SuggestAttributeButtonActionPerformed
		int level = Integer.parseInt(creatureLevel.getText());

		int base = (int) Math.pow(10, level / 100 + 1);

		int atk = (int) ((25 * Math.sqrt(level) + (level * level / 3000.0)
				- level * 0.2 - 10)
				/ base * base);
		int def = (int) ((4.2 * Math.sqrt(level) + (level * level / 100000.0)
				+ 7 / (level + 1) - level * 0.08)
				/ base * base);
		int hp = (int) (level * level * 0.08 + level * 2 + 50);
		int xp = ((level * level * level / 100 + level * 10) / base) * base;
		int respawn = ((900 + (level * level) / 10 + (level * level * level) / 400) / base)
				* base;

//		int pos = creatureList.getSelectedIndex();
//		DefaultCreature actual = (DefaultCreature) filteredCreatures.get(pos);

		if (justUpdateCreature) {
			creatureATK.setText(Integer.toString(atk));
			creatureDEF.setText(Integer.toString(def));
			creatureHP.setText(Integer.toString(hp));
			creatureRespawn.setText(Integer.toString(respawn));
			creatureXP.setText(Integer.toString(xp));
		}

		suggestedATK.setText(Integer.toString(atk));
		suggestedDEF.setText(Integer.toString(def));
		suggestedHP.setText(Integer.toString(hp));
		suggestedRespawn.setText(Integer.toString(respawn));
		suggestedXP.setText(Integer.toString(xp));
	} // GEN-LAST:event_SuggestAttributeButtonActionPerformed

	private void jSaveToFileActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jSaveToFileActionPerformed
		JFileChooser fc = new JFileChooser(".");
		fc.setSelectedFile(new File(EditorXML.creaturesFile));
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == 0) {
			java.io.File file = fc.getSelectedFile();
			try {
				PrintWriter out = new PrintWriter(new FileOutputStream(file));
				out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				out.println("<creatures>");

				for (DefaultCreature c : xml.getCreatures()) {
					out.println(c.toXML());
				}

				out.println("</creatures>");
				out.close();
				xml.creaturesChangeClear();
			} catch (FileNotFoundException e) {
			}
		}
	} // GEN-LAST:event_jSaveToFileActionPerformed

	private void setButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_setButtonActionPerformed
		try {
			xml.creaturesChange();
			addButton.setEnabled(true);
			setButton.setForeground(Color.BLACK);

			int pos = creatureList.getSelectedIndex();
			DefaultCreature actual = filteredCreatures.get(pos);

			actual.setCreatureName(creatureName.getText());
			actual.setCreatureClass((String) creatureClass.getSelectedItem());
			actual.setCreatureSubclass(creatureSubclass.getText());
			String[] sizes = creatureSize.getText().split(",");
			actual.setSize(Integer.parseInt(sizes[0]),
					Integer.parseInt(sizes[1]));
			actual.setTileId(creatureTileid.getText());

			actual.setLevel(Integer.parseInt(creatureLevel.getText()),
					Integer.parseInt(creatureXP.getText()) * 20);
			actual.setRespawnTime(Integer.parseInt(creatureRespawn.getText()));

			actual.setDescription(creatureDescription.getText());

			actual.setRPStats(Integer.parseInt(creatureHP.getText()),
					Integer.parseInt(creatureATK.getText()),
					Integer.parseInt(creatureDEF.getText()),
					Double.parseDouble(creatureSpeed.getText()));

			/* Drops */
			List<DropItem> dropList = new LinkedList<DropItem>();
			BufferedReader reader = new BufferedReader(new StringReader(
					creatureDrops.getText()));
			String line = reader.readLine();
			while (line != null) {
				String[] tok = line.split(";");
				String[] minmax = tok[1].replace("[", "").replace("]", "").split(
						",");
				dropList.add(new DropItem(tok[0].trim(),
						Double.parseDouble(tok[2].trim()),
						Integer.parseInt(minmax[0].trim()),
						Integer.parseInt(minmax[1].trim())));
				line = reader.readLine();
			}
			actual.setDropItems(dropList);

			List<EquipItem> equipList = new LinkedList<EquipItem>();
			reader = new BufferedReader(new StringReader(
					creatureEquips.getText()));
			line = reader.readLine();
			while (line != null) {
				String[] tok = line.split(";");
				equipList.add(new EquipItem(tok[1].trim(), tok[0].trim(),
						Integer.parseInt(tok[2].trim())));
				line = reader.readLine();
			}
			actual.setEquipedItems(equipList);

			Map<String, String> profiles = new LinkedHashMap<String, String>();
			List<String> noises = new LinkedList<String>();

			reader = new BufferedReader(new StringReader(creatureAI.getText()));
			line = reader.readLine();
			while (line != null) {
				if (line.startsWith("says")) {
					noises.add(line.replace("says", "").replace(":", "").trim());
				} else {
					int i = line.indexOf("=");
					String key = null;
					String val = null;
					if (i != -1) {
						key = line.substring(0, i - 1).trim();
						val = line.substring(i + 1).trim();
					} else {
						key = line;
					}

					profiles.put(key, val);
				}

				line = reader.readLine();
			}
			actual.setAIProfiles(profiles);
			//actual.setNoiseLines(noises);

			xml.sortCreatures(filteredCreatures);
			xml.updateFrameContents();
			refresh();

			creatureList.setSelectedIndex(pos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // GEN-LAST:event_setButtonActionPerformed

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_addButtonActionPerformed
		xml.getCreatures().add(new DefaultCreature(null, null, null, null));
		updateItemLists();
		updateCreatureList(null, filter.getText());
		creatureList.setSelectedIndex(filteredCreatures.size() - 1);
		creatureList.ensureIndexIsVisible(filteredCreatures.size() - 1);
		refresh();
		setButton.setForeground(Color.RED);
		addButton.setEnabled(false);
	} // GEN-LAST:event_addButtonActionPerformed

	private void creatureListValueChanged(
			javax.swing.event.ListSelectionEvent evt) { // GEN-FIRST:event_creatureListValueChanged
		refresh();
	} // GEN-LAST:event_creatureListValueChanged

	private double getItemValue(String name) {
		for (DefaultItem it : xml.getItems()) {
			if (it.getItemName().equals(name)) {
				return it.getValue();
			}
		}

		return -1;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton ClearButton;

	private javax.swing.JButton FilterButton;

	private javax.swing.JButton SuggestAttributeButton;

	private javax.swing.JButton addButton;

	private javax.swing.JLabel amountOfCreatures;

	private javax.swing.JTextArea creatureAI;

	private javax.swing.JTextField creatureATK;

	private javax.swing.JComboBox creatureClass;

	private javax.swing.JTextField creatureDEF;

	private javax.swing.JTextArea creatureDescription;

	private javax.swing.JTextArea creatureDrops;

	private javax.swing.JTextArea creatureEquips;

	private javax.swing.JTextField creatureGFXLocation;

	private javax.swing.JTextField creatureHP;

	private javax.swing.JPanel creatureImage;

	private javax.swing.JTextField creatureLevel;

	private javax.swing.JList creatureList;

	private javax.swing.JTextField creatureName;

	private javax.swing.JTextField creatureRespawn;

	private javax.swing.JTextField creatureRespawn1;

	private javax.swing.JTextField creatureSize;

	private javax.swing.JTextField creatureSpeed;

	private javax.swing.JTextField creatureSubclass;

	private javax.swing.JTextField creatureTileid;

	private javax.swing.JTextField creatureValue;

	private javax.swing.JTextField creatureXP;

	private javax.swing.JTabbedPane data;

	private javax.swing.JTextField filter;

	private javax.swing.JComboBox filteredField;

	private javax.swing.JList itemList;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel10;

	private javax.swing.JLabel jLabel11;

	private javax.swing.JLabel jLabel12;

	private javax.swing.JLabel jLabel13;

	private javax.swing.JLabel jLabel14;

	private javax.swing.JLabel jLabel15;

	private javax.swing.JLabel jLabel16;

	private javax.swing.JLabel jLabel17;

	private javax.swing.JLabel jLabel18;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;

	private javax.swing.JLabel jLabel4;

	private javax.swing.JLabel jLabel5;

	private javax.swing.JLabel jLabel6;

	private javax.swing.JLabel jLabel7;

	private javax.swing.JLabel jLabel8;

	private javax.swing.JLabel jLabel9;

	private javax.swing.JMenu jLoad;

	private javax.swing.JMenuItem jLoadFromFile;

	private javax.swing.JMenuBar jMenuBar1;

	private javax.swing.JPanel jPanel1;

	private javax.swing.JPanel jPanel2;

	private javax.swing.JPanel jPanel3;

	private javax.swing.JPanel jPanel4;

	private javax.swing.JPanel jPanel5;

	private javax.swing.JPanel jPanel6;

	private javax.swing.JPanel jPanel7;

	private javax.swing.JPanel jPanel8;

	private javax.swing.JMenu jSave;

	private javax.swing.JMenuItem jSaveToFile;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JScrollPane jScrollPane2;

	private javax.swing.JCheckBox justEditCreature;

	private javax.swing.JButton pushIntoArea;

	private javax.swing.JButton setButton;

	private javax.swing.JTextField suggestedATK;

	private javax.swing.JTextField suggestedDEF;

	private javax.swing.JTextField suggestedHP;

	private javax.swing.JTextField suggestedRespawn;

	private javax.swing.JTextField suggestedXP;

	private javax.swing.JButton updateGFXButton;
	// End of variables declaration//GEN-END:variables

}
