package games.stendhal.server.core.account;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import marauroa.common.game.Result;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * validates the new account name is not a character name
 * 
 * @author kymara
 */
public class IsNotCharacterNameValidator implements AccountParameterValidator {
	
	private static Logger logger = Logger.getLogger(IsNotCharacterNameValidator.class);
	

	private final String username;
	/**
	 * creates an IsNotCharacterNameValidator.
	 * 
     * @param username
	 *             account username of character creator
	 */
	public IsNotCharacterNameValidator(final String username) {
		this.username = username;
	}

	public Result validate() {
		 try {
			 if(DAORegister.get().get(CharacterDAO.class).getAccountName(username) != null) {
				 return Result.FAILED_CHARACTER_EXISTS;
			 }
		} catch (SQLException e) {
			logger.error("Error while trying to validate username", e);
			return Result.FAILED_EXCEPTION;		
		}
		 return null;
	}
}
