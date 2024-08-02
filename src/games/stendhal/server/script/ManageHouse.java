/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import static games.stendhal.server.maps.quests.houses.HouseTax.TAX_PAYMENT_PERIOD;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import games.stendhal.common.NotificationType;
import games.stendhal.server.constants.StandardMessages;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.impl.AbstractAdminScript;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.HouseBuying;
import games.stendhal.server.maps.quests.houses.HouseTax;
import games.stendhal.server.maps.quests.houses.HouseUtilities;
import games.stendhal.server.util.TimeUtil;


/**
 * Script for managing and debugging House Buying quest.
 */
public class ManageHouse extends AbstractAdminScript {

	/** Quest ID/name. */
	private static final String SLOT = "house";
	/** Player instance being updated. */
	private Player player;
	/** Housing tax manager instance. */
	private HouseTax houseTax;

	private static final List<String> COMMANDS = Arrays.asList(
		"status", "info", "inspect",
		"unset-house",
		"set-house",
		"set-lock",
		"set-unpaid"
	);


	@Override
	protected void run(final List<String> args) {
		final int argc = args.size();
		final String name = args.get(0);
		final String command = args.get(1).toLowerCase(Locale.ENGLISH);
		if (!checkCommand(command) || !checkParamCount(argc, command)) {
			return;
		}
		player = SingletonRepository.getRuleProcessor().getPlayer(name);
		if (player == null) {
			StandardMessages.playerNotOnline(admin, name);
			return;
		}
		final HouseBuying quest = (HouseBuying)
				SingletonRepository.getStendhalQuestSystem().getQuestFromSlot(ManageHouse.SLOT);
		if (quest == null) {
			admin.sendPrivateText(NotificationType.ERROR, "Quest \"" + ManageHouse.SLOT
					+ "\" not available.");
			return;
		}
		houseTax = quest.getHouseTax();

		if (Arrays.asList("status", "info", "inspect").contains(command)) {
			showStatus();
		} else if ("unset-house".equals(command)) {
			unsetHouse();
		} else if ("set-house".equals(command)) {
			final String city = String.join(" ", args.subList(2, argc-1));
			final Integer number = checkIntParam(args.get(argc-1), "<house-number>");
			if (number == null) {
				return;
			}
			setHouse(city, number);
		} else if ("set-lock".equals(command)) {
			final Integer number = checkIntParam(args.get(2), "<lock-number>");
			if (number == null) {
				return;
			}
			setLock(number);
		} else if ("set-unpaid".equals(command)) {
			final Integer periods = checkIntParam(args.get(2), "<count>");
			if (periods == null) {
				return;
			}
			setUnpaidTaxPeriods(periods);
		} else {
			StandardMessages.unknownCommand(admin, command);
			showUsage();
		}
	}

	/**
	 * Checks for a valid command.
	 *
	 * @param command
	 *   Command being executed.
	 * @return
	 *   {@code true} if the command is recognized.
	 */
	private boolean checkCommand(final String command) {
		if (!ManageHouse.COMMANDS.contains(command)) {
			StandardMessages.unknownCommand(admin, command);
			showUsage();
			return false;
		}
		return true;
	}

	/**
	 * Checks for correct parameter count for a specific command.
	 *
	 * @param argc
	 *   Total parameter count.
	 * @param command
	 *   Sub-command being executed.
	 * @return
	 *   {@code true} if parameter count is within min/max values.
	 */
	private boolean checkParamCount(final int argc, final String command) {
		final int min = getMinParams(command);
		final int max = getMaxParams(command);
		if (argc < min) {
			StandardMessages.missingParameter(admin, command);
			showUsage();
			return false;
		} else if (max > -1 && argc > max) {
			StandardMessages.excessParameter(admin, command);
			showUsage();
			return false;
		}
		return true;
	}

