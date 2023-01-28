/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import games.stendhal.server.core.engine.generateini.DatabaseConfiguration;
import games.stendhal.server.core.engine.generateini.DatabaseType;
import games.stendhal.server.core.engine.generateini.H2DatabaseConfiguration;
import games.stendhal.server.core.engine.generateini.MySqlDatabaseConfiguration;
import games.stendhal.server.core.engine.generateini.ServerIniConfiguration;

/**
 * Generate a server.ini in a container environment. Uses fixed datbase H2 and provides ability to setup
 * path to database files of H2.
 */
public class AutomaticGenerateINI {
	
	private static final String STENDHAL_KEY_SIZE = "STENDHAL_KEY_SIZE";
	private static final String STENDHAL_DB_TYPE = "STENDHAL_DB_TYPE";
	private static final String STENDHAL_DB_PATH = "STENDHAL_DB_PATH";
	static final String STENDHAL_DB_HOST = "STENDHAL_DB_HOST";
	static final String STENDHAL_DB_NAME = "STENDHAL_DB_NAME";
	static final String STENDHAL_DB_USER = "STENDHAL_DB_USER";
	static final String STENDHAL_DB_PASSWORD = "STENDHAL_DB_PASSWORD";

    private String databasePath;
    private Integer keySize;
    private DatabaseType databaseType;
    private String databaseHost;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;

    public AutomaticGenerateINI(Map<String, String> environment) {
        this.keySize = Integer.parseInt(environment.getOrDefault(STENDHAL_KEY_SIZE, "512"));
        this.databaseType = DatabaseType.valueOf(environment.getOrDefault(STENDHAL_DB_TYPE, "h2db").toUpperCase());
        this.databasePath = environment.getOrDefault(STENDHAL_DB_PATH, "/stendhal/data/h2db");
        this.databaseHost = environment.get(STENDHAL_DB_HOST);
    	this.databaseName = environment.get(STENDHAL_DB_NAME);
    	this.databaseUser = environment.get(STENDHAL_DB_USER);
    	this.databasePassword = environment.get(STENDHAL_DB_PASSWORD);
    }

    public void write(OutputStream outputStream) {
        final PrintWriter out = new PrintWriter(outputStream);
        DatabaseConfiguration db;
        switch (this.databaseType) {
		case MYSQL:
			db = new MySqlDatabaseConfiguration(this.databaseHost, this.databaseName, this.databaseUser, this.databasePassword);
			break;
		case H2DB:
		default:
			db = new H2DatabaseConfiguration(databasePath);
			break;
        }
        ServerIniConfiguration configuration = new ServerIniConfiguration(db , keySize);
        configuration.write(out);
        out.close();
    }

}
