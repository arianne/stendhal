/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.curses;

import games.stendhal.bot.core.StandardClientFramework;
import games.stendhal.bot.textclient.TextClientFramework;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.server.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;

import jcurses.system.Toolkit;
import jcurses.util.Protocol;


public class CursesClient {
    private CursesWindow clientWindow;
    String username = null;
    String password = null;
    String character = null;
    String host = null;
    String port = "32160";
    boolean showWorld = false;
    boolean createAccount = false;
    
    public void connect(final String[] args) {
        try {
            if (args.length > 0) {
                int i = 0;

                while (i != args.length) {
                    if (args[i].equals("-u")) {
                        username = args[i + 1];
                    } else if (args[i].equals("-p")) {
                        password = args[i + 1];
                    } else if (args[i].equals("-c")) {
                        character = args[i + 1];
                    } else if (args[i].equals("-h")) {
                        host = args[i + 1];
                    } else if (args[i].equals("-P")) {
                        port = args[i + 1];
                    } else if (args[i].equals("-W")) {
                        if ("1".equals(args[i + 1])) {
                            showWorld = true;
                        }
                    } else if (args[i].equals("-a")) {
                        createAccount = true;
                    }
                    i++;
                }

                if (createAccount) {
                    username = "testuser" + StringUtils.generateStringOfCharacters(10);
                    password = username;
                    character = username;
                    System.out.println(username);
                }

                if ((username != null) 
                        && (character != null) && (host != null)
                        && (port != null)) {
                    if (password == null) {
                        System.out.print("Password: \u001B[37;47m");
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        password = br.readLine();
                        System.out.print("\u001B[m");
                        
                    }
                    System.out.println("Connecting");

                
                    return;
                }
            }

            System.out.println("Stendhal CursesClient");
            System.out.println();
            System.out.println("  games.stendhal.bot.curses.CursesClient -u username -p pass -h host -P port -c character");
            System.out.println();
            System.out.println("Required parameters");
            StandardClientFramework.printConnectionParameters();
            System.out.println("Optional parameters");
            System.out.println("* -W\tShow world content? 0 or 1");
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void startClient() throws Exception {
        System.setProperty("jcurses.protocol.filename", "jcurses.log");
        Protocol.activateChannel(Protocol.DEBUG);
        Protocol.debug("startClient()");
        clientWindow = new CursesWindow(0, 0, 
               Toolkit.getScreenWidth(), Toolkit.getScreenHeight(), 
               character + " - Stendhal");
        clientWindow.show();
    }
    
    public void gameLoop() throws SocketException {
        Protocol.debug("gameLoop()");
        new CursesUI(clientWindow);
        Protocol.debug("CursesUI initialized");
        TextClientFramework client = new TextClientFramework(host, username, password, character, port, showWorld, createAccount);
        Protocol.debug("TextClientFramework initialized");
        client.script();
        Protocol.debug("login script executed");
    }

    /**
     * starts the curses client
     *
     * @param args standard Stendhal command line parameters
     * @throws Exception in case of an unexpected error
     */
    public static void main(String[] args) throws Exception {
        CursesClient client = new CursesClient();
        SlashActionRepository.register();
        Protocol.debug("initialized SlashActionRepository");
        client.connect(args);
        client.startClient();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
			public void run() {
                Toolkit.shutdown();
            }
        });
        client.gameLoop();
    }

}
