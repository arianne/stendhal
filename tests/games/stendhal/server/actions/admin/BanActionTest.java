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
package games.stendhal.server.actions.admin;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

@Ignore // TODO: undo bans, doing a rollback is not helpful because the bans are done by the BanAction using another transaction
public class BanActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for perform.
	 * @throws Throwable
	 */
	@Test
	public void testPerform() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);
			CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);
			BanAction ban = new BanAction();
			Player player = PlayerTestHelper.createPlayer("bob");
			RPAction action = new RPAction();
			action.put("target", player.getName());
			action.put("hours", -1);
			action.put("reason", "Because we were testing banning");

			// I don't know if this is still needed.
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}

			// we do a character check now. Made the the character name and the account name the same
			if (!characterDAO.hasCharacter(transaction, player.getName(), player.getName())) {
				characterDAO.addCharacter(transaction,  player.getName(), player.getName(), player, new Timestamp(new Date().getTime()));
			}

			System.out.println(DAORegister.get().get(CharacterDAO.class).getAccountName(transaction, player.getName()));
			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));

			TransactionPool.get().commit(transaction);

			ban.perform(player , action);

			transaction = TransactionPool.get().beginWork();

			assertEquals("banned", accountDAO.getAccountStatus(transaction, player.getName()));

			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

	/**
	 * Tests for commandCenterPerform.
	 * @throws Throwable
	 */
	@Test
	public void testCommandCenterPerform() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);
			CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);

			Player player = PlayerTestHelper.createPlayer("bobby");
			Player admin = PlayerTestHelper.createPlayer("admin");
			RPAction action = new RPAction();
			action.put("type", "ban");
			action.put("target", player.getName());
			action.put("hours",  -1);
			action.put("reason", "whynot");
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}

			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			if (!accountDAO.hasPlayer(transaction, admin.getName())) {
				accountDAO.addPlayer(transaction, admin.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}
			// we do a character check now. Made the the character name and the account name the same
	    	if (!characterDAO.hasCharacter(transaction, player.getName(), player.getName())) {
				characterDAO.addCharacter(transaction,  player.getName(), player.getName(), player, new Timestamp(new Date().getTime()));
	    	}

			accountDAO.setAccountStatus(transaction, admin.getName(), "active");

			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));

			TransactionPool.get().commit(transaction);

			assertFalse(CommandCenter.execute(admin , action));
			admin.clearEvents();
			admin.setAdminLevel(5000);
			assertTrue(CommandCenter.execute(admin , action));

			transaction = TransactionPool.get().beginWork();
			System.out.println(admin.events().get(0).toString());
			assertEquals("banned", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));
			assertFalse(admin.events().isEmpty());
			assertThat(admin.events().get(0).toString(), containsString("RPEvent private_text with Attributes of Class(private_text): "));
			assertThat(admin.events().get(0).toString(), containsString("[text=You have banned account bobby (character: bobby) until end of time for: whynot]"));
			assertThat(admin.events().get(0).toString(), containsString("[texttype=PRIVMSG]"));

			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

	/**
	 * Tests without the valid character
	 * @throws Throwable
	 */
	@Test
	public void testWithoutCharacter() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);

			Player player = PlayerTestHelper.createPlayer("bobbby");
			Player admin = PlayerTestHelper.createPlayer("admin");
			RPAction action = new RPAction();
			action.put("type", "ban");
			action.put("target", player.getName());
			action.put("hours",  -1);
			action.put("reason", "Because I can't type the correct name");
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}

			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			if (!accountDAO.hasPlayer(transaction, admin.getName())) {
				accountDAO.addPlayer(transaction, admin.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}

			accountDAO.setAccountStatus(transaction, admin.getName(), "active");

			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));

			TransactionPool.get().commit(transaction);

			assertFalse(CommandCenter.execute(admin , action));
			admin.clearEvents();
			admin.setAdminLevel(5000);
			assertTrue(CommandCenter.execute(admin , action));

			transaction = TransactionPool.get().beginWork();
			System.out.println(admin.events().get(0).toString());
			assertFalse("banned".equals(accountDAO.getAccountStatus(transaction, player.getName())));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));
			assertFalse(admin.events().isEmpty());
			assertThat(admin.events().get(0).toString(), containsString("RPEvent private_text with Attributes of Class(private_text): "));
			assertThat(admin.events().get(0).toString(), containsString("[text=No such character]"));
			assertThat(admin.events().get(0).toString(), containsString("[texttype=ERROR]"));

			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

	/**
	 * Tests for a temporary ban
	 * @throws Throwable
	 */
	@Test
	public void testTemporaryBan() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);
			CharacterDAO characterDAO = DAORegister.get().get(CharacterDAO.class);

			Player player = PlayerTestHelper.createPlayer("bobby");
			Player admin = PlayerTestHelper.createPlayer("admin");
			RPAction action = new RPAction();
			action.put("type", "ban");
			action.put("target", player.getName());
			action.put("hours",  1);
			action.put("reason", "We want to test the temporary bans");
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}

			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			if (!accountDAO.hasPlayer(transaction, admin.getName())) {
				accountDAO.addPlayer(transaction, admin.getName(), new byte[0], "schnubbel", new Timestamp(new Date().getTime()));
			}
			// we do a character check now. Made the the character name and the account name the same
	    	if (!characterDAO.hasCharacter(transaction, player.getName(), player.getName())) {
				characterDAO.addCharacter(transaction,  player.getName(), player.getName(), player, new Timestamp(new Date().getTime()));
	    	}

			accountDAO.setAccountStatus(transaction, admin.getName(), "active");

			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));

			TransactionPool.get().commit(transaction);

			assertFalse(CommandCenter.execute(admin , action));
			admin.clearEvents();
			admin.setAdminLevel(5000);
			assertTrue(CommandCenter.execute(admin , action));

			transaction = TransactionPool.get().beginWork();
			System.out.println(admin.events().get(0).toString());
			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));
			assertFalse(admin.events().isEmpty());
			assertThat(admin.events().get(0).toString(), containsString("RPEvent private_text with Attributes of Class(private_text): "));
			// not sure of a good way to reliably test the timestamping
			assertThat(admin.events().get(0).toString(), containsString("[text=You have banned account bobby (character: bobby) until "));
			assertThat(admin.events().get(0).toString(), containsString("for: We want to test the temporary bans"));
			assertThat(admin.events().get(0).toString(), containsString("[texttype=PRIVMSG]"));

			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}

}
