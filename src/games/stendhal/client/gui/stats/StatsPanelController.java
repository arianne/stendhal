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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Object for listening for various user state changes that should
 * be show.
 */
public class StatsPanelController {
	private static final String[] MONEY_SLOTS = { "bag", "lhand", "rhand" };
	private StatsPanel panel;
	private static	StatsPanelController instance;
	
	/**
	 * Create a new <code>StatsPanelController</code>. There
	 * should be only one, so the constructor is hidden.
	 */
	private StatsPanelController() {
		panel = new StatsPanel();
	}
	
	/**
	 * Get the <code>StatsPanelController</code> instance.
	 */
	public static synchronized StatsPanelController get() {
		if (instance == null) {
			instance = new StatsPanelController();
		}
		return instance;
	}
	
	/**
	 * Get the <code>StatsPanel</code> component this controller
	 * is keeping updated.
	 * 
	 * @return StatsPanel
	 */
	public StatsPanel getComponent() {
		return panel;
	}
	
	/**
	 * Add listeners for all the properties this object follows.
	 * 
	 * @param pcs property change support of the user
	 */
	public void registerListeners(PropertyChangeSupport pcs) {
		PropertyChangeListener listener = new HPChangeListener(); 
		pcs.addPropertyChangeListener("hp", listener);
		pcs.addPropertyChangeListener("base_hp", listener);
		
		listener = new ATKChangeListener();
		pcs.addPropertyChangeListener("atk", listener);
		pcs.addPropertyChangeListener("atk_xp", listener);
		
		listener = new DEFChangeListener();
		pcs.addPropertyChangeListener("def", listener);
		pcs.addPropertyChangeListener("def_xp", listener);
		
		listener = new XPChangeListener();
		pcs.addPropertyChangeListener("xp", listener);
		
		listener = new LevelChangeListener();
		pcs.addPropertyChangeListener("level", listener);
		
		listener = new WeaponChangeListener();
		pcs.addPropertyChangeListener("atk_item", listener);
		
		listener = new ArmorChangeListener();
		pcs.addPropertyChangeListener("def_item", listener);
		
		listener = new MoneyChangeListener();
		for (String slot : MONEY_SLOTS) {
			pcs.addPropertyChangeListener(slot, listener);
		}
		
		listener = new EatingChangeListener();
		pcs.addPropertyChangeListener("eating", listener);
		pcs.addPropertyChangeListener("choking", listener);
		
		listener = new PoisonedChangeListener();
		pcs.addPropertyChangeListener("poisoned", listener);
		
		listener = new AwayChangeListener();
		pcs.addPropertyChangeListener("away", listener);
		
		listener = new GrumpyChangeListener();
		pcs.addPropertyChangeListener("grumpy", listener);
		
		listener = new KarmaChangeListener();
		pcs.addPropertyChangeListener("karma", listener);
	}
	
	/**
	 * Listener for HP and base_hp changes.
	 */
	private class HPChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			if (event.getPropertyName().equals("hp")) {
				panel.setHP(Integer.parseInt((String) event.getNewValue()));
			} else {
				panel.setMaxHP(Integer.parseInt((String) event.getNewValue()));
			}
		}
	}
	
	/**
	 * Listener for atk and atk_xp changes.
	 */
	private class ATKChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			if ("atk".equals(event.getPropertyName())) {
				panel.setATK(Integer.parseInt((String) event.getNewValue()));
			} else {
				panel.setATKXP(Integer.parseInt((String) event.getNewValue()));
			}
		}
	}
	
	/**
	 * Listener for def and def_xp changes.
	 */
	private class DEFChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			if (event.getPropertyName().equals("def")) {
				panel.setDEF(Integer.parseInt((String) event.getNewValue()));
			} else {
				panel.setDEFXP(Integer.parseInt((String) event.getNewValue()));
			}
		}
	}
	
	/**
	 * Listener for xp changes.
	 */
	private class XPChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setXP(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	/**
	 * Listener for level changes.
	 */
	private class LevelChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setLevel(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	/**
	 * Listener for weapon atk changes.
	 */
	private class WeaponChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setWeaponAtk(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	/**
	 * Listener for armor def changes.
	 */
	private class ArmorChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setItemDef(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	/**
	 * Listener for eating and choking changes.
	 */
	private class EatingChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			boolean newStatus = event.getNewValue() != null;
			if ("eating".equals(event.getPropertyName())) {
				panel.setEating(newStatus);
			} else {
				panel.setChoking(newStatus);
			}
		}
	}
	
	/**
	 * Listener for poisoned status changes.
	 */
	private class PoisonedChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			panel.setPoisoned(event.getNewValue() != null);
		}
	}
	
	/**
	 * Listener for away status changes.
	 */
	private class AwayChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			panel.setAway(event.getNewValue() != null);
		}
	}
	
	/**
	 * Listener for karma changes.
	 */
	private class KarmaChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
		
			try {
				String newKarma = (String) event.getNewValue();
				panel.setKarma(Double.parseDouble(newKarma));
			} catch (NumberFormatException e) {
				Logger.getLogger(StatsPanelController.class).error("Invalid karma value", e);
			}
		}
	}
	
	/**
	 * Listener for grumpy status changes.
	 */
	private class GrumpyChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			panel.setGrumpy(event.getNewValue() != null);
		}
	}
	
	/**
	 * Listener for money changes.
	 * Due to there being no "money" property for the player, this
	 * need to listen to all the slots where it's possible to store money.
	 */
	private class MoneyChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			RPSlot slot = (RPSlot) event.getOldValue();
			if (slot != null) {
				for (final RPObject object : slot) {
					panel.removeMoney(slot.getName(), object);
				}
			}
			
			slot = (RPSlot) event.getNewValue();
			if (slot != null) {
				for (final RPObject object : slot) {
					// add everything. let the panel figure out if it's money
					panel.addMoney(slot.getName(), object);
				}
			}
		}
	}
}
