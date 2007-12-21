package games.stendhal.server.core.rp.guilds;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a specific Guild.
 * This can have a name, a path to a URL where an image is located, etc.
 * @author timothyb89
 */
public class Guild {
    
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
    private URL imageURL;
    
    /**
     * The members of the guild.
     */
    private List<GuildMember> members;
    
    /**
     * All of the permissions the guild has.
     */
    private List<GuildPermission> permissions;
    
    /**
     * The GuildPermission for admins. 
     * Admins and normal classes are required.
     */
    private GuildPermission adminRank;
    
    /**
     * The GuildPermission for normal users.
     * This is required to create a guild.
     */
    private GuildPermission normalRank;

    public Guild(String name, String slogan, URL imagePath, 
            GuildPermission adminRank, GuildPermission normalRank) {
        this.name = name;
        this.slogan = slogan;
        this.imageURL = imagePath;
        this.adminRank = adminRank;
        this.normalRank = normalRank;
        
        members = new LinkedList<GuildMember>();
        permissions = new LinkedList<GuildPermission>();
    }

    public GuildPermission getAdminRank() {
        return adminRank;
    }

    public void setAdminRank(GuildPermission adminRank) {
        this.adminRank = adminRank;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuildPermission getNormalRank() {
        return normalRank;
    }

    public void setNormalRank(GuildPermission normalRank) {
        this.normalRank = normalRank;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
    
    public boolean isAdmin(GuildMember member) {
        int memberRank = member.getPermission().getRank();
        if (memberRank == getAdminRank().getRank()) {
            return true;
        } else {
            return false;
        }
    }
    
}
