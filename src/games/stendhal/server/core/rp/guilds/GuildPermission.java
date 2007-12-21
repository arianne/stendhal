package games.stendhal.server.core.rp.guilds;

import java.util.Collections;
import java.util.List;

/**
 * Manages permissions for a guild (ex. can assign a rank (like adminlevel) to a
 * GuildMember. This also defines some basic permissions, although creation 
 * later is possible, and can possibly be guild specific.
 * By default, all players with adminlevel > 800(?) get Admin rights in a guild
 * (not creator, however)
 * @author timothyb89
 */
public class GuildPermission {
    
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
    public static final GuildPermission NORMAL = new GuildPermission("Normal", 100);
    
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
     * Constructs a GuildPermission.
     * @param id An identifier for the permission.
     * @param rank The rank the permission gets.
     */
    public GuildPermission(String id, int rank) {
        this.id = id;
        this.rank = rank;
    }
    
    public String getID() {
        return id;
    }
    
    public int getRank() {
        return rank;
    }
    
    public static GuildPermission getPermission(int rank, List<GuildPermission> possible) {
        Collections.sort(possible, new GuildPermissionComparator());
        
        for (GuildPermission gp : possible) {
            if (rank < gp.getRank()) {
                continue;
            } else {
                return gp;
            }
        }
        
        return NORMAL; // default
    }
    
}
