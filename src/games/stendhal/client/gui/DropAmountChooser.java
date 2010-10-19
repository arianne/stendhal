/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.gui.layout.SBoxLayout;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * A class for showing a selector for dropped item amounts and taking control
 * of the drop handling of those drops.
 */
public class DropAmountChooser {
	final StackableItem item;
	final DropTarget target;
	final Point location;
	final JPopupMenu popup;
	JSpinner spinner;
	
	/**
	 * Create a new DropAmountChooser.
	 * 
	 * @param item the item whose drop should be handled by the DropAmountChooser
	 * @param target target where the item should be dropped if the user chooses
	 * 	an amount greater than 0
	 * @param point drop location
	 */
	DropAmountChooser(StackableItem item, DropTarget target, Point point) {
		this.item = item;
		this.target = target;
		location = point;
		popup = createPopup();
	}
	
	/**
	 * Show the chooser popup.
	 * 
	 * @param parent parent component
	 * @param location location of the popup in parent coordinates
	 */
	protected void show(Component parent, Point location) {
		popup.show(parent, location.x, location.y);
	}
	
	/**
	 * Construct the popup.
	 * 
	 * @return popup
	 */
	private JPopupMenu createPopup() {
		JPopupMenu menu = new JPopupMenu();
		
		SpinnerModel model = new SpinnerNumberModel(1, 0, item.getQuantity(), 1);
		spinner = new JSpinner(model);
		JButton button = new JButton("Drop");
		button.addActionListener(new DropListener());
		
		JComponent content = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		content.add(spinner);
		content.add(button);
		menu.add(content);
		
		return menu;
	}
	
	/**
	 * Listener for the user pressing the "Drop" button. Drop the chosen amount
	 * of items to the target.
	 */
	private class DropListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object value = spinner.getValue();
			if (value instanceof Integer) {
				int amount = (Integer) value;
				// No need to send drop commands if the user does not want to
				// drop anything
				if (amount > 0) {
					target.dropEntity(item, amount, location);
				}
			}
			popup.setVisible(false);
		}
	}
}
