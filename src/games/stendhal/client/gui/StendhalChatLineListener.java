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
      String text = playerChatText.getText();
      text.trim();
      
      if(text[0]!='/')
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
            RPAction tell = new RPAction();
            tell.put("type","where");
            tell.put("target", command[1]);
            client.send(tell);
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
            RPAction tell = new RPAction();
            tell.put("type","addbuddy");
            tell.put("target", command[1]);
            client.send(tell);
            }
          }
        else if(text.startsWith("/remove ")) // Removes a existing buddy from buddy list
          {
          String[] command = parseString(text, 2);
          if(command != null)
            {
            RPAction tell = new RPAction();
            tell.put("type","removebuddy");
            tell.put("target", command[1]);
            client.send(tell);
            }
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
                          "- /where <player>          \tPrints the location of the player",
                          );
          StendhalClient.get().addEventLine(text,Color.green);
          gameObjects.addText(this, text, Color.green);
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
    
