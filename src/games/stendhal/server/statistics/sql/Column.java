package games.stendhal.server.statistics.sql;

/**
 * Represents an SQL column
 *
 * @author hendrik
 */
public class Column {
	private String name;
	private String datatype;

	/**
	 * gets the datatype
	 *
	 * @return datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * sets the datatype
	 *
	 * @param datatype sql datatype
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * get the column name
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the column name
	 *
	 * @param name name of the column
	 */
	public void setName(String name) {
		this.name = name;
	}

	
}
