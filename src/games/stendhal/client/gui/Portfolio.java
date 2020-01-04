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
package games.stendhal.client.gui;


import javax.swing.SwingUtilities;

import games.stendhal.client.listener.FeatureChangeListener;

/**
 * A Portfolio.
 */
@SuppressWarnings("serial")
class Portfolio extends SlotWindow implements FeatureChangeListener {
	/**
	 * Creates a Portfolio.
	 */
	public Portfolio() {
		// Remember: when you change numbers below, also
		// correctly match available slot in:
		// src/games/stendhal/server/entity/RPEntityRPClass.java
		super("portfolio", 3, 3);
		// A panel window; forbid closing
		setCloseable(false);
	}

	//
	// Portfolio
	//

	/**
	 * Disable the Portfolio.
	 */
	private void disablePortfolio() {
		/*
		 * You can not really lose a keyring for now, but
		 * a disable message is received at every map change.
		 * Just ignore it. (And after keyrings are made to
		 * real items, this whole file will be obsolete anyway).
		 */
	}

	//
	// FeatureChangeListener
	//

	/**
	 * A feature was disabled.
	 *
	 * @param name
	 *            The name of the feature.
	 */
	@Override
	public void featureDisabled(final String name) {
		if (name.equals("portfolio")) {
			disablePortfolio();
		}
	}

	/**
	 * A feature was enabled.
	 *
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            Optional feature specific data.
	 */
	@Override
	public void featureEnabled(final String name, final String value) {
		if (name.equals("portfolio")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(!isVisible()) {
						setVisible(true);
					}
				}
			});
		}
	}
}
