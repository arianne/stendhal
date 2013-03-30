/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.settings;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.styled.styles.StyleFactory;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.NotificationType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Page for style settings.
 */
class StyleSettings {
	private static final String STYLE_PROPERTY = "ui.style";
	private static final String DEFAULT_STYLE = "Wood (default)";
	
	/** Container for the setting components. */
	private final JComponent page;
	
	/**
	 * Create new StyleSettings.
	 */
	StyleSettings() {
		int pad = SBoxLayout.COMMON_PADDING;
		page = SBoxLayout.createContainer(SBoxLayout.VERTICAL, pad);
		
		page.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		
		// Style selector
		JComponent hbox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, pad);
		JComponent selectorLabel = new JLabel("Styles:");
		hbox.add(selectorLabel);
		JComponent selector = createStyleSelector();
		hbox.add(selector);
		selector.setToolTipText("<html>The style used to draw the controls in the game client."
				+ "<p>This affects the look only, and will not change the behavior of the game.</html>");
		page.add(hbox);

	}
	
	/**
	 * Get the component containing the style settings.
	 * 
	 * @return style settings page
	 */
	JComponent getComponent() {
		return page;
	}
	
	/**
	 * Create a selector for styles.
	 * 
	 * @return combo box with style options
	 */
	private JComponent createStyleSelector() {
		final JComboBox selector = new JComboBox();
		
		// Fill with available styles
		for (String s : StyleFactory.getAvailableStyles()) {
			selector.addItem(s);
		}
		
		final WtWindowManager wm = WtWindowManager.getInstance();
		String currentStyle = wm.getProperty(STYLE_PROPERTY, DEFAULT_STYLE);
		selector.setSelectedItem(currentStyle);
		 
		selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selected = selector.getSelectedItem();
				wm.setProperty(STYLE_PROPERTY, selected.toString());
				ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("",
						"The new style will be used the next time you start the game client.",
						NotificationType.CLIENT));
			}
		});
		
		return selector;
	}
}
