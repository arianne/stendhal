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
package games.stendhal.client.gui.stats;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import games.stendhal.client.entity.StatusID;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

class StatusIconPanel extends JComponent {
    /** Status bar icons */
    private static final String iconFolder = "data/sprites/status/panel/";

	private static final ImageIcon eatingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/eat.png"));
	private static final ImageIcon chokingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/choking.png"));

	private final static Sprite awaySprite, grumpySprite;
	static {
		final SpriteStore store = SpriteStore.get();
		awaySprite = store.getSprite("data/sprites/ideas/away.png");
		grumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
	}

	final JLabel eating, choking;
	final AnimatedIcon away, grumpy;
    private final Map<StatusID, JLabel> statusIDMap;

	protected StatusIconPanel() {
		setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL));
		setOpaque(false);

		eating = new JLabel(eatingIcon);
		add(eating);
		eating.setVisible(false);

		choking = new JLabel(chokingIcon);
		add(choking);
		choking.setVisible(false);

		away = new AnimatedIcon(awaySprite, 2000);
		add(away);
		away.setVisible(false);

		grumpy = new AnimatedIcon(grumpySprite, 2000);
		add(grumpy);
		grumpy.setVisible(false);

		/** Initialize map */
        statusIDMap = new EnumMap<StatusID, JLabel>(StatusID.class);
        statusIDMap.put(StatusID.CONFUSE, createStatusIndicator("confuse"));
        statusIDMap.put(StatusID.POISON, createStatusIndicator("poison"));
        statusIDMap.put(StatusID.SHOCK, createStatusIndicator("shock"));
        statusIDMap.put(StatusID.ZOMBIE, createStatusIndicator("zombie"));
        statusIDMap.put(StatusID.HEAVY, createStatusIndicator("heavy"));
	}

	/**
	 * Create and add a status indicator label.
	 *
	 * @param identifier string identifier used to look up for the label icon
	 * @return the created label
	 */
	private JLabel createStatusIndicator(String identifier) {
		Icon icon = new ImageIcon(DataLoader.getResource(iconFolder + identifier + ".png"));
		JLabel label = new JLabel(icon);
		label.setVisible(false);
		add(label);

		return label;
	}

	/**
	 * Display or hide eating icon
	 *
	 * @param isEating
	 */
	protected void setEating(boolean isEating) {
		if (eating.isVisible() != isEating) {
			// A hack to prevent eating and choking icons appearing
			// at the same time
			if (isEating) {
				if (!choking.isVisible()) {
					eating.setVisible(true);
				}
			} else {
				eating.setVisible(false);
			}
		}
	}

	/**
	 * Display or hide choking icon
	 *
	 * @param isChoking
	 */
	protected void setChoking(boolean isChoking) {
		if (choking.isVisible() != isChoking) {
			choking.setVisible(isChoking);
		}
		// A hack to prevent eating and choking icons appearing
		// at the same time
		if (isChoking) {
			eating.setVisible(false);
		}
	}

    /**
     * Display or hide a status icon.
     *
     * @param ID
     *      The ID value of the status
     * @param visible
     *      Show the icon
     */
    void setStatus(final StatusID ID, final boolean visible) {
        final JLabel status = statusIDMap.get(ID);
        if (status.isVisible() != visible) {
            status.setVisible(visible);
        }
    }

    /**
     * Hide all status icons. This is called when the user entity is deleted.
     */
    void resetStatuses() {
    	for (JLabel status : statusIDMap.values()) {
    		if (status.isVisible()) {
    			status.setVisible(false);
    		}
    	}
    }

	/**
	 * Set the away status message. null value will hide the icon.
	 *
	 * @param message
	 */
	void setAway(String message) {
		boolean isAway = message != null;
		if (isAway) {
			away.setToolTipText("<html>You are away with the message:<br><b>" + message);
		}
		if (away.isVisible() != isAway) {
			away.setVisible(isAway);
		}
	}

	/**
	 * Set the grumpy status message. null value will hide the icon.
	 *
	 * @param message
	 */
	void setGrumpy(String message) {
		boolean isGrumpy = message != null;
		if (isGrumpy) {
			grumpy.setToolTipText("<html>You are grumpy with the message:<br><b>" + message);
		}
		if (grumpy.isVisible() != isGrumpy) {
			grumpy.setVisible(isGrumpy);
		}
	}
}
