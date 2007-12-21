package games.stendhal.server.core.rp.guilds;

import games.stendhal.server.entity.player.Player;

/**
 * Represents a member of a Guild. A guild member consists of a player, a guild, 
 * and a GuildPermission.
 * @author timothyb89
 */
public class GuildMember {
    
    private Player player;
    
    private Guild guild;
    private GuildPermission permission;

    public GuildMember(Player player, Guild guild, GuildPermission permission) {
        this.player = player;
        this.guild = guild;
        this.permission = permission;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public GuildPermission getPermission() {
        return permission;
    }

    public void setPermission(GuildPermission permission) {
        this.permission = permission;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
}
