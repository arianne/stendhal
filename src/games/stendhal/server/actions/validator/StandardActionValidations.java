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

import static games.stendhal.common.constants.Actions.TEXT;

/**
 * standard action validations
 *
 * @author hendrik
 */
public class StandardActionValidations {

	/** validation for chat */
	public static final ActionValidation CHAT;

	static {
		ActionValidation validation = new ActionValidation();
		validation.add(new ActionAttributesExist(TEXT));
		validation.add(new ActionSenderUseChatBucket(TEXT));
		validation.add(new ActionSenderNotGagged());
		CHAT = validation;
	}
}
