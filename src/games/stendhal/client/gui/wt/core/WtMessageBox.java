/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * MessageBox.java
 *
 * Created on 23. Oktober 2005, 11:09
 */

package games.stendhal.client.gui.wt.core;

import games.stendhal.client.IGameScreen;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple MessageBox.
 * 
 * @author matthias
 */
public class WtMessageBox extends WtPanel implements WtClickListener,
		WtCloseListener {

	/** the max height of the panel. */
	private static final int MAX_HEIGHT = 100;

	/** space between the buttons. */
	private static final int BUTTON_SPACING = 5;

	/** the text panel. */
	private final WtTextPanel textPanel;

	/** the button. */
	private final List<WtButton> buttons;

	/** name of the button clicked when the window is closed. */
	private final String closeButtonName;

	/** false when the messagebox still has to layout the buttons . */
	private boolean layedout;

	/**
	 * Creates a new instance of MessageBox.
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param message
	 * @param buttonCombination
	 * @param gameScreen
	 */
	public WtMessageBox(final String name, final int x, final int y, final int width, final String message,
			final ButtonCombination buttonCombination, final IGameScreen gameScreen) {
		super(name, x, y, width, MAX_HEIGHT, gameScreen);

		textPanel = new WtTextPanel("messageboxtext", 5, 0, width - 20,
				MAX_HEIGHT, message, gameScreen);
		addChild(textPanel);

		buttons = new ArrayList<WtButton>();
		for (final ButtonEnum buttonEnum : buttonCombination.getButtons()) {
			final WtButton button = buttonEnum.getButton(gameScreen);
			button.registerClickListener(this);
			buttons.add(button);
		}
		this.closeButtonName = buttonCombination.getCloseButton().getName();

		int fullWidth = (buttons.size() - 1) * BUTTON_SPACING;
		for (final WtButton button : buttons) {
			fullWidth += button.getWidth();
			addChild(button);
		}

		int xpos = (getWidth() - fullWidth) / 2;

		for (final WtButton button : buttons) {
			button.moveTo(xpos, 0);
			xpos += button.getWidth() + BUTTON_SPACING;
		}

		setMinimizeable(false);
		setFrame(true);
		setTitleBar(true);
		// we're watching ourself
		registerCloseListener(this);
	}

	/**
	 * Draw the messagebox contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param clientArea
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(final Graphics2D clientArea) {
		// layout the buttons
		if (!layedout) {
			final int lastHeight = textPanel.getLastHeight();
			for (final WtButton button : buttons) {
				button.moveTo(button.getX(), lastHeight);
			}
			layedout = true;
		}

		super.drawContent(clientArea);
	}

	/**
	 * clicked a button.
	 * 
	 * @param name
	 * @param point
	 * @param gameScreen
	 */
	public void onClick(final String name, final Point point, final IGameScreen gameScreen) {
		// tell our listeners that a button has been clicked
		notifyClickListeners(name, point);
		removeCloseListener(this);
		destroy(gameScreen);
	}

	/**
	 * closed this window.
	 * 
	 * @param name
	 */
	public void onClose(final String name) {
		// pseudoclicked the close button
		onClick(closeButtonName, null, gameScreen);
	}

	/** some default buttons. */
	public enum ButtonEnum {
		YES("Yes", 50, 30), NO("No", 50, 30), CANCEL("Cancel", 50, 30), OK(
				"Ok", 50, 30), QUIT("Quit", 50, 30);

		private String name;

		private int width;

		private int height;

		/**
		 * private construction.
		 * 
		 * @param name
		 * @param width
		 * @param height
		 */
		private ButtonEnum(final String name, final int width, final int height) {
			this.name = name;
			this.width = width;
			this.height = height;
		}

		/** @return the name of this button. */
		public String getName() {
			return name;
		}

		/**
		 * Creates a new wt-button.
		 * 
		 * @param gameScreen
		 * @return the created button
		 */
		public WtButton getButton(final IGameScreen gameScreen) {
			return new WtButton(name, width, height, name, gameScreen);
		}
	}

	/** some button combinations. */
	public enum ButtonCombination {
		OK(ButtonEnum.OK, ButtonEnum.OK), YES_NO(ButtonEnum.NO, ButtonEnum.YES,
				ButtonEnum.NO), YES_NO_CANCEL(ButtonEnum.CANCEL,
				ButtonEnum.YES, ButtonEnum.NO, ButtonEnum.CANCEL), OK_CANCEL(
				ButtonEnum.CANCEL, ButtonEnum.OK, ButtonEnum.CANCEL), QUIT_CANCEL(
				ButtonEnum.CANCEL, ButtonEnum.QUIT, ButtonEnum.CANCEL);

		/** list of buttons for this combination. */
		private List<ButtonEnum> buttons;

		/** default button when the window is closed. */
		private ButtonEnum closeButton;

		/**
		 * Constructor.
		 * 
		 * @param closeButton
		 * @param buttons
		 */
		private ButtonCombination(final ButtonEnum closeButton, final ButtonEnum... buttons) {
			final List<ButtonEnum> buttonList = new ArrayList<ButtonEnum>();
			this.closeButton = closeButton;
			for (final ButtonEnum button : buttons) {
				buttonList.add(button);
			}
			this.buttons = Collections.unmodifiableList(buttonList);
		}

		/**
		 * @return a list with the buttons.
		 */
		public List<ButtonEnum> getButtons() {
			return buttons;
		}

		/** @return a list with the buttons. */
		public ButtonEnum getCloseButton() {
			return closeButton;
		}

	}
}
