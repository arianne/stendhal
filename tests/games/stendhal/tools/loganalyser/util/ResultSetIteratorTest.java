package games.stendhal.tools.loganalyser.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.junit.Test;



public class ResultSetIteratorTest {

	/**
	 * Returns false for boolean, null for object and 0 for numbers.
	 * please dont add missing override tags and they break compatibility with java 1.5. 
	 */
	private class ResultSetImplementation implements ResultSet {
		public boolean absolute(final int row) throws SQLException {
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

		public int findColumn(final String columnLabel) throws SQLException {
			return 0;
		}

		public boolean first() throws SQLException {
			return false;
		}

		public Array getArray(final int columnIndex) throws SQLException {
			return null;
		}

		public Array getArray(final String columnLabel) throws SQLException {
			return null;
		}

		public InputStream getAsciiStream(final int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getAsciiStream(final String columnLabel) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
			return null;
		}

		public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
			return null;
		}

		public InputStream getBinaryStream(final int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getBinaryStream(final String columnLabel) throws SQLException {
			return null;
		}

		public Blob getBlob(final int columnIndex) throws SQLException {
			return null;
		}

		public Blob getBlob(final String columnLabel) throws SQLException {
			return null;
		}

		public boolean getBoolean(final int columnIndex) throws SQLException {
			return false;
		}

		public boolean getBoolean(final String columnLabel) throws SQLException {
			return false;
		}

		public byte getByte(final int columnIndex) throws SQLException {
			return 0;
		}

		public byte getByte(final String columnLabel) throws SQLException {
			return 0;
		}

		public byte[] getBytes(final int columnIndex) throws SQLException {
			return null;
		}

		public byte[] getBytes(final String columnLabel) throws SQLException {
			return null;
		}

		public Reader getCharacterStream(final int columnIndex) throws SQLException {
			return null;
		}

		public Reader getCharacterStream(final String columnLabel) throws SQLException {
			return null;
		}

		public Clob getClob(final int columnIndex) throws SQLException {
			return null;
		}

		public Clob getClob(final String columnLabel) throws SQLException {
			return null;
		}

		public int getConcurrency() throws SQLException {
			return 0;
		}

		public String getCursorName() throws SQLException {
			return null;
		}

		public Date getDate(final int columnIndex) throws SQLException {
			return null;
		}

		public Date getDate(final String columnLabel) throws SQLException {
			return null;
		}

		public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
			return null;
		}

		public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
			return null;
		}

		public double getDouble(final int columnIndex) throws SQLException {
			return 0;
		}

		public double getDouble(final String columnLabel) throws SQLException {
			return 0;
		}

		public int getFetchDirection() throws SQLException {
			return 0;
		}

		public int getFetchSize() throws SQLException {
			return 0;
		}

		public float getFloat(final int columnIndex) throws SQLException {
			return 0;
		}

		public float getFloat(final String columnLabel) throws SQLException {
			return 0;
		}

		public int getHoldability() throws SQLException {
			return 0;
		}

		public int getInt(final int columnIndex) throws SQLException {
			return 0;
		}

		public int getInt(final String columnLabel) throws SQLException {
			return 0;
		}

		public long getLong(final int columnIndex) throws SQLException {
			return 0;
		}

		public long getLong(final String columnLabel) throws SQLException {
			return 0;
		}

		public ResultSetMetaData getMetaData() throws SQLException {
			return null;
		}

		public Reader getNCharacterStream(final int columnIndex) throws SQLException {
			return null;
		}

		public Reader getNCharacterStream(final String columnLabel) throws SQLException {
			return null;
		}

		public String getNString(final int columnIndex) throws SQLException {
			return null;
		}

		public String getNString(final String columnLabel) throws SQLException {
			return null;
		}

		public Object getObject(final int columnIndex) throws SQLException {
			return null;
		}

		public Object getObject(final String columnLabel) throws SQLException {
			return null;
		}

		public Object getObject(final int columnIndex, final Map<String, Class< ? >> map) throws SQLException {
			return null;
		}

