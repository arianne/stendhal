package games.stendhal.client.gui.stats;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class StatsPanelController {
	private static final String[] MONEY_SLOTS = { "bag", "lhand", "rhand" };
	private StatsPanel panel;
	private static	StatsPanelController instance;
	
	private StatsPanelController() {
		panel = new StatsPanel();
	}
	
	public static synchronized StatsPanelController get() {
		if (instance == null) {
			instance = new StatsPanelController();
		}
		return instance;
	}
	
	public StatsPanel getComponent() {
		return panel;
	}
	
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
	}
	
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
	
	private class XPChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setXP(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	private class LevelChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setLevel(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	private class WeaponChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setWeaponAtk(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
	private class ArmorChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			panel.setItemDef(Integer.parseInt((String) event.getNewValue()));
		}
	}
	
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
	
	private class PoisonedChangeListener implements PropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event == null) {
				return;
			}
			
			panel.setPoisoned(event.getNewValue() != null);
		}
	}
	
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
