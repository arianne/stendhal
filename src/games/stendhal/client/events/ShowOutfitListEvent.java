/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.events;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import games.stendhal.client.GameScreen;
import games.stendhal.client.OutfitStore;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.ScrolledViewport;
import games.stendhal.client.gui.imageviewer.ImageViewWindow;
import games.stendhal.client.gui.imageviewer.ItemListImageViewerEvent.HeaderRenderer;
import games.stendhal.client.gui.imageviewer.ViewPanel;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;


public class ShowOutfitListEvent extends Event<Entity> {

	private static final Logger logger = Logger.getLogger(ShowOutfitListEvent.class);

	private static final int PAD = 5;


	@Override
	public void execute() {
		if (!event.has("outfits")) {
			logger.warn("Could not create bestiary: Event does not have \"enemies\" attribute");
			return;
		}

		new ImageViewWindow(event.get("title"), createViewPanel());
	}

	private ViewPanel createViewPanel() {
		return new ViewPanel() {
			@Override
			public void prepareView(final Dimension maxSize) {
				Dimension screenSize = GameScreen.get().getSize();
				final int maxPreferredWidth = screenSize.width - 80;

				if (event.has("caption")) {
					JLabel header = new JLabel("<html><div width=" + (maxPreferredWidth
							- 10) + ">" + event.get("caption") + "</div></html>");
					header.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
					add(header, BorderLayout.NORTH);
				}

				final JTable table = createTable();
				// Prevents selection
				table.setEnabled(false);
				table.setFillsViewportHeight(true);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				TableColumn col = table.getColumnModel().getColumn(0);
				col.setCellRenderer(new DefaultTableCellRenderer());

				DefaultTableCellRenderer r = new DefaultTableCellRenderer();
				r.setHorizontalAlignment(SwingConstants.CENTER);

				col = table.getColumnModel().getColumn(1);
				col.setCellRenderer(new SpriteCellRenderer());

				col = table.getColumnModel().getColumn(2);
				col.setCellRenderer(r);

				HeaderRenderer hr = new HeaderRenderer();
				Enumeration<TableColumn> cols = table.getColumnModel().getColumns();
				while (cols.hasMoreElements()) {
					TableColumn c = cols.nextElement();
					c.setHeaderRenderer(hr);
				}

				adjustColumnWidths(table);
				adjustRowHeights(table);

				ScrolledViewport viewPort = new ScrolledViewport(table);
				/*
				 * maxPreferredWidth is incorrect, but java does not seem to support
				 * max-width property for div's, so all the cells report the same
				 * preferred width anyway.
				 */
				viewPort.getComponent().setPreferredSize(new Dimension(maxPreferredWidth,
						Math.min(screenSize.height - 100, table.getPreferredSize().height
								+ hr.getPreferredSize().height + 4 * PAD)));
				viewPort.getComponent().setBackground(table.getBackground());
				add(viewPort.getComponent(), BorderLayout.CENTER);

				setVisible(true);
			}

			private JTable createTable() {
				final String[] columnNames = { "Name", "Preview", "Price" };

				final List<String> outfits = Arrays.asList(event.get("outfits").split(":"));

				final Object[][] data = new Object[outfits.size()][];
				int i = 0;
				for (final String of: outfits) {
					//data[i] = of.split(";");
					data[i] = createDataRow(of.split(";"));
					i++;
				}
				return new JTable(data, columnNames);
			}

			private Object[] createDataRow(final String[] outfit) {
				final Object[] rval = new Object[3];

				boolean overrideHideBase = outfit.length > 3 && outfit[3].equals("showall");

				final List<String> hideOverrides = new ArrayList<>();
				if (!overrideHideBase && outfit.length > 3) {
					for (final String o: outfit[3].split(",")) {
						hideOverrides.add(o);
					}
				}

				String outfitString = outfit[1];
				if (!event.has("show_base") && !overrideHideBase) {
					final Map<String, String> tmpValues = new HashMap<>();
					for (final String layer: outfitString.split(",")) {
						final String[] keyValue = layer.split("=");
						tmpValues.put(keyValue[0], keyValue[1]);
					}

					// overwrite base layers so they are hidden
					for (final String hideLayer: Arrays.asList("body", "head", "eyes")) {
						if (!hideOverrides.contains("show" + hideLayer)) {
							tmpValues.put(hideLayer, "-1");
						}
					}

					final StringBuilder newOutfit = new StringBuilder();
					final int layerCount = tmpValues.size();
					int idx = 0;
					for (final String layerName: tmpValues.keySet()) {
						newOutfit.append(layerName + "=" + tmpValues.get(layerName));

						if (idx < layerCount - 1) {
							newOutfit.append(",");
						}
						idx++;
					}

					outfitString = newOutfit.toString();
				}

				int xIndex = 1;
				int yIndex = 2;
				if (outfit.length > 5) {
					try {
						xIndex = Integer.parseInt(outfit[4]);
						yIndex = Integer.parseInt(outfit[5]);
					} catch (final NumberFormatException e) {
						logger.warn("Failed to set frame index, using default");
					}
				}

				rval[0] = outfit[0];
				rval[1] = ((ImageSprite) OutfitStore.get().getAdjustedOutfit(outfitString, null, null, null)).getFrame(xIndex, yIndex);
				rval[2] = outfit[2];

				return rval;
			}

			/**
			 * Adjust the column widths of a table based on the table contents.
			 *
			 * @param table adjusted table
			 */
			private void adjustColumnWidths(JTable table) {
				final TableColumnModel model = table.getColumnModel();
				for (int column = 0; column < table.getColumnCount(); column++) {
					TableColumn tc = model.getColumn(column);
					int width = tc.getWidth();
					for (int row = 0; row < table.getRowCount(); row++) {
						Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
						width = Math.max(width, comp.getPreferredSize().width);
					}

					tc.setPreferredWidth(width);
				}
			}

			/**
			 * Adjust the row heights of a table based on the table contents.
			 *
			 * @param table adjusted table
			 */
			private void adjustRowHeights(JTable table) {
				for (int row = 0; row < table.getRowCount(); row++) {
					int rowHeight = table.getRowHeight();

					for (int column = 0; column < table.getColumnCount(); column++) {
						Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
						rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
					}

					table.setRowHeight(row, rowHeight);
				}
			}
		};
	}

	/**
	 * Renderer for the item sprite cells.
	 */
	private static class SpriteCellRenderer extends JComponent implements TableCellRenderer {
		private Sprite sprite;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object color,
				boolean isSelected, boolean hasFocus, int row, int col) {
			Object obj = table.getValueAt(row, col);
			if (obj instanceof Sprite) {
				sprite = (Sprite) obj;
			} else {
				sprite = null;
			}
			return this;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d = new Dimension();
			if (sprite != null) {
				d.width = sprite.getWidth() + 2 * PAD;
				d.height = sprite.getHeight() + 2 * PAD;
			}
			return d;
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (sprite != null) {
				sprite.draw(g, (getWidth() - sprite.getWidth()) / 2, (getHeight() - sprite.getHeight()) / 2);
			}
		}
	}
}
