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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.common.TradeState;
import marauroa.common.game.RPAction;

/**
 * Object for processing trade state changes and sending trading commands.
 */
public final class TradingController {
	/** The controller instance. */
	private static TradingController instance;
	/** Trading window component. */
	private final TradingWindow window;

	private IEntity tradingPartner;
	private IEntity user;

	private TradeState myState;
	private TradeState partnerState;

	/**
	 * Create the controller instance.
	 */
	private TradingController() {
		window = new TradingWindow(this);
	}

	/**
	 * Get the trading window component.
	 *
	 * @return trading window
	 */
	public JComponent getWindow() {
		return window;
	}

	/**
	 * Set the new trading state.
	 *
	 * @param user the trading user
	 * @param partner the trading partner
	 * @param myState state of the user
	 * @param partnerState state of the trading partner
	 */
	public void setState(IEntity user, IEntity partner, TradeState myState, TradeState partnerState) {
		setMyState(myState);
		setPartner(partner);
		setUser(user);
		setPartnerState(partnerState);
		if (myState != TradeState.NO_ACTIVE_TRADE) {
			if (myState == TradeState.TRADE_COMPLETED) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						/*
						 * Completed a trade. Close the window.
						 */
						window.close();
					}
				});
			} else if (window.getParent() == null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						// Starting a trade, and there was no window visible.
						j2DClient.get().addWindow(window);
						window.setVisible(true);
					}
				});
			}
		}
	}

	/**
	 * Set the current trading partner.
	 *
	 * @param partner new trading partner
	 */
	private void setPartner(final IEntity partner) {
		if (partner != tradingPartner) {
			tradingPartner = partner;
			/*
			 * Partner gets set to null on cancelled trade. Do not show the
			 * window if the user already closed it
			 */
			if (partner != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						window.setPartnerSlot(partner, "trade");
						window.setPartnerName(partner.getName());
					}
				});
			}
		}
	}

	/**
	 * Set the current user, if it has changed.
	 *
	 * @param user current user
	 */
	private void setUser(final IEntity user) {
		if (this.user != user) {
			this.user = user;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					window.setUserSlot(user, "trade");
				}
			});
		}
	}

	/**
	 * Set the trading state of the user.
	 *
	 * @param state user trade state
	 */
	private void setMyState(TradeState state) {
		if (myState != state) {
			myState = state;
			onMyStateChanged();
		}
	}

	/**
	 * Set the trading status of the partner.
	 *
	 * @param state partner trade state
	 */
	private void setPartnerState(TradeState state) {
		if (partnerState != state) {
			partnerState = state;
			onPartnerStateChanged();
		}
	}

	/**
	 * Process changes of the user's trade state. Modify the window so that it
	 * allows the same operations as the server.
	 */
	private void onMyStateChanged() {
		Runnable guiChange = null;
		switch (myState) {
		case NO_ACTIVE_TRADE:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.disableAll();
					/*
					 * A hack to hide the partners' trading slot in case he
					 * starts a new trade with someone else while our trading
					 * window is still visible.
					 */
					if (window.isShowing()) {
						window.setPartnerSlot(user, "trade");
					}
				}
			};
			break;
		case MAKING_OFFERS:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(false);
					window.allowOffer(true);
					window.allowCancel(true);
					window.setUserStatus(myState);
				}
			};
			break;
		case LOCKED:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(partnerState == TradeState.LOCKED);
					window.allowOffer(false);
					window.allowCancel(true);
					window.setUserStatus(myState);
				}
			};
			break;
		case DEAL_WAITING_FOR_OTHER_DEAL:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(false);
					window.allowOffer(false);
					window.allowCancel(true);
					// It's the partner's offer that has been accepted
					window.setPartnerStatus(myState);
				}
			};
			break;
		default:
				// do nothing
		}


		if (guiChange != null) {
			SwingUtilities.invokeLater(guiChange);
		}
	}

	/**
	 * Process changes of the trading partner's trade state. Modify the window
	 * so that it allows the same operations as the server.
	 */
	private void onPartnerStateChanged() {
		Runnable guiChange = null;
		switch (partnerState) {
		case MAKING_OFFERS:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(false);
					window.allowCancel(true);
					window.setPartnerStatus(partnerState);
					// - keep current offering state
				}
			};
			break;
		case LOCKED:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(myState == TradeState.LOCKED);
					window.allowOffer(myState != TradeState.LOCKED);
					window.allowCancel(true);
					window.setPartnerStatus(partnerState);
				}
			};
			break;
		case DEAL_WAITING_FOR_OTHER_DEAL:
			guiChange = new Runnable() {
				@Override
				public void run() {
					window.allowAccept(true);
					window.allowOffer(false);
					window.allowCancel(true);
					// It's the user's trade that has been accepted
					window.setUserStatus(partnerState);
				}
			};
			break;
		default:
				// do nothing
		}

		if (guiChange != null) {
			SwingUtilities.invokeLater(guiChange);
		}
	}

	/**
	 * Get the trading controller instance.
	 *
	 * @return controller instance
	 */
	public static synchronized TradingController get() {
		if (instance == null) {
			instance = new TradingController();
		}

		return instance;
	}

	/**
	 * Make a trading action.
	 *
	 * @return a trading action
	 */
	private RPAction makeAction() {
		RPAction action = new RPAction();
		action.put("type", "trade");
		return action;
	}

	/**
	 * Send a trade cancelled action to the server.
	 */
	void cancelTrade() {
		RPAction action = makeAction();
		action.put("action", "cancel");
		ClientSingletonRepository.getClientFramework().send(action);
	}

	/**
	 * Send a lock offer action to the server.
	 */
	void lockTrade() {
		RPAction action = makeAction();
		action.put("action", "lock");
		ClientSingletonRepository.getClientFramework().send(action);
	}

	/**
	 * Send an accept trade action to the server.
	 */
	void acceptTrade() {
		RPAction action = makeAction();
		action.put("action", "deal");
		ClientSingletonRepository.getClientFramework().send(action);
	}
}
