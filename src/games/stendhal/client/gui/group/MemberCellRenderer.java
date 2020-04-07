/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.group;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.stats.HPIndicator;

/**
 * A cell renderer for the member list. Shows names of the members, the leader
 * in bold. Members that are present have a colored HP bar, like on the game
 * screen. Absent members have a grayed out HP bar.
 */
class MemberCellRenderer implements ListCellRenderer<Member> {
	private final JComponent renderer = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
	private final JLabel label;
	private final HPBar hpBar;

	private final Font boldFont;
	private final Font normalFont;

	/**
	 * Create a new MemberCellRenderer.
	 */
	MemberCellRenderer() {
		label = new JLabel();
		label.setOpaque(false);
		renderer.add(label);
		Font f = label.getFont();
		if ((f.getStyle() & Font.BOLD) != 0) {
			boldFont = f;
			normalFont = f.deriveFont(f.getStyle() ^ Font.BOLD);
		} else {
			normalFont = f;
			boldFont = f.deriveFont(f.getStyle() | Font.BOLD);
		}

		hpBar = new HPBar();
		renderer.add(hpBar, SLayout.EXPAND_X);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Member> list,
			Member member, int index, boolean isSelected, boolean cellHasFocus) {
		label.setText(member.getName());
		if (member.isLeader()) {
			label.setFont(boldFont);
		} else {
			label.setFont(normalFont);
		}

		/*
		 * This is very, very ugly. JList does not resize the component
		 * normally, so the StatusDisplayBar never gets the chance to update
		 * the model. Try to figure out the correct width.
		 */
		Insets insets = hpBar.getInsets();
		int barWidth = list.getWidth() - insets.left - insets.right - 2;
		insets = list.getInsets();
		barWidth -= insets.left + insets.right;
		hpBar.getModel().setMaxRepresentation(barWidth);

		if (member.isPresent()) {
			hpBar.setPresent(true);
			hpBar.setRatio(member.getHpRatio());
		} else {
			hpBar.setPresent(false);
		}

		return renderer;
	}

	/**
	 * HP bar component with a grayed out mode for absent members.
	 */
	private static class HPBar extends HPIndicator {

		/**
		 * Set present or absent.
		 *
		 * @param present present status of the member
		 */
		void setPresent(boolean present) {
			// Show full, but gray bar
			if (!present) {
				setBarColor(Color.LIGHT_GRAY);
				getModel().setValue(1.0);
			}
		}
	}
}
