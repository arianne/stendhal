package games.stendhal.tools.loganalyser.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

public class ResultSetIteratorTest {

	/**
	 * Returns false for boolean, null for object and 0 for numbers.
	 * 
	 */
	private class ResultSetImplementation implements ResultSet {
		public boolean absolute(int row) throws SQLException {
			return false;
		}

		public void afterLast() throws SQLException {

		}

		public void beforeFirst() throws SQLException {

		}

		public void cancelRowUpdates() throws SQLException {

		}

		public void clearWarnings() throws SQLException {

		}

		public void close() throws SQLException {

		}

		public void deleteRow() throws SQLException {

		}

		public int findColumn(String columnLabel) throws SQLException {
			return 0;
		}

		public boolean first() throws SQLException {
			return false;
		}

		public Array getArray(int columnIndex) throws SQLException {
			return null;
		}

		public Array getArray(String columnLabel) throws SQLException {
			return null;
		}

		public InputStream getAsciiStream(int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getAsciiStream(String columnLabel) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
			return null;
		}

		public InputStream getBinaryStream(int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getBinaryStream(String columnLabel) throws SQLException {
			return null;
		}

		public Blob getBlob(int columnIndex) throws SQLException {
			return null;
		}

		public Blob getBlob(String columnLabel) throws SQLException {
			return null;
		}

		public boolean getBoolean(int columnIndex) throws SQLException {
			return false;
		}

		public boolean getBoolean(String columnLabel) throws SQLException {
			return false;
		}

		public byte getByte(int columnIndex) throws SQLException {
			return 0;
		}

		public byte getByte(String columnLabel) throws SQLException {
			return 0;
		}

		public byte[] getBytes(int columnIndex) throws SQLException {
			return null;
		}

		public byte[] getBytes(String columnLabel) throws SQLException {
			return null;
		}

		public Reader getCharacterStream(int columnIndex) throws SQLException {
			return null;
		}

		public Reader getCharacterStream(String columnLabel) throws SQLException {
			return null;
		}

		public Clob getClob(int columnIndex) throws SQLException {
			return null;
		}

		public Clob getClob(String columnLabel) throws SQLException {
			return null;
		}

		public int getConcurrency() throws SQLException {
			return 0;
		}

		public String getCursorName() throws SQLException {
			return null;
		}

		public Date getDate(int columnIndex) throws SQLException {
			return null;
		}

		public Date getDate(String columnLabel) throws SQLException {
			return null;
		}

		public Date getDate(int columnIndex, Calendar cal) throws SQLException {
			return null;
		}

		public Date getDate(String columnLabel, Calendar cal) throws SQLException {
			return null;
		}

		public double getDouble(int columnIndex) throws SQLException {
			return 0;
		}

		public double getDouble(String columnLabel) throws SQLException {
			return 0;
		}

		public int getFetchDirection() throws SQLException {
			return 0;
		}

		public int getFetchSize() throws SQLException {
			return 0;
		}

		public float getFloat(int columnIndex) throws SQLException {
			return 0;
		}

		public float getFloat(String columnLabel) throws SQLException {
			return 0;
		}

		public int getHoldability() throws SQLException {
			return 0;
		}

		public int getInt(int columnIndex) throws SQLException {
			return 0;
		}

		public int getInt(String columnLabel) throws SQLException {
			return 0;
		}

		public long getLong(int columnIndex) throws SQLException {
			return 0;
		}

		public long getLong(String columnLabel) throws SQLException {
			return 0;
		}

		public ResultSetMetaData getMetaData() throws SQLException {
			return null;
		}

		public Reader getNCharacterStream(int columnIndex) throws SQLException {
			return null;
		}

		public Reader getNCharacterStream(String columnLabel) throws SQLException {
			return null;
		}

		public NClob getNClob(int columnIndex) throws SQLException {
			return null;
		}

		public NClob getNClob(String columnLabel) throws SQLException {
			return null;
		}

		public String getNString(int columnIndex) throws SQLException {
			return null;
		}

		public String getNString(String columnLabel) throws SQLException {
			return null;
		}

		public Object getObject(int columnIndex) throws SQLException {
			return null;
		}

		public Object getObject(String columnLabel) throws SQLException {
			return null;
		}

		public Object getObject(int columnIndex, Map<String, Class< ? >> map) throws SQLException {
			return null;
		}

		public Object getObject(String columnLabel, Map<String, Class< ? >> map) throws SQLException {
			return null;
		}

		public Ref getRef(int columnIndex) throws SQLException {
			return null;
		}

		public Ref getRef(String columnLabel) throws SQLException {
			return null;
		}

		public int getRow() throws SQLException {
			return 0;
		}

		public RowId getRowId(int columnIndex) throws SQLException {
			return null;
		}

		public RowId getRowId(String columnLabel) throws SQLException {
			return null;
		}

		public SQLXML getSQLXML(int columnIndex) throws SQLException {
			return null;
		}

		public SQLXML getSQLXML(String columnLabel) throws SQLException {
			return null;
		}

		public short getShort(int columnIndex) throws SQLException {
			return 0;
		}

		public short getShort(String columnLabel) throws SQLException {
			return 0;
		}

		public Statement getStatement() throws SQLException {
			return null;
		}

		public String getString(int columnIndex) throws SQLException {
			return null;
		}

		public String getString(String columnLabel) throws SQLException {
			return null;
		}

		public Time getTime(int columnIndex) throws SQLException {
			return null;
		}

		public Time getTime(String columnLabel) throws SQLException {
			return null;
		}

		public Time getTime(int columnIndex, Calendar cal) throws SQLException {
			return null;
		}

		public Time getTime(String columnLabel, Calendar cal) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(int columnIndex) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(String columnLabel) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
			return null;
		}

