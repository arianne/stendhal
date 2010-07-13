package games.stendhal.server.maps.semos.bank;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.GuaranteedDelayedPlayerTextSender;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.area.WalkBlocker;
import games.stendhal.server.entity.mapstuff.chest.PersonalChest;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.portal.Teleporter;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;

import java.awt.geom.Rectangle2D;
import java.util.Set;

public class Vault extends StendhalRPZone {

	private PersonalChest chest;

	public Vault(final String name, final StendhalRPZone zone,
			final Player player) {
		super(name, zone);

		init(player);

	}

	private void init(final Player player) {
		Portal portal = new Teleporter(new Spot(player.getZone(),
				player.getX(), player.getY()));
		portal.setPosition(4, 8);
		add(portal);

		chest = new PersonalChest();
		chest.setPosition(4, 2);
		add(chest);

		WalkBlocker walkblocker = new WalkBlocker();
		walkblocker.setPosition(2, 5);
		walkblocker
				.setDescription("You see a wastebin, handily placed for items you wish to dispose of.");
		add(walkblocker);
		// Add a sign explaining about equipped items
		final Sign book = new Sign();
		book.setPosition(2, 2);
		book
				.setText("Items left on the ground will be returned to you when you leave the vault, as it is assumed they were equipped by mistake. There is a wastebin provided below for anything you want to throw away. It will be emptied automatically when you leave the vault.");
		book.setEntityClass("book_blue");
		book.setResistance(0);
		add(book);
		disallowIn();
		this.addMovementListener(new VaultMovementListener());
	}

	private static final class VaultMovementListener implements
			MovementListener {
		public Rectangle2D getArea() {
			return new Rectangle2D.Double(0, 0, 100, 100);
		}

		public void onEntered(final ActiveEntity entity,
				final StendhalRPZone zone, final int newX, final int newY) {
			// ignore
		}

		public void onExited(final ActiveEntity entity,
				final StendhalRPZone zone, final int oldX, final int oldY) {
			if (!(entity instanceof Player)) {
				return;
			}
			if (zone.getPlayers().size() == 1) {
				Set<Item> itemsOnGround = zone.getItemsOnGround();
				for (Item item : itemsOnGround) {
					// ignore items which are in the wastebin
					if (!(item.getX() == 2 && item.getY() == 5)) {
						String message;
						boolean equippedToBag = ((RPEntity) entity).equip(
								"bag", item);
						if (equippedToBag) {

							message = Grammar.quantityplnoun(item.getQuantity(), item.getName())
												+ " which you left on the floor in the vault have been automatically "
												+ "returned to your bag.";
							
							new GameEvent(((RPEntity) entity).getName(), "equip", item.getName(), "vault", "bag", Integer.toString(item.getQuantity())).raise();
						} else {
							boolean equippedToBank = ((RPEntity) entity).equip(
									"bank", item);
							if (equippedToBank) {
								message =  Grammar.quantityplnoun(item.getQuantity(), item.getName())
								+ " which you left on the floor in the vault have been automatically "
								+ "returned to your bank chest.";
								
								new GameEvent(((RPEntity) entity).getName(), "equip", item.getName(), "vault", "bank", Integer.toString(item.getQuantity())).raise();
							} else {
								// the player lost their items
								message = Grammar.quantityplnoun(item.getQuantity(), item.getName())
													+ " which you left on the floor in the vault have been thrown into "
													+ "the void, because there was no space to fit them into either your "
													+ "bank chest or your bag.";
								
								// the timeout method enters the zone and coords of item, this is useful we will know it was in vault
								new ItemLogger().timeout(item);
							}
						}
						
						// tell the player the message 
						notifyPlayer(((RPEntity) entity).getName(), message);
					} else {
						// the timeout method enters the zone and coords of item, this is useful, this is useful we will know it was in wastebin
						new ItemLogger().timeout(item);
					}
					
				}
				// since we are about to destroy the vault, change the player
				// zoneid to semos bank so that if they are relogging,
				// they can enter back to the bank (not the default zone of
				// PlayerRPClass).
				// If they are scrolling out or walking out the portal it works
				// as before.
				entity.put("zoneid", "int_semos_bank");
				entity.put("x", "9");
				entity.put("y", "27");

				TurnNotifier.get().notifyInTurns(2, new VaultRemover(zone));
			}
		}

		public void onMoved(final ActiveEntity entity,
				final StendhalRPZone zone, final int oldX, final int oldY,
				final int newX, final int newY) {
			// ignore
		}
	}

	/**
	 * Notifies the user of the vault in the name of Dagobert.
	 * 
	 * @param target the player to be notified
	 * @param message the delivered message
	 */
	private static void notifyPlayer(final String target, final String message)  {
		// only uses postman if they logged out. Otherwise, just send the private message.
		
		final Player player = SingletonRepository.getRuleProcessor().getPlayer(target);

		new GuaranteedDelayedPlayerTextSender("Dagobert", player, message, 2);
		
	}
	
	@Override
	public void onFinish() throws Exception {
		this.remove(chest);

	}
}
