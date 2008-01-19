/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.util;

import java.util.Properties;

import javax.swing.table.AbstractTableModel;

/** a tablemodel which handles properties. */
public class PropertiesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private Properties properties;

	private String[] columnNames = { "Name", "Value" };

	/**
	 * creates the tablemodel. The model makes a defensive copy of the
	 * properties (so it is not modified)
	 * 
	 * @param p
	 *            the properties
	 * @param readOnlyProps
	 *            readonly properties. May be <code>null</code>
	 */
	public PropertiesTableModel(Properties p) {
		properties = new Properties();
		if (p != null) {
			properties.putAll(p);
		}
	}

	public int getRowCount() {
		return properties.size() + 1;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Returns wether the given position in the table is editable. Values can
	 * only be edited when they have a name.
	 */
	public boolean isCellEditable(int row, int col) {
		return (col == 0) || (col == 1 && getValueAt(row, 0) != null);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object[] array = properties.keySet().toArray();
		if (rowIndex >= 0 && rowIndex < properties.size()) {
			if (columnIndex == 0) {
				return array[rowIndex];
			} else if (columnIndex == 1) {
				return properties.get(array[rowIndex]);
			}
		}
		return null;
	}

	public void setValueAt(Object value, int row, int col) {
		// TODO: When the name is set to an empty string, consider removing the
		// property (and ignore when it happens on the last row).
		if (row >= 0) {
			if (row >= properties.size() && col == 0) {
				if (((String) value).length() > 0) {
					properties.setProperty((String) value, "");
					fireTableDataChanged();
				}
			} else {
				if (col == 1) {
					properties.setProperty((String) getValueAt(row, 0), (String) value);
					fireTableCellUpdated(row, col);
				} else if (col == 0) {
					String val = (String) getValueAt(row, 1);
					if (getValueAt(row, col) != null) {
						properties.remove(getValueAt(row, col));
					}
					if (((String) value).length() > 0) {
						properties.setProperty((String) value, val);
					}
					fireTableDataChanged();
				}
			}
		}
	}

	public void remove(Object key) {
		properties.remove(key);
		fireTableDataChanged();
	}

	/*
	 * public void update(Properties props) { properties = props;
	 * fireTableDataChanged(); }
	 */
	public Properties getProperties() {
		return properties;
	}
}
