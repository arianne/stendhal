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
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * Represents a member of a Guild. A guild member consists of a player, a guild, 
 * and a GuildPermission. 
 * As only the Strings of player/guild names are stored all guilds are loaded
 * from their names (or separate id?).
 * @author timothyb89
 */
public class GuildMember extends Entity {

    /**
     * The attribute for the guild, stored as a string.
     */
    private static final String ATTR_GUILD = "guild";
    
    /**
     * The name of the player this GuildMember represents.
     */
    private static final String ATTR_PLAYER = "player";

    /**
     * The rank of the player in their guild.
     */
    private static final String ATTR_RANK = "rank";
    
    /**
     * The permission identifier (ex. "admin" for a guild admin)
     */
    private static final String ATTR_PERMISSION_ID = "permissionid";
    
    /**
     * The rpclass of this entity.
     */
    private static final String RPCLASS = "guild_member";
    
    private Player player;
//	private String playerName;
    private Guild guild;
    private GuildPermission permission;

    public GuildMember(final Player player, final Guild guild, final GuildPermission permission) {
        this.player = player;
        this.guild = guild;
        this.permission = permission;

        //build rpobject
        setRPClass(RPCLASS);
        store();
        put(ATTR_PLAYER, player.getName());
        put(ATTR_RANK, permission.getRank());
        put(ATTR_GUILD, guild.getName());
        put(ATTR_PERMISSION_ID, permission.getIdentifier());
    }
    
    public GuildMember(final RPObject obj) {
        super(obj);
        store();
        
        loadData();
    }

    public static void generateRPClass() {
        final RPClass clazz = new RPClass(RPCLASS);
        clazz.isA("entity");
        clazz.addAttribute(ATTR_PLAYER, Type.STRING, Definition.HIDDEN);
        clazz.addAttribute(ATTR_RANK, Type.INT, Definition.HIDDEN);
        clazz.addAttribute(ATTR_GUILD, Type.STRING, Definition.HIDDEN);
        clazz.addAttribute(ATTR_PERMISSION_ID, Type.STRING, Definition.HIDDEN);
    }
    
    private void loadData() {
//		playerName = get(ATTR_PLAYER);
    	guild = SingletonRepository.getGuildList().getGuild(get(ATTR_GUILD));
    	permission = guild.getPermission(getInt(ATTR_RANK));
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(final Guild guild) {
        this.guild = guild;
    }

    public GuildPermission getPermission() {
        return permission;
    }

    public void setPermission(final GuildPermission permission) {
        this.permission = permission;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }
}
