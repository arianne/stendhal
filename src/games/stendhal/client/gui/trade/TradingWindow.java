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
package games.stendhal.client.gui.trade;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.InternalManagedWindow;
import games.stendhal.client.gui.SlotGrid;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.common.TradeState;
import games.stendhal.common.grammar.Grammar;

/**
 * The trading window. Panels for each of the trader's trading slots and
 * buttons for the trading operations.
 */
class TradingWindow extends InternalManagedWindow {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -8404391354667893368L;

	private final TradingController controller;
	private final SlotGrid partnerSlots;
	private final SlotGrid mySlots;
	private final JLabel partnersOfferLabel;

	private final JButton offerButton;
	private final JButton acceptButton;
	private final JButton cancelButton;

	private final JLabel myOfferStatus;
	private final JLabel partnerOfferStatus;

	/**
	 * Create a new TradingWindow.
	 *
	 * @param controller controller to use for the trade operations
	 */
	public TradingWindow(final TradingController controller) {
		super("trade", "Trading");
		this.controller = controller;

		final int padding = SBoxLayout.COMMON_PADDING;
		JComponent content = SBoxLayout.createContainer(SBoxLayout.VERTICAL, padding);
		content.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
		JComponent slotRow = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, padding);
		content.add(slotRow);

		/*
		 * Create the trading partner's side
		 */
		JComponent partnerColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL, padding);
		partnersOfferLabel = new JLabel("Partner's offer");
		partnersOfferLabel.setAlignmentX(CENTER_ALIGNMENT);
		partnerColumn.add(partnersOfferLabel);
		partnerSlots = new SlotGrid(2, 2);
		partnerColumn.add(partnerSlots);

		partnerOfferStatus = new JLabel("Changing");
		partnerOfferStatus.setAlignmentX(CENTER_ALIGNMENT);
		partnerColumn.add(partnerOfferStatus);

		acceptButton = new JButton("Accept");
		acceptButton.setEnabled(false);
		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.acceptTrade();
			}
		});
		acceptButton.setAlignmentX(RIGHT_ALIGNMENT);
		partnerColumn.add(acceptButton);

		slotRow.add(partnerColumn);

		slotRow.add(new JSeparator(SwingConstants.VERTICAL), SLayout.EXPAND_Y);

		/*
		 * Create user offer's side
		 */
		JComponent myColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		JLabel myOfferLabel = new JLabel("My offer");
		myOfferLabel.setAlignmentX(CENTER_ALIGNMENT);
		myColumn.add(myOfferLabel);

		mySlots = new SlotGrid(2, 2);
		mySlots.setAcceptedTypes(EntityMap.getClass("item", null, null));
		myColumn.add(mySlots);
		slotRow.add(myColumn);

		myOfferStatus = new JLabel("Changing");
		myOfferStatus.setAlignmentX(CENTER_ALIGNMENT);
		myColumn.add(myOfferStatus);

		offerButton = new JButton("Offer");
		offerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.lockTrade();
			}
		});
		offerButton.setAlignmentX(RIGHT_ALIGNMENT);
		myColumn.add(offerButton);

		/*
		 * Separate the cancel button from the rest of the components to
		 * highlight its special status.
		 */
		content.add(new JSeparator(SwingConstants.HORIZONTAL), SLayout.EXPAND_X);

		// Cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close the window and cancel the trade
				close();
			}
		});

		cancelButton.setAlignmentX(RIGHT_ALIGNMENT);
		content.add(cancelButton);

		setContent(content);
	}

	@Override
	public void close() {
		super.close();
		controller.cancelTrade();
	}

	/**
	 * Set the name of the trading partner.
	 *
	 * @param name
	 */
	void setPartnerName(String name) {
		setTitle("Trading with " + name);
		partnersOfferLabel.setText(Grammar.suffix_s(name) + " offer");
	}

	/**
	 * Set the user's status indicator.
	 *
	 * @param state new status
	 */
	void setUserStatus(TradeState state) {
		setTraderStatus(myOfferStatus, state);
	}

	/**
	 * Set the trading partner's status indicator.
	 *
	 * @param state new status
	 */
	void setPartnerStatus(TradeState state) {
		setTraderStatus(partnerOfferStatus, state);
	}

	/**
	 * Set the status message and color of an indicator label.
	 *
	 * @param indicator label to be changed
	 * @param state new state
	 */
	private void setTraderStatus(JLabel indicator, TradeState state) {
		switch (state) {
		case NO_ACTIVE_TRADE:
			indicator.setForeground(Color.GRAY);
			indicator.setText("Inactive");
			break;
		case MAKING_OFFERS:
			indicator.setForeground(Color.GRAY);
			indicator.setText("Changing");
			break;
		case LOCKED:
			indicator.setForeground(Color.WHITE);
			indicator.setText("Offered");
			break;
		case DEAL_WAITING_FOR_OTHER_DEAL:
			indicator.setForeground(Color.GREEN);
			indicator.setText("ACCEPTED");
			break;
		default:

		}
	}

	/**
	 * Set the user's trading slot.
	 *
	 * @param user
	 * @param slot
	 */
	void setUserSlot(IEntity user, String slot) {
		mySlots.setSlot(user, slot);
	}

	/**
	 * Set the partner's trading slot.
	 *
	 * @param partner
	 * @param slot
	 */
	void setPartnerSlot(IEntity partner, String slot) {
		partnerSlots.setSlot(partner, slot);
	}

	/**
	 * Disable all activity. Trade has been cancelled.
	 */
	void disableAll() {
		offerButton.setEnabled(false);
		acceptButton.setEnabled(false);
		cancelButton.setEnabled(false);
		setUserStatus(TradeState.NO_ACTIVE_TRADE);
		setPartnerStatus(TradeState.NO_ACTIVE_TRADE);
	}

	/**
	 * Define if the player is allowed to lock an offer.
	 *
	 * @param allow
	 */
	void allowOffer(boolean allow) {
		offerButton.setEnabled(allow);
	}

	/**
	 * Define if the player is allowed to accept the trade.
	 *
	 * @param allow
	 */
	void allowAccept(boolean allow) {
		acceptButton.setEnabled(allow);
	}

	/**
	 * Define if the player is allowed to cancel the trade.
	 *
	 * @param allow
	 */
	void allowCancel(boolean allow) {
		cancelButton.setEnabled(allow);
	}
}
