package games.stendhal.server.core.rp.guilds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates certain parts of a guild with a regex, such as the name, a 
 * permission id, etc.
 * @author timothyb89
 */
public class GuildAttributeValidator {
    
    private static String guildIDRegex = "[a-zA-Z0-9]{4,30}+";
    private static String guildNameRegex = "[\\w!& ]{4,30}+";
    private static String guildSloganRegex = "[\\w!-&() ]{4, 150}+";
    
    public static boolean validateGuildID(String id) {
        if (matches(guildIDRegex, id)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean validateGuildName(String name) {
        if (matches(guildNameRegex, name)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean validateGuildSlogan(String slogan) {
        if (matches(guildSloganRegex, slogan)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean matches(String regex, String context) {
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(context);
        return mat.matches();
    }
    
}
