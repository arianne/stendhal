/***************************************************************************
 *                      (C) Copyright 2012 Faiumoni                        *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.validator;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;

/**
 * standard action validations
 *
 * @author hendrik
 */
public class StandardActionValidations {

	/** validation for chat */
	public static final ActionValidation CHAT;

	/** validation for private chat message */
	public static final ActionValidation PRIVATE_CHAT;

	static {
		ActionValidation validation = new ActionValidation();
		validation.add(new ActionAttributesExist(TEXT));
		validation.add(new ActionSenderUseChatBucket(TEXT));
		validation.add(new ActionSenderNotGagged());
		CHAT = validation;

		validation = new ActionValidation();
		validation.add(new ActionAttributesExist(TARGET));
		validation.add(CHAT);
		validation.add(new ActionSenderNotInJail());
		validation.add(new ActionTargetOnline(TARGET, true));
		validation.add(new ActionTargetNotAway(TARGET, true));
		validation.add(new ActionTargetNotGrumpyTowardsSender(TARGET));
		validation.add(new ActionTargetNotIgnoringSender(TARGET));
		PRIVATE_CHAT = validation;
	}
}