		public Object getObject(final String columnLabel, final Map<String, Class< ? >> map) throws SQLException {
			return null;
		}

		public Ref getRef(final int columnIndex) throws SQLException {
			return null;
		}

		public Ref getRef(final String columnLabel) throws SQLException {
			return null;
		}

		public int getRow() throws SQLException {
			return 0;
		}

		public short getShort(final int columnIndex) throws SQLException {
			return 0;
		}

		public short getShort(final String columnLabel) throws SQLException {
			return 0;
		}

		public Statement getStatement() throws SQLException {
			return null;
		}

		public String getString(final int columnIndex) throws SQLException {
			return null;
		}

		public String getString(final String columnLabel) throws SQLException {
			return null;
		}

		public Time getTime(final int columnIndex) throws SQLException {
			return null;
		}

		public Time getTime(final String columnLabel) throws SQLException {
			return null;
		}

		public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
			return null;
		}

		public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(final int columnIndex) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(final String columnLabel) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
			return null;
		}

		public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
			return null;
		}

		public int getType() throws SQLException {
			return 0;
		}

		public URL getURL(final int columnIndex) throws SQLException {
			return null;
		}

		public URL getURL(final String columnLabel) throws SQLException {
			return null;
		}

		public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
			return null;
		}

		public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
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

		public boolean relative(final int rows) throws SQLException {
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

		public void setFetchDirection(final int direction) throws SQLException {

		}

		public void setFetchSize(final int rows) throws SQLException {

		}

		public void updateArray(final int columnIndex, final Array x) throws SQLException {

		}

		public void updateArray(final String columnLabel, final Array x) throws SQLException {

		}

		public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {

		}

		public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {

		}

		public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {

		}

		public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {

		}

		public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {

		}

		public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {

		}

		public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {

		}

		public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {

		}

		public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {

		}

		public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {

		}

		public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {

		}

		public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {

		}

		public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {

		}

		public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {

		}

		public void updateBlob(final int columnIndex, final Blob x) throws SQLException {

		}

		public void updateBlob(final String columnLabel, final Blob x) throws SQLException {

		}

		public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {

		}

		public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {

		}

		public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {

		}

		public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
		}

		public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {

		}

		public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {

		}

		public void updateByte(final int columnIndex, final byte x) throws SQLException {

		}

		public void updateByte(final String columnLabel, final byte x) throws SQLException {

		}

		public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {

		}

		public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {

		}

		public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {

		}

		public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {

		}

		public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {

		}

		public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {

		}

		public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {

		}

		public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {

		}

		public void updateClob(final int columnIndex, final Clob x) throws SQLException {

		}

		public void updateClob(final String columnLabel, final Clob x) throws SQLException {

		}

		public void updateClob(final int columnIndex, final Reader reader) throws SQLException {

		}

		public void updateClob(final String columnLabel, final Reader reader) throws SQLException {

		}

		public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {

		}

		public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {

		}

		public void updateDate(final int columnIndex, final Date x) throws SQLException {

		}

		public void updateDate(final String columnLabel, final Date x) throws SQLException {

		}

		public void updateDouble(final int columnIndex, final double x) throws SQLException {

		}

		public void updateDouble(final String columnLabel, final double x) throws SQLException {

		}

		public void updateFloat(final int columnIndex, final float x) throws SQLException {

		}

		public void updateFloat(final String columnLabel, final float x) throws SQLException {

		}

		public void updateInt(final int columnIndex, final int x) throws SQLException {

		}

		public void updateInt(final String columnLabel, final int x) throws SQLException {

		}

		public void updateLong(final int columnIndex, final long x) throws SQLException {

		}

		public void updateLong(final String columnLabel, final long x) throws SQLException {

		}

		public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {

		}

		public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {

		}

		public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {

		}

		public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {

		}

	
		public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {

		}

		public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {

		}

		public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {

		}

		public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {

		}

		public void updateNString(final int columnIndex, final String string) throws SQLException {

		}

		public void updateNString(final String columnLabel, final String string) throws SQLException {
		}

		public void updateNull(final int columnIndex) throws SQLException {
		}

		public void updateNull(final String columnLabel) throws SQLException {
		}

		public void updateObject(final int columnIndex, final Object x) throws SQLException {
		}

		public void updateObject(final String columnLabel, final Object x) throws SQLException {
		}

		public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
		}

		public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
		}

		public void updateRef(final int columnIndex, final Ref x) throws SQLException {
		}

		public void updateRef(final String columnLabel, final Ref x) throws SQLException {
		}

		public void updateRow() throws SQLException {
		}

		public void updateShort(final int columnIndex, final short x) throws SQLException {
		}

		public void updateShort(final String columnLabel, final short x) throws SQLException {
		}

		public void updateString(final int columnIndex, final String x) throws SQLException {
		}

		public void updateString(final String columnLabel, final String x) throws SQLException {
		}

		public void updateTime(final int columnIndex, final Time x) throws SQLException {
		}

		public void updateTime(final String columnLabel, final Time x) throws SQLException {
		}

		public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
		}

		public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
		}

		public boolean wasNull() throws SQLException {
			return false;
		}

		public boolean isWrapperFor(final Class< ? > iface) throws SQLException {
			return false;
		}

		public <T> T unwrap(final Class<T> iface) throws SQLException {
			return null;
		}


	}

	/**
	 * Returns false for boolean, null for object and 0 for numbers.
	 *
	 */
	private class StatementImplementation implements Statement {
		public void addBatch(final String sql) throws SQLException {
		}

		public void cancel() throws SQLException {
		}

		public void clearBatch() throws SQLException {
		}

		public void clearWarnings() throws SQLException {
		}

		public void close() throws SQLException {
		}

		public boolean execute(final String sql) throws SQLException {
			return false;
		}

		public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
			return false;
		}

		public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
			return false;
		}

		public boolean execute(final String sql, final String[] columnNames) throws SQLException {
			return false;
		}

		public int[] executeBatch() throws SQLException {
			return null;
		}

		public ResultSet executeQuery(final String sql) throws SQLException {
			return null;
		}

		public int executeUpdate(final String sql) throws SQLException {
			return 0;
		}

		public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
			return 0;
		}

		public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
			return 0;
		}

		public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
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

		public boolean getMoreResults(final int current) throws SQLException {
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

		public void setCursorName(final String name) throws SQLException {
		}

		public void setEscapeProcessing(final boolean enable) throws SQLException {
		}

		public void setFetchDirection(final int direction) throws SQLException {
		}

		public void setFetchSize(final int rows) throws SQLException {
		}

		public void setMaxFieldSize(final int max) throws SQLException {
		}

		public void setMaxRows(final int max) throws SQLException {
		}

		public void setPoolable(final boolean poolable) throws SQLException {
		}

		public void setQueryTimeout(final int seconds) throws SQLException {
		}

		public boolean isWrapperFor(final Class< ? > iface) throws SQLException {
			return false;
		}

		public <T> T unwrap(final Class<T> iface) throws SQLException {
			return null;
		}
	}

	/**
	 * Returns null for createObject().
	 * 
	 */
	private class ResultSetIterImplentation extends ResultSetIterator<String> {
		private ResultSetIterImplentation(final Statement statement, final ResultSet resultSet) {
			super(statement, resultSet);
		}

		@Override
		protected String createObject() {

			return null;
		}
	}

	@Test
	public void testResultSetIterator() {
		new ResultSetIterImplentation(new StatementImplementation(), new ResultSetImplementation());
		
	}

	@Test
	public void testCreateObject() {
		final ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
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
		final ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
																		new ResultSetImplementation()) {
			protected String object = "";

			@Override
			protected String createObject() {
				object = object + "a";
				return object;
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
		final ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation() {

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
		final ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
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
		final ResultSetIterator<String> iter = new ResultSetIterImplentation(new StatementImplementation(),
																		new ResultSetImplementation());
		assertSame(iter, iter.iterator());

	}

}
