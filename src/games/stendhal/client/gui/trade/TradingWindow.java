package games.stendhal.client.gui.trade;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.InternalManagedWindow;
import games.stendhal.client.gui.SlotGrid;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.common.Grammar;

/**
 * The trading window. Panels for each of the trader's trading slots and
 * buttons for the trading operations.
 */
class TradingWindow extends InternalManagedWindow {
	private final TradingController controller;
	private final SlotGrid partnerSlots;
	private final SlotGrid mySlots;
	private final JLabel partnersOfferLabel;
	
	private final JButton offerButton;
	private final JButton acceptButton;
	private final JButton cancelButton;
	
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
		partnerColumn.add(partnersOfferLabel);
		partnerSlots = new SlotGrid(2, 2);
		partnerColumn.add(partnerSlots);
		
		acceptButton = new JButton("Accept");
		acceptButton.setEnabled(false);
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.acceptTrade();
			}
		});
		acceptButton.setAlignmentX(RIGHT_ALIGNMENT);
		partnerColumn.add(acceptButton);
		
		slotRow.add(partnerColumn);
		
		slotRow.add(new JSeparator(SwingConstants.VERTICAL),
				SBoxLayout.constraint(SLayout.EXPAND_Y));
		
		/*
		 * Create user offer's side
		 */
		JComponent myColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		JLabel myOfferLabel = new JLabel("My offer");
		myColumn.add(myOfferLabel);
		mySlots = new SlotGrid(2, 2);
		myColumn.add(mySlots);
		slotRow.add(myColumn);
		
		offerButton = new JButton("Offer");
		offerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.lockTrade();
			}
		});
		offerButton.setAlignmentX(RIGHT_ALIGNMENT);
		myColumn.add(offerButton);
		
		// Cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.cancelTrade();
			}
		});
		// separate it from the offer making buttons
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
