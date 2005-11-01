/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.net.*;
import java.util.*;

import marauroa.client.*;
import marauroa.client.net.*;
import marauroa.common.game.*;
import marauroa.common.net.*;

public class textClient extends Thread
  {
  private String host;
  private String username;
  private String password;
  private String character;
  
  private Map<RPObject.ID,RPObject> world_objects;
  // TODO: never used? remove me
  // private RPObject myRPObject;

  private marauroa.client.ariannexp clientManager;
  private PerceptionHandler handler;

  public textClient(String h, String u, String p, String c) throws SocketException
    {
    host=h;
    username=u;
    password=p;
    character=c;
    
    world_objects=new HashMap<RPObject.ID, RPObject>();

    handler=new PerceptionHandler(new DefaultPerceptionListener()
      {
      public int onException(Exception e, marauroa.common.net.MessageS2CPerception perception)      
        {
        e.printStackTrace();
        System.out.println(perception);
        return 0;
        }
        
      public boolean onMyRPObject(boolean changed,RPObject object)
        {
        if(changed)
          {
          // TODO: never used? remove me
          // myRPObject=object;
          }
            
        return false;
        }
      });
    
    clientManager=new marauroa.client.ariannexp("games/stendhal/log4j.properties")
      {
      protected String getGameName()
        {
        return "stendhal";
        }
        
      protected String getVersionNumber()
        {
        return stendhal.VERSION;
        }    
      
      protected void onPerception(MessageS2CPerception message)
        {
        try
          {
          handler.apply(message,world_objects);
          int i=message.getPerceptionTimestamp();
    
          RPAction action=new RPAction();
          if(i%50==0)
            {
            action.put("type","move");
            action.put("dy","-1");
            clientManager.send(action);
            }
          else if(i%50==20)
            {
            action.put("type","move");
            action.put("dy","1");
            clientManager.send(action);
            }
              

          System.out.println("<World contents ------------------------------------->");
          int j=0;
          for(RPObject object: world_objects.values())
             {
             j++;
             System.out.println(j+". "+object);
             }
          System.out.println("</World contents ------------------------------------->");
          
          }
        catch(Exception e)
          {
          onError(3,"Exception while applying exception");
          }
        }
        
      protected List<TransferContent> onTransferREQ(List<TransferContent> items)
        {
        for(TransferContent item: items)
          {
          item.ack=true;
          }
        
        return items;
        }
        
      protected void onTransfer(List<TransferContent> items)
        {
        System.out.println("Transfering ----");
        for(TransferContent item: items)
          {
          System.out.println(item);
          for(byte ele: item.data) System.out.print((char)ele);
          }          
        }
     
      protected void onAvailableCharacters(String[] characters)
        {
        System.out.println("Characters available");
        for(String characterAvail: characters)
          {
          System.out.println(characterAvail);
          }
        
        try
          {
          chooseCharacter(character);
          }
        catch(Exception e)
          {
          e.printStackTrace();          
          }
        }
        
      protected void onServerInfo(String[] info)
        {
        System.out.println("Server info");
        for(String info_string: info)
          {
          System.out.println(info_string);
          }
        }

      protected void onError(int code, String reason)
        {
        System.out.println(reason);
        }
      };
    
    }
   
  public void run()
    {
    try
      {
      clientManager.connect(host,32160);      
      clientManager.login(username,password);
      }
    catch(SocketException e)
      {
      return;
      }
    catch(ariannexpTimeoutException e)
      {
      System.out.println ("textClient can't connect to Stendhal server. Server is down?");
      return;
      }

    boolean cond=true;

    while(cond)  
      {
      clientManager.loop(0);
      try{sleep(100);}catch(InterruptedException e){};
      }
    
    while(clientManager.logout()==false);
    }
  
  public static void main (String[] args)
    {
    try
      {
      if(args.length>0)
        {
        int i=0;
        String username=null;
        String password=null;
        String character=null;
        String host=null;
    
        while(i!=args.length)
          {
          if(args[i].equals("-u"))
            {
            username=args[i+1];
            }
          else if(args[i].equals("-p"))
            {
            password=args[i+1];
            }
          else if(args[i].equals("-c"))
            {
            character=args[i+1];
            }
          else if(args[i].equals("-h"))
            {
            host=args[i+1];
            }
          i++;
          }        
          
        if(username!=null && password!=null && character!=null && host!=null)
          {
          System.out.println("Parameter operation");
          new textClient(host,username,password,character).start();
          return;
          }
        }

      System.out.println("Stendhal textClient");
      System.out.println();
      System.out.println("  games.stendhal.textClient -u username -p pass -h host -c character");
      System.out.println();
      System.out.println("Required parameters");
      System.out.println("* -h\tHost that is running Marauroa server");
      System.out.println("* -u\tUsername to log into Marauroa server");
      System.out.println("* -p\tPassword to log into Marauroa server");
      System.out.println("* -c\tCharacter used to log into Marauroa server");
      }
    catch(Exception e)
      {
      e.printStackTrace();
      System.exit(1);
      }
    }
  }
  
