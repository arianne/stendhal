package games.stendhal.client;

import games.stendhal.client.gui.*;

public class stendhal extends Thread
  {
  public static boolean doLogin=false;
  
  public static void main(String args[]) 
    {
    StendhalClient client=new StendhalClient();
    new StendhalFirstScreen(client);
    
    while(!doLogin)
      {
      try{Thread.sleep(200);}catch(Exception e){}
      }
    
    System.out.println ("Executing this part of code");
    new j2DClient(client);
    }    
  }
