package games.stendhal.server;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.JDBCSQLHelper;
import marauroa.server.game.db.JDBCTransaction;
import marauroa.server.game.db.NoDatabaseConfException;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

public class StendhalPlayerDatabase extends JDBCDatabase implements Iterable<RPObject> {

	private static final Logger logger = Log4J.getLogger(StendhalPlayerDatabase.class);

	private StendhalPlayerDatabase(Properties connInfo) {
		super(connInfo);
		try {
	        configureDatabase();
        } catch (SQLException e) {
        	throw new NoDatabaseConfException(e);
        }		
	}

	private void configureDatabase() throws SQLException {
	    Transaction trans=getTransaction();
		JDBCSQLHelper.get().runDBScript(trans,"games/stendhal/server/stendhal_init.sql");
		trans.commit();
    }

	public static IDatabase newConnection() throws IOException {
		Configuration conf = Configuration.getConfiguration();
		Properties props = new Properties();

		props.put("jdbc_url", conf.get("jdbc_url"));
		props.put("jdbc_class", conf.get("jdbc_class"));
		props.put("jdbc_user", conf.get("jdbc_user"));
		props.put("jdbc_pwd", conf.get("jdbc_pwd"));

		return new StendhalPlayerDatabase(props);
	}

	@Override
	public void storeCharacter(Transaction transaction, String username, String character,
	        RPObject player) throws SQLException, IOException {
		super.storeCharacter(transaction, username, character, player);
		
		/*
		 * Here goes the stendhal specific code. 
		 */
		try {
			Connection connection = ((JDBCTransaction) transaction).getConnection();
			Statement stmt = connection.createStatement();
			
			Player instance=(Player)player;			
			
			String head=null;
			String armor=null;
			String lhand=null;
			String rhand=null;
			String legs=null;
			String feet=null;
			String cloak=null;
			
			Item item=null;
			item=instance.getHelmet();
			if(item!=null) {
			  head=item.getName();
			}

			item=instance.getArmor();
			if(item!=null) {
			  armor=item.getName();
			}

			item=instance.getShield();
			if(item!=null) {
			  lhand=item.getName();
			}

			List<Item> items=instance.getWeapons();
			if(items.size()>0) {
			  rhand=items.get(0).getName();
			}

			item=instance.getLegs();
			if(item!=null) {
			  legs=item.getName();
			}

			item=instance.getBoots();
			if(item!=null) {
			  feet=item.getName();
			}

			item=instance.getCloak();
			if(item!=null) {
			  cloak=item.getName();
			}

			
			// first try an update
			String query = "UPDATE character_stats SET "+
			   "sentence='" + StringChecker.escapeSQLString(instance.getSentence())+"', "+
			   "age=" + instance.getAge()+", "+
			   "level=" + instance.getLevel()+", "+
			   "outfit='" + instance.getOutfit().getCode()+"', "+
			   "xp=" + instance.getXP()+", "+
			   "money=" + instance.getNumberOfEquipped("money")+", "+
			   "atk=" + instance.getATK()+", "+
			   "def=" + instance.getDEF()+", "+
			   "hp=" + instance.getBaseHP()+", "+
			   "karma=" + instance.getKarma()+", "+
			   
			   "head='" + StringChecker.escapeSQLString(head)+"', "+
			   "armor='" + StringChecker.escapeSQLString(armor)+"', "+
			   "lhand='" + StringChecker.escapeSQLString(lhand)+"', "+
			   "rhand='" + StringChecker.escapeSQLString(rhand)+"', "+
			   "legs='" + StringChecker.escapeSQLString(legs)+"', "+
			   "feet='" + StringChecker.escapeSQLString(feet)+"', "+
			   "cloak='" + StringChecker.escapeSQLString(cloak)+"'"+
			   " WHERE name='" + StringChecker.escapeSQLString(player.get("name"))+"'";
			logger.debug("storeCharacter is running: "+query);
			int count = stmt.executeUpdate(query);
			
			if (count == 0) {
				// no row was modified, so we need to do an insert
				query = "INSERT INTO character_stats (name, sentence, age, level, outfit, xp, money, atk, def, hp, karma" +
						"head, armor, lhand, rhand, legs, feet, cloak) VALUES (" +
				   "'"+StringChecker.escapeSQLString(instance.getName())+"', "+
				   "'"+StringChecker.escapeSQLString(instance.getSentence())+"', "+
				   instance.getAge()+", "+
				   instance.getLevel()+", "+
				   "'"+instance.getOutfit().getCode()+"', "+
				   instance.getXP()+", "+
				   instance.getNumberOfEquipped("money")+", "+
				   instance.getATK()+", "+
				   instance.getDEF()+", "+
				   instance.getBaseHP()+", "+
				   instance.getKarma()+
				   "'"+StringChecker.escapeSQLString(head)+"', "+
				   "'"+StringChecker.escapeSQLString(armor)+"', "+
				   "'"+StringChecker.escapeSQLString(lhand)+"', "+
				   "'"+StringChecker.escapeSQLString(rhand)+"', "+
				   "'"+StringChecker.escapeSQLString(legs)+"', "+
				   "'"+StringChecker.escapeSQLString(feet)+"', "+
				   "'"+StringChecker.escapeSQLString(cloak)+"'"+
				   ")";
;
				logger.debug("storeCharacter is running: "+query);
				stmt.executeUpdate(query);
			}
			stmt.close();
		} catch (SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	/*
	 * TODO: Remove once the above is done.
	 * 
	 * 
	@Override
	public synchronized int storeRPObject(Transaction trans, RPObject object) throws SQLException {
		Connection connection = ((JDBCTransaction) trans).getConnection();

		int object_id = -1;

		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DeflaterOutputStream out_stream = new DeflaterOutputStream(array);
		OutputSerializer serializer = new OutputSerializer(out_stream);

		try {
			object.writeObject(serializer, DetailLevel.FULL);
			out_stream.close();
		} catch (IOException e) {
			logger.error("Problem while serializing rpobject: " + object, e);
			throw new SQLException("Problem while serializing rpobject");
		}
		byte[] content = array.toByteArray();

		// setup stream for blob
		ByteArrayInputStream inStream = new ByteArrayInputStream(content);

		String objectid = null;

		if (object.has("#db_id")) {
			objectid = object.get("#db_id");
			object_id = object.getInt("#db_id");
		}

		String name = null;
		if (object.has("name")) {
			name = object.get("name");
		}

		String outfit = "0";
		if (object.has("outfit_org")) {
			outfit = object.get("outfit_org");
		} else if (object.has("outfit")) {
			outfit = object.get("outfit");
		}

		int level = 0;
		if (object.has("level")) {
			level = object.getInt("level");
		}

		int xp = 0;
		if (object.has("xp")) {
			xp = object.getInt("xp");
		}

		String query;

		if ((objectid != null) && hasRPObject(trans, object_id)) {
			query = "update avatars set name='" + name + "',outfit='" + outfit + "',level=" + level + ",xp=" + xp
			        + ",data=? where object_id=" + objectid;
		} else {
			query = "insert into avatars(object_id,name,outfit,level,xp,data) values(" + objectid + ",'" + name + "','"
			        + outfit + "'," + level + "," + xp + ",?)";
		}
		logger.debug("storeRPObject is executing query " + query);

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setBinaryStream(1, inStream, inStream.available());
		ps.executeUpdate();
		ps.close();

		// If object is new, get the objectid we gave it.
		if (objectid == null) {
			Statement stmt = connection.createStatement();
			query = "select LAST_INSERT_ID() as inserted_id from avatars";
			logger.debug("storeRPObject is executing query " + query);
			ResultSet result = stmt.executeQuery(query);

			result.next();
			object_id = result.getInt("inserted_id");

			stmt.close();
		}

		return object_id;
	}*/

	private static StendhalPlayerDatabase playerDatabase = null;

	/**
	 * This method returns an instance of PlayerDatabase
	 * 
	 * @return A shared instance of PlayerDatabase
	 */
	public static StendhalPlayerDatabase getDatabase() {
		try {
			if (playerDatabase == null) {
				logger.info("Starting Stendhal JDBC Database");
				playerDatabase = (StendhalPlayerDatabase) newConnection();
			}

			return playerDatabase;
		} catch (Exception e) {
			logger.error("cannot get database connection", e);
			throw new NoDatabaseConfException(e);
		}
	}

	class PlayerIterator implements Iterator<RPObject> {
		private ResultSet result;
		private Transaction trans;
		
		public PlayerIterator() throws SQLException {
			trans=getTransaction();
			Connection connection = trans.getConnection();
			Statement stmt = connection.createStatement();
			String query = "select object_id from characters";

			logger.debug("iterator is executing query " + query);
			result = stmt.executeQuery(query);
		}

		public boolean hasNext() {
	        try {
	            return result.next();
            } catch (SQLException e) {
	            logger.error(e,e);
	            return false;
            }
        }

		public RPObject next() {
			try {
	            int objectid = result.getInt("object_id");
	            return loadRPObject(trans, objectid);
            } catch (Exception e) {
            	logger.warn(e,e);
	            return null;
            }
        }

		public void remove() {
			throw new UnsupportedOperationException();
        }
		
	}
	
	public Iterator<RPObject> iterator() {
		try {
	        return new PlayerIterator();
        } catch (SQLException e) {
        	logger.warn(e,e);
        	return null;
        }
    }

	/**
	 * Returns the points in the specified hall of fame
	 *
	 * @param trans      Transaction
	 * @param playername name of the player
	 * @param fametype   type of the hall of fame
	 * @return points or 0 in case there is no entry
	 * @throws GenericDatabaseException in case of an database error
	 */
	public int getHallOfFamePoints(Transaction trans, String playername, String fametype) {
		int res = 0;
		try {
			Connection connection = ((JDBCTransaction) trans).getConnection();
			Statement stmt = connection.createStatement();

			String query = "SELECT points FROM halloffame WHERE charname='" + StringChecker.escapeSQLString(playername)
			        + "' AND fametype='" + StringChecker.escapeSQLString(fametype) + "'";
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				res = result.getInt("points");
			}
			result.close();
			stmt.close();
		} catch (Exception sqle) {
			logger.warn("Error reading hall of fame", sqle);
		}

		return res;
	}

	/**
	 * Stores an entry in the hall of fame
	 *
	 * @param trans      Transaction
	 * @param playername name of the player
	 * @param fametype   type of the hall of fame
	 * @param points     points to store
	 * @throws SQLException 
	 * @throws GenericDatabaseException in case of an database error
	 */
	public void setHallOfFamePoints(Transaction trans, String playername, String fametype, int points) throws SQLException {
		try {
			Connection connection = ((JDBCTransaction) trans).getConnection();
			Statement stmt = connection.createStatement();

			// first try an update
			String query = "UPDATE halloffame SET points='" + StringChecker.escapeSQLString(Integer.toString(points))
			        + "' WHERE charname='" + StringChecker.escapeSQLString(playername) + "' AND fametype='"
			        + StringChecker.escapeSQLString(fametype) + "';";
			int count = stmt.executeUpdate(query);
			
			if (count == 0) {
				// no row was modified, so we need to do an insert
				query = "INSERT INTO halloffame (charname, fametype, points) VALUES ('" + StringChecker.escapeSQLString(playername)
				        + "','" + StringChecker.escapeSQLString(fametype) + "','" + StringChecker.escapeSQLString(Integer.toString(points)) + "');";
				stmt.executeUpdate(query);
			}
			stmt.close();
		} catch (SQLException sqle) {
			logger.warn("error adding game event", sqle);
			throw sqle;
		}
	}

	/**
	 * Cleans the old chat log entries.
	 */
	public void cleanChatLog(Transaction trans) {
		/*try {
			Connection connection = ((JDBCTransaction) trans).getConnection();
			Statement stmt = connection.createStatement();
			logger.info("Cleaning chat log");
			stmt.executeUpdate("UPDATE gameEvents SET param1=null, param2=null WHERE param2 IS NOT NULL AND event='chat' AND timedate < DATE_SUB(CURDATE(), INTERVAL 2 DAY);");
			stmt.close();
		} catch (SQLException e) {
			logger.error(e, e);
		}*/
	}
}
