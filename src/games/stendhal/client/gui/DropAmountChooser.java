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
package games.stendhal.client.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.gui.layout.SBoxLayout;

/**
 * A class for showing a selector for dropped item amounts and taking control
 * of the drop handling of those drops.
 */
class DropAmountChooser {
	/** Space between the popup border and actual content components. */
	private static final int BORDER = 2;

	/** Item to be dropped. */
	private final StackableItem item;
	/** Target where the user is dropping the item. */
	private final DropTarget target;
	/** Drop location within the target component. */
	private final Point location;
	/** Created selector popup menu. */
	private final JPopupMenu popup;
	/** Number selector within the popup menu. */
	private JSpinner spinner;

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

		/*
		 * Select the text when the the popup is displayed. Unfortunately the
		 * selection is normally cleared when the text field gets focus, so we
		 * need to do it in a focus listener. Also the focus listener gets run
		 * before the selection is cleared so to get the desired effect the
		 * selection needs to be pushed to the event queue.
		 */
		final JComponent field = getTextField();
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						((JTextComponent) field).selectAll();
					}
				});
			}
		});
	}

	/**
	 * Show the chooser popup.
	 *
	 * @param parent parent component
	 * @param location location of the popup in parent coordinates
	 */
	protected void show(Component parent, Point location) {
		popup.show(parent, location.x, location.y);
		/*
		 * Needs to be after show(). Also, heavy weight popups need time to
		 * appear, so the focus request needs to be pushed to the end of the
		 * event queue.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTextField().requestFocus();
			}
		});
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
		// Special document for editing numbers
		PlainDocument doc = new PlainDocument() {
			private DocumentFilter filter;
			@Override
			public DocumentFilter getDocumentFilter() {
				if (filter == null) {
					filter = new NumberDocumentFilter(getTextField(), true);
				}
				return filter;
			}
		};
		getTextField().setDocument(doc);

		JButton button = new JButton("Drop");

		ActionListener dropAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDrop();
			}
		};
		/*
		 * Drop items at either clicking the drop button, or when enter is
		 * pressed.
		 */
		getTextField().addActionListener(dropAction);
		button.addActionListener(dropAction);

		JComponent content = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		content.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
		content.add(spinner);
		content.add(button);
		menu.add(content);

		return menu;
	}

	/**
	 * Perform the drop.
	 */
	private void doDrop() {
		Object value = spinner.getValue();
		if (value instanceof Integer) {
			int amount = (Integer) value;
			// No need to send drop commands if the user does not want to
			// drop anything
			if (amount > 0) {
				target.dropEntity(item, amount, location);
			}
			popup.setVisible(false);
		}
	}

	/**
	 * Get the editable text field component of the spinner.
	 *
	 * @return text field
	 */
	private JTextField getTextField() {
		// There really seems to be no simpler way to do this
		JComponent editor = spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor) editor).getTextField();
		} else {
			Logger.getLogger(DropAmountChooser.class).error("Unknown editor type", new Throwable());
			// This will not work, but at least it won't crash the client
			return new JTextField();
		}
	}
}
