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
package games.stendhal.client.gui.styled;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;

public class StyledCheckBoxUI extends BasicCheckBoxUI {
	private static final int ICON_WIDTH = 14;

	private static StyledCheckBoxUI instance;
	private final Style style;
	/** Icon for active, non selected state */
	private Icon defaultIcon;
	/** Icon for active, selected state */
	private Icon defaultSelectedIcon;
	/** Icon for disabled, non selected state*/
	private Icon disabledIcon;
	/** Icon for disabled, selected state*/
	private Icon disabledSelectedIcon;

	/**
	 * Create StyledCheckBoxUI for a check box.
	 *
	 * @param checkBox <code>JCheckBox</code> to create an UI for
	 * @return a ComponentUI instance
	 */
	// required by UIManager. Not necessarily called from one thread
	public static synchronized ComponentUI createUI(JComponent checkBox) {
		// Label UIs can be shared
		if (instance == null) {
			instance = new StyledCheckBoxUI(StyleUtil.getStyle());
		}

		return instance;
	}

	/**
	 * Create a new StyledCheckBoxUI.
	 *
	 * @param style pixmap style
	 */
	public StyledCheckBoxUI(Style style) {
		this.style = style;
		createIcons();
	}

	/*
	 * Copied from StyledButtonUI. Unfortunately we do not inherit that.
	 */
	@Override
	protected void paintText(Graphics graphics, AbstractButton button,
			Rectangle textRect, String text) {
		if (button.isEnabled()) {
			super.paintText(graphics, button, textRect, text);
		} else {
			int shift = graphics.getFontMetrics().getAscent();

			StyleUtil.paintDisabledText(style, graphics, text, textRect.x, textRect.y + shift);
		}
	}

	@Override
	public void installUI(JComponent component) {
		super.installUI(component);
		component.setForeground(style.getForeground());
		component.setOpaque(false);
		component.setFont(style.getFont().deriveFont(Font.BOLD));

		if (component instanceof JCheckBox) {
			JCheckBox checkBox = (JCheckBox) component;
			checkBox.setIcon(defaultIcon);
			checkBox.setSelectedIcon(defaultSelectedIcon);
			checkBox.setDisabledIcon(disabledIcon);
			checkBox.setDisabledSelectedIcon(disabledSelectedIcon);
		}
	}

	/**
	 * Create the icons for all the states.
	 */
	private void createIcons() {
		Border border = style.getBorderDown();
		Insets insets = border.getBorderInsets(null);

		// Image used as the template for the icons
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(ICON_WIDTH, ICON_WIDTH, Transparency.OPAQUE);

		// Active, not selected
		Graphics2D g = image.createGraphics();
		g.setColor(style.getForeground());
		g.fillRect(0, 0, ICON_WIDTH, ICON_WIDTH);
		g.setClip(0, 0, ICON_WIDTH, ICON_WIDTH);
		border.paintBorder(null, g, 0, 0, ICON_WIDTH, ICON_WIDTH);
		g.dispose();
		defaultIcon = new ImageIcon(image);

		// Active, selected
		// Icon does not copy the image, so we need a new one
		BufferedImage image2 = gc.createCompatibleImage(ICON_WIDTH, ICON_WIDTH, Transparency.OPAQUE);
		g = image2.createGraphics();
		// Copy the old image to the background
		g.drawImage(image, 0, 0, null);
		// Draw the tick
		BasicStroke stroke = new BasicStroke(2);
		g.setStroke(stroke);
		g.setColor(style.getShadowColor());
		drawTick(g, insets);
		g.dispose();
		defaultSelectedIcon = new ImageIcon(image2);

		// Inactive, not selected
		image2 = gc.createCompatibleImage(ICON_WIDTH, ICON_WIDTH, Transparency.OPAQUE);
		g = image2.createGraphics();
		g.setColor(style.getShadowColor());
		g.fillRect(0, 0, ICON_WIDTH, ICON_WIDTH);
		g.setClip(0, 0, ICON_WIDTH, ICON_WIDTH);
		border.paintBorder(null, g, 0, 0, ICON_WIDTH, ICON_WIDTH);
		g.dispose();
		disabledIcon = new ImageIcon(image2);

		// Inactive, selected
		image = gc.createCompatibleImage(ICON_WIDTH, ICON_WIDTH, Transparency.OPAQUE);
		g = image.createGraphics();
		// Copy unselected image
		g.drawImage(image2, 0, 0, null);
		g.setStroke(stroke);
		g.setColor(style.getHighLightColor());
		drawTick(g, insets);
		g.dispose();
		disabledSelectedIcon = new ImageIcon(image);
	}

	/**
	 * Draw selection marker.
	 *
	 * @param g graphics
	 * @param insets component insets
	 */
	private void drawTick(Graphics g, Insets insets) {
		g.drawLine(insets.left + 2, ICON_WIDTH / 2, insets.left + 4, ICON_WIDTH - insets.bottom - 2);
		g.drawLine(ICON_WIDTH - insets.right - 2, ICON_WIDTH / 3, insets.left + 4, ICON_WIDTH - insets.bottom - 2);
	}
}
