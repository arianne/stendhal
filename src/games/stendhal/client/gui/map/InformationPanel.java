/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.map;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import games.stendhal.client.entity.User;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.sprite.DataLoader;

/**
 * Area for displaying information about a zone.
 */
class InformationPanel extends JComponent {
	/** Maximum number of skull icons in the danger indicator. */
	private static final int MAX_SKULLS = 5;
	/**
	 * Value to added to the player level when calculating the amount of skulls
	 * to show. <b>Must be at least 1.</b>. Higher values mean that low level
	 * players require higher danger level at a zone for a certain amount of
	 * skulls.
	 */
	private static final int SKULLS_LEVEL_ADD = 3;
	/**
	 * Textual description of the danger level. There should be MAX_SKULLS + 1
	 * of these.
	 */
	private static final String[] dangerLevelStrings = {
		"The area feels safe.",
		"The area feels relatively safe.",
		"The area feels somewhat dangerous.",
		"The area feels dangerous.",
		"The area feels very dangerous!",
		"The area feels extremely dangerous. Run away!"
	};

	/** Zone name display. */
	private final JTextPane nameField;
	/** Attribute set needed for centering the zone name text. */
	private final SimpleAttributeSet center = new SimpleAttributeSet();
	/** Danger level icons */
	private final DangerIndicator dangerIndicator;
	/** Current relative danger level. */
	private int dangerLevel;
	/**
	 * A component overlaying the zone text and the danger indicator. This is
	 * for holding a common tool tip for them both. JTextPane consumes mouse
	 * events so setting a tool tip for the common parent does not work.
	 */
	private final JComponent glassPane;

	/**
	 * Create a new InformationPanel.
	 */
	InformationPanel() {
		setLayout(new OverlayLayout(this));
		JComponent container = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		glassPane = new JComponent(){};
		add(glassPane);
		add(container);

		// ** Zone name **
		nameField = new JTextPane();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		nameField.setAlignmentX(CENTER_ALIGNMENT);
		nameField.setOpaque(true);
		nameField.setBackground(getBackground());
		nameField.setForeground(Color.WHITE);
		nameField.setFocusable(false);
		nameField.setEditable(false);
		container.add(nameField, SLayout.EXPAND_X);

		// ** Danger display **
		dangerIndicator = new DangerIndicator(MAX_SKULLS);
		dangerIndicator.setAlignmentX(CENTER_ALIGNMENT);
		container.add(dangerIndicator);
		// Default to safe, so that we always have a tooltip
		describeDanger(0);
	}

	/**
	 * Set the tool tip describing zone danger level.
	 *
	 * @param dangerLevel zone danger level, value in range [0-5].
	 */
	private void describeDanger(int dangerLevel) {
		glassPane.setToolTipText(dangerLevelStrings[dangerLevel]);
	}

	/**
	 * Set the name of the zone.
	 *
	 * @param name
	 */
	void setZoneName(String name) {
		nameField.setText(name);
		StyledDocument doc = nameField.getStyledDocument();
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		// Necessary when the needed space gets smaller.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				nameField.revalidate();
			}
		});
	}

	/**
	 * Set the zone danger level.
	 *
	 * @param dangerLevel danger level
	 */
	void setDangerLevel(double dangerLevel) {
		int skulls = (int) Math.min(5, Math.round(2 * dangerLevel / (User.getPlayerLevel() + SKULLS_LEVEL_ADD)));
		if (this.dangerLevel != skulls) {
			this.dangerLevel = skulls;
			dangerIndicator.setRelativeDanger(skulls);
			describeDanger(skulls);
		}
	}

	/**
	 * A skull row component for danger level display.
	 */
	private static class DangerIndicator extends JComponent {
		/** Indicator icon image. */
		private static final ImageIcon skullIcon = new ImageIcon(DataLoader.getResource("data/gui/danger.png"));

		/** The indicator icons */
		private final JComponent[] indicators;

		/**
		 * Create a new DangerIndicator.
		 *
		 * @param maxSkulls maximum number of skulls to display
		 */
		DangerIndicator(int maxSkulls) {
			setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL));
			indicators = new JComponent[maxSkulls];
			for (int i = 0; i < maxSkulls; i++) {
				JLabel indicator = new JLabel(skullIcon);
				// Avoid showing a row of skulls on login
				indicator.setVisible(false);
				add(indicator);
				indicators[i] = indicator;
			}
		}

		/**
		 * Set the relative danger level.
		 *
		 * @param skulls amount of skulls to show
		 */
		void setRelativeDanger(int skulls) {
			for (int i = 0; i < indicators.length; i++) {
				indicators[i].setVisible(i < skulls);
			}
		}
	}
}
