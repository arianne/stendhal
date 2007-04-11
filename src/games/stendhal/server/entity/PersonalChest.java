package games.stendhal.server.entity;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.BankAccessorManager;
import games.stendhal.server.entity.slot.Banks;
import games.stendhal.server.entity.slot.DecoratingSlot;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.UseListener;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.IRPZone;
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
 */
public class PersonalChest extends Entity implements UseListener { 
	private static final String SLOT_NAME = "content";

	private Player attending;
	private IRPZone zone;
	private String bankName;
	private boolean open;
	private DecoratingSlot slot;

	/**
	 * Create a personal chest using a specific bank slot.
	 *
	 * @param bank Bank
	 */
	public PersonalChest(Banks bank) {
		super();
		put("type", "chest");
		open = false;
		this.bankName = bank.getSlotName();
		BankAccessorManager.get().add(bank, this);

		attending = null;
		
		slot = new DecoratingSlot(SLOT_NAME);
		addSlot(slot);

		TurnListener turnListener = new TurnListener() {
			public void onTurnReached(int currentTurn, String message) {
				if (attending != null) {
					/* If player is not next to depot clean it. */
					if (!nextTo(attending) || !zone.has(attending.getID())) {
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

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	@Override
	public void update() {
		super.update();
		open = false;
		if (has("open")) {
			open = true;
			slot.bindEntitySlot((EntitySlot) attending.getSlot(bankName));
		}
	}

	public void open() {
		this.open = true;
		slot.bindEntitySlot((EntitySlot) attending.getSlot(bankName));
		put("open", "");
	}

	public void close() {
		this.open = false;
		if (has("open")) {
			remove("open");
		}
		slot.unbindEntitySlot();
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public String describe() {
		String text = "You see a chest.";
		if (hasDescription()) {
			text = getDescription();
		}
		text += " It is " + (isOpen() ? "open" : "closed") + ".";
		if (isOpen()) {
			text += " You can #inspect this item to see its contents.";
		}
		return (text);
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		zone = StendhalRPWorld.get().getRPZone(player.getID());

		if (player.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				attending = player;
				open();
			}
			notifyWorldAboutChanges();
		}
	}
}
