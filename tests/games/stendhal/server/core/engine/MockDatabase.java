package games.stendhal.server.core.engine;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import marauroa.server.game.db.Accessor;
import marauroa.server.game.db.JDBCTransaction;
import marauroa.server.game.db.Transaction;

public class MockDatabase extends StendhalPlayerDatabase {
	@Override
	protected void initialize() {
	}

	@Override
	protected void configureDatabase() throws SQLException {

	}

	public MockDatabase() {
		super(null);
		database = this;
	}

	public static void resetDatabase() {

		database = null;
	}

	@Override
	public Transaction getTransaction() {
		return new JDBCTransaction(null) {
			@Override
			public void begin() throws SQLException {

			}

			@Override
			public void commit() throws SQLException {

			}

			@Override
			public Accessor getAccessor() {
				return new Accessor() {

					public void close() throws SQLException {

					}

					public int execute(String sql) throws SQLException {

						return 0;
					}

					public void execute(String sql, InputStream... inputStreams) throws SQLException, IOException {

					}

					public void executeBatch(String sql, InputStream... inputStreams) throws SQLException, IOException {

					}

					public ResultSet query(String sql) throws SQLException {

						return null;
					}

					public int querySingleCellInt(String sql) throws SQLException {

						return 0;
					}
				};

			}

			@Override
			public Connection getConnection() {

				return null;
			}

			@Override
			public void rollback() throws SQLException {

			}
		};
	}

}
