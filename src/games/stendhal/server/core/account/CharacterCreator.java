package games.stendhal.server.core.account;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.Result;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

/**
 * Creates a new character as requested by a client.
 */
public class CharacterCreator {
	private static Logger logger = Logger.getLogger(CharacterCreator.class);
	private ValidatorList validators = new ValidatorList();

	private String username;
	private String character;
	private RPObject template;

	/**
	 * create a CharacterCreator.
	 * 
	 * @param username
	 *            name of the user
	 * @param character
	 *            name of the new character
	 * @param template
	 *            template to base this character on
	 */
	public CharacterCreator(String username, String character, RPObject template) {
		this.username = username;
		this.character = character;
		this.template = template;
		setupValidatorsForCharacter();
	}

	private void setupValidatorsForCharacter() {
		validators.add(new NotEmptyValidator(character));
		validators.add(new MinLengthValidator(character, 4));
		validators.add(new MaxLengthValidator(character, 20));

		validators.add(new LowerCaseValidator(character));
		validators.add(new NameCharacterValidator(character));
		validators.add(new ReservedSubStringValidator(character));
	}

	/**
	 * tries to create this character.
	 * 
	 * @return CharacterResult
	 */
	public CharacterResult create() {
		Result result = validators.runValidators();
		if (result != null) {
			return new CharacterResult(result, character, template);
		}

		IDatabase database = SingletonRepository.getPlayerDatabase();
		Transaction trans = database.getTransaction();

		try {
			if (database.hasCharacter(trans, username, character)) {
				logger.warn("Character already exist: " + character);
				return new CharacterResult(Result.FAILED_PLAYER_EXISTS,
						character, template);
			}

			Player object = Player.createEmptyZeroLevelPlayer(character);

			/*
			 * Finally we add it to database.
			 */
			database.addCharacter(trans, username, character, object);
			trans.commit();

			return new CharacterResult(Result.OK_CREATED, character, object);
		} catch (Exception e) {
			try {
				trans.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("Can't create character", e);
			return new CharacterResult(Result.FAILED_EXCEPTION, character, template);
		}
	}
	
}
