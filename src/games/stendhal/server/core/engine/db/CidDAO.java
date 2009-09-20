package games.stendhal.server.core.engine.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;

/**
 * Database access for cid.
 */
public class CidDAO {

	private Logger logger = Logger.getLogger(CidDAO.class);

	/**
	 * logs a cid
	 *
	 * @param transaction DBTransaction
	 * @param charname  name of character
	 * @param address   ip-address
	 * @param cid       cid
	 * @throws SQLException in case of an database error
	 */
	private void log(DBTransaction transaction, String charname, String address, String cid) throws SQLException {
		String query = "insert into cid(charname, address, cid) values ('[charname]', '[address]', '[cid]')";
		logger.debug("loadCharacter is executing query " + query);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("charname", charname);
		params.put("address", address);
		params.put("cid", cid);
		transaction.execute(query, params);
	}

	/**
	 * logs a cid
	 *
	 * @param charname  name of character
	 * @param address   ip-address
	 * @param cid       cid
	 */
	public void log(String charname, String address, String cid) {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			log(transaction, charname, address, cid);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			TransactionPool.get().rollback(transaction);
			logger.error(e, e);
		}
	}

}
