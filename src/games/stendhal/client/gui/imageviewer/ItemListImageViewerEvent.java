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

import games.stendhal.client.GameScreen;
import games.stendhal.client.gui.ComponentPaintCache;
import games.stendhal.client.gui.ComponentPaintCache.Cacheable;
import games.stendhal.client.gui.ScrolledViewport;
import games.stendhal.client.gui.textformat.HTMLBuilder;
import games.stendhal.client.gui.textformat.StringFormatter;
import games.stendhal.client.gui.textformat.TextAttributeSet;
import games.stendhal.client.sprite.DataLoader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Opens a styled internal frame displaying an item list
 * 
 * @author hendrik
 */
public final class ItemListImageViewerEvent extends ViewPanel {
	private static final long serialVersionUID = -6114543463410539585L;
	/** Scrolling speed when using the mouse wheel. */
	private static final int SCROLLING_SPEED = 8;
	/** Formatter for item name underlining. */
	private final StringFormatter<Map<TextAttribute, Object>, TextAttributeSet> formatter
		= new StringFormatter<Map<TextAttribute, Object>, TextAttributeSet>();
	/** Default attributes for the item name formatter (empty). */
	private final TextAttributeSet defaultAttrs = new TextAttributeSet();

	private final RPEvent event;

	/**
	 * Creates a new ItemListImageViewerEvent.
	 *
	 * @param event event
	 */
	public ItemListImageViewerEvent(RPEvent event) {
		this.event = event;
		
		// Just formatting § for now. Nothing should currently use #
		TextAttributeSet set = new TextAttributeSet();
		set.setAttribute(TextAttribute.UNDERLINE, "u");
		formatter.addStyle('§', set);
	}

	/**
	 * Shows the window.
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
			ScrolledViewport viewPort = new ScrolledViewport(panel);
			// More reasonable scrolling speed when using the mouse wheel
			viewPort.setScrollingSpeed(SCROLLING_SPEED);
			panel.add(label);
			Dimension screenSize = GameScreen.get().getSize();
			viewPort.getComponent().setPreferredSize(new Dimension(screenSize.width - 80, screenSize.height - 100));
			add(viewPort.getComponent(), BorderLayout.CENTER);
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
	 * Creates an item row.
	 *
	 * @param html  out buffer to append to
	 * @param item  RPObject representing an item to display
	 */
	private void createRow(StringBuilder html, RPObject item) {
		URL url = getItemImageURL(item);
		String price = getFormatedPrice(item);

		html.append("<tr  style=\"color: #D0D0D0\"><td>");
		html.append("<div style=\"width:32px !important; height:32px; background-repeat: no-repeat; background-image: url('" + url.toString() + "')\">&nbsp;</div>");
		html.append("</td><td>");
		html.append(price);
		html.append("</td><td>");
		
		String text = item.get("description_info");
		HTMLBuilder build = new HTMLBuilder();
		formatter.format(text, defaultAttrs, build);
		html.append(build.toHTML());
		
		html.append("</td></tr>");
	}

	/**
	 * Formats the price depending on its sign.
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
		String itemSubclass = item.get("subclass");
		for(String t:fishes) {
			if(t.equals(itemSubclass)) {
				itemSubclass = "unknown_fish";
			}
		}
		String itemName = item.get("class") + "/" + itemSubclass;
		String imagePath = "/data/sprites/items/" + itemName + ".png";
		URL url = DataLoader.getResource(imagePath);
		return url;
	}
	
	/**
	 * A JPanel using caching for itself and its child components for faster
	 * drawing. Speed is important for large on screen windows because they get
	 * redrawn in every game loop due to the game screen being redrawn.
	 */
	private static class CachedPanel extends JPanel implements Cacheable {

		/**
		 * serial version UID
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