	/**
	 * Checks a string for number representation.
	 *
	 * @param value
	 *   String to be checked.
	 * @param param
	 *   Parameter name (e.g. <id>).
	 * @return
	 *   Numeric value of {@code value} or {@code null} if not a number.
	 */
	private Integer checkIntParam(final String value, final String param) {
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			StandardMessages.paramMustBeNumber(admin, param);
			showUsage();
		}
		return null;
	}

	/**
	 * Retrieves player's house.
	 *
	 * @return
	 *   House portal owned by player or {@code null}.
	 */
	private HousePortal getHouse() {
		return HouseUtilities.getPlayersHouse(player);
	}

	/**
	 * Displays housing status of player to admin.
	 */
	private void showStatus() {
		String msg = "Housing status for player " + player.getName() + ":";
		msg += "\n&nbsp;&nbsp;Quest slot: " + ManageHouse.SLOT;
		msg += "\n&nbsp;&nbsp;Quest state: " + player.getQuest(ManageHouse.SLOT);
		final boolean taxing = houseTax != null;
		msg += "\n&nbsp;&nbspTaxing status: " + (taxing ? "active" : "unavailable");
		msg += "\n&nbsp;&nbsp;Owns house: ";
		final HousePortal portal = getHouse();
		if (portal == null) {
			msg += "no";
		} else {
			msg += portal.getDoorId();
			msg += "\n&nbsp;&nbsp;Lock number: " + portal.getLockNumber();
			if (taxing) {
				final int periods = houseTax.getUnpaidTaxPeriods(portal);
				msg += "\n&nbsp;&nbsp;Unpaid tax periods: " + periods + " (" + houseTax.getTaxDebt(periods)
						+ " money)";
			}
			int secs = (int) ((portal.getExpireTime() - System.currentTimeMillis()) / 1000);
			msg += "\n&nbsp;&nbsp;Expiration: ";
			if (secs > 0) {
				final int days = Math.max(0, secs / TimeUtil.SECONDS_IN_DAY);
				secs %= TimeUtil.SECONDS_IN_DAY;
				final int hours = Math.max(0, secs / TimeUtil.SECONDS_IN_HOUR);
				secs %= TimeUtil.SECONDS_IN_HOUR;
				final int mins = Math.max(0, secs / TimeUtil.SECONDS_IN_MINUTE);
				secs %= TimeUtil.SECONDS_IN_MINUTE;
				msg += days + "d " + hours + "h " + mins + "m " + secs + "s";
			} else if (secs < 0) {
				msg += "expired";
			}
		}
		admin.sendPrivateText(msg);
	}

	/**
	 * Notifies player and admin about changes to quest slot state.
	 *
	 * @param oldState
	 *   Previous state of quest.
	 */
	private void onQuestChanged(final String oldState) {
		StandardMessages.changedQuestState(admin, player, ManageHouse.SLOT, oldState,
				player.getQuest(ManageHouse.SLOT));
	}

	/**
	 * Notifies player and admin about changes to house ownership.
	 *
	 * @param oldPortal
	 *   Owned house portal before change.
	 */
	private void onHouseChanged(final HousePortal oldPortal) {
		final String oldId = oldPortal != null ? oldPortal.getDoorId() : "(none)";
		final HousePortal portal = getHouse();
		final String newId = portal != null ? portal.getDoorId() : "(none)";
		if (!newId.equals(oldId)) {
			player.sendPrivateText(NotificationType.SUPPORT, "Admin " + admin.getTitle()
				+ " changed your house from '" + oldId + "' to '" + newId + "'");
		}
		showStatus();
	}

	/**
	 * Notifies player that housing debt has changed.
	 */
	private void onDebtChanged() {
		player.sendPrivateText(NotificationType.SUPPORT, "Admin " + admin.getTitle()
				+ " adjusted your housing status, you currently owe " + getCurrentDebt() + " in taxes.");
		showStatus();
	}

	/**
	 * Changes house lock and unsets owner.
	 *
	 * @param portal
	 *   House portal to be updated.
	 */
	private void repossess(final HousePortal portal) {
		if (portal != null) {
			// repossess house
			portal.changeLock();
			portal.setOwner("");
		}
	}

	/**
	 * Removes player housing status.
	 *
	 * FIXME: this should probably require a confirmation
	 */
	private void unsetHouse() {
		// NOTE: need to get portal before changing slot state
		final HousePortal oldPortal = getHouse();
		final String oldState = player.getQuest(ManageHouse.SLOT);
		player.setQuest(ManageHouse.SLOT, null);
		if (oldPortal == null) {
			admin.sendPrivateText("Player " + player.getName() + " does not own a house.");
			if (oldState != null) {
				onQuestChanged(oldState);
			}
			return;
		}
		HousePortal newPortal = getHouse();
		if (newPortal == null) {
			// DEBUG:
			System.out.println("repossed with setQuest: old portal owner: " + oldPortal.getOwner());

			// portal was repossessed with setQuest
			onHouseChanged(oldPortal);
			return;
		}
		// repossess old house
		repossess(oldPortal);
		newPortal = getHouse();
		if (newPortal != null) {
			admin.sendPrivateText(NotificationType.WARNING, "Failed to reposses old house.");
		}
		onHouseChanged(oldPortal);
	}

	/**
	 * Sets player housing status.
	 *
	 * NOTE: any keys player currently has will not work with house portal
	 *
	 * FIXME: this should probably require a confirmation
	 *
	 * @param city
	 *   Name of city where house is located.
	 * @param number
	 *   House ID/number.
	 */
	private void setHouse(final String city, final int number) {
		final String id = city + " " + number;
		final HousePortal portal = HouseUtilities.getHousePortal(id);
		if (portal == null) {
			admin.sendPrivateText(NotificationType.ERROR, "\"" + id + "\" does not exist");
			return;
		}

		// DEBUG:
		System.out.println("owner is null: " + (portal.getOwner() == null));

		final String name = player.getName();
		final String currentOwner = portal.getOwner();
		if (currentOwner != null && !"".equals(currentOwner)) {
			if (!name.equals(currentOwner)) {
				admin.sendPrivateText(NotificationType.ERROR, "\"" + id + "\" is already owned by "
						+ currentOwner);
				return;
			} else {
				admin.sendPrivateText(NotificationType.ERROR, "Player " + name + " already owns \""
						+ id + "\"");
				return;
			}
		}
		final HousePortal oldPortal = HouseUtilities.getPlayersHouse(player);
		// repossess old house
		repossess(oldPortal);
		portal.changeLock();
		portal.setOwner(name);
		// expiration must be set before update quest slot
		setExpiration(oldPortal != null ? oldPortal.getExpireTime() / 1000
				: System.currentTimeMillis() + (TAX_PAYMENT_PERIOD * 5));
		player.setQuest(ManageHouse.SLOT, String.valueOf(number));
		onHouseChanged(oldPortal);
	}

	/**
	 * Does nothing.
	 *
	 * @param number
	 *   New lock number.
	 */
	private void setLock(@SuppressWarnings("unused") final int number) {
		admin.sendPrivateText(NotificationType.WARNING, "Changing lock may be unsafe as it could make"
				+ " old keys work again. It would be better to update keys.");
		return;
		/*
		final HousePortal portal = getHouse();
		if (portal == null) {
			admin.sendPrivateText(NotificationType.ERROR, "Player does not own a house, cannot set lock.");
			return;
		}
		portal.put("lock_number", number);
		showStatus();
		*/
	}

	/**
	 * Sets expiration time of portal.
	 *
	 * @param secs
	 *   Amount of time in which portal expires. Use negative value for past due.
	 * @return
	 *   {@code true} if new expiration differs from old.
	 */
	private boolean setExpiration(final long secs) {
		if (houseTax == null) {
			admin.sendPrivateText(NotificationType.WARNING, "House taxing not active.");
		}
		final HousePortal portal = getHouse();
		if (portal == null) {
			admin.sendPrivateText("Player " + player.getName() + " does not own a house.");
			return false;
		}
		final long oldExpire = portal.getExpireTime();
		portal.setExpireTime(System.currentTimeMillis() + (secs * 1000));
		return portal.getExpireTime() != oldExpire;
	}

	/**
	 * Sets expiration time of portal.
	 *
	 * @param periods
	 *   Number of payment periods. Use negative value for past due.
	 * @return
	 *   {@code true} if new expiration differs from old.
	 */
	private boolean setExpiration(final int periods) {
		return setExpiration((long) (periods * TAX_PAYMENT_PERIOD));
	}

	/**
	 * Sets the number of months for which player owes taxes.
	 *
	 * @param periods
	 *   Number of payment periods.
	 */
	private void setUnpaidTaxPeriods(final int periods) {
		if (setExpiration(-periods)) {
			onDebtChanged();
		} else {
			admin.sendPrivateText("No changes.");
			showStatus();
		}
	}

	/**
	 * Retrieves the number of unpaid tax periods by player.
	 *
	 * @return
	 *   Tax periods.
	 */
	private int getUnpaidTaxPeriods() {
		return houseTax == null ? 0 : houseTax.getUnpaidTaxPeriods(player);
	}

	/**
	 * Calculates money currently owed in taxes by player.
	 *
	 * @return
	 */
	private int getCurrentDebt() {
		return houseTax == null ? 0 : houseTax.getTaxDebt(getUnpaidTaxPeriods());
	}

	@Override
	protected int getMinParams() {
		return 2;
	}

	/**
	 * Retrieves minimum number of parameters required for a command.
	 *
	 * @param command
	 *   Sub-command being executed.
	 * @return
	 *   Minimum parameter count.
	 */
	private int getMinParams(final String command) {
		if ("set-house".equals(command)) {
			return 4;
		} else if (Arrays.asList("set-unpaid", "set-lock").contains(command)) {
			return 3;
		}
		return getMinParams();
	}

	/**
	 * Retrieves maximum number of parameters required for a command.
	 *
	 * @param command
	 *   Sub-command being executed.
	 * @return
	 *   Maximum parameter count.
	 */
	private int getMaxParams(final String command) {
		if ("unset-house".equals(command)) {
			return 2;
		} else if (Arrays.asList("set-unpaid", "set-lock").contains(command)) {
			return 3;
		}
		return getMaxParams();
	}

	@Override
	protected List<String> getParamStrings() {
		return Arrays.asList(
			"<player> status",
			"<player> unset-house",
			"<player> set-house <city> <house-number>",
			"<player> set-lock <lock-number>",
			"<player> set-unpaid <count>"
		);
	}

	@Override
	protected List<String> getParamDetails() {
		return Arrays.asList(
			"player: Name of player.",
			"status: Show housing status.",
			"unset-house: Remove quest state, house, and tax status from player.",
			"set-house: Sets player's owned house.",
			"city: Name of city where house is located (spaces allowed).",
			"house-number: House ID/number.",
			"lock-number: Lock ID/number.",
			"count: Number of unpaid periods (months)."
		);
	}
}
