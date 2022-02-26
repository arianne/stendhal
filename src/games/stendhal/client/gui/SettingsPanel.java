/* $Id$ */
/***************************************************************************
 *                  (C) Copyright 2005-2012 - Stendhal                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import games.stendhal.client.actions.SlashAction;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;

/**
 * The panel where you can adjust your settings.
 */
class SettingsPanel extends JButton {
	/** Extra padding for
	 *  empty space above and below the separators in the popup menu
	 */
	private static final int SEPARATOR_MARGIN = 8;

	/**
	 * Creates a new instance of SettingsPanel.
	 */
	SettingsPanel() {
		setText("Menu");
		setIcon(createArrowIcon(true));
		setSelectedIcon(createArrowIcon(false));
		// Don't steal focus from the game screen
		setFocusable(false);

		// JMenu is not flexible enough for custom layout management.
		final JPopupMenu menu = new JPopupMenu();

		/*
		 * JPopupMenu is unable to cope with nested components, so we need to
		 * use a complicated layout manager. GroupLayout could in principle
		 * produce the the desired layout, but any changes to the menu structure
		 * would have global effects, so to spare the sanity of anyone adding
		 * new menu items, we use GridBag and hope for the best.
		 */
		GridBagLayout layout = new GridBagLayout();
		menu.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		// Accounts
		Column column = new Column(menu, c);
		createAccountsMenu(column);

		addSeparator(menu, c);

		// Tools
		column = new Column(menu, c);
		createToolsMenu(column);

		addSeparator(menu, c);

		// Commands
		column = new Column(menu, c);
		createCommandsMenu(column);

		addSeparator(menu, c);

		// Help
		column = new Column(menu, c);
		createHelpMenu(column);

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Place the menu right justified to the button
				menu.show(SettingsPanel.this, getWidth() - menu.getPreferredSize().width, getHeight());
			}
		});
		/*
		 * Menu is its own component root, so it needs to be registered to
		 * listening font size changes.
		 */
		WindowUtils.watchFontSize(menu);
	}

	/**
	 * Add a column separator.
	 *
	 * @param container
	 * @param c
	 */
	private void addSeparator(JComponent container, GridBagConstraints c) {
		c.gridy = 0;
		c.gridx++;
		c.gridheight = GridBagConstraints.REMAINDER;
		JComponent separator = new JSeparator(SwingConstants.VERTICAL);
		// Add a bit empty space to top and bottom
		Insets tmpInsets = c.insets;
		c.insets = new Insets(SEPARATOR_MARGIN, 0, SEPARATOR_MARGIN, 0);
		container.add(separator, c);
		// Reset to default
		c.gridheight = 1;
		c.insets = tmpInsets;
	}

	/**
	 * Create arrow icon for the menu button.
	 *
	 * @param etched <code>true</code> if the arrow should be drawn as an etched
	 * image. If <code>false</code> the arrow will be drawn as a raised image
	 *
	 * @return arrow icon
	 */
	private Icon createArrowIcon(boolean etched) {
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		Color highLight, shadow;
		Style style = StyleUtil.getStyle();
		if (style != null) {
			highLight = style.getHighLightColor();
			shadow = style.getShadowColor();
		} else {
			highLight = Color.WHITE;
			shadow = Color.BLACK;
		}
		Color nwColor, seColor;
		if (etched) {
			nwColor = shadow;
			seColor = highLight;
		} else {
			nwColor = highLight;
			seColor = shadow;
		}
		g.setColor(nwColor);
		g.drawPolyline(new int[] { 7, 0, 13}, new int[] { 11, 4, 4}, 3);
		g.setColor(seColor);
		g.drawLine(7, 11, 14, 4);
		g.dispose();

		return new ImageIcon(image);
	}

	/**
	 * Create the account control submenu.
	 *
	 * @param column
	 */
	private void createAccountsMenu(Column column) {
		JComponent label = createMenuTitle("Accounts");
		column.addComponent(label);

		JMenuItem item = createMenuItem("Change Password", "changepassword");
		column.addComponent(item);
		item = createMenuItem("Merge Accounts", "merge");
		column.addComponent(item);
		item = createMenuItem("Login History", "loginhistory");
		column.addComponent(item);
	}

	/**
	 * Create the tools submenu.
	 *
	 * @param column
	 */
	private void createToolsMenu(Column column) {
		JComponent label = createMenuTitle("Tools");
		column.addComponent(label);

		JMenuItem item = createMenuItem("Take Screenshot", "takescreenshot");
		column.addComponent(item);
		item = createMenuItem("Settings", "settings");
		column.addComponent(item);
	}

	/**
	 * Create the game commands submenu.
	 *
	 * @param column
	 */
	private void createCommandsMenu(Column column) {
		JComponent label = createMenuTitle("Commands");
		column.addComponent(label);

		JMenuItem item = createMenuItem("Atlas", "atlas");
		column.addComponent(item);
		item = createMenuItem("Online Players", "who");
		column.addComponent(item);
		item = createMenuItem("Hall of Fame", "halloffame");
		column.addComponent(item);
		item = createMenuItem("Travel Log", "travellog");
		column.addComponent(item);
	}

	/**
	 * Create the help submenu.
	 *
	 * @param column
	 */
	private void createHelpMenu(Column column) {
		JComponent label = createMenuTitle("Help");
		column.addComponent(label);

		JMenuItem item = createMenuItem("Manual", "manual");
		column.addComponent(item);
		item = createMenuItem("FAQ", "faq");
		column.addComponent(item);
		item = createMenuItem("Beginners Guide", "beginnersguide");
		column.addComponent(item);
		item = createMenuItem("Commands", "help");
		column.addComponent(item);
		item = createMenuItem("Rules", "rules");
		column.addComponent(item);
	}

	/**
	 * Create title for a sumbenu.
	 *
	 * @param title title string
	 * @return component with the title
	 */
	private JComponent createMenuTitle(String title) {
		JLabel label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(5, 4, 10, 0));
		return label;
	}

	/**
	 * Create a menu item.
	 *
	 * @param name title of the menu item
	 * @param action SlashAction name
	 * @return menu item
	 */
	private JMenuItem createMenuItem(String name, String action) {
		JMenuItem item = new VariableWidthMenuItem(name);
		item.addActionListener(new CommandActionListener(action));
		item.setBorder(null);
		return item;
	}

	/**
	 * Listener for menu actions.
	 */
	private static class CommandActionListener implements ActionListener {
		private final SlashAction action;

		/**
		 * Create a CommandActionListener.
		 *
		 * @param identifier SlashAction name
		 */
		public CommandActionListener(String identifier) {
			action = SlashActionRepository.get(identifier);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			action.execute(null, null);
		}
	}

	/**
	 * A workaround for a design flaw in swing menu items. BasicMenuItemUI tries
	 * to force all menu items to the same width, instead of doing that properly
	 * in the menu layout manager. So this JMenuItem stores the preferred size
	 * before it gets knowledge about other items in the menu.
	 */
	private static class VariableWidthMenuItem extends JMenuItem {
		Dimension pref;
		VariableWidthMenuItem(String name) {
			super(name);
			pref = super.getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			return pref;
		}
	}

	/**
	 * A helper class for filling the grid in top down order. An object that
	 * represents the next column given a constraints object that has been used
	 * for the previous columns.
	 */
	private static class Column {
		final JComponent container;
		final GridBagConstraints c;

		/**
		 * Create a new ColumnHelper.
		 *
		 * @param container parent component
		 * @param c constraints object to be used, and which will be modified
		 * 	for subsequent uses
		 */
		Column(JComponent container, GridBagConstraints c) {
			this.container = container;
			this.c = c;
			c.gridy = 0;
			c.gridx++;
			c.fill = GridBagConstraints.BOTH;
		}

		/**
		 * Add a component to the colum.
		 *
		 * @param component
		 */
		void addComponent(JComponent component) {
			container.add(component, c);
			c.gridy++;
		}
	}
}
