/* CC-BY Hendrik Brummermann <nhb_web@nexgo.de>
 * (But becomes GPL because it is required to link against GPL code).
 */
package games.stendhal.bot.postman;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Player;
import games.stendhal.client.events.TalkEvent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Postman
 *
 * @author hendrik
 */
public class Postman implements Runnable {
    private static Postman postman = null;
    private static Logger logger = Logger.getLogger(Postman.class);
    private Properties messages = new Properties();
    
    /**
     * Singleton
     */
    private Postman() {
        // singleton
    }

    /**
     * Get the instance (Singleton-Pattern)
     *
     * @return Postman
     */
    public static synchronized Postman getPostman() {
        if (postman == null) {
            postman = new Postman();
            Thread t = new Thread(postman, "Postman");
            t.setPriority(Thread.MIN_PRIORITY);
            t.setDaemon(true);
            t.start();
            
            //shout("Please restart your client every hour or so to save your progress. We have some trouble with server crashes.");
            
            try {
                postman.messages.loadFromXML(new FileInputStream(System.getProperty("user.home") + "/.stendhal-postman.xml"));
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return postman;
    }

    /**
     * Processes a talk event.
     *
     * @param entity
     * @param base
     * @param diff
     */
    public void processTalkEvent(TalkEvent entity, RPObject base, RPObject diff) {
        try {
            if (diff == null) {
                //System.err.println("diff=null");
                return;
            }
            String greeting = "Hi, I am the postman. How can I #help you?";
            String intro = "I store messages for offline players and deliver them on login.\n";
            String helpMessage = "Usage:\n/msg postman help \t This help-message\n/msg postman tell #player #message \t I will deliver your #message when #player logs in.";
            if (diff.has("private_text") && (entity instanceof Player)) {
                if (((Player)entity).getName().equals("postman")) {
                    String text = diff.get("private_text");

                    java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
                    String dateString = formatter.format(new Date());
                    System.err.println(dateString + text);

                    StringTokenizer st = new StringTokenizer(text, " ");
                    String from = st.nextToken();
                    String arianneCmd = st.nextToken(); // tells
                    st.nextToken(); // you:
                    //System.out.println(text);
                    
                    if (arianneCmd.equals("tells")) {
                        // Command was send by a player 
                        String cmd = st.nextToken(); // cmd
                        if (cmd.startsWith("/")) {
                            cmd = cmd.substring(1);
                        }
                        if (cmd.equalsIgnoreCase("tell") || cmd.equalsIgnoreCase("msg") || cmd.equalsIgnoreCase("/tell") || cmd.equalsIgnoreCase("/msg")) {
                            onTell(from, st);
                        } else if (cmd.equalsIgnoreCase("hi")) {
                            tell(from, greeting); 
                        } else if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("job") || cmd.equalsIgnoreCase("letter") || cmd.equalsIgnoreCase("offer") || cmd.equalsIgnoreCase("parcel")) {
                            tell(from, intro + helpMessage);
                        } else if (cmd.equalsIgnoreCase("where")) {
                            onWhere();
                        } else {
                            tell(from, "Sorry, I did not understand you. (Did you forget the \"tell\"?)\n" + helpMessage);
                        }
                    } else if (arianneCmd.equals("Players")) {
                            onWhoResponse(st);
                    }
                }
    
            // Public message
            } else if (diff.has("text")) {
                if ((entity instanceof Player) && !((Player)entity).getName().equals("postman")) {
                    String text = diff.get("text");
                    String playerName = "";
                    if (entity instanceof Player) {
                        playerName = ((Player)entity).getName();
                    }
                    
                    java.text.Format formatter = new java.text.SimpleDateFormat("[HH:mm] ");
                    String dateString = formatter.format(new Date());
                    System.err.println(dateString + playerName + ": " + text);

                    StringTokenizer st = new StringTokenizer(text, " ");
                    String cmd = "";
                    if (st.hasMoreTokens()) {
                        cmd = st.nextToken();
                    }
                    if (cmd.equalsIgnoreCase("hi")) {
                        chat(greeting);
                    } else if (cmd.equalsIgnoreCase("bye")) {
                        chat("Bye.");
                    } else if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("info") || cmd.equalsIgnoreCase("job") || cmd.equalsIgnoreCase("offer") || cmd.equalsIgnoreCase("letter") || cmd.equalsIgnoreCase("parcel")) {
                        chat(intro + helpMessage);
                    } else if (cmd.equalsIgnoreCase("msg") || cmd.equalsIgnoreCase("tell")) {
                        onTell(playerName, st);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e, e);
        }
    }
    
    /**
     * response to "who"
     */
    private void onWhoResponse(StringTokenizer st) {
    	String lastUserPart = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            //System.err.println("Player: " + token);
            int pos = token.indexOf("(");
            if (pos < 0) {
            	lastUserPart = lastUserPart + " " + token;
            	continue;
            }
            String user = lastUserPart + token.substring(0, pos);
            lastUserPart = "";

            // Are there messages for this player? 
            Iterator itr = messages.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().toString();
                if (key.startsWith(user + "!")) {
                    String from = key.substring(key.indexOf("!") + 1);
                    String message = messages.getProperty(key);
                    if (from.equals(user)) {
                        from = "You";
                    }
                    tell(user, from + " asked me to deliver this message: \n" + message.trim());
                    itr.remove();
                    break; // workaround: Only the last message processed in one turn is delievered
                }
            }
        }

        // Save to disk
        try {
            messages.storeToXML(new FileOutputStream(System.getProperty("user.home") + "/.stendhal-postman.xml"), "This are the messages postman should deliver.");
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    private void onTell(String from, StringTokenizer st) {
        String param = null;
        String msg = null;
        //System.err.println("!" + from + "! !" + cmd + "! !" + msg + "!");
        if (st.hasMoreTokens()) {
            param = st.nextToken(); // player
        }
        if (st.hasMoreTokens()) {
            msg = st.nextToken("\0").trim(); // the rest of the message
        }
        String old = messages.getProperty(param + "!" + from);
        tell(from, "Message accepted for delivery.");
        if (old != null) {
            //tell(from, "I am discarding your previous message to " + param + ": " + old);
            msg = old + "\n" + msg;
        }
        messages.put(param + "!" + from, msg);

        // Save to disk
        try {
            messages.storeToXML(new FileOutputStream(System.getProperty("user.home") + "/.stendhal-postman.xml"), "This are the messages postman should deliver.");
        } catch (Exception e) {
            logger.error(e, e);
        }

    }

    private void onWhere() {
        RPAction who = new RPAction();
        who.put("type","who");
        StendhalClient.get().send(who);
    }

    
    private void tell(String to, String message) {
        if (to.equals("postman")) {
            logger.warn("I am not speaking to myself: " + message);
            return;
        }
        RPAction tell = new RPAction();
        tell.put("type","tell");
        tell.put("target", to);
        tell.put("text", message);
        StendhalClient.get().send(tell);
    }

    private void chat(String message) {
        RPAction chat=new RPAction();
        chat.put("type","chat");
        chat.put("text", message);
        StendhalClient.get().send(chat);
    }

    @SuppressWarnings("unused")
    private static void shout(String message) {
        RPAction chat=new RPAction();
        chat.put("type","tellall");
        chat.put("text", message);
        StendhalClient.get().send(chat);
    }

    
    public void run() {
        while (true) {
            RPAction who = new RPAction();
            who.put("type","who");
            StendhalClient.get().send(who);
            
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                logger.error(e, e);
            }
        }
    }
}
