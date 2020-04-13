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

import static games.stendhal.client.gui.settings.SettingsProperties.HP_BAR_PROPERTY;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import games.stendhal.client.entity.StatusID;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * Display panel for status icons and player stats. The methods may be safely
 * called outside the event dispatch thread.
 */
class StatsPanel extends JPanel {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -353271026575752035L;

	private final StatLabel hpLabel, atkLabel, defLabel, ratkLabel, xpLabel, levelLabel, moneyLabel;
	private final HPIndicator hpBar;
	private final StatusIconPanel statusIcons;
	private final KarmaIndicator karmaIndicator;
	private final ManaIndicator manaIndicator;

	StatsPanel() {
		super();
		setLayout(new SBoxLayout(SBoxLayout.VERTICAL));

		statusIcons = new StatusIconPanel();
		add(statusIcons);

		karmaIndicator = KarmaIndicator.create();
		karmaIndicator.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		add(karmaIndicator, SLayout.EXPAND_X);

		manaIndicator = ManaIndicator.create();
		manaIndicator.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		manaIndicator.setToolTipText("Mana");
		add(manaIndicator, SLayout.EXPAND_X);

		hpLabel = new StatLabel();
		add(hpLabel, SLayout.EXPAND_X);

		hpBar = new HPIndicator();
		hpBar.setPreferredSize(new Dimension(0, 10));
		hpBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		add(hpBar, SLayout.EXPAND_X);

		// show/hide HP bar depending on settings property
		toggleHPBar(WtWindowManager.getInstance().getPropertyBoolean(HP_BAR_PROPERTY, true));

		atkLabel = new StatLabel();
		add(atkLabel, SLayout.EXPAND_X);

		ratkLabel = new StatLabel();
		add(ratkLabel, SLayout.EXPAND_X);
		/* only show RATK stat if set by server
		 *
		 * TODO: this can be removed in future versions
		 */
		ratkLabel.setVisible(false);

		defLabel = new StatLabel();
		add(defLabel, SLayout.EXPAND_X);

		xpLabel = new StatLabel();
		add(xpLabel, SLayout.EXPAND_X);

		levelLabel = new StatLabel();
		add(levelLabel, SLayout.EXPAND_X);

		moneyLabel = new StatLabel();
		add(moneyLabel, SLayout.EXPAND_X);
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
	 * Update the HP indicator bar.
	 *
	 * @param maxhp
	 * 		Player's maximum HP value.
	 * @param hp
	 * 		Player's actual HP value.
	 */
	void setHPBar(final int maxhp, final int hp) {
		hpBar.setHP(maxhp, hp);
	}

	/**
	 * Show/Hide HP bar.
	 *
	 * @param show
	 * 		If <code>true</code>, HP bar will be visible.
	 */
	void toggleHPBar(final boolean show) {
		hpBar.setVisible(show);
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
	 * Set the ratk description string.
	 *
	 * @param ratk
	 */
	void setRatk(String ratk) {
		/* only show RATK stat if set by server
		 *
		 * TODO: this can be removed in future versions
		 */
		if (!ratkLabel.isVisible()) {
			ratkLabel.setVisible(true);
		}
		ratkLabel.setText(ratk);
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
	 * Set player mana.
	 *
	 * @param mana
	 */
	void setMana(double mana) {
		manaIndicator.setMana(mana);
	}

	/**
	 * Set player base mana.
	 *
	 * @param baseMana
	 */
	void setBaseMana(double baseMana) {
		manaIndicator.setBaseMana(baseMana);
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
	 * Show or hide status indicator.
	 *
	 * @param ID
	 *         Status ID
	 * @param visible
	 *         Show indicator
	 */
	void setStatus(final StatusID ID, final boolean visible) {
	    statusIcons.setStatus(ID, visible);
	}

	/**
	 * Hide all status icons. This is called when the user entity is deleted.
	 */
	void resetStatuses() {
		statusIcons.resetStatuses();
	}

	/**
	 * Set the away message. null hides the indicator
	 *
	 * @param message
	 */
	void setAway(final String message) {
		statusIcons.setAway(message);
	}

	/**
	 * Show or hide grumpy indicator. null hides the indicator
	 *
	 * @param message
	 */
	void setGrumpy(final String message) {
		statusIcons.setGrumpy(message);
	}

	/**
	 * A multi line, label like component for the status rows.
	 */
	private static class StatLabel extends JTextArea {
		public StatLabel() {
			setOpaque(false);
			setEditable(false);
			setFocusable(false);
			setWrapStyleWord(true);
			setLineWrap(true);
			Style style = StyleUtil.getStyle();
			if (style != null) {
				setForeground(style.getForeground());
			}
		}
	}
}
