/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import java.awt.Color;
import java.awt.Dimension;

import games.stendhal.client.gui.LinearScalingModel;
import games.stendhal.client.gui.StatusDisplayBar;


/**
 * A bar indicator component for HP.
 */
public class HPIndicator extends StatusDisplayBar {

	/** Default preferred height of the component. */
	private static final int DEFAULT_HEIGHT = 6;


	public HPIndicator() {
		super(new LinearScalingModel());
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
		setMinimumSize(getPreferredSize());
	}

	/**
	 * Set the ratio of HP/Maximum HP.
	 *
	 * @param ratio
	 * 		HP ratio.
	 */
	public void setRatio(float ratio) {
		// hack to prevent ratio from being over 1.0
		if (ratio > 1.0f) {
			ratio = 1.0f;
		}

		// Pick a color from red to green depending on the hp ratio.
		float r = Math.min((1.0f - ratio) * 2.0f, 1.0f);
		float g = Math.min(ratio * 2.0f, 1.0f);
		setBarColor(new Color(r, g, 0.0f));
		getModel().setValue(ratio);
	}

	/**
	 *
	 * @param maxhp
	 * @param hp
	 */
	public void setHP(final int maxhp, final int hp) {
		final float ratio = (float) hp / maxhp;
		setRatio(ratio);
	}
}
