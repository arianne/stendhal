package games.stendhal.server.core.account;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.Result;

/**
 * validates name is not an NPC name
 * 
 * @author kymara
 */
public class NPCNameValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * creates a NPCNameValidator.
	 * 
	 * @param parameterValue
	 *            value to validate
	 */
	public NPCNameValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Result validate() {
		final NPCList npcs = SingletonRepository.getNPCList();
		for (final String name : npcs.getNPCs()) {
			if (name.equals(parameterValue)) {
				return Result.FAILED_RESERVED_NAME;
			}	
		}
		return null;
	}
}