		public int getType() throws SQLException {
			return 0;
		}

		public URL getURL(int columnIndex) throws SQLException {
			return null;
		}

		public URL getURL(String columnLabel) throws SQLException {
			return null;
		}

		public InputStream getUnicodeStream(int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getUnicodeStream(String columnLabel) throws SQLException {
			return null;
		}

		public SQLWarning getWarnings() throws SQLException {
			return null;
		}

		public void insertRow() throws SQLException {

		}

		public boolean isAfterLast() throws SQLException {
			return false;
		}

		public boolean isBeforeFirst() throws SQLException {
			return false;
		}

		public boolean isClosed() throws SQLException {
			return false;
		}

		public boolean isFirst() throws SQLException {
			return false;
		}

		public boolean isLast() throws SQLException {
			return false;
		}

		public boolean last() throws SQLException {
			return false;
		}

		public void moveToCurrentRow() throws SQLException {

		}

		public void moveToInsertRow() throws SQLException {

		}

		public boolean next() throws SQLException {
			return false;
		}

		public boolean previous() throws SQLException {
			return false;
		}

		public void refreshRow() throws SQLException {

		}

		public boolean relative(int rows) throws SQLException {
			return false;
		}

		public boolean rowDeleted() throws SQLException {
			return false;
		}

		public boolean rowInserted() throws SQLException {
			return false;
		}

		public boolean rowUpdated() throws SQLException {
			return false;
		}

		public void setFetchDirection(int direction) throws SQLException {

		}

		public void setFetchSize(int rows) throws SQLException {

		}

		public void updateArray(int columnIndex, Array x) throws SQLException {

		}

		public void updateArray(String columnLabel, Array x) throws SQLException {

		}

		public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

		}

		public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

		}

