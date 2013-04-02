package org.mattyo161.commons.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
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
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.RowProcessor;

/**
 * This is a helper class for the DBSync tool to wrap a result set with some special functionality
 * used to handle working with Access, since you cannot access columns more then once this class makes
 * it possible by using the DBUtils BasicRowProcessor to convert the ResultSet to a Map/Array, but it
 * only does this for Access ResultSets.
 * 
 * Currently only the basic methods that are used in DBSync have been implemented.
 * 
 * @author mattyo1
 *
 */
public class DBSyncResultSet implements ResultSet {
	ResultSet rs = null;
	Map rsMap = null;
	Object[] rsArray = null;
	boolean useMap = false;
	
	public DBSyncResultSet(ResultSet rs) {
		this.rs = rs;
		try {
			String connectionURL = this.rs.getStatement().getConnection().getMetaData().getURL().toLowerCase();
			if (connectionURL.indexOf("microsoft access driver") > 0 || connectionURL.indexOf("mdb") > 0) {
				useMap = true;
			}
		} catch (SQLException e) {
			// just skip it for now.
		}
	}

	/**
	 * Retrun a Map object from the current result set if the Map has not be cached then it will cache it.
	 * @return
	 * @throws SQLException
	 */
	public Map getRsMap() throws SQLException {
		if (rsMap == null) {
			rsMap = BasicRowProcessor.instance().toMap(rs);
		}
		return rsMap;
	}


	/**
	 * Retrun an Object[] Array from the current result set if the Array has not be cached then it will cache it.
	 * @return
	 * @throws SQLException
	 */
	public Object[] getRsArray() throws SQLException {
		if (rsArray == null) {
			rsArray = BasicRowProcessor.instance().toArray(rs);
		}
		return rsArray;
	}

	public boolean absolute(int row) throws SQLException {
		// TODO Auto-generated method stub
		return rs.absolute(row);
	}

	public void afterLast() throws SQLException {
		// TODO Auto-generated method stub
		rs.afterLast();
	}

