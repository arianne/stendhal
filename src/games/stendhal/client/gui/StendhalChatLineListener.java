package games.stendhal.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JTextField;
import java.util.*;
import marauroa.common.game.RPAction;
import games.stendhal.client.*;

import marauroa.common.Log4J;
import org.apache.log4j.Logger;

public class StendhalChatLineListener implements ActionListener, KeyListener
  {
  /** the logger instance. */
  private static final Logger logger = Log4J.getLogger(StendhalChatLineListener.class);

  private StendhalClient client;
  private JTextField playerChatText;
  private LinkedList<String> lines;
  private int actual;
  
  private String lastPlayerTell;
  
  public StendhalChatLineListener(StendhalClient client, JTextField playerChatText)
    {
    super();
    this.client=client;
    this.playerChatText=playerChatText;
    lines=new LinkedList<String>();
    actual=0;
    }

  private static String[] parseString(String s, int nbPart)
    {
    String[] res = new String[nbPart];
    String [] t;
    int i;
    s = s.trim();
    for(i=0;i<nbPart - 1;i++)
      {
      t = nextString(s);
      if(t == null) {
        return null;
      }
      res[i] = t[1];
      s = t[0].trim();
      }
    res[i] = s;
    return res;
    }

  private static String[] nextString(String from) {
    char[] cFrom = from.toCharArray();
    String[] res = new String[2];
    res[0] = "";
    res[1] = "";
    int quote = 0;
    char sep = ' ';
    int i=0;
    if(cFrom[0] == '\'') {
      quote = 1;
    }
    if(cFrom[0] == '"') {
      quote = 2;
    }
    if(quote != 0) {
      i++;
      sep = cFrom[0];
    }
    for(;i<cFrom.length;i++) {
      switch(quote) {
        case 0:
        case 1:
          if(cFrom[i] == sep) {
              res[0] = from.substring(i+1);
              return res;
          }
          res[1] += cFrom[i];
          break;
        case 2:
          if(cFrom[i]=='"')
            {
            res[0] = from.substring(i+1);
            return res;
            }
          else
            {
            i++;
            if(i==cFrom.length)
              {
              return null;
              }
            
            res[1] += cFrom[i];
            }
          break;
      }
    }
    if(quote == 0) {
      return res;
    }
    return null;
  }


      
    public void keyPressed(KeyEvent e) 
      {
      if(e.isShiftDown())
        {
        switch(e.getKeyCode())
          {
          case KeyEvent.VK_UP:
            {
            if(actual>0)
              {
              playerChatText.setText(lines.get(actual-1));
              actual--;          
              }
            break;
            }          
          case KeyEvent.VK_DOWN:   
            {
            if(actual<lines.size())
              {
              playerChatText.setText(lines.get(actual));
              actual++;          
              }
            break;
            }          
          }
        }
      }
        
    public void keyReleased(KeyEvent e) 
      {
      }
  
    public void keyTyped(KeyEvent e) 
      {
      }
  
    public void actionPerformed(ActionEvent e)
      {
      logger.debug ("Player wrote: "+playerChatText.getText());
      String text = playerChatText.getText();
      text = text.trim();
      
      if (text.length() == 0)
        return;
      
      if(text.charAt(0)!='/')
        {
        // Chat command. The most frequent one.
        RPAction chat=new RPAction();
        chat.put("type","chat");
        chat.put("text",playerChatText.getText());
        client.send(chat);
        }
      else
        {
        if(text.startsWith("//") && lastPlayerTell!=null)
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction tell = new RPAction();
            tell.put("type","tell");
            tell.put("target", lastPlayerTell);
            tell.put("text", command[1]);
            client.send(tell);
            }
          }
        if(text.startsWith("/tell ") ||text.startsWith("/msg ")) // Tell command
          {
          String[] command = parseString(text, 3);
          if(command != null)
            {
            RPAction tell = new RPAction();
            tell.put("type","tell");
            lastPlayerTell= command[1];
            tell.put("target", command[1]);
            tell.put("text", command[2]);
            client.send(tell);
            }
          }
        else if(text.startsWith("/support ")) // Support command
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction tell = new RPAction();
            tell.put("type","support");
            tell.put("text", command[1]);
            client.send(tell);
            }
          }
        else if(text.startsWith("/where ")) // Tell command
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction where = new RPAction();
            where.put("type","where");
            where.put("target", command[1]);
            client.send(where);
            }
          }
        else if(text.equals("/who")) // Who command
          {
          RPAction who = new RPAction();
          who.put("type","who");
          client.send(who);
          }
        else if(text.startsWith("/add ")) // Add a new buddy to buddy list
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction add = new RPAction();
            add.put("type","addbuddy");
            add.put("target", command[1]);
            client.send(add);
            }
          }
        else if(text.startsWith("/remove ")) // Removes a existing buddy from buddy list
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction remove = new RPAction();
            remove.put("type","removebuddy");
            remove.put("target", command[1]);
            client.send(remove);
            }
          }
        else if(text.startsWith("/tellall ")) // Tell everybody admin command
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction tellall = new RPAction();
            tellall.put("type","tellall");
            tellall.put("text", command[1]);
            client.send(tellall);
            }
          }
        else if(text.startsWith("/teleport ")) // Teleport target(PLAYER NAME) to  zone-x,y
          {
          String[] command = parseString(text, 5);
          if(command != null)
            {
            RPAction teleport = new RPAction();
            teleport.put("type","teleport");
            teleport.put("target", command[1]);
            teleport.put("zone", command[2]);
            teleport.put("x", command[3]);
            teleport.put("y", command[4]);
            client.send(teleport);
            }
          }
        else if(text.startsWith("/teleportto ")) // TeleportTo target(PLAYER NAME)
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction teleport = new RPAction();
            teleport.put("type","teleportto");
            teleport.put("target", command[1]);
            client.send(teleport);
            }
          }
        else if(text.startsWith("/alter ")) // Set/Add/Substract target(PLAYER NAME) attribute
          {
          String[] command = parseString(text, 5);
          if(command != null)
            {
            RPAction alter = new RPAction();
            alter.put("type","alter");
            alter.put("target", command[1]);
            alter.put("stat", command[2]);
            alter.put("mode", command[3]);
            alter.put("value", command[4]);
            client.send(alter);
            }
          }
        else if(text.startsWith("/summon ")) // Summon a creature at x,y
          {
          String[] command = parseString(text, 4);
          if(command != null)
            {
            RPAction summon = new RPAction();
            summon.put("type","summon");
            summon.put("creature", command[1]);
            summon.put("x", command[2]);
            summon.put("y", command[3]);
            client.send(summon);
            }
          }
        else if(text.startsWith("/summonat ")) // Summon a creature at x,y
          {
          String[] command;
          command = parseString(text, 5);

          if(command != null && !command[4].trim().equals(""))
            {
            RPAction summon = new RPAction();
            summon.put("type","summonat");
            summon.put("target", command[1]);
            summon.put("slot", command[2]);
            summon.put("item", command[3]);
            summon.put("amount", command[4]);
            client.send(summon);
            
            }
          else
            {
            command = parseString(text, 4);
            if(command != null)
              {
              RPAction summon = new RPAction();
              summon.put("type","summonat");
              summon.put("target", command[1]);
              summon.put("slot", command[2]);
              summon.put("item", command[3]);
              client.send(summon);
              }
            }
          }
        else if(text.startsWith("/inspect ")) // Returns a complete description of the target
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction add = new RPAction();
            add.put("type","inspect");
            add.put("target", command[1]);
            client.send(add);
            }
          }
        else if(text.startsWith("/invisible")) // Makes admin invisible for creatures
          {
          RPAction invisible = new RPAction();
          invisible.put("type","invisible");
          client.send(invisible);
          }
        else if(text.equals("/help")) // Help command
          {          
          String[] lines={"Detailed manual refer at http://arianne.sourceforge.net/wiki/index.php/StendhalManual",
                          "This brief help show you the most used commands:",
                          "- /tell <player> <message> \tWrites a private message to player",
                          "- // <message>             \tWrites a private message to last player we talked with",
                          "- /msg <player> <message>  \tWrites a private message to player",
                          "- /support <message>       \tAsk for support to admins",
                          "- /who                     \tShow online players",
                          "- /add <player>            \tAdd player to the buddy list",
                          "- /remove <player>         \tRemoves player from buddy list",
                          "- /where <player>          \tPrints the location of the player",
                          "- /sound volume <value>    \tsets sound system loudness (0..100)",
                          "- /sound mute <value>      \tsets sound system mute (on/off)"
                          };
          for(String line: lines)
            {
            StendhalClient.get().addEventLine(line,Color.gray);
            }
          }  

        else if(text.equals("/gmhelp")) // Help command
          {          
          String[] lines={"Detailed manual refer at http://arianne.sourceforge.net/wiki/index.php/StendhalManual#Admin",
                          "This brief help show you the most used commands:",
                          "- /tellall <message>                      \tWrites a private message to all players",
                          "- /teleport <player> <zone> <x> <y>       \tTeleport the player ",
                          "- /teleportto <player>                    \tTeleport us to the player ",
                          "- /alter <player> <attrib> <mode> <value> \tChange by SETting, ADDing or SUBstrating the stat of player",
                          "- /summon <creature|item> <x> <y>         \tSummon an item or creature at x,y",
                          "- /summonat <player> <slot> <ite> [amount]\tSummon an item at the slot of the given player",
                          "- /invisible                              \tMakes this player invisible for creatures",
                          };
          for(String line: lines)
            {
            StendhalClient.get().addEventLine(line,Color.gray);
            }
          } 
        
        else if(text.startsWith("/sound ")) // Sound Setup command
        {
           String[] command = parseString(text, 3);
           if ( command != null )
           {
              if ( command[1].equals( "mute" ) )
              {
                 SoundSystem.get().setMute( command[2].indexOf("on") != -1 );
              }
                    
              if ( command[1].equals( "volume" ) )
              {
                 int vol = Integer.parseInt(command[2]);
                 SoundSystem.get().setVolume( vol );
              }
           }
        }
        
        }

      lines.add(playerChatText.getText());
      actual=lines.size();
      
      if(lines.size()>50)
        {
        lines.remove(0);
        actual--;
        }       
      
      playerChatText.setText("");
      }
    }  
    
