/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.dialog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import tiled.io.MapReader;
import tiled.io.MapWriter;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.widget.VerticalStaticJPanel;

public class PluginDialog extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 4957565032649732798L;

	private PluginClassLoader pluginLoader;
	private JList pluginList = null;
	private JButton closeButton, infoButton, removeButton;

	public PluginDialog(JFrame parent, PluginClassLoader pluginLoader) {
		super(parent, "Available Plugins", true);
		this.pluginLoader = pluginLoader;

		init();
		pack();
		setLocationRelativeTo(getOwner());
	}

	private void init() {
		/* LIST PANEL */
		MapReader[] readers;
		MapWriter[] writers;

		try {
			readers = pluginLoader.getReaders();
			writers = pluginLoader.getWriters();
			String[] plugins = new String[readers.length + writers.length];

			for (int i = 0; i < readers.length; i++) {
				plugins[i] = readers[i].getPluginPackage();
			}
			for (int i = 0; i < writers.length; i++) {
				plugins[i + readers.length] = writers[i].getPluginPackage();
			}
			pluginList = new JList(plugins);
		} catch (Throwable e) {
			e.printStackTrace();
			pluginList = new JList();
		}

		pluginList.addListSelectionListener(this);

		JScrollPane pluginScrollPane = new JScrollPane(pluginList);
		pluginScrollPane.setAutoscrolls(true);
		pluginScrollPane.setPreferredSize(new Dimension(200, 150));

		/* BUTTON PANEL */
		infoButton = new JButton("Info");
		removeButton = new JButton("Remove");
		closeButton = new JButton("Close");
		infoButton.addActionListener(this);
		removeButton.addActionListener(this);
		closeButton.addActionListener(this);

		JPanel buttonPanel = new VerticalStaticJPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(infoButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(removeButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(closeButton);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(pluginScrollPane);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(buttonPanel);

		setContentPane(mainPanel);
		getRootPane().setDefaultButton(closeButton);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		updateButtons();
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == closeButton) {
			this.dispose();
		} else if (source == removeButton) {
			// TODO: Implement plugin remove functionality
		} else if (source == infoButton) {
			JDialog info = new JDialog(this);
			JTextArea ta = new JTextArea(25, 30);
			int index = pluginList.getSelectedIndex();

			MapReader[] readers;
			MapWriter[] writers;
			try {
				readers = pluginLoader.getReaders();
				writers = pluginLoader.getWriters();
				if (index < readers.length) {
					ta.setText(readers[index].getDescription());
					info.setTitle(readers[index].getPluginPackage());
				} else {
					index -= readers.length;
					ta.setText(writers[index].getDescription());
					info.setTitle(writers[index].getPluginPackage());
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			ta.setEditable(false);
			ta.setFont(new Font("Courier", Font.PLAIN, 12));
			info.getContentPane().add(ta);
			info.setLocationRelativeTo(this);
			info.pack();
			info.setVisible(true);
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		updateButtons();
	}

	private void updateButtons() {
		boolean validSelection = pluginList.getSelectedIndex() >= 0;
		infoButton.setEnabled(validSelection);
		// TODO: Enable "Remove" button when functional
		removeButton.setEnabled(validSelection && false);
	}
}