		public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

		}

		public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

		}

		public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

		}

		public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

		}

		public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

		}

		public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

		}

		public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

		}

		public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

		}

		public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

		}

		public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

		}

		public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

		}

		public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

		}

		public void updateBlob(int columnIndex, Blob x) throws SQLException {

		}

		public void updateBlob(String columnLabel, Blob x) throws SQLException {

		}

		public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

		}

		public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

		}

		public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

		}

		public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		}

		public void updateBoolean(int columnIndex, boolean x) throws SQLException {

		}

		public void updateBoolean(String columnLabel, boolean x) throws SQLException {

		}

		public void updateByte(int columnIndex, byte x) throws SQLException {

		}

		public void updateByte(String columnLabel, byte x) throws SQLException {

		}

		public void updateBytes(int columnIndex, byte[] x) throws SQLException {

		}

		public void updateBytes(String columnLabel, byte[] x) throws SQLException {

		}

		public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

		}

		public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

		}

		public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

		}

		public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

		}

		public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

		}

		public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

		}

		public void updateClob(int columnIndex, Clob x) throws SQLException {

		}

		public void updateClob(String columnLabel, Clob x) throws SQLException {

		}

		public void updateClob(int columnIndex, Reader reader) throws SQLException {

		}

		public void updateClob(String columnLabel, Reader reader) throws SQLException {

		}

		public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

		}

		public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

		}

		public void updateDate(int columnIndex, Date x) throws SQLException {

		}

		public void updateDate(String columnLabel, Date x) throws SQLException {

		}

		public void updateDouble(int columnIndex, double x) throws SQLException {

		}

		public void updateDouble(String columnLabel, double x) throws SQLException {

		}

		public void updateFloat(int columnIndex, float x) throws SQLException {

		}

		public void updateFloat(String columnLabel, float x) throws SQLException {

		}

		public void updateInt(int columnIndex, int x) throws SQLException {

		}

		public void updateInt(String columnLabel, int x) throws SQLException {

		}

		public void updateLong(int columnIndex, long x) throws SQLException {

		}

		public void updateLong(String columnLabel, long x) throws SQLException {

		}

		public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

		}

		public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

		}

		public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

		}

		public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

		}

		public void updateNClob(int columnIndex, NClob clob) throws SQLException {

		}

		public void updateNClob(String columnLabel, NClob clob) throws SQLException {

		}

		public void updateNClob(int columnIndex, Reader reader) throws SQLException {

		}

		public void updateNClob(String columnLabel, Reader reader) throws SQLException {

		}

		public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

		}

		public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

		}

		public void updateNString(int columnIndex, String string) throws SQLException {

		}

		public void updateNString(String columnLabel, String string) throws SQLException {
		}

		public void updateNull(int columnIndex) throws SQLException {
		}

		public void updateNull(String columnLabel) throws SQLException {
		}

		public void updateObject(int columnIndex, Object x) throws SQLException {
		}

		public void updateObject(String columnLabel, Object x) throws SQLException {
		}

		public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		}

		public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		}

		public void updateRef(int columnIndex, Ref x) throws SQLException {
		}

		public void updateRef(String columnLabel, Ref x) throws SQLException {
		}

		public void updateRow() throws SQLException {
		}

		public void updateRowId(int columnIndex, RowId x) throws SQLException {
		}

		public void updateRowId(String columnLabel, RowId x) throws SQLException {
		}

		public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		}

		public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		}

		public void updateShort(int columnIndex, short x) throws SQLException {
		}

		public void updateShort(String columnLabel, short x) throws SQLException {
		}

		public void updateString(int columnIndex, String x) throws SQLException {
		}

		public void updateString(String columnLabel, String x) throws SQLException {
		}

		public void updateTime(int columnIndex, Time x) throws SQLException {
		}

		public void updateTime(String columnLabel, Time x) throws SQLException {
		}

		public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		}

		public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		}

		public boolean wasNull() throws SQLException {
			return false;
		}

		public boolean isWrapperFor(Class< ? > iface) throws SQLException {
			return false;
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}
	}

	/**
	 * Returns false for boolean, null for object and 0 for numbers.
	 * 
	 */
	private class StatementImplementation implements Statement {
		public void addBatch(String sql) throws SQLException {
		}

		public void cancel() throws SQLException {
		}

		public void clearBatch() throws SQLException {
		}

		public void clearWarnings() throws SQLException {
		}

		public void close() throws SQLException {
		}

		public boolean execute(String sql) throws SQLException {
			return false;
		}

		public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
			return false;
		}

		public boolean execute(String sql, int[] columnIndexes) throws SQLException {
			return false;
		}

		public boolean execute(String sql, String[] columnNames) throws SQLException {
			return false;
		}

		public int[] executeBatch() throws SQLException {
			return null;
		}

		public ResultSet executeQuery(String sql) throws SQLException {
			return null;
		}

		public int executeUpdate(String sql) throws SQLException {
			return 0;
		}

		public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
			return 0;
		}

		public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
			return 0;
		}

		public int executeUpdate(String sql, String[] columnNames) throws SQLException {
			return 0;
		}

		public Connection getConnection() throws SQLException {
			return null;
		}

		public int getFetchDirection() throws SQLException {
			return 0;
		}

		public int getFetchSize() throws SQLException {
			return 0;
		}

		public ResultSet getGeneratedKeys() throws SQLException {
			return null;
		}

		public int getMaxFieldSize() throws SQLException {
			return 0;
		}

		public int getMaxRows() throws SQLException {
			return 0;
		}

		public boolean getMoreResults() throws SQLException {
			return false;
		}

		public boolean getMoreResults(int current) throws SQLException {
			return false;
		}

		public int getQueryTimeout() throws SQLException {
			return 0;
		}

		public ResultSet getResultSet() throws SQLException {
			return null;
		}

		public int getResultSetConcurrency() throws SQLException {
			return 0;
		}

		public int getResultSetHoldability() throws SQLException {
			return 0;
		}

		public int getResultSetType() throws SQLException {
			return 0;
		}

		public int getUpdateCount() throws SQLException {
			return 0;
		}

		public SQLWarning getWarnings() throws SQLException {
			return null;
		}

		public boolean isClosed() throws SQLException {
			return false;
		}

		public boolean isPoolable() throws SQLException {
			return false;
		}

		public void setCursorName(String name) throws SQLException {
		}

		public void setEscapeProcessing(boolean enable) throws SQLException {
		}

		public void setFetchDirection(int direction) throws SQLException {
		}

		public void setFetchSize(int rows) throws SQLException {
		}

		public void setMaxFieldSize(int max) throws SQLException {
		}

		public void setMaxRows(int max) throws SQLException {
		}

		public void setPoolable(boolean poolable) throws SQLException {
		}

		public void setQueryTimeout(int seconds) throws SQLException {
		}

		public boolean isWrapperFor(Class< ? > iface) throws SQLException {
			return false;
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}
	}

	/**
	 * Returns null for createObject().
	 * 
	 */
	private class ResultSetIterImplentation extends ResultSetIterator<String> {
		private ResultSetIterImplentation(Statement statement, ResultSet resultSet) {
			super(statement, resultSet);
		}

		@Override
		protected String createObject() {

			return null;
		}
	}

	@Test
	public void testResultSetIterator() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
				new ResultSetImplementation());
	}

	@Test
	public void testCreateObject() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(	new StatementImplementation(),
																		new ResultSetImplementation()) {
			@Override
			protected String createObject() {
				return "result";
			}

		};
		assertThat(iter.createObject(), is("result"));
	}

	@Test
	public void testHasNext() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
				new ResultSetImplementation());
		assertFalse(iter.hasNext());
		assertFalse(iter.hasNext());
		iter = new ResultSetIterImplentation(new StatementImplementation(), new ResultSetImplementation() {
			@Override
			public boolean next() throws SQLException {

				return true;
			}
		});
		assertTrue(iter.hasNext());
		assertTrue(iter.hasNext());
		iter = new ResultSetIterImplentation(new StatementImplementation(), new ResultSetImplementation() {
			@Override
			public boolean next() throws SQLException {
				throw new SQLException();
			}
		});
		assertFalse(iter.hasNext());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testNext() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(	new StatementImplementation(),
																		new ResultSetImplementation()) {
			protected String object = "";

			@Override
			protected String createObject() {
				return object += "a";
			}

			@Override
			public boolean hasNext() {
				return true;
			}
		};
		assertTrue(iter.hasNext());
		assertThat(iter.next(), is("a"));
		assertTrue(iter.hasNext());
		assertThat(iter.next(), is("aa"));

	}

	private boolean statementClosehasbeenCalled;
	private boolean resultClosehasbeenCalled;

	@Test
	public void testClose() {
		statementClosehasbeenCalled = false;
		resultClosehasbeenCalled = false;
		ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation() {

			@Override
			public void close() throws SQLException {
				statementClosehasbeenCalled = true;
			}
		}, new ResultSetImplementation() {
			@Override
			public boolean next() throws SQLException {
				return false;
			}

			@Override
			public void close() throws SQLException {
				resultClosehasbeenCalled = true;
				throw new SQLException();

			}
		});
		iter.next();
		assertTrue(resultClosehasbeenCalled);
		assertTrue(statementClosehasbeenCalled);

	}

	@Test
	public void testRemove() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
				new ResultSetImplementation() {
					@Override
					public void deleteRow() throws SQLException {
						throw new SQLException();
					}
				});
		iter.remove();
		assertTrue("no exception thrown", true);
	}

	@Test
	public void testIterator() {
		ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
																		new ResultSetImplementation());
		assertSame(iter, iter.iterator());

	}

}
