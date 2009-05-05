package games.stendhal.server.maps.semos.bank;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
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
		// Add a sign explaining about dropped items
		final Sign book = new Sign();
		book.setPosition(2, 2);
		book
				.setText("Items left on the ground will be returned to you when you leave the vault, as it is assumed they were dropped by mistake. There is a wastebin provided below for anything you want to throw away. It will be emptied automatically when you leave the vault.");
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

		}

		public void onExited(final ActiveEntity entity,
				final StendhalRPZone zone, final int oldX, final int oldY) {
			if (!(entity instanceof Player)) {
				return;
			}
			if (zone.getPlayers().size() == 1) {
				final Player postman = SingletonRepository.getRuleProcessor()
						.getPlayer("postman");
				Set<Item> itemsOnGround = zone.getItemsOnGround();
				for (Item item : itemsOnGround) {
					// ignore items which are in the wastebin
					if (!(item.getX() == 2 && item.getY() == 5)) {
						boolean equippedToBag = ((RPEntity) entity).equip(
								"bag", item);
						if (equippedToBag) {
							// player may not have been online so use postman to
							// send info message
							if (postman != null) {
								postman
										.sendPrivateText("Dagobert tells you: tell "
												+ ((RPEntity) entity).getName()
												+ " The "
												+ Grammar.quantityplnoun(item
														.getQuantity(), item
														.getName())
												+ " which you left on the floor in the vault have been automatically "
												+ "returned to your bag.");
							}
						} else {
							boolean equippedToBank = ((RPEntity) entity).equip(
									"bank", item);
							if (equippedToBank) {
								if (postman != null) {
									postman
											.sendPrivateText("Dagobert tells you: tell "
													+ ((RPEntity) entity)
															.getName()
													+ " The "
													+ Grammar.quantityplnoun(
															item.getQuantity(),
															item.getName())
													+ " which you left on the floor in the vault have been automatically "
													+ "returned to your bank chest.");
								}
							} else {
								// the player lost their items
								if (postman != null) {
									postman
											.sendPrivateText("Dagobert tells you: tell "
													+ ((RPEntity) entity)
															.getName()
													+ " The "
													+ Grammar.quantityplnoun(
															item.getQuantity(),
															item.getName())
													+ " which you left on the floor in the vault have been thrown into "
													+ "the void, because there was no space to fit them into either your "
													+ "bank chest or your bag.");
								}
							}
						}
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

				SingletonRepository.getRPWorld().removeZone(zone);
			}
		}

		public void onMoved(final ActiveEntity entity,
				final StendhalRPZone zone, final int oldX, final int oldY,
				final int newX, final int newY) {

		}
	}

	@Override
	public void onFinish() throws Exception {
		this.remove(chest);

	}
}
