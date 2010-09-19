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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

/**
 * Represents a specific Guild. This can have a name, a path to a URL where an
 * image is located, etc.
 * 
 * @author timothyb89
 */
public class Guild extends Entity {

	public static final int MAX_MEMBERS = 100;

	/**
	 * The guild name.
	 */
	private String name;

	/**
	 * The guild slogan.
	 */
	private String slogan;

	/**
	 * A url to the path of an image or logo representing the guild.
	 */
	private String imageURL;

	/**
	 * The members of the guild.
	 */
	private List<GuildMember> members;

	/**
	 * All of the permissions the guild has.
	 */
	private List<GuildPermission> permissions;

	/**
	 * The GuildPermission for admins. Admins and normal classes are required.
	 */
	private GuildPermission adminRank;

	/**
	 * The GuildPermission for normal users. This is required to create a guild.
	 */
	private GuildPermission normalRank;

	/**
	 * The identifier of the guild. This has no spaces, special characters, etc.
	 * This is used to link GuildMembers to Guilds, etc.
	 */
	private String identifier;

	private static final String RPCLASS = "guild";
	private static final String SLOT_MEMBERS = "members";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_SLOGAN = "slogan";
	private static final String ATTR_IMAGEURL = "image";
	private static final String ATTR_ADMINRANK = "adminrank";
	private static final String ATTR_NORMALRANK = "normalrank";
	private static final String ATTR_IDENTIFIER = "identifier";

	public Guild(final String name, final String slogan, final String imageURL, final GuildPermission adminRank, final GuildPermission normalRank,
			final String identifier) {
		this.name = name;
		this.slogan = slogan;
		this.imageURL = imageURL;
		this.adminRank = adminRank;
		this.normalRank = normalRank;

		members = new LinkedList<GuildMember>();
		permissions = new LinkedList<GuildPermission>();

		setRPClass(RPCLASS);
		store();
		put(ATTR_NAME, name);
		put(ATTR_SLOGAN, slogan);
		put(ATTR_IMAGEURL, imageURL);
		put(ATTR_ADMINRANK, adminRank.getIdentifier());
		put(ATTR_NORMALRANK, normalRank.getIdentifier());
		put(ATTR_IDENTIFIER, identifier);
	}

	public Guild(final RPObject obj) {
		super(obj);
		store();
		loadData();
	}

	public static void generateRPClass() {
		final RPClass clazz = new RPClass(RPCLASS);
		clazz.isA("entity");

		clazz.addRPSlot(SLOT_MEMBERS, MAX_MEMBERS, Definition.HIDDEN);

		clazz.addAttribute(ATTR_NAME, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(ATTR_SLOGAN, Type.LONG_STRING, Definition.HIDDEN);
		clazz.addAttribute(ATTR_IMAGEURL, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(ATTR_ADMINRANK, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(ATTR_NORMALRANK, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(ATTR_IDENTIFIER, Type.STRING, Definition.HIDDEN);
	}

	private void loadData() {
		permissions = SingletonRepository.getGuildPermissionList().getPermissionsForGuild(this);
		name = get(ATTR_NAME);
		slogan = get(ATTR_SLOGAN);
		imageURL = get(ATTR_IMAGEURL);
		adminRank = getRank(get(ATTR_ADMINRANK));
		normalRank = getRank(get(ATTR_NORMALRANK));
		identifier = get(ATTR_IDENTIFIER);

		// load members
		final RPSlot membersSlot = getSlot(SLOT_MEMBERS);
		for (final RPObject o : membersSlot) {
			members.add(new GuildMember(o));
		}
	}

	public GuildPermission getAdminRank() {
		return adminRank;
	}

	public void setAdminRank(final GuildPermission adminRank) {
		this.adminRank = adminRank;
		put(ATTR_ADMINRANK, adminRank.getIdentifier());
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
		put(ATTR_IDENTIFIER, identifier);
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(final String imageURL) {
		this.imageURL = imageURL;
		put(ATTR_IMAGEURL, imageURL);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
		put(ATTR_NAME, name);
	}

	public GuildPermission getNormalRank() {
		return normalRank;
	}

	public void setNormalRank(final GuildPermission normalRank) {
		this.normalRank = normalRank;
		put(ATTR_NORMALRANK, normalRank.getIdentifier());
	}

	public List<GuildPermission> getPermissions() {
		return permissions;
	}

	public GuildPermission getPermission(final String identifier) {
		for (final GuildPermission gp : getPermissions()) {
			if (gp.getIdentifier().equals(identifier)) {
				return gp;
			}
		}
		return null;
	}

	public GuildPermission getPermission(final int rank) {
		return GuildPermission.getPermission(rank, getPermissions());
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(final String slogan) {
		this.slogan = slogan;
		put(ATTR_SLOGAN, slogan);
	}

	public boolean isAdmin(final GuildMember member) {
		final int memberRank = member.getPermission().getRank();
		return memberRank == getAdminRank().getRank();
	}

	public GuildPermission getRank(final String identifier) {
		for (final GuildPermission g : permissions) {
			if (g.getIdentifier().equals(identifier)) {
				return g;
			}
		}
		return null;
	}

	public void addMember(final GuildMember m) {
		members.add(m);
		final RPSlot slot = getSlot(SLOT_MEMBERS);
		if (!slot.isFull()) {
			slot.add(m);
		}
	}

	public void removeMember(final GuildMember m) {
		members.remove(m);
		final RPSlot slot = getSlot(SLOT_MEMBERS);
		if (slot.has(m.getID())) {
			slot.remove(m.getID());
		}
	}

}
