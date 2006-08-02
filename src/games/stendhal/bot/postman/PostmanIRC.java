package games.stendhal.bot.postman;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

/**
 * IRC Appender
 *
 * @author hendrik
 */
public class PostmanIRC extends PircBot {
	private static Logger logger = Logger.getLogger(PostmanIRC.class);
	private Properties prop = new Properties();
	
	public PostmanIRC() {
        try {
            this.prop.loadFromXML(new FileInputStream(System.getProperty("user.home") + "/.stendhal-postman-conf.xml"));
        } catch (Exception e) {
            logger.error(e, e);
        }
	}

	/**
	 * Postman IRC bot.
	 *
	 * @throws IrcException 
	 * @throws IOException 
	 * @throws NickAlreadyInUseException 
	 */
	public void connect() throws NickAlreadyInUseException, IOException, IrcException {
		setName(prop.getProperty("name"));
		setLogin(prop.getProperty("login"));
		setVersion("0.1");
	    setVerbose(true);
	    connect("irc.freenode.net");
	    joinChannel("#arianne");
	    joinChannel("#arianne-support");
	    String pass = prop.getProperty("pass");
	    sendMessage("NickServ", "identify " + pass);
	}

	public static void main(String[] main) throws NickAlreadyInUseException, IOException, IrcException {
	    // Now start our bot up.
		PostmanIRC bot = new PostmanIRC();
		bot.connect();
	}
}
