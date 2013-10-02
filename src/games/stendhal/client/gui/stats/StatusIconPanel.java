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

import games.stendhal.client.entity.StatusID;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

class StatusIconPanel extends JComponent {
    private static Map<StatusID, JLabel> statusIDMap;
    
    /** Status bar icons */
    private static final String iconFolder = "data/sprites/status/panel/";
    
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6263002049434805442L;
	private static final ImageIcon eatingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/eat.png"));
	private static final ImageIcon chokingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/choking.png"));
	private static final ImageIcon poisonIcon = new ImageIcon(DataLoader.getResource(iconFolder + "poison.png"));
	private static final ImageIcon confuseIcon = new ImageIcon(DataLoader.getResource(iconFolder + "confuse.png"));
	private static final ImageIcon shockIcon = new ImageIcon(DataLoader.getResource(iconFolder + "shock.png"));
	
	private final static Sprite awaySprite, grumpySprite;
	static {
		final SpriteStore store = SpriteStore.get();
		awaySprite = store.getSprite("data/sprites/ideas/away.png");
		grumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
	}

	final JLabel eating, choking;
	final AnimatedIcon away, grumpy;
	
	protected StatusIconPanel() {
		setLayout(new SBoxLayout(SBoxLayout.HORIZONTAL));
		setOpaque(false);
		
		eating = new JLabel(eatingIcon);
		add(eating);
		eating.setVisible(false);
		
		choking = new JLabel(chokingIcon);
		add(choking);
		choking.setVisible(false);
		
		JLabel poison = new JLabel(poisonIcon);
		add(poison);
		poison.setVisible(false);
		
		JLabel confuse = new JLabel(confuseIcon);
		add(confuse);
		confuse.setVisible(false);
		
		JLabel shock = new JLabel(shockIcon);
		add(shock);
		shock.setVisible(false);
		
		away = new AnimatedIcon(awaySprite, 2000);
		add(away);
		away.setVisible(false);
		
		grumpy = new AnimatedIcon(grumpySprite, 2000);
		add(grumpy);
		grumpy.setVisible(false);
		
		/** Initialize map */
        statusIDMap = new EnumMap<StatusID, JLabel>(StatusID.class); {
            statusIDMap.put(StatusID.CONFUSE, confuse);
            statusIDMap.put(StatusID.POISON, poison);
            statusIDMap.put(StatusID.SHOCK, shock);
        }
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
