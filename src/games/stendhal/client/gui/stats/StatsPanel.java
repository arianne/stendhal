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

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display panel for status icons and player stats. The methods may be safely
 * called outside the event dispatch thread.
 */
public class StatsPanel extends JPanel {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -353271026575752035L;

	private final StatLabel hpLabel, atkLabel, defLabel, xpLabel, levelLabel, moneyLabel;
	private final StatusIconPanel statusIcons;
	private final KarmaIndicator karmaIndicator;
	
	StatsPanel() {
		super();
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		
		statusIcons = new StatusIconPanel(); 
		add(statusIcons);
		
		karmaIndicator = new KarmaIndicator();
		karmaIndicator.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		karmaIndicator.setToolTipText("Karma");
		add(karmaIndicator);
		
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
	 * Set the HP description string.
	 * 
	 * @param hp
	 */
	void setHP(String hp) {
		hpLabel.setText(hp);
	}

	/**
	 * Set the atk description string.
	 * 
	 * @param atk
	 */
	void setAtk(String atk) {
		atkLabel.setText(atk);
	}

	/**
	 * Set the def description string
	 * 
	 * @param def
	 */
	void setDef(String def) {
		defLabel.setText(def);
	}

	/**
	 * Set the XP description string.
	 * 
	 * @param xp
	 */
	void setXP(String xp) {
		xpLabel.setText(xp);
	}

	/**
	 * Set player karma.
	 * 
	 * @param karma
	 */
	void setKarma(double karma) {
		karmaIndicator.setValue(karma);
	}
	
	/**
	 * Set the level description.
	 * 
	 * @param level level description
	 */
	void setLevel(String level) {
		levelLabel.setText(level);
	}
	
	/**
	 * Set the money description string.
	 * 
	 * @param money
	 */
	void setMoney(String money) {
		moneyLabel.setText(money);
	}
	
	/**
	 * Show or hide the eating status indicator.
	 * 
	 * @param eating
	 */
	void setEating(final boolean eating) {
		statusIcons.setEating(eating);
	}
	
	/**
	 * Show or hide the choking status indicator.
	 * 
	 * @param choking
	 */
	void setChoking(final boolean choking) {
		statusIcons.setChoking(choking);
	}
	
	/**
	 * Show or hide the poisoned status indicator.
	 * 
	 * @param poisoned
	 */
	void setPoisoned(final boolean poisoned) {
		statusIcons.setPoisoned(poisoned);
	}
	
	/**
	 * Show or hide away indicator.
	 * 
	 * @param away
	 */
	void setAway(final boolean away) {
		statusIcons.setAway(away);
	}
	
	/**
	 * Show or hide grumpy indicator
	 * 
	 * @param grumpy
	 */
	void setGrumpy(final boolean grumpy) {
		statusIcons.setGrumpy(grumpy);
	}

	private static class StatLabel extends JLabel {
		private static final long serialVersionUID = -6830358556358203566L;

		public StatLabel() {
			// unbold
			Font f = getFont();
			if ((f.getStyle() & Font.BOLD) != 0) {
				setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			}
		}
	}
}
