package games.stendhal.server.core.engine.generateini;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import marauroa.common.crypto.RSAKey;

public class ServerIniConfigurationTest extends TestCase {

    private static final Date FIXED_DATE  = new Date(0);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
    private static final BigInteger N = new BigInteger("27589337880622167149566855031201078241218568900664800652168408894520896214784592456421102448544305731046309374186279143629838201421190157282512920346888296230131072115446956972775880311738630492239167618359336655119023913362723476989557322720989568156648633458184618695333136566311334027924114681361069710488041661");
    private static final BigInteger D = new BigInteger("20232181112456255909682360356214124043560283860487520478256833189315323890842034468042141795599157536100626874403271371995214681042206115340509474921051417227725705013448998220265389087719392924733983760957442260014978024520655221185504022062734688680546685592911391587811226309898115759790087662518564367650476571");
    private static final BigInteger E = new BigInteger("15");

    public void testWrite() {
        DatabaseConfiguration db = new H2DatabaseConfiguration();
        RSAKey key = new RSAKey(N, D, E);
        ServerIniConfiguration cfg = new ServerIniConfiguration(db, key, FIXED_DATE);
        StringWriter stringWriter = new StringWriter();
        cfg.write(new PrintWriter(stringWriter));
        assertEquals(
            "# Generated .ini file for Test Game at " + DATE_FORMAT.format(FIXED_DATE) + System.lineSeparator()
            + "# Database and factory classes. Don't edit." + System.lineSeparator()
            + "database_implementation=games.stendhal.server.core.engine.StendhalPlayerDatabase" + System.lineSeparator()
            + "factory_implementation=games.stendhal.server.core.engine.StendhalRPObjectFactory" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "# Database information. Edit to match your configuration." + System.lineSeparator()
            + "database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter" + System.lineSeparator()
            + "jdbc_url=jdbc:h2:~/stendhal/database/h2db;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE" + System.lineSeparator()
            + "jdbc_class=org.h2.Driver" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "# TCP port stendhald will use. " + System.lineSeparator()
            + "tcp_port=32160" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "# World and RP configuration. Don't edit." + System.lineSeparator()
            + "world=games.stendhal.server.core.engine.StendhalRPWorld" + System.lineSeparator()
            + "ruleprocessor=games.stendhal.server.core.engine.StendhalRPRuleProcessor" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "turn_length=300" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "server_typeGame=stendhal" + System.lineSeparator()
            + "server_name=stendhal Marauroa server" + System.lineSeparator()
            + "server_version=1.41.5" + System.lineSeparator()
            + "server_contact=https://sourceforge.net/tracker/?atid=514826&group_id=66537&func=browse" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "# Extensions configured on the server. Enable at will." + System.lineSeparator()
            + "#server_extension=xxx" + System.lineSeparator()
            + "#xxx=some.package.Classname" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "statistics_filename=./server_stats.xml" + System.lineSeparator()
            + "" + System.lineSeparator()
            + "n = 27589337880622167149566855031201078241218568900664800652168408894520896214784592456421102448544305731046309374186279143629838201421190157282512920346888296230131072115446956972775880311738630492239167618359336655119023913362723476989557322720989568156648633458184618695333136566311334027924114681361069710488041661" + System.lineSeparator()
            + "e = 15" + System.lineSeparator()
            + "d = 20232181112456255909682360356214124043560283860487520478256833189315323890842034468042141795599157536100626874403271371995214681042206115340509474921051417227725705013448998220265389087719392924733983760957442260014978024520655221185504022062734688680546685592911391587811226309898115759790087662518564367650476571" + System.lineSeparator(),
            stringWriter.toString());
    }

}
