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

import games.stendhal.client.gui.LinearScalingModel;
import games.stendhal.client.gui.StatusDisplayBar;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A cell renderer for the member list. Shows names of the members, the leader
 * in bold. Members that are present have a colored HP bar, like on the game
 * screen. Absent members have a grayed out HP bar. 
 */
class MemberCellRenderer implements ListCellRenderer {
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
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Member member = (Member) value;
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
	private static class HPBar extends StatusDisplayBar {
		/** Default preferred height of the component */
		private static final int DEFAULT_HEIGHT = 6;
		
		/**
		 * Create new HP bar.
		 */
		HPBar() {
			super(new LinearScalingModel());
			setBackground(Color.DARK_GRAY);
			setForeground(Color.WHITE);
			setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
			setMinimumSize(getPreferredSize());
		}
		
		/**
		 * Set present or absent.
		 * 
		 * @param present
		 */
		void setPresent(boolean present) {
			// Show full, but gray bar
			setBarColor(Color.LIGHT_GRAY);
			getModel().setValue(1.0);
		}
		
		/**
		 * Set the ratio of HP/Maximum HP.
		 * 
		 * @param ratio
		 */
		void setRatio(float ratio) {
			// Pick a color from red to green depending on the hp ratio. 
			float r = Math.min((1.0f - ratio) * 2.0f, 1.0f);
			float g = Math.min(ratio * 2.0f, 1.0f);
			setBarColor(new Color(r, g, 0.0f));
			getModel().setValue(ratio);
		}
	}
}