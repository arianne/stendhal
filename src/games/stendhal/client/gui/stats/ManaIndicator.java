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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.SwingUtilities;

import games.stendhal.client.gui.LinearScalingModel;
import games.stendhal.client.gui.StatusDisplayBar;

/**
 * A bar indicator component for mana.
 */
public class ManaIndicator extends StatusDisplayBar implements PropertyChangeListener {
	private static final long serialVersionUID = 3462088641737184898L;

	private static ManaIndicator instance;

	private final LinearScalingModel model;

	/**
	 * Create the ManaIndicator instance.
	 *
	 * @return instance
	 */
	static synchronized ManaIndicator create() {
		if (instance == null) {
			LinearScalingModel model = new LinearScalingModel();
			instance = new ManaIndicator(model);
		} else {
			throw new IllegalStateException("Instance already created");
		}

		return instance;
	}

	/**
	 * Create a new mana indicator.
	 *
	 * @param model scaling model used for the indicator
	 */
	private ManaIndicator(final LinearScalingModel model) {
		super(model);
		this.model = model;
		setVisible(false);
		setBarColor(new Color(49, 75, 253));
	}

	/**
	 * gets the instance
	 *
	 * @return ManaIndicator
	 */
	public static ManaIndicator get() {
		if (instance == null) {
			throw new IllegalStateException("ManaIndicator not initialized");
		}
		return instance;
	}

	/**
	 * Set the mana value. This method may be called outside the event dispatch
	 * thread.
	 *
	 * @param mana
	 */
	public void setMana(double mana) {
		model.setValue(mana);
	}

	/**
	 * Set the base_mana value. This method may be called outside the event dispatch
	 * thread.
	 *
	 * @param base_mana
	 */
	public void setBaseMana(double base_mana) {
		model.setMaxValue(base_mana);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}

		// disable
		Map<?, ?> oldMap = (Map<?, ?>) evt.getOldValue();
		if ((oldMap != null) && oldMap.containsKey("spells")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
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
				@Override
				public void run() {
					setVisible(true);
				}
			});
		}
	}
}