	public void beforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		rs.beforeFirst();
	}

	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub
		rs.cancelRowUpdates();
	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		rs.clearWarnings();
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		rs.close();
	}

	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.deleteRow();
	}

	public int findColumn(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.findColumn(columnName);
	}

	public boolean first() throws SQLException {
		boolean returnValue = rs.first();
		if (useMap) {
			rsMap = null;
			rsArray = null;
		}
		return returnValue;
	}

	public Array getArray(int i) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getArray(i);
	}

	public Array getArray(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getArray(colName);
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getAsciiStream(columnIndex);
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getAsciiStream(columnName);
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBigDecimal(columnIndex,scale);
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBigDecimal(columnIndex);
	}

	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBigDecimal(columnName, scale);
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBigDecimal(columnName);
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getAsciiStream(columnIndex);
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBinaryStream(columnName);
	}

	public Blob getBlob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBlob(i);
	}

	public Blob getBlob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBlob(colName);
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBoolean(columnIndex);
	}

	public boolean getBoolean(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBoolean(columnName);
	}

	public byte getByte(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getByte(columnIndex);
	}

	public byte getByte(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getByte(columnName);
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBytes(columnIndex);
	}

	public byte[] getBytes(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getBytes(columnName);
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getCharacterStream(columnIndex);
	}

	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getCharacterStream(columnName);
	}

	public Clob getClob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getClob(i);
	}

	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getClob(colName);
	}

	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getConcurrency();
	}

	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getCursorName();
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDate(columnIndex, cal);
	}

	public Date getDate(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDate(columnIndex);
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDate(columnName, cal);
	}

	public Date getDate(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDate(columnName);
	}

	public double getDouble(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDouble(columnIndex);
	}

	public double getDouble(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getDouble(columnName);
	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getFetchDirection();
	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getFetchSize();
	}

	public float getFloat(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getFloat(columnIndex);
	}

	public float getFloat(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getFloat(columnName);
	}

	public int getInt(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getInt(columnIndex);
	}

	public int getInt(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getInt(columnName);
	}

	public long getLong(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getLong(columnIndex);
	}

	public long getLong(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getLong(columnName);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getMetaData();
	}

	public Object getObject(int i, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getObject(i, map);
	}

	public Object getObject(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		if (useMap) {
			return getRsArray()[columnIndex];
		} else {
			return rs.getObject(columnIndex);
		}
	}

	public Object getObject(String colName, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getObject(colName,map);
	}

	public Object getObject(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		if (useMap) {
			return getRsMap().get(columnName);
		} else {
			return rs.getObject(columnName);
		}
	}

	public Ref getRef(int i) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getRef(i);
	}

	public Ref getRef(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getRef(colName);
	}

	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getRow();
	}

	public short getShort(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getShort(columnIndex);
	}

	public short getShort(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getShort(columnName);
	}

	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getStatement();
	}

	public String getString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getString(columnName);
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTime(columnIndex, cal);
	}

	public Time getTime(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTime(columnIndex);
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTime(columnName, cal);
	}

	public Time getTime(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTime(columnName);
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTimestamp(columnIndex, cal);
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTimestamp(columnIndex);
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTimestamp(columnName, cal);
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getTimestamp(columnName);
	}

	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getType();
	}

	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getUnicodeStream(columnIndex);
	}

	public InputStream getUnicodeStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getUnicodeStream(columnName);
	}

	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getURL(columnIndex);
	}

	public URL getURL(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return rs.getURL(columnName);
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return rs.getWarnings();
	}

	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.insertRow();
	}

	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return rs.isAfterLast();
	}

	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return rs.isBeforeFirst();
	}

	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return rs.isFirst();
	}

	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return rs.isLast();
	}

	public boolean last() throws SQLException {
		boolean returnValue = rs.last();
		if (useMap) {
			rsMap = null;
			rsArray = null;
		}
		return returnValue;
	}

	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.moveToCurrentRow();
	}

	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.moveToInsertRow();
	}

	public boolean next() throws SQLException {
		// TODO Auto-generated method stub
		boolean returnValue = rs.next();
		if (useMap) {
			rsMap = null;
			rsArray = null;
		}
		return returnValue;
	}
	
	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return rs.previous();
	}

	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.refreshRow();
	}

	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return rs.relative(rows);
	}

	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return rs.rowDeleted();
	}

	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return rs.rowInserted();
	}

	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return rs.rowUpdated();
	}

	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub
		rs.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException {
		// TODO Auto-generated method stub
		rs.setFetchSize(rows);
		
	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateArray(columnIndex, x);
	}

	public void updateArray(String columnName, Array x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateArray(columnName, x);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateAsciiStream(columnIndex, x, length);
	}

	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateAsciiStream(columnName, x, length);
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBigDecimal(columnIndex, x);
	}

	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBigDecimal(columnName, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBinaryStream(columnIndex, x, length);
	}

	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBinaryStream(columnName, x, length);
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBlob(columnIndex, x);
	}

	public void updateBlob(String columnName, Blob x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBlob(columnName, x);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBoolean(columnIndex, x);
	}

	public void updateBoolean(String columnName, boolean x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBoolean(columnName, x);
	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateByte(columnIndex, x);
	}

	public void updateByte(String columnName, byte x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateByte(columnName, x);
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBytes(columnIndex, x);
	}

	public void updateBytes(String columnName, byte[] x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateBytes(columnName, x);
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateCharacterStream(columnIndex, x, length);
	}

	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateCharacterStream(columnName, reader, length);
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateClob(columnIndex, x);
	}

	public void updateClob(String columnName, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateClob(columnName, x);
	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateDate(columnIndex, x);
	}

	public void updateDate(String columnName, Date x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateDate(columnName, x);
	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateDouble(columnIndex, x);
	}

	public void updateDouble(String columnName, double x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateDouble(columnName, x);
	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateFloat(columnIndex, x);
	}

	public void updateFloat(String columnName, float x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateFloat(columnName, x);
	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		// TODO Auto-generated method stub
		rs.updateInt(columnIndex, x);
	}

	public void updateInt(String columnName, int x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateLong(String columnName, long x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNull(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNull(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateObject(String columnName, Object x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRef(String columnName, Ref x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub
		rs.updateRow();
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateShort(String columnName, short x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateString(int columnIndex, String x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateString(String columnName, String x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateTime(String columnName, Time x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return rs.wasNull();
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
