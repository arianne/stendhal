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

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A bar indicator component for mana.
 */
public class ManaIndicator extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 3462088641737184898L;

	private static ManaIndicator instance;

	private static final String IMAGE_FILE_NAME = "data/gui/mana_scale.png";
	
	/** Mana scaled to pixels */
	private double mana;
	private double base_mana;
	private final Sprite image;
	
	/**
	 * Create a new mana indicator.
	 */
	public ManaIndicator() {
		instance = this;
		setVisible(false);
		final SpriteStore store = SpriteStore.get();
		image = store.getSprite(IMAGE_FILE_NAME);
		
		// We don't draw the background
		setOpaque(false);
	}

	/**
	 * gets the instance
	 *
	 * @return ManaIndicator
	 */
	public static ManaIndicator get() {
		return instance;
	}

	/**
	 * Set the mana value. This method may be called outside the event dispatch
	 * thread.
	 * 
	 * @param mana
	 */
	public void setMana(double mana) {
		this.mana = mana;
		repaint();
	}

	/**
	 * Set the base_mana value. This method may be called outside the event dispatch
	 * thread.
	 * 
	 * @param base_mana
	 */
	public void setBaseMana(double base_mana) {
		this.base_mana = (int) Math.floor(base_mana);
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension pref = new Dimension(image.getWidth(), image.getHeight());
		
		Insets insets = getInsets();
		pref.width += insets.left + insets.right;
		pref.height += insets.top + insets.bottom;
		
		return pref;
	}
	
	@Override
	public Dimension getMinimumSize() {
		// Preferred is also the minimum size where the bar can be drawn properly
		return getPreferredSize();
	}
	
	/**
	 * Scale a mana value to bar length.
	 * 
	 * @param mana player mana
	 * @return length of the drawn bar in pixels
	 */
	private int scale(double mana) {
		if(base_mana == 0) {
			return 0;
		} else {
			return (int) (image.getWidth() * mana / base_mana);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Insets insets = getInsets();
		// Paint black what is not covered by the colored bar
		g.setColor(Color.BLACK);
		g.fillRect(insets.left, insets.top, image.getWidth(), image.getHeight());
		// Draw appropriate length of the image
		g.clipRect(insets.left, insets.top, scale(mana), getHeight());
		image.draw(g, insets.left, insets.top);
		this.setToolTipText("Mana: " + (int) mana + "/" + (int) base_mana);
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}

		// disable
		Map<?, ?> oldMap = (Map<?, ?>) evt.getOldValue();
		if ((oldMap != null) && oldMap.containsKey("spells")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setVisible(false);
				}
			});
		}

		// enable
		Map<?, ?> newMap = (Map<?, ?>) evt.getNewValue();
		if ((newMap != null) && newMap.containsKey("spells")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setVisible(true);
				}
			});
		}
	}
}
