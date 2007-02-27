package games.stendhal.server.entity;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A PersonalChest is a Chest that can be used by everyone, but shows
 * different contents depending on the player who is currently using
 * it. Thus, a player can put in items into this chest and be sure that
 * nobody else will be able to take them out.
 * 
 * Caution: each PersonalChest must be placed in such a way that only one
 * player can stand next to it at a time, to prevent other players from
 * stealing while the owner is looking at his items.
 * TODO: fix this.
 * 
 * TODO: make it possible to have several banks in different cities. Currently
 * all PersonalChests access the same bank slot of a player. This is feature
 * request 1510680.
 */
public class PersonalChest extends Chest {
	/**
	 * The default bank slot name.
	 */
	public static final String	DEFAULT_BANK	= "bank";

	private Player attending;

	private IRPZone zone;

	private String	bankName;


	/**
	 * Create a personal chest using the default bank slot.
	 */
	public PersonalChest() throws AttributeNotFoundException {
		this(DEFAULT_BANK);
	}


	/**
	 * Create a personal chest using a specific bank slot.
	 *
	 * @param	bankName	The name of the bank slot.
	 */
	public PersonalChest(String bankName)
	 throws AttributeNotFoundException {
		this.bankName = bankName;

		attending = null;
		
		TurnListener turnListener = new TurnListener() {

			public void onTurnReached(int currentTurn, String message) {
				if (attending != null) {
					/* Can be replaced when we add Equip event */
					/* Mirror player objects */
					RPSlot content = getBankSlot();
					content.clear();

					for (RPObject item : getSlot("content")) {
						content.add(item);
					}

					// A hack to allow client update correctly the chest...
					content = getSlot("content");
					content.clear();

					for (RPObject item : getBankSlot()) {
						content.add(item);
					}

					/* If player is not next to depot clean it. */
					if (!nextTo(attending)
							|| !zone.has(attending.getID())) {
						content = getSlot("content");
						
						List<RPObject> itemsList=new LinkedList<RPObject>();

						for (RPObject item : getSlot("content")) {
							itemsList.add(item);
						}
						
						content.clear();

						// NOTE: As content.clear() remove the contained flag of the object
						// we need to do this hack.
						RPSlot playerContent = getBankSlot();
						playerContent.clear();
						
						for(RPObject item: itemsList) {
							playerContent.add(item);							
						}					
						
						close();
						PersonalChest.this.notifyWorldAboutChanges();
						
						attending = null;
					}
				}
				TurnNotifier.get().notifyInTurns(0, this, null);
			}
		};
		TurnNotifier.get().notifyInTurns(0, turnListener, null);
	}


	/**
	 * Get the slot that holds items for this chest.
	 *
	 * @return	A per-player/per-bank slot.
	 */
	protected RPSlot getBankSlot() {
		/*
		 * It's assumed attending != null when called
		 */
		return attending.getSlot(bankName);
	}


	@Override
	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		zone = StendhalRPWorld.get().getRPZone(player.getID());

		if (player.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				attending = player;

				RPSlot content = getSlot("content");
				content.clear();

				for (RPObject item : getBankSlot()) {					
					content.add(item);
				}

				open();
			}
			notifyWorldAboutChanges();
		}
	}
}
