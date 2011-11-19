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

import games.stendhal.common.Level;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Object for listening for various user state changes that should
 * be show.
 */
public class StatsPanelController {
	private static final String[] MONEY_SLOTS = { "bag", "lhand", "rhand" };
	private StatsPanel panel;
	private static	StatsPanelController instance;
	
	/**
	 * The money objects.
	 * First level keys are the slot name. Second level is the object id.
	 */
	private final HashMap<String, HashMap<String, RPObject>> money = new HashMap<String, HashMap<String, RPObject>>();
	
	private int level;
	private int xp;
	private int hp;
	private int maxhp;
	
	private int atk;
	private int atkxp;
	private int weaponAtk;
	
	private int def;
	private int defxp;
	private int itemDef;
	
	private int mana;
	private int base_mana;
	
	/**
	 * Create a new <code>StatsPanelController</code>. There
	 * should be only one, so the constructor is hidden.
	 */
	private StatsPanelController() {
		panel = new StatsPanel();
	}
	
	/**
	 * Get the <code>StatsPanelController</code> instance.
	 * 
	 * @return the StatsPanelController instance
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
		pcs.addPropertyChangeListener("modified_base_hp", listener);
		
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
		
		listener = new ManaChangeListener();
		pcs.addPropertyChangeListener("mana", listener);
		pcs.addPropertyChangeListener("base_mana", listener);
	}
	
	/**
	 * Called when xp or level has changed
	 */
	private void updateLevel() {
		final int next = Level.getXP(level + 1) - xp;
		// Show "em-dash" for max level players rather than 
		// a confusing negative required xp.
		final String nextS = (next < 0) ? "\u2014" : Integer.toString(next);
			
		final String text = "Level: " + level + " (" + nextS + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setLevel(text);
			}
		});
	}
	
	/**
	 * Called when HP or max HP has changed.
	 */
	private void updateHP() {
		final String text = "HP: " + hp + "/" + maxhp;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setHP(text);
			}
		});
	}
	
	/**
	 * Called when atk, atkxp, or weaponAtk changes.
	 */
	private void updateAtk() {
		// atk uses 10 levels shifted starting point
		final int next = Level.getXP(atk - 9) - atkxp;
		final String text = "ATK: " + atk + "×" + (1 + weaponAtk) + " (" + next + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setAtk(text);
			}
		});
	}
	
	/**
	 * Called when def, defxp, or itemDef changes
	 */
	private void updateDef() {
		// def uses 10 levels shifted starting point
		final int next = Level.getXP(def - 9) - defxp;
		final String text = "DEF: " + def + "×" + (1 + itemDef) + " (" + next + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setDef(text);
			}
		});
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
				hp = Integer.parseInt((String) event.getNewValue());
			} else if (event.getPropertyName().equals("modified_base_hp")) {
				maxhp = Integer.parseInt((String) event.getNewValue());
			}
			updateHP();
		}
	}
	
	/**
	 * Called when there are additions to a potential money slot.
	 * 
	 * @param slot
	 * @param object
	 */
	@SuppressWarnings("null")
	private void addMoney(String slot, RPObject object) {
		HashMap<String, RPObject> set = money.get(slot);
		String id = object.get("id"); 
		
		boolean add = false;
		if ("money".equals(object.get("class"))) {
			add = true;
		}
		if (set == null) {
			if (add) {
				set = new HashMap<String, RPObject>();
				money.put(slot, set);
			}
		} else if (set.containsKey(id) && object.has("quantity")) {
			// Has been checked to be money before. Add only if there's 
			// quantity though. Adding to empty slots can create add events without.
			// Then the quantity has arrived in previous event
			add = true;
		}

		if (add) {
			set.put(object.get("id"), object);
			updateMoney();
		}
	}

	/**
	 * Called when items are removed from a potential money slot.
	 * 
	 * @param slot
	 * @param obj
	 */
	private void removeMoney(String slot, RPObject obj) {
		HashMap<String, RPObject> set = money.get(slot);
		if (set != null) {
			if (set.remove(obj.get("id")) != null) {
				updateMoney();
			}
		}
	}
	
	/**
	 * Count the money, and update the label text.
	 */
	private void updateMoney() {
		int amount = 0;
		
		for (HashMap<String, RPObject> stack : money.values()) {
			for (RPObject obj : stack.values()) {
				amount += obj.getInt("quantity");
			}
		}
		final String text = "Money: " + amount;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setMoney(text);
			}
		});
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
				atk = Integer.parseInt((String) event.getNewValue());
			} else {
				atkxp = Integer.parseInt((String) event.getNewValue());
			}
			updateAtk();
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
				def =  Integer.parseInt((String) event.getNewValue());
			} else {
				defxp = Integer.parseInt((String) event.getNewValue());
			}
			updateDef();
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
			xp = Integer.parseInt((String) event.getNewValue());
			updateLevel();
			final String text = "XP: " + xp;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					panel.setXP(text);
				}
			});
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
			level = Integer.parseInt((String) event.getNewValue());
			updateLevel();
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
			weaponAtk = Integer.parseInt((String) event.getNewValue());
			updateAtk();
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
			itemDef = Integer.parseInt((String) event.getNewValue());
			updateDef();
		}
	}
	
	/**
	 * Listener for eating and choking changes.
	 */
	private class EatingChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			// Deleted attribute can raise a null event
			if (event == null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						panel.setEating(false);
						panel.setChoking(false);
					}
				});
				return;
			}
			
			final boolean newStatus = event.getNewValue() != null;
			if ("eating".equals(event.getPropertyName())) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						panel.setEating(newStatus);
					}
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						panel.setChoking(newStatus);
					}
				});
			}
		}
	}
	
	/**
	 * Listener for poisoned status changes.
	 */
	private class PoisonedChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			// Deleted attribute can raise a null event
			Object value = null;
			if (event != null) {
				value = event.getNewValue();
			}
			
			final boolean poisoned = value != null;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					panel.setPoisoned(poisoned);
				}
			});
		}
	}
	
	/**
	 * Listener for away status changes.
	 */
	private class AwayChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			// Deleted attribute can raise a null event
			Object value = null;
			if (event != null) {
				value = event.getNewValue();
			}
			final boolean away = value != null;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					panel.setAway(away);
				}
			});
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
	 * Listener for mana changes.
	 */
	private class ManaChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
		
			try {
				if (event.getPropertyName().equals("mana")) {
					mana = Integer.parseInt((String) event.getNewValue());
					panel.setMana(mana);
				} else {
					base_mana = Integer.parseInt((String) event.getNewValue());
					panel.setBaseMana(base_mana);
				}
			} catch (NumberFormatException e) {
				Logger.getLogger(StatsPanelController.class).error("Invalid mana value", e);
			}
		}
	}
	
	/**
	 * Listener for grumpy status changes.
	 */
	private class GrumpyChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			// Deleted attribute can raise a null event
			Object value = null;
			if (event != null) {
				value = event.getNewValue();
			}
			final boolean grumpy = value != null;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					panel.setGrumpy(grumpy);
				}
			});
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
					removeMoney(slot.getName(), object);
				}
			}
			
			slot = (RPSlot) event.getNewValue();
			if (slot != null) {
				for (final RPObject object : slot) {
					// add everything. let the panel figure out if it's money
					addMoney(slot.getName(), object);
				}
			}
		}
	}
}
