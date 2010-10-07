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
package games.stendhal.client.gui.imageviewer;

import games.stendhal.client.stendhal;
import games.stendhal.client.gui.ComponentPaintCache;
import games.stendhal.client.gui.ComponentPaintCache.Cacheable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Opens a styled internal frame displaying an item list
 * 
 * @author hendrik
 */
public class ItemListImageViewerEvent extends ViewPanel {
	private static final long serialVersionUID = -6114543463410539585L;

	private RPEvent event;

	/**
	 * creates a new ItemListImageViewerEvent
	 *
	 * @param event event
	 */
	public ItemListImageViewerEvent(RPEvent event) {
		this.event = event;
	}

	/**
	 * shows the window
	 */
	public void view() {
		new ImageViewWindow(event.get("title"), this);
	}


	@Override
	public void prepareView(final Dimension maxSize) {
		JLabel label = createLabel();

		// If there are too many entry, add a scrollbar.
		// Note: setMaximumSize does not work, so we use setPreferredSize and check
		//       the number of entries ourself.
		if (event.getSlot("content").size() > 6) {
			// Speed up drawing. The html table won't change anyway.
			JPanel panel = new CachedPanel();
			JScrollPane scrollPane = new JScrollPane();
			panel.add(label);
			scrollPane.setViewportView(panel);
			scrollPane.setPreferredSize(new Dimension(stendhal.screenSize.width - 80, stendhal.screenSize.height - 100));
			add(scrollPane, BorderLayout.CENTER);
		} else {
			add(label);
		}
		
		setVisible(true);
	}

	private JLabel createLabel() {
		StringBuilder html = new StringBuilder();
		html.append("<html><body style=\"color: #FFFFFF\">");
		if (event.has("caption")) {
			html.append("<p>" + event.get("caption") + "</p><br>");
		}
		html.append("<table border=\"1\" width=\"400px\">");
		html.append("<tr><th>Item</th><th>Price</th><th>Description</th></tr>");

		RPSlot slot = event.getSlot("content");
		for (RPObject item : slot) {
			createRow(html, item);
		}

		
		html.append("</table></body></html>");

		JLabel label = new JLabel(html.toString());
		return label;
	}

	/**
	 * creates an item row
	 *
	 * @param html  out buffer to append to
	 * @param item  RPObject representing an item to display
	 */
	private void createRow(StringBuilder html, RPObject item) {
		URL url = getItemImageURL(item);
		String price = getFormatedPrice(item);

		html.append("<tr  style=\"color: #D0D0D0\"><td>");
		html.append("<img src=\"" + url.toString() + "\">");
		html.append("</td><td>");
		html.append(price);
		html.append("</td><td>");
		String text = item.get("description_info");
		if (text == null) {
			// compatibility with 0.85 server
			text = item.get("description");
		}
		html.append(text);
		html.append("</td></tr>");
	}

	/**
	 * formats the price depending on its sign.
	 *
	 * @param item representing an item to display 
	 * @return html code to display price
	 */
	private String getFormatedPrice(RPObject item) {
		String price = "";
		if (item.has("price")) {
			int priceInt = item.getInt("price");
			price = "<span style=\"color: ";
			if (priceInt < 0) {
				price = price + "#FFFFFF\">";
			} else {
				price = price + "#00FF00\">";
			}
			price = price + Math.abs(priceInt) + "</span>";
		}
		return price;
	}

	private URL getItemImageURL(RPObject item) {
		// remove fish images from signs
		List<String> fishes = Arrays.asList("arctic_char", "clown-fish", "cod", "mackerel", "perch", "roach", "surgeonfish", "trout", "red-lionfish");
		String itemSubClass = item.get("subclass");
		for(String t:fishes) {
			if(t.equals(itemSubClass)) {
				itemSubClass = "unknown_fish";
			}
		}
		String itemName = item.get("class") + "/" + itemSubClass;
		String imagePath = "/data/sprites/items/" + itemName + ".png";
		URL url = this.getClass().getResource(imagePath);
		return url;
	}
	
	/**
	 * A JPanel using caching for itself and its child components for faster
	 * drawing. Speed is important for large on screen windows because they get
	 * redrawn in every game loop due to the game screen being redrawn.
	 */
	private static class CachedPanel extends JPanel implements Cacheable {

		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = -5591729630898137399L;

		private final ComponentPaintCache cache;
		/**
		 * Create a new CachedPanel.
		 */
		public CachedPanel() {
			cache = new ComponentPaintCache(this);
			// cache the label as well
			cache.setPaintChildren(true);
		}
		
		@Override
		public void paint(Graphics g) {
			cache.paintComponent(g);
		}
		
		// *** Cached painting requires exposing the following 
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
		
		@Override
		public void paintBorder(Graphics g) {
			super.paintBorder(g);
		}
		
		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
		}
	}
}
