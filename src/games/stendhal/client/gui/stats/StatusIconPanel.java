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

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

class StatusIconPanel extends JComponent {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6263002049434805442L;
	private static final ImageIcon eatingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/eat.png"));
	private static final ImageIcon chokingIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/choking.png"));
	private static final ImageIcon poisonIcon = new ImageIcon(DataLoader.getResource("data/sprites/ideas/poisoned.png"));
	
	private final static Sprite awaySprite, grumpySprite;
	static {
		final SpriteStore store = SpriteStore.get();
		awaySprite = store.getSprite("data/sprites/ideas/away.png");
		grumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
	}

	final JLabel eating, choking, poison;
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
		
		poison = new JLabel(poisonIcon);
		add(poison);
		poison.setVisible(false);
		
		away = new AnimatedIcon(awaySprite, 13, 19, 4, 2000);
		add(away);
		away.setVisible(false);
		
		grumpy = new AnimatedIcon(grumpySprite, 12, 20, 4, 2000);
		add(grumpy);
		grumpy.setVisible(false);
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
	 * Display or hide poisoned icon
	 * 
	 * @param poisoned
	 */
	protected void setPoisoned(boolean poisoned) {
		if (poison.isVisible() != poisoned) {
			poison.setVisible(poisoned);
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
