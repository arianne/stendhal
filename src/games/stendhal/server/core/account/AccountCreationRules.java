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

/**
 * rules for account creation.
 *
 * @author hendrik
 */
class AccountCreationRules {
	private final ValidatorList validators = new ValidatorList();

	private final String username;
	private final String password;
	private final String email;

	/**
	 * creates a new AccountCreationRules instance.
	 *
	 * @param username
	 *            name of the user
	 * @param password
	 *            password for this account
	 * @param email
	 *            email contact
	 */
	protected AccountCreationRules(final String username, final String password, final String email) {
		this.username = username.trim();
		this.password = password.trim();
		this.email = email.trim();
	}

	private void setupValidatorsForUsername() {
		validators.add(new NotEmptyValidator(username));
		validators.add(new MinLengthValidator(username, 4));
		validators.add(new MaxLengthValidator(username, 20));

		validators.add(new LowerCaseValidator(username));
		validators.add(new NameCharacterValidator(username));
		validators.add(new ReservedSubStringValidator(username));
		validators.add(new NPCNameValidator(username));
		validators.add(new CreatureNameValidator(username));

		validators.add(new IsNotCharacterNameValidator(username));
	}

	private void setupValidatorsForPassword() {
		validators.add(new NotEmptyValidator(password));
		validators.add(new MinLengthValidator(password, 4));
		validators.add(new MaxLengthValidator(password, 100));
		validators.add(new CommonPassword(password));
		// This is only a warning in the client:
		// validators.add(new PasswordDiffersFromUsernameValidator(username,
		// password));
	}

	private void setupValidatorsForEMail() {
		validators.add(new MaxLengthValidator(email, 100));
		validators.add(new EMailCharacterValidator(email));
	}

	/**
	 * returns a complete list of all rules which must be enforced during.
	 * account creation
	 *
	 * @return ValidatorList
	 */
	public ValidatorList getAllRules() {
		setupValidatorsForUsername();
		setupValidatorsForPassword();
		setupValidatorsForEMail();
		return validators;
	}
}
