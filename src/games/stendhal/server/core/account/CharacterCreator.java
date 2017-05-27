/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.account;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.Result;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Creates a new character as requested by a client.
 */
public class CharacterCreator {
	private static Logger logger = Logger.getLogger(CharacterCreator.class);
	private final ValidatorList validators = new ValidatorList();

	private final String username;
	private final String character;
	private final RPObject template;

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
	public CharacterCreator(final String username, final String character, final RPObject template) {
		this.username = username;
		this.character = character;
		this.template = template;
		setupValidatorsForCharacter();
	}

	private void setupValidatorsForCharacter() {
		validators.add(new NotEmptyValidator(character));
		validators.add(new MinLengthValidator(character, 4));
		validators.add(new MaxLengthValidator(character, 20));

		validators.add(new NPCNameValidator(character));
		validators.add(new CreatureNameValidator(character));

		validators.add(new LowerCaseValidator(character));
		validators.add(new NameCharacterValidator(character));
		validators.add(new ReservedSubStringValidator(character));

		validators.add(new IsNotOtherAccountNameValidator(character, username));
	}

	/**
	 * tries to create this character.
	 *
	 * @return CharacterResult
	 */
	public CharacterResult create() {
		final Result result = validators.runValidators();
		if (result != null) {
			return new CharacterResult(result, character, template);
		}

		final TransactionPool transactionPool = SingletonRepository.getTransactionPool();
		final DBTransaction trans = transactionPool.beginWork();
		final CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);

		try {
			if (characterDAO.hasCharacter(trans, character)) {
				logger.warn("Character already exist: " + character);
				transactionPool.commit(trans);
				return new CharacterResult(Result.FAILED_PLAYER_EXISTS,
						character, template);
			}

			final Player object = Player.createZeroLevelPlayer(character, template);

			// monitor new account names
			final String text = "Character " + character + " (" + username + ") has been created.";
			SingletonRepository.getRuleProcessor().sendMessageToSupporters(text);

			// Finally we add it to database.
			characterDAO.addCharacter(trans, username, character, object);
			transactionPool.commit(trans);

			return new CharacterResult(Result.OK_CREATED, character, object);
		} catch (final Exception e) {
			transactionPool.rollback(trans);
			logger.error("Can't create character", e);
			return new CharacterResult(Result.FAILED_EXCEPTION, character, template);
		}
	}

}
