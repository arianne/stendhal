/***************************************************************************
 *                 (C) Copyright 2003-2015 - Faiumoni e.V.                 *
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * ButtonUI implementation for drawing PixmapStyle buttons.
 */
public class StyledButtonUI extends BasicButtonUI {
	/** Shared UI instance for all buttons. */
	private static final StyledButtonUI UI = new StyledButtonUI(StyleUtil.getStyle());
	/**
	 * Listener for button focus changes. Contains no state so it can be
	 * shared for all buttons.
	 */
	private static final DefaultButtonFocusListener FOCUS_LISTENER = new DefaultButtonFocusListener();
	/** Used style. */
	private final Style style;

	/**
	 * Create a new StyledButtonUI.
	 *
	 * @param style used pixmap style
	 */
	public StyledButtonUI(Style style) {
		this.style = style;
	}

	/**
	 * Required by UIManager.
	 *
	 * @param button component to create UI for
	 * @return UI delegate
	 */
	public static ComponentUI createUI(JComponent button) {
		return UI;
	}

	@Override
	public void paint(Graphics graphics, JComponent button) {
		paintBackground(graphics, button);

		// Restore normal look after pressing ends, if needed
		if (button instanceof AbstractButton) {
			ButtonModel model = ((AbstractButton) button).getModel();
			if (!model.isPressed()) {
				// Try to avoid switching borders if the button has none or custom
				// borders
				if (button.getBorder().equals(style.getBorderDown())) {
					button.setBorder(style.getBorder());
				}
			}
			if (model.isRollover()) {
				hilite(graphics, button);
			}

			if (button instanceof JButton) {
				if (((JButton) button).isDefaultButton()) {
					Insets insets = button.getInsets();
					graphics.setColor(style.getShadowColor());
					int width = button.getWidth() - insets.right - insets.left - 3;
					int height = button.getHeight() - insets.top - insets.bottom - 3;
					graphics.drawRect(insets.left + 1, insets.right + 1, width, height);
				}
			}
		}

		super.paint(graphics, button);
	}

	@Override
	protected void paintButtonPressed(Graphics graphics, AbstractButton button) {
		// Try to avoid switching borders if the button has none, or custom
		// borders
		if (style.getBorder().equals(button.getBorder())) {
			button.setBorder(style.getBorderDown());
		}
	}

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
	protected void paintFocus(Graphics graphics, AbstractButton button,
			Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		graphics.setColor(style.getShadowColor());
		graphics.drawRect(textRect.x, textRect.y, textRect.width, textRect.height);
	}

	@Override
	public Dimension getPreferredSize(JComponent button) {
		/*
		 * The default styles do some weird trick with their borders that
		 * affects only the preferred size, but not the minimum size. Making
		 * a special border type that takes the size of the margin does not
		 * work, because that increases the minimum size as well. Anyway, the
		 * effect can be simulated like this.
		 */
		Dimension dim = super.getPreferredSize(button);

		if (button instanceof AbstractButton) {
			Insets margin = ((AbstractButton) button).getMargin();
			dim.width += margin.left + margin.right;
			dim.height += margin.top + margin.bottom;
		}

		return dim;
	}

	/**
	 * Draw the background image.
	 *
	 * @param graphics graphics
	 * @param button component whose background gets drawn
	 */
	private void paintBackground(Graphics graphics, JComponent button) {
		StyleUtil.fillBackground(style, graphics, 0, 0, button.getWidth(), button.getHeight());
	}

	/**
	 * Draws the mouse focus highlighting.
	 *
	 * @param graphics graphics
	 * @param button button to be highlighted
	 */
	private void hilite(Graphics graphics, JComponent button) {
		graphics.setColor(style.getHighLightColor());
		Insets insets = button.getInsets();
		// -1 to avoid right and bottom lines ending under the border
		int width = button.getWidth() - insets.right - insets.left - 1;
		int height = button.getHeight() - insets.top - insets.bottom - 1;
		graphics.drawRect(insets.left, insets.top, width, height);
	}

	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.addFocusListener(FOCUS_LISTENER);
		button.setForeground(style.getForeground());
		button.setBorder(style.getBorder());
	}

	/**
	 * Listener that follows when a button gets the focus, and makes it the
	 * default when it does. This makes enter push the selected button, instead
	 * of just space.
	 */
	private static class DefaultButtonFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent e) {
			changeDefault(e.getComponent(), true);
		}

		@Override
		public void focusLost(FocusEvent e) {
			changeDefault(e.getComponent(), false);
		}

		/**
		 * Change the default button of the root pane of the specified
		 * component.
		 *
		 * @param component the component whose root pane's default button
		 *	should be changed. It must be a JButton.
		 * @param setDefault if <code>true</code>, set the JButton specified by
		 * 	<code>component</code> as the default. Otherwise the default button
		 * 	is set to none
		 */
		private void changeDefault(Component component, boolean setDefault) {
			if (component instanceof JButton) {
				JButton button = (JButton) component;
				JRootPane parent = button.getRootPane();
				/*
				 * In some conditions, when pushing the button results
				 * in it being removed from the root container, a focus
				 * event can be processed after the button (or its parents)
				 * has been removed, so we need to check for a non-null
				 * root for a focused button, even though that seems
				 * nonsensical.
				 */
				if (parent != null) {
					if (setDefault) {
						parent.setDefaultButton(button);
					} else {
						parent.setDefaultButton(null);
					}
				}
			}
		}
	}
}
