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
    
    public static boolean validateGuildID(final String id) {
        return matches(guildIDRegex, id);
    }
    
    public static boolean validateGuildName(final String name) {
        return matches(guildNameRegex, name);
    }
    
    public static boolean validateGuildSlogan(final String slogan) {
        return matches(guildSloganRegex, slogan);
    }
    
    public static boolean matches(final String regex, final String context) {
        final Pattern pat = Pattern.compile(regex);
        final Matcher mat = pat.matcher(context);
        return mat.matches();
    }
    
}
