package games.stendhal.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import marauroa.common.game.*;

public class StendhalKeyInputHandler extends KeyAdapter 
  {
  private Map<Integer, Object> pressed;
  private StendhalClient client;
  private boolean pressedESC;
  
  public StendhalKeyInputHandler(StendhalClient client)
    {
    super();
    this.client=client;
    pressed=new HashMap<Integer,Object>();
    }
    
  public boolean isExitRequested()
    {
    return pressedESC;
    }
     
  public void onKeyPressed(KeyEvent e)  
    {
    RPAction action=new RPAction();
    action.put("type","move");
    
    switch(e.getKeyCode())
      {
      case KeyEvent.VK_LEFT:
        action.put("dx",-0.5);
        break;
      case KeyEvent.VK_RIGHT:
        action.put("dx",0.5);
        break;
      case KeyEvent.VK_UP:
        action.put("dy",-0.5);
        break;
      case KeyEvent.VK_DOWN:      
        action.put("dy",0.5);
        break;
      }

    if(action.has("dx") || action.has("dy"))
      {
      System.out.println("Sending action: "+action);            
      client.send(action);
      }    
    }
    
  public void onKeyReleased(KeyEvent e)  
    {
    RPAction action=new RPAction();
    action.put("type","move");
    
    switch(e.getKeyCode())
      {
      case KeyEvent.VK_LEFT:
        action.put("dx",0);
        break;
      case KeyEvent.VK_RIGHT:
        action.put("dx",0);
        break;
      case KeyEvent.VK_UP:
        action.put("dy",0);
        break;
      case KeyEvent.VK_DOWN:      
        action.put("dy",0);
        break;
      }

    if(action.has("dx") || action.has("dy"))
      {
      System.out.println("Sending action: "+action);            
      client.send(action);
      }    
    }
    
  public void keyPressed(KeyEvent e) 
    {
    if(!pressed.containsKey(new Integer(e.getKeyCode())))
      {
      onKeyPressed(e);
      pressed.put(new Integer(e.getKeyCode()),null);
      }      
    }
      
  public void keyReleased(KeyEvent e) 
    {
    onKeyReleased(e);
    pressed.remove(new Integer(e.getKeyCode()));
    }

  public void keyTyped(KeyEvent e) 
    {
    // if we hit escape, then quit the game
    if (e.getKeyChar() == 27) 
      {
      client.requestLogout();
      }
    }
  }
