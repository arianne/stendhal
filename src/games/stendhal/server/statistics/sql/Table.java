package games.stendhal.server.statistics.sql;

import java.util.ArrayList;

/**
 * Represents an SQL table
 *
 * @author hendrik
 */
public class Table {
	private String name;
	private ArrayList<Column> columns;
	// private ArrayList<Index> indexes;

	/**
	 * return the column list
	 *
	 * @return column list
	 */
	public ArrayList<Column> getColumns() {
		return columns;
	}

	/**
	 * sets the column name
	 *
	 * @param columns column list
	 */
	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	/**
	 * gets the table name
	 *
	 * @return table name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name
	 *
	 * @param name table name
	 */
	public void setName(String name) {
		this.name = name;
	}

	
}
