package games.stendhal.bot.core;

import games.stendhal.client.update.Version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.ClientFramework;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

/**
 * a standard implementation of the client framework
 * @author hendrik
 *
 */
public class StandardClientFramework extends ClientFramework {
	private String character;
	private PerceptionHandler handler;
	protected Map<RPObject.ID, RPObject> worldObjects;


	public StandardClientFramework(String character, PerceptionHandler handler) {
		super("games/stendhal/log4j.properties");
		this.character = character;
		this.handler = handler;
		this.worldObjects = new HashMap<RPObject.ID, RPObject>();
	}
	

	@Override
	protected String getGameName() {
		return "stendhal";
	}

	@Override
	protected String getVersionNumber() {
		return Version.VERSION;
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
		try {
			handler.apply(message, worldObjects);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(
			final List<TransferContent> items) {
		for (final TransferContent item : items) {
			item.ack = true;
		}

		return items;
	}

	@Override
	protected void onServerInfo(final String[] info) {
		// do nothing
	}

	@Override
	protected void onAvailableCharacters(final String[] characters) {
		try {
			chooseCharacter(character);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onTransfer(final List<TransferContent> items) {
		// do nothing
	}

	@Override
	protected void onPreviousLogins(final List<String> previousLogins) {
		// do nothing
	}
}
