package games.stendhal.client.gui.stats;

import games.stendhal.client.entity.User;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPanel;
import games.stendhal.common.Level;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;

public class StatsPanel extends StyledJPanel {
	private final StatLabel hpLabel, atkLabel, defLabel, xpLabel, levelLabel, moneyLabel;
	private final StatusIconPanel statusIcons;
	private int hp, maxhp, atk, atkxp, weaponAtk, def, defxp, itemDef, xp, level;
	/**
	 * The money objects.
	 * First level keys are the slot name. Second level is the object id.
	 */
	private HashMap<String, HashMap<String, RPObject>> money = new HashMap<String, HashMap<String, RPObject>>();
	
	private boolean initialized = false;
	
	public StatsPanel() {
		super(WoodStyle.getInstance());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		statusIcons = new StatusIconPanel(); 
		add(statusIcons);
		
		hpLabel = new StatLabel();
		add(hpLabel);
		
		atkLabel = new StatLabel();
		add(atkLabel);
		
		defLabel = new StatLabel();
		add(defLabel);
		
		xpLabel = new StatLabel();
		add(xpLabel);
		
		levelLabel = new StatLabel();
		add(levelLabel);
		
		moneyLabel = new StatLabel();
		add(moneyLabel);
	}
	
	/**
	 * An ugly hack to work around java 5 and 6 treating this differently.
	 * Should be removed in case this panel gets placed somewhere it determines
	 * the width.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		//d.width = 0;
		return d;
	}
	
	/**
	 * Initialize from the values of <code>User</code>.
	 * This is needed because some values are not sent on user creation. 
	 */
	private void init() {
		if (!initialized) {
			User user = User.get();
			if (user == null) {
				return;
			}
			
			setHP(user.getHP());
			setMaxHP(user.getBase_hp());
			setXP(user.getXp());
			setLevel(user.getLevel());
			
			initialized = true;
		}
	}
	
	protected void setHP(int hp) {
		this.hp = hp;
		updateHP();
	}
	
	protected void setMaxHP(int hp) {
		this.maxhp = hp;
		updateHP();
	}
	
	protected void setATK(int atk) {
		this.atk = atk;
		init();
		updateATK();
	}
	
	protected void setATKXP(int atkxp) {
		this.atkxp = atkxp;
		updateATK();
	}
	
	protected void setWeaponAtk(int atk) {
		this.weaponAtk = atk;
		updateATK();
	}
	
	protected void setDEF(int def) {
		this.def = def;
		updateDEF();
	}
	
	protected void setDEFXP(int defxp) {
		this.defxp = defxp;
		updateDEF();
	}
	
	protected void setItemDef(int def) {
		itemDef = def;
		updateDEF();
	}
	
	protected void setXP(int xp) {
		this.xp = xp;
		xpLabel.setText("XP: " + xp);
		updateLevel();
	}
	
	protected void setLevel(int level) {
		this.level = level;
		updateLevel();
	}
	
	protected void setEating(final boolean eating) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusIcons.setEating(eating);
			}
		});
	}
	
	protected void setChoking(final boolean choking) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusIcons.setChoking(choking);
			}
		});
	}
	
	protected void setPoisoned(final boolean poisoned) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusIcons.setPoisoned(poisoned);
			}
		});
	}
		
	protected void addMoney(String slot, RPObject object) {
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
	
	protected void removeMoney(String slot, RPObject obj) {
		HashMap<String, RPObject> set = money.get(slot);
		if (set != null) {
			if (set.remove(obj.get("id")) != null) {
				updateMoney();
			}
		}
	}
	
	private void updateHP() {
		final String text = "HP: " + hp + "/" + maxhp;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				hpLabel.setText(text);
				hpLabel.paintImmediately(hpLabel.getVisibleRect());
			}
		});
	}
	
	private void updateATK() {
		// atk uses 10 levels shifted starting point
		final int next = Level.getXP(atk - 9) - atkxp;
		final String text = "ATK: " + atk + "×" + (1 + weaponAtk) + " (" + next + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				atkLabel.setText(text);
				atkLabel.paintImmediately(atkLabel.getVisibleRect());
			}
		});
	}
	
	private void updateDEF() {
		// def uses 10 levels shifted starting point
		final int next = Level.getXP(def - 9) - defxp;
		final String text = "DEF: " + def + "×" + (1 + itemDef) + " (" + next + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				defLabel.setText(text);
				defLabel.paintImmediately(defLabel.getVisibleRect());
			}
		});
	}
	
	private void updateLevel() {
		final int next = Level.getXP(level + 1) - xp;
		// Show "em-dash" for max level players rather than 
		// a confusing negative required xp.
		final String nextS = (next < 0) ? "\u2014" : Integer.toString(next);
			
		final String text = "Level: " + level + " (" + nextS + ")";
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				levelLabel.setText(text);
				levelLabel.paintImmediately(levelLabel.getVisibleRect());
			}
		});
	}
	
	private void updateMoney() {
		int amount0 = 0;
		
		for (HashMap<String, RPObject> stack : money.values()) {
			for (RPObject obj : stack.values()) {
				amount0 += obj.getInt("quantity");
			}
		}
		final int amount = amount0;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moneyLabel.setText("Money: " + amount);
				moneyLabel.paintImmediately(moneyLabel.getVisibleRect());
			}
		});
	}
	
	private static class StatLabel extends JLabel {
		public StatLabel() {
			setForeground(Color.WHITE);
			// unbold
			Font f = getFont();
			if ((f.getStyle() & Font.BOLD) != 0) {
				setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			}
		}
	}
}
