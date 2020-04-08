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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.ScrolledViewport;
import games.stendhal.client.gui.imageviewer.ImageViewWindow;
import games.stendhal.client.gui.imageviewer.ItemListImageViewerEvent.HeaderRenderer;
import games.stendhal.client.gui.imageviewer.ViewPanel;

public class BestiaryEvent extends Event<RPEntity> {

	// logger instance
	private static Logger logger = Logger.getLogger(BestiaryEvent.class);

	private boolean hasRare = false;
	private boolean hasAbnormal = false;

	@Override
	public void execute() {
		if (event.has("enemies")) {
			// much of this is taken from games.stendhal.client.gui.imageviewer.ItemListImageViewerEvent
			final ViewPanel panel = new ViewPanel() {
				private static final int PAD = 5;

				@Override
				public void prepareView(final Dimension maxSize) {
					Dimension screenSize = GameScreen.get().getSize();
					int maxPreferredWidth = screenSize.width - 80;

					final StringBuilder headerText = new StringBuilder("\"???\" = unknown");
					JLabel header = new JLabel();
					header.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
					add(header, BorderLayout.NORTH);

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
					col.setCellRenderer(r);

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

					// show explanation of "rare" & "abnormal" creatures in header
					if (hasRare || hasAbnormal) {
						headerText.append("<br>");
						if (!hasRare) {
							headerText.append("\"abnormal\"");
						} else {
							headerText.append("\"rare\"");
							if (hasAbnormal) {
								headerText.append(" and \"abnormal\"");
							}
						}

						headerText.append(" creatures not required for achievements");
					}

					header.setText("<html><div width=" + (maxPreferredWidth
							- 10) + ">" + headerText.toString() + "</div></html>");

					setVisible(true);
				}

				private JTable createTable() {
					final String[] columnNames = { "Name", "Solo", "Shared" };

					final List<String> enemies = Arrays.asList(event.get("enemies").split(";"));

					final Object[][] data = new Object[enemies.size()][];
					int i = 0;
					for (final String e: enemies) {
						data[i] = createDataRow(e.split(","));
						i++;
					}

					return new JTable(data, columnNames);
				}

				private Object[] createDataRow(final String[] enemy) {
					final Object[] rval = new Object[4];

					final String name = enemy[0];

					if (isRare(name)) {
						hasRare = true;
					} else if (isAbnormal(name)) {
						hasAbnormal = true;
					}

					rval[0] = name;
					rval[1] = "";
					rval[2] = "";

					if (enemy[1].equals("true")) {
						rval[1] = "✔";
					}
					if (enemy[2].equals("true")) {
						rval[2] = "✔";
					}

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

			new ImageViewWindow("Bestiary", panel);
		} else {
			logger.warn("Could not create bestiary: Event does not have \"enemies\" attribute");
		}
	}

	/**
	 * Checks name for "rare" identifier.
	 *
	 * @param enemyName
	 * 		String to check.
	 * @return
	 * 		<code>true</code> if enemyName ends with "(rare)".
	 */
	private boolean isRare(final String enemyName) {
		return enemyName.endsWith("(rare)");
	}

	/**
	 * Checks name for "abnormal" identifier.
	 *
	 * @param enemyName
	 * 		String to check.
	 * @return
	 * 		<code>true</code> if enemyName ends with "(abnormal)".
	 */
	private boolean isAbnormal(final String enemyName) {
		return enemyName.endsWith("(abnormal)");
	}
}
