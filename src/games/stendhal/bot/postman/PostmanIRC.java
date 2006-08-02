package games.stendhal.bot.postman;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

/**
 * IRC Appender
 *
 * @author hendrik
 */
public class PostmanIRC extends PircBot {

	/**
	 * Postman IRC bot.
	 *
	 * @throws IrcException 
	 * @throws IOException 
	 * @throws NickAlreadyInUseException 
	 */
	public void connect() throws NickAlreadyInUseException, IOException, IrcException {
		setName("postman-bot");
		setLogin("postman");
		setVersion("0.1");
	    setVerbose(true);
	    connect("irc.freenode.net");
	    joinChannel("#arianne");
	    // sendMessage("NickServ", "identify ");
	}

	public static void main(String[] main) throws NickAlreadyInUseException, IOException, IrcException {
	    // Now start our bot up.
		PostmanIRC bot = new PostmanIRC();
		bot.connect();
	}
}
