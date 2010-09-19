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
package games.stendhal.server.core.rp.guilds;

import games.stendhal.server.entity.Entity;

import java.util.Collections;
import java.util.List;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * Manages permissions for a guild (ex. can assign a rank (like adminlevel) to a
 * GuildMember. This also defines some basic permissions, although creation 
 * later is possible, and can possibly be guild specific.
 * By default, all players with adminlevel > 800(?) get Admin rights in a guild
 * (not creator, however)
 * @author timothyb89
 */
public class GuildPermission extends Entity {

	//TODO: refactor this (durkham)
	private static boolean RPCLASS_GENERATED = false;
	
	/**
	 * Only a single user can be given this rank. It should have exactly the 
	 * same permissions as Admin, but show importance to the creator.
	 */
	public static final GuildPermission CREATOR = new GuildPermission("Creator", 2000);
	
	/**
	 * By default, this is the permission the guild's creator is given (see 
	 * CREATOR). An admin can do anything a moderator can do, but they have some
	 * additional abilities.
	 */
	public static final GuildPermission ADMIN = new GuildPermission("Administrator", 1000);
	
	/**
	 * A moderator can, for example, send messages to all guild members, as well
	 * as remove users (reversable by server/guild admin). Moderators can only 
	 * remove users without confirmation (as adding a user to a guild removes 
	 * them from any other guild they may be in)
	 */
	public static final GuildPermission MODERATOR = new GuildPermission("Moderator", 500);
	
	/**
	 * A normal user. This is the default rank given to a user when they first 
	 * join the guild. The can participate in guild chats, meetings, etc but can
	 * also invite other users into the guild (with confirmation from both the 
	 * invited player and a mod/admin).
	 */
	public static final GuildPermission DEFAULT = new GuildPermission("Normal", 100);
	
	/**
	 * The permission name. Mainly used for display.
	 */
	private String id;
	
	/**
	 * The permission rank. For all its worth, this functions primarily the same 
	 * as the admin system.
	 */
	private int rank;
	
	/**
	 * The guild this permission is used in.
	 */
	private String guild;
	
	private static final String RPCLASS = "guild_permission";
	private static final String ATTR_ID = "identifier";
	private static final String ATTR_RANK = "rank";
	private static final String ATTR_GUILD = "guild";

	public GuildPermission(final String id, final int rank) {
		this.id = id;
		this.rank = rank;

		if (!RPCLASS_GENERATED) {
			generateRPClass();
		}
		
		setRPClass(RPCLASS);
		store();
		put(ATTR_ID, id);
		put(ATTR_RANK, rank);
	}

	/**
	 * Constructs a GuildPermission.
	 * @param id An identifier for the permission.
	 * @param guild the identifier of the guild this permission is for
	 * @param rank The rank the permission gets.
	 */
	public GuildPermission(final String id, final String guild, final int rank) {
		this(id, rank);
		this.guild = guild;
		put(ATTR_GUILD, guild);
	}

	public GuildPermission(final RPObject obj) {
		super(obj);
		store();

		loadData();
	}

	public static void generateRPClass() {
		if (!RPCLASS_GENERATED) {
			final RPClass clazz = new RPClass(RPCLASS);
			clazz.isA("entity");
			clazz.addAttribute(ATTR_ID, Type.STRING, Definition.HIDDEN);
			clazz.addAttribute(ATTR_RANK, Type.INT, Definition.HIDDEN);
			clazz.addAttribute(ATTR_GUILD, Type.STRING, Definition.HIDDEN);
		}
		RPCLASS_GENERATED = true;
	}

	private void loadData() {
		id = get(ATTR_ID);
		rank = getInt(ATTR_RANK);
		guild = get(ATTR_GUILD);
	}

	public String getIdentifier() {
		return id;
	}

	public int getRank() {
		return rank;
	}

	public String getGuild() {
		return guild;
	}

	public static GuildPermission getPermission(final int rank, final List<GuildPermission> possible) {
		//sort by rank
		Collections.sort(possible, new GuildPermissionComparator()); 

		for (final GuildPermission gp : possible) {
			if (rank < gp.getRank()) {
				continue;
			} else {
				return gp;
			}
		}

		return DEFAULT; 
	}
}
