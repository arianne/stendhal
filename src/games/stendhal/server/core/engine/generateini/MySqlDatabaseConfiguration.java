/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.generateini;

/**
 * Representation of a MySQL datatabse configuration.
 *
 */
public class MySqlDatabaseConfiguration extends DatabaseConfiguration {

	private String dbName;
	private String dbHost;
	private String dbUser;
	private String dbPassword;

	public MySqlDatabaseConfiguration(String databaseHost, String databaseName, String user, String password) {
		super();
		this.dbHost = databaseHost;
		this.dbName = databaseName;
		this.dbUser = user;
		this.dbPassword = password;
	}



	@Override
	public String toIni() {
		StringBuilder sb = new StringBuilder();
		sb.append("database_adapter=marauroa.server.db.adapter.MySQLDatabaseAdapter");
		sb.append(System.lineSeparator());
		sb.append("jdbc_url=jdbc:mysql://" + dbHost + "/" + dbName + "?useUnicode=yes&characterEncoding=UTF-8");
		sb.append(System.lineSeparator());
		sb.append("jdbc_class=com.mysql.jdbc.Driver");
		sb.append(System.lineSeparator());
		sb.append("jdbc_user=" + dbUser);
		sb.append(System.lineSeparator());
		sb.append("jdbc_pwd=" + dbPassword);
		sb.append(System.lineSeparator());
		return sb.toString();
	}

}
