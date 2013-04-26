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

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.creature.impl.DropItem;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import marauroa.common.Pair;

import org.xml.sax.SAXException;

/*
 * JItem.java
 *
 * Created on 12 de mayo de 2007, 13:22
 */

/**
 *
 * @author miguel
 */
@SuppressWarnings("serial")
public class JItem extends javax.swing.JFrame {
	boolean justUpdateItem;

	private List<DefaultItem> filteredItems;

	private EditorXML xml;

	private List<Pair<DefaultCreature, DropItem>> usedAt;

	/**
	 * Creates new form JItem.
	 *  
	 * @param xml
	 */
	public JItem(EditorXML xml) {
		this.xml = xml;
		initComponents();
		loadData();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		justUpdateItem = false;
	}

	private void loadData() {
		filteredItems = xml.getItems();
		setLists();
		itemList.setSelectedIndex(0);
		refresh();
	}

	void setLists() {
		itemEquipable.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				return EditorXML.slots[i];
			}

			@Override
			public int getSize() {
				return EditorXML.slots.length;
			}
		});

		itemClass.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "ammunition", "armor", "axe", "book", "boots",
						"box", "cloak", "club", "container", "crystal", "CVS", "drink",
						"food", "grower", "helmet", "herb", "jewellery", "key",
						"legs", "misc", "missile", "money", "projectiles",
						"ranged", "resource", "ring", "scent", "scroll",
						"shield", "special", "sword", "tool" }));

		updateItemList(null, filterValue.getText());
		updateCreatureList(null);
	}

	private void updateCreatureList(String item) {
		itemUsedAtList.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				return xml.getCreatures().get(i).getCreatureName();
			}

			@Override
			public int getSize() {
				return xml.getCreatures().size();
			}
		});
	}

	private void updateItemList(String field, String filter) {
		filteredItems = new LinkedList<DefaultItem>();

		if (filter.length() > 0) {
			for (DefaultItem c : xml.getItems()) {
				boolean add = false;

				if (field == null || field.equals("Name")) {
					add = c.getItemName().contains(filter);
				} else if (field.contains("Class")) {
					add = c.getItemClass().contains(filter);
				} else if (field.contains("Value")) {
					// TODO
				}

				if (add) {
					filteredItems.add(c);
				}
			}
		} else {
			filteredItems = xml.getItems();
		}

		itemList.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				DefaultItem item = filteredItems.get(i);
				return "(" + item.getItemClass() + ") " + item.getItemName();
			}

			@Override
			public int getSize() {
				return filteredItems.size();
			}
		});

		amountOfItems.setText(Integer.toString(xml.getItems().size()));
	}

	private void clean(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(2, 2, imageResource.getWidth() - 2,
				imageResource.getHeight() - 2);
	}

	private void drawSinglePart(Sprite sprite, double w, double h, Graphics g) {
		clean(g);
		if (w == 1.0D && h == 2D) {
			w = 1.5D;
		}
		int offset = (int) (h * 2D * 32D);
		if (sprite.getHeight() < offset) {
			offset = 0;
		}
		int x = imageResource.getWidth() / 2 - (int) ((w * 32D) / 2D);
		int y = imageResource.getHeight() / 2 - (int) ((h * 32D) / 2D);
		sprite.draw(g, x, y, 0, offset, (int) (w * 32D), (int) (h * 32D));
	}

	private void refresh() {
		int pos = itemList.getSelectedIndex();

		if (pos < 0) {
			return;
		}

		DefaultItem actual = filteredItems.get(pos);

		if (actual.getItemName() == null) {
			return;
		}
		itemName.setText(actual.getItemName());
		itemImplementation.setSelectedItem(actual.getImplementation().getCanonicalName());
		itemDescription.setText(actual.getDescription());

		itemClass.setSelectedItem(actual.getItemClass());
		itemSubclass.setText(actual.getItemSubclass());
		String gfxLocation = "/" + actual.getItemClass() + "/"
				+ actual.getItemSubclass() + ".png";
		Sprite spr = SpriteStore.get().getSprite(
				"stendhal/data/sprites/items" + gfxLocation);

		drawSinglePart(spr, 1, 1, imageResource.getGraphics());
		itemGFXLocation.setText(gfxLocation);

		StringBuilder os = new StringBuilder();
		for (Map.Entry<String, String> entry : actual.getAttributes().entrySet()) {
			os.append(entry.getKey());
			if (entry.getValue() != null) {
				os.append(" = " + entry.getValue() + "\n");
			}
		}
		itemAttributes.setText(os.toString());
		itemWeight.setText(Double.toString(actual.getWeight()));
		itemValue.setText(Integer.toString(actual.getValue()));

		suggestedValueButtonActionPerformed(null);

		itemEquipable.clearSelection();
		Vector<Integer> selected = new Vector<Integer>();

		for (String slot : actual.getEquipableSlots()) {
			for (int i = 0; i < EditorXML.slots.length; i++) {
				if (slot.equals(EditorXML.slots[i])) {
					selected.add(i);
					break;
				}
			}
		}

		int[] array = new int[selected.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = selected.get(i);
		}
		itemEquipable.setSelectedIndices(array);

		usedAt = new LinkedList<Pair<DefaultCreature, DropItem>>();
		for (DefaultCreature c : xml.getCreatures()) {
			List<DropItem> drops = c.getDropItems();
			for (DropItem it : drops) {
				if (it.name.equals(actual.getItemName())) {
					usedAt.add(new Pair<DefaultCreature, DropItem>(c, it));
					break;
				}
			}
		}

		itemUsedAtList.setModel(new javax.swing.AbstractListModel() {
			@Override
			public Object getElementAt(int i) {
				Pair<DefaultCreature, DropItem> item = usedAt.get(i);
				return "(" + item.first().getLevel() + ")"
						+ item.first().getCreatureName() + " prob: "
						+ item.second().probability + " amount: ["
						+ item.second().min + "," + item.second().max + "]";
			}

			@Override
			public int getSize() {
				return usedAt.size();
			}
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		itemList = new javax.swing.JList();
		addButton = new javax.swing.JButton();
		setButton = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		itemName = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		itemImplementation = new javax.swing.JComboBox();
		jLabel6 = new javax.swing.JLabel();
		itemDescription = new javax.swing.JTextField();
		amountOfItems = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		itemClass = new javax.swing.JComboBox();
		itemSubclass = new javax.swing.JTextField();
		itemGFXLocation = new javax.swing.JTextField();
		imageResource = new javax.swing.JPanel();
		updateGFXButton = new javax.swing.JButton();
		jPanel4 = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		itemAttributes = new javax.swing.JTextArea();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		itemValue = new javax.swing.JTextField();
		itemSuggestedValue = new javax.swing.JTextField();
		itemWeight = new javax.swing.JTextField();
		suggestedValueButton = new javax.swing.JButton();
		jPanel5 = new javax.swing.JPanel();
		jLabel10 = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		itemEquipable = new javax.swing.JList();
		jLabel11 = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		itemUsedAtList = new javax.swing.JList();
		filterCondition = new javax.swing.JComboBox();
		filterValue = new javax.swing.JTextField();
		filterButton = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		jMenuBar1 = new javax.swing.JMenuBar();
		jLoad = new javax.swing.JMenu();
		jLoadFromFile = new javax.swing.JMenuItem();
		jSave = new javax.swing.JMenu();
		jSaveToFile = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Stendhal Item Editor 2.30");
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

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
		itemList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
				itemListValueChanged(evt);
			}
		});

		jScrollPane1.setViewportView(itemList);

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

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel1.setText("Name");

		itemName.setText("jTextField1");

		jLabel2.setText("Implementation");

		itemImplementation.setEditable(true);
		itemImplementation.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		jLabel6.setText("Description");

		itemDescription.setText("jTextField2");

		amountOfItems.setText("jLabel12");

		jLabel13.setText("items available");

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout.createSequentialGroup().addContainerGap().add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jLabel2).add(jLabel1).add(jLabel6)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								itemImplementation, 0, 349, Short.MAX_VALUE).add(
								itemDescription,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								349, Short.MAX_VALUE).add(
								jPanel1Layout.createSequentialGroup().add(
										itemName,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										149,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										47, Short.MAX_VALUE).add(amountOfItems).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jLabel13))).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel1Layout.createSequentialGroup().addContainerGap().add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel1).add(itemName,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								jLabel13).add(amountOfItems)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel2).add(itemImplementation,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel1Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel6).add(itemDescription,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)));

		jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel3.setText("Class");

		jLabel4.setText("Subclass");

		jLabel5.setText("GFX Location");

		itemClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"Item 1", "Item 2", "Item 3", "Item 4" }));

		itemSubclass.setText("jTextField3");

		itemGFXLocation.setText("jTextField4");

		imageResource.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		org.jdesktop.layout.GroupLayout imageResourceLayout = new org.jdesktop.layout.GroupLayout(
				imageResource);
		imageResource.setLayout(imageResourceLayout);
		imageResourceLayout.setHorizontalGroup(imageResourceLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 147,
				Short.MAX_VALUE));
		imageResourceLayout.setVerticalGroup(imageResourceLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 152,
				Short.MAX_VALUE));

		updateGFXButton.setText("Update GFX");

		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel2Layout.createSequentialGroup().addContainerGap().add(
						jPanel2Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								jPanel2Layout.createSequentialGroup().add(
										jPanel2Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jLabel3).add(jLabel4).add(
												jLabel5)).add(28, 28, 28).add(
										jPanel2Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING,
												false).add(itemGFXLocation).add(
												itemSubclass).add(itemClass, 0,
												182, Short.MAX_VALUE))).add(
								updateGFXButton)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED, 30,
						Short.MAX_VALUE).add(imageResource,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel2Layout.createSequentialGroup().addContainerGap().add(
						jPanel2Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel3).add(itemClass,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel2Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel4).add(itemSubclass,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel2Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel5).add(itemGFXLocation,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED, 33,
						Short.MAX_VALUE).add(updateGFXButton).addContainerGap()).add(
				imageResource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));

		jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jLabel7.setText("Attributes");

		itemAttributes.setColumns(20);
		itemAttributes.setRows(5);

		jLabel8.setText("Value");

		jLabel9.setText("Weight");

		itemSuggestedValue.setBackground(new java.awt.Color(204, 204, 204));

		itemWeight.setText("jTextField7");

		suggestedValueButton.setText("Suggest Value");
		suggestedValueButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				suggestedValueButtonActionPerformed(evt);
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
								jLabel7).add(itemAttributes,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								250,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								jPanel4Layout.createSequentialGroup().add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jPanel4Layout.createSequentialGroup().add(
														3, 3, 3).add(jLabel9)).add(
												jLabel8)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING).add(
												jPanel4Layout.createSequentialGroup().add(
														itemValue,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														75,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED).add(
														itemSuggestedValue,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														59, Short.MAX_VALUE)).add(
												itemWeight,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												140, Short.MAX_VALUE))).add(
								suggestedValueButton)).addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel4Layout.createSequentialGroup().addContainerGap().add(
						jLabel7).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel4Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING, false).add(
								jPanel4Layout.createSequentialGroup().add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel8).add(
												itemValue,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
												itemSuggestedValue,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel4Layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												jLabel9).add(
												itemWeight,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).add(
										suggestedValueButton)).add(
								itemAttributes,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								123,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)));

		jLabel10.setText("Equipable");

		itemEquipable.setModel(new javax.swing.AbstractListModel() {
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
		jScrollPane2.setViewportView(itemEquipable);

		jLabel11.setText("Used at");

		itemUsedAtList.setBackground(new java.awt.Color(204, 255, 204));
		itemUsedAtList.setFont(new java.awt.Font("Dialog", 1, 10));
		itemUsedAtList.setModel(new javax.swing.AbstractListModel() {
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
		jScrollPane3.setViewportView(itemUsedAtList);

		org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel5Layout.createSequentialGroup().addContainerGap().add(
						jPanel5Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jLabel10).add(jScrollPane2,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								185,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
						26, 26, 26).add(
						jPanel5Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jLabel11).add(jScrollPane3,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								253, Short.MAX_VALUE)).addContainerGap()));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				jPanel5Layout.createSequentialGroup().addContainerGap().add(
						jPanel5Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								jLabel10).add(jLabel11)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						jPanel5Layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								jScrollPane2).add(jScrollPane3,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

		filterCondition.setBackground(new java.awt.Color(204, 204, 204));
		filterCondition.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Name", "Class", "Value" }));

		filterValue.setBackground(new java.awt.Color(204, 204, 204));

		filterButton.setText("Filter");
		filterButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				filterButtonActionPerformed(evt);
			}
		});

		clearButton.setText("Clear");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearButtonActionPerformed(evt);
			}
		});

		jLoad.setText("Load");
		jLoadFromFile.setText("From File");
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
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								jPanel5,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(jPanel4,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(jPanel2,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								layout.createSequentialGroup().add(
										filterCondition,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										98,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										filterValue,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										96, Short.MAX_VALUE)).add(
								layout.createSequentialGroup().add(filterButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										68, Short.MAX_VALUE).add(clearButton)).add(
								jScrollPane1,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								200,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup().add(addButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										88, Short.MAX_VALUE).add(setButton))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap(
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING).add(
								layout.createSequentialGroup().add(
										jPanel1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel2,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel4,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel5,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
								layout.createSequentialGroup().add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												filterCondition,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
												filterValue,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE).add(
												filterButton).add(clearButton)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jScrollPane1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										522,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING).add(
												setButton).add(addButton))))));
		pack();
	} // </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_formWindowClosing
		xml.requestFormClosing(this);
	} // GEN-LAST:event_formWindowClosing

	private void jLoadFromFileActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jLoadFromFileActionPerformed
		String cdata = null;

		JFileChooser fc = new JFileChooser(".");
		fc.setSelectedFile(new File(EditorXML.itemsFile));
		fc.setDialogTitle("Choose items XML file");
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == 0) {
			java.io.File file = fc.getSelectedFile();
			cdata = file.getAbsolutePath();
		} else {
			return;
		}

		try {
			xml.updateItemsFromFile(cdata);
			xml.itemsChangeClear();
		} catch (SAXException ex) {
			ex.printStackTrace();
		}
	} // GEN-LAST:event_jLoadFromFileActionPerformed

	private void jSaveToFileActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jSaveToFileActionPerformed
		JFileChooser fc = new JFileChooser(".");
		fc.setSelectedFile(new File(EditorXML.itemsFile));
		fc.setDialogTitle("Choose items XML file to save");
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == 0) {
			java.io.File file = fc.getSelectedFile();
			try {
				PrintWriter out = new PrintWriter(new FileOutputStream(file));
				out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				out.println("<items>");

				for (DefaultItem c : xml.getItems()) {
					out.println(c.toXML());
				}

				out.println("</items>");
				out.close();
				xml.itemsChangeClear();
			} catch (FileNotFoundException e) {
			}
		}
	} // GEN-LAST:event_jSaveToFileActionPerformed

	private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_clearButtonActionPerformed
		filterValue.setText("");
		updateItemList(null, filterValue.getText());
	} // GEN-LAST:event_clearButtonActionPerformed

	private void itemListValueChanged(javax.swing.event.ListSelectionEvent evt) { // GEN-FIRST:event_itemListValueChanged
		refresh();
	} // GEN-LAST:event_itemListValueChanged

	private int suggestItemValue(Map<String, String> attributes) {
		int value = -1;

		if (attributes.containsKey("atk")) {
			// Weapon
			int atk = Integer.parseInt(attributes.get("atk"));
			int rate = 5;
			if (attributes.containsKey("rate")) {
				rate = Integer.parseInt(attributes.get("rate"));
			}

			value = (int) (10 * atk * atk * atk * 5.0 / rate);
		} else if (attributes.containsKey("def")) {
			int def = Integer.parseInt(attributes.get("def"));

			value = 7 * def * def * def;
		} else if (attributes.containsKey("regen")) {
			int amount = Integer.parseInt(attributes.get("amount"));
			int regen = Integer.parseInt(attributes.get("regen"));
			int frecuency = Integer.parseInt(attributes.get("frequency"));

			value = (amount * regen) / (5 * frecuency);
		}

		return value;
	}

	private void suggestedValueButtonActionPerformed(
			java.awt.event.ActionEvent evt) { // GEN-FIRST:event_suggestedValueButtonActionPerformed
		Map<String, String> attributes = new LinkedHashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new StringReader(
					itemAttributes.getText()));
			String line = reader.readLine();
			while (line != null) {
				String[] tok = line.split("=");
				attributes.put(tok[0].trim(), tok[1].trim());
				line = reader.readLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		int value = suggestItemValue(attributes);
		if (value < 0) {
			itemSuggestedValue.setText("No suggestion.");
		} else {
			itemSuggestedValue.setText(Integer.toString(value));
			if (justUpdateItem) {
				itemValue.setText(itemSuggestedValue.getText());
			}
		}
	} // GEN-LAST:event_suggestedValueButtonActionPerformed

	private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_filterButtonActionPerformed
		updateItemList((String) filterCondition.getSelectedItem(),
				filterValue.getText());
	} // GEN-LAST:event_filterButtonActionPerformed

	private void setButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_setButtonActionPerformed
		try {
			xml.itemsChange();
			addButton.setEnabled(true);
			setButton.setForeground(Color.BLACK);

			int pos = itemList.getSelectedIndex();
			DefaultItem actual = filteredItems.get(pos);

			actual.setItemName(itemName.getText());
			actual.setDescription(itemDescription.getText());
			actual.setImplementation(Class.forName((String) itemImplementation.getSelectedItem()));

			actual.setItemClass((String) itemClass.getSelectedItem());
			actual.setItemSubclass(itemSubclass.getText());
			actual.setTileId(-1);

			Map<String, String> attributes = new LinkedHashMap<String, String>();
			BufferedReader reader = new BufferedReader(new StringReader(
					itemAttributes.getText()));
			String line = reader.readLine();
			while (line != null) {
				String[] tok = line.split("=");
				attributes.put(tok[0].trim(), tok[1].trim());
				line = reader.readLine();
			}
			actual.setAttributes(attributes);
			actual.setWeight(Double.parseDouble(itemWeight.getText()));

			actual.setValue(Integer.parseInt(itemValue.getText()));

			List<String> canEquip = new LinkedList<String>();
			for (Object sel : itemEquipable.getSelectedValues()) {
				canEquip.add((String) sel);
			}
			actual.setEquipableSlots(canEquip);

			xml.sortItems(filteredItems);
			xml.updateFrameContents();

			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // GEN-LAST:event_setButtonActionPerformed

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_addButtonActionPerformed
		xml.getItems().add(new DefaultItem(null, null, null, -1));
		updateItemList(null, filterValue.getText());
		updateCreatureList(null);
		itemList.setSelectedIndex(filteredItems.size() - 1);
		itemList.ensureIndexIsVisible(filteredItems.size() - 1);
		refresh();
		setButton.setForeground(Color.RED);
		addButton.setEnabled(false);
	} // GEN-LAST:event_addButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addButton;

	private javax.swing.JLabel amountOfItems;

	private javax.swing.JButton clearButton;

	private javax.swing.JButton filterButton;

	private javax.swing.JComboBox filterCondition;

	private javax.swing.JTextField filterValue;

	private javax.swing.JPanel imageResource;

	private javax.swing.JTextArea itemAttributes;

	private javax.swing.JComboBox itemClass;

	private javax.swing.JTextField itemDescription;

	private javax.swing.JList itemEquipable;

	private javax.swing.JTextField itemGFXLocation;

	private javax.swing.JComboBox itemImplementation;

	private javax.swing.JList itemList;

	private javax.swing.JTextField itemName;

	private javax.swing.JTextField itemSubclass;

	private javax.swing.JTextField itemSuggestedValue;

	private javax.swing.JList itemUsedAtList;

	private javax.swing.JTextField itemValue;

	private javax.swing.JTextField itemWeight;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel10;

	private javax.swing.JLabel jLabel11;

	private javax.swing.JLabel jLabel13;

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

	private javax.swing.JPanel jPanel4;

	private javax.swing.JPanel jPanel5;

	private javax.swing.JMenu jSave;

	private javax.swing.JMenuItem jSaveToFile;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JScrollPane jScrollPane2;

	private javax.swing.JScrollPane jScrollPane3;

	private javax.swing.JButton setButton;

	private javax.swing.JButton suggestedValueButton;

	private javax.swing.JButton updateGFXButton;
	// End of variables declaration//GEN-END:variables

}
