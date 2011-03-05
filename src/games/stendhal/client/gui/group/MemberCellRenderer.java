/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

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
	
	final Font boldFont;
	final Font normalFont;
	
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
		renderer.add(hpBar, SBoxLayout.constraint(SLayout.EXPAND_X));
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Member member = (Member) value;
		label.setText(member.getName());
		if (member.isLeader()) {
			label.setFont(boldFont);
		} else {
			label.setFont(normalFont);
		}
		
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
	private static class HPBar extends JComponent {
		private static final long serialVersionUID = 1L;

		/** Default preferred width of the component */
		private static final int DEFAULT_WIDTH = 20;
		/** Default preferred height of the component */
		private static final int DEFAULT_HEIGHT = 6;
		
		/** Default preferred dimensions */
		private final Dimension defaultDim = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		private boolean present;
		/** HP ratio */
		private float ratio;
		
		/**
		 * Set present or absent.
		 * 
		 * @param present
		 */
		void setPresent(boolean present) {
			this.present = present;
		}
		
		/**
		 * Set the ratio of HP/Maximum HP.
		 * 
		 * @param ratio
		 */
		void setRatio(float ratio) {
			this.ratio = ratio;
		}
		
		@Override
		public void paintComponent(Graphics graphics) {
			if (present) {
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, 0, getWidth(), getHeight());
				
				// pick a color from red to green depending on the hp ratio
				float r = Math.min((1.0f - ratio) * 2.0f, 1.0f);
				float g = Math.min(ratio * 2.0f, 1.0f);
				graphics.setColor(new Color(r, g, 0.0f));
				graphics.fillRect(0, 0, (int) (ratio * getWidth()), getHeight());
			} else {
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.fillRect(0, 0, getWidth(), getHeight());
			}
			graphics.setColor(Color.WHITE);
			graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return defaultDim;
		}
	}
}