package games.stendhal.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JTextField;
import java.util.LinkedList;
import marauroa.common.game.RPAction;
import games.stendhal.client.*;

public class StendhalChatLineListener implements ActionListener, KeyListener
    {
    private StendhalClient client;
    private JTextField playerChatText;
    private LinkedList<String> lines;
    private int actual;
    
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
      System.out.println ("Player wrote: "+playerChatText.getText());
      String text = playerChatText.getText();
      text.trim();
      
      if(text.charAt(0)!='/')
        {
        // Chat command. The most frecuent one.
        RPAction chat=new RPAction();
        chat.put("type","chat");
        chat.put("text",playerChatText.getText());
        client.send(chat);
        }
      else
        {
        if(text.startsWith("/tell ") ||text.startsWith("/msg ")) // Tell command
          {
          String[] command = parseString(text, 3);
          if(command != null)
            {
            RPAction tell = new RPAction();
            tell.put("type","tell");
            tell.put("target", command[1]);
            tell.put("text", command[2]);
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
                          "- /msg <player> <message>  \tWrites a private message to player",
                          "- /who                     \tShow online players",
                          "- /add <player>            \tAdd player to the buddy list",
                          "- /remove <player>         \tRemoves player from buddy list",
                          "- /where <player>          \tPrints the location of the player"
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
                          "- /tellall <message>                     \tWrites a private message to all players",
                          "- /teleport <player> <zone> <x> <y>      \tTeleport the player ",
                          "- /alter <player> <attrib> <mode> <value>\tChange by SETting, ADDing or SUBstrating the stat of player",
                          "- /summon <creature|item> <x> <y>        \tSummon and item or creature at x,y",
                          "- /invisible                             \tMakes this player invisible for creatures",
                          };
          for(String line: lines)
            {
            StendhalClient.get().addEventLine(line,Color.gray);
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
    
