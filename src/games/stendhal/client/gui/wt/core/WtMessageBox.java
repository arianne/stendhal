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

	/** the max height of the panel */
	private static final int MAX_HEIGHT = 100;

	/** space between the buttons */
	private static final int BUTTON_SPACING = 5;

	/** the text panel */
	private WtTextPanel textPanel;

	/** the button */
	private List<WtButton> buttons;

	/** name of the button clicked when the window is closed */
	private String closeButtonName;

	/** false when the messagebox still has to layout the buttons */
	private boolean layedout;

	/** Creates a new instance of MessageBox */
	public WtMessageBox(String name, int x, int y, int width, String message,
			ButtonCombination buttonCombination) {
		super(name, x, y, width, MAX_HEIGHT);

		textPanel = new WtTextPanel("messageboxtext", 5, 0, width - 20,
				MAX_HEIGHT, message);
		addChild(textPanel);

		buttons = new ArrayList<WtButton>();
		for (ButtonEnum buttonEnum : buttonCombination.getButtons()) {
			WtButton button = buttonEnum.getButton();
			button.registerClickListener(this);
			buttons.add(button);
		}
		this.closeButtonName = buttonCombination.getCloseButton().getName();

		int fullWidth = (buttons.size() - 1) * BUTTON_SPACING;
		for (WtButton button : buttons) {
			fullWidth += button.getWidth();
			addChild(button);
		}

		int xpos = (getWidth() - fullWidth) / 2;

		for (WtButton button : buttons) {
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
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(Graphics2D clientArea) {
		// layout the buttons
		if (!layedout) {
			int lastHeight = textPanel.getLastHeight();
			for (WtButton button : buttons) {
				button.moveTo(button.getX(), lastHeight);
			}
			layedout = true;
		}

		super.drawContent(clientArea);
	}

	/** clicked a button */
	public void onClick(String name, Point point) {
		// tell our listeners that a button has been clicked
		notifyClickListeners(name, point);
		removeCloseListener(this);
		destroy();
	}

	/** closed the window */
	public void onClose(String name) {
		// pseudoclicked the close button
		onClick(closeButtonName, null);
	}

	/** some default buttons */
	public enum ButtonEnum {
		YES("Yes", 50, 30),
		NO("No", 50, 30),
		CANCEL("Cancel", 50, 30),
		OK("Ok", 50, 30),
		QUIT("Quit", 50, 30);

		private String name;

		private int width;

		private int height;

		/** private constructon */
		private ButtonEnum(String name, int width, int height) {
			this.name = name;
			this.width = width;
			this.height = height;
		}

		/** returns the name of this button */
		public String getName() {
			return name;
		}

		/** returns a new wt-button */
		public WtButton getButton() {
			return new WtButton(name, width, height, name);
		}
	}

	/** some button combinations */
	public enum ButtonCombination {
		OK(ButtonEnum.OK, ButtonEnum.OK),
		YES_NO(ButtonEnum.NO, ButtonEnum.YES, ButtonEnum.NO),
		YES_NO_CANCEL(
				ButtonEnum.CANCEL,
				ButtonEnum.YES,
				ButtonEnum.NO,
				ButtonEnum.CANCEL),
		OK_CANCEL(ButtonEnum.CANCEL, ButtonEnum.OK, ButtonEnum.CANCEL),
		QUIT_CANCEL(ButtonEnum.CANCEL, ButtonEnum.QUIT, ButtonEnum.CANCEL);

		/** list of buttons for this combination */
		private List<ButtonEnum> buttons;

		/** default button when the window is closed */
		private ButtonEnum closeButton;

		/** contructor */
		private ButtonCombination(ButtonEnum closeButton, ButtonEnum... buttons) {
			List<ButtonEnum> buttonList = new ArrayList<ButtonEnum>();
			this.closeButton = closeButton;
			for (ButtonEnum button : buttons) {
				buttonList.add(button);
			}
			this.buttons = Collections.unmodifiableList(buttonList);
		}

		/** returns a list with the buttons */
		public List<ButtonEnum> getButtons() {
			return buttons;
		}

		/** returns a list with the buttons */
		public ButtonEnum getCloseButton() {
			return closeButton;
		}

	}
}
