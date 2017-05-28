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
package games.stendhal.client.gui.spells;

import javax.swing.SwingUtilities;

import games.stendhal.client.gui.SlotWindow;
import games.stendhal.client.listener.FeatureChangeListener;
/**
 * Container displaying the spells of the player.
 *
 * @author madmetzger
 *
 */
public class Spells extends SlotWindow implements FeatureChangeListener {

	private static final long serialVersionUID = 79889495195014549L;

	public Spells() {
		super("spells", 3, 2);
		//panel window, no closing allowed
		setCloseable(false);
	}

	@Override
	public void featureDisabled(final String name) {
		if (name.equals("spells")) {
			if(isVisible()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(false);
					}
				});
			}
		}
	}

	@Override
	public void featureEnabled(final String name, final String value) {
		if (name.equals("spells")) {
			if(!isVisible()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(true);
					}
				});
			}
		}
	}

}
