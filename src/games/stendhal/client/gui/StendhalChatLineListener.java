package games.stendhal.client.gui;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.sound.SoundSystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JTextField;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

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
        if(text.startsWith("//"))
          {
          if(lastPlayerTell!=null)
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
          }
        else if(text.startsWith("/tell ") ||text.startsWith("/msg ")) // Tell command
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
        else if(text.startsWith("/supporta ") ||text.startsWith("/supportanswer "))
        {
        String[] command = parseString(text, 3);
        if(command != null)
          {
          RPAction tell = new RPAction();
          tell.put("type","supportanswer");
          tell.put("target", command[1]);
          tell.put("text", command[2]);
          client.send(tell);
          }
        }
        else if(text.startsWith("/where "))
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
        else if(text.startsWith("/drop ")) // Drop command
          {
          String[] command = parseString(text, 3);
          if(command != null)
            {
            String itemName = command[2];
            int quantity;
      
            try 
              {
              quantity = Integer.parseInt(command[1]);
              } 
            catch (NumberFormatException ex) 
              {
              return;
              }
            RPObject player = client.getPlayer();
            int itemID = -1;
            for(RPObject item: player.getSlot("bag"))
              {
              if (item.get("name").equals(itemName)) 
                {
                itemID = item.getID().getObjectID();
                break;
                }
              }
            if (itemID != -1) 
              {
              RPAction drop = new RPAction();
              drop.put("type", "drop");
              drop.put("baseobject", player.getID().getObjectID());
              drop.put("baseslot", "bag");
              drop.put("x", player.get("x"));
              drop.put("y", player.get("y"));
              drop.put("quantity", quantity);
              drop.put("baseitem", itemID);
              client.send(drop);
              } 
            else 
              {
              client.addEventLine("You don't have any "+itemName, Color.black);
              }
            }
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
        else if(text.startsWith("/adminlevel ")) // Display or adjust adminlevel
        {
        String[] command = parseString(text, 3);

        if(command != null)
          {
          RPAction adminlevel = new RPAction();
          adminlevel.put("type","adminlevel");
          adminlevel.put("target", command[1]);
          if (!command[2].trim().equals("")) {
              adminlevel.put("newlevel", command[2]);
          }
          client.send(adminlevel);
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
        else if(text.startsWith("/summonat ")) // Summon an item in a slot
          {
          String[] command = parseString(text, 5);
          if(command != null)
            {
            RPAction summon = new RPAction();
            summon.put("type","summonat");
            summon.put("target", command[1]);
            summon.put("slot", command[2]);
            summon.put("item", command[3]);
            if (!command[4].trim().equals("")) {
                summon.put("amount", command[4]);
            }
            client.send(summon);
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

        else if(text.startsWith("/jail ")) // Returns a complete description of the target
          {
          String[] command = parseString(text, 3);
          if(command != null)
            {
            RPAction add = new RPAction();
            add.put("type","jail");
            add.put("target", command[1]);
            add.put("minutes", command[2]);
            client.send(add);
            }
          }

        else if(text.startsWith("/quit"))
          {
          client.getGameGUI().showQuitDialog();
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
                          "- /msg <player> <message> \tWrites a private message to player",
                          "- // <message> \t\tWrites a private message to last player we talked with",
                          "- /support <message> \tAsk for support to admins",
                          "- /who \t\tShow online players",
                          "- /drop <quantity> <item>\tDrops a amount of items from player.",
                          "- /add <player> \t\tAdd player to the buddy list",
                          "- /remove <player> \tRemoves player from buddy list",
                          "- /where <player> \t\tPrints the location of the player",
                          "- /quit \t\tLeaves the game",
                          "- /sound volume <value> \tsets sound system loudness (0..100)",
                          "- /sound mute <value> \tsets sound system mute (on/off)"
                          };
          for(String line: lines)
            {
            StendhalClient.get().addEventLine(line,Color.gray);
            }
          }  
        else if(text.equals("/gmhelp")) // Help command
          {          
          String[] lines={"Detailed manual refer at http://arianne.sourceforge.net/wiki/index.php?title=Stendhal:Administration",
                          "This brief help show you the most used gm commands:",
                          "- /adminlevel <player> [<newlevel>] \t\t displays or adjusts adminlevel",
                          "- /tellall <message> \t\tWrites a private message to all players",
                          "- /jail <player> \t\tSend a player directly to jail",
                          "- /script <scriptname> \t\tload or reload a server side groovy script",
                          "- /teleport <player> <zone> <x> <y> \tTeleport the player ",
                          "- /teleportto <player> \t\tTeleport us to the player ",
                          "- /alter <player> <attrib> <mode> <value> \tChange by SETting, ADDing or SUBtracting the stat of player",
                          "- /summon <creature|item> <x> <y> \tSummon an item or creature at x,y",
                          "- /summonat <player> <slot> <item> <amount> Summon an item at the slot of the given player",
                          "- /invisible \t\t\tMakes this player invisible for creatures",
                          "- /inspect <player> \t\t\tShows detailed info about the player",
                          "- /destroy <entity> \t\t\tDestroy completly an entity."
                          };
          for(String line: lines)
            {
            StendhalClient.get().addEventLine(line,Color.gray);
            }
          } 
        
        else if(text.equals("/gmhelp_alter")) // Help command
          {          
          String[] lines={"/alter <player> <attrib> <mode> <value> \tChange by SETting, ADDing or SUBtracting the stat of player",
                          "Some of the values that can be changed is: atk, atk_xp, def, def_xp, xp, outfit",
                          "When modifying the outfit SET a 8 digit number, where the first 2 is the HAIR, second 2 is HEAD, third 2 is OUTFIT and last 2 is BODY",
		  	  "Example: /alter player outfit set 12109901",
			  "This example will make the player 'player' look like danter."
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

        // unhandled /command, may be a ServerExtension command
        else if(text.startsWith("/"))
          {
          boolean hasTarget = text.indexOf(" ")>0;
          int parms = 2;
          if(hasTarget)
            {
            parms = 3;
            }
          String[] command = parseString(text, parms);
          if(command != null)
            {
            RPAction extension = new RPAction();
            extension.put("type", command[0].substring(1));
            if(hasTarget)
              {
              extension.put("target", command[1]);
              extension.put("args", command[2]);
              }
            client.send(extension);
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
