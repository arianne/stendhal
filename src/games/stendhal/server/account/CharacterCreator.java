/**
 * 
 */
package games.stendhal.server.account;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.RuleManager;
import games.stendhal.server.rule.RuleSetFactory;

import java.sql.SQLException;

import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Result;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;
import marauroa.test.TestHelper;

import org.apache.log4j.Logger;

/**
 * Creates a new character as requested by a client.
 */
public class CharacterCreator {
	private static Logger logger = Logger.getLogger(CharacterCreator.class);

	private String username;
	private String character;
	private RPObject template;

	/**
	 * create a CharacterCreator
	 *
	 * @param username  name of the user
	 * @param character name of the new character
	 * @param template  tempalte to base this character on
	 */
	public CharacterCreator(String username, String character, RPObject template) {
		this.username = username;
		this.character = character;
		this.template = template;
	}

	private boolean isValidCharactername(String username) {
		/** TODO: Complete this. Should read the list from XML file */
		if (username.indexOf(' ') != -1) {
			return false;
		}
		// TODO: Fix bug [ 1672627 ] 'admin' not allowed in username but GM_ and
		// _GM are
		if (username.toLowerCase().contains("admin")) {
			return false;
		}
		
		// Ensure username is at least 4 characters length.
		if( username.length()<4)  {
			return false;
		}
		
		return true;
	}

	/**
	 * tries to create this character
	 *
	 * @return CharacterResult
	 */
	public CharacterResult create() {
		/*
		 * TODO: Refactor Invalid patterns for username should be stored in a
		 * text file or XML file.
		 */
		if (!isValidCharactername(character)) {
			return new CharacterResult(Result.FAILED_EXCEPTION, character, template);
		}

		JDBCDatabase database = (JDBCDatabase) DatabaseFactory.getDatabase();
		Transaction trans = database.getTransaction();
		
		try {
			if (database.hasCharacter(trans, username, character)) {
				logger.warn("Character already exist: " + character);
				return new CharacterResult(Result.FAILED_PLAYER_EXISTS,
						character, template);
			}
		
			/*
			 * TODO: Refactor OMG! Hide in a method. Even better, move it to
			 * player class as it is its duty to provide a empty level 0 player.
			 */
			/*
			 * Create the player character object
			 */
			Player object = new Player(new RPObject());
			object.setID(RPObject.INVALID_ID);
		
			object.put("type", "player");
			object.put("name", character);
			object.put("outfit", new Outfit().getCode());
			object.put("base_hp", 100);
			object.put("hp", 100);
			object.put("atk", 10);
			object.put("atk_xp", 0);
			object.put("def", 10);
			object.put("def_xp", 0);
			object.put("xp", 0);
		
			/*
			 * TODO: Update the above to use Player and RPEntity methods.
			 */
			object.update();
		
			RuleManager manager = RuleSetFactory.getRuleSet("default");
		
			object.addSlot("armor");
			Entity entity = manager.getEntityManager().getItem("leather_armor");
			RPSlot slot = object.getSlot("armor");
			slot.add(entity);
		
			object.addSlot("rhand");
			entity = manager.getEntityManager().getItem("club");
			slot = object.getSlot("rhand");
			slot.add(entity);
		
			/*
			 * Finally we add it to database.
			 */
			database.addCharacter(trans, username, character, object);
			return new CharacterResult(Result.OK_CREATED, character, object);
		} catch (Exception e) {
			try {
				trans.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("Can't create character", e);
			TestHelper.fail();
			return new CharacterResult(Result.FAILED_EXCEPTION, character, template);
		}
	}
}
