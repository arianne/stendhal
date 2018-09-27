/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2004, 2015 Grzegorz Kowal
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice,
	   this list of conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors
	   may be used to endorse or promote products derived from this software without
	   specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
	AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package net.sf.launch4j.example;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class SimpleApp extends JFrame {
	public SimpleApp(String[] args) {
		super("Java Application");

		if (args.length == 1 && "throw".equals(args[0])) {
			throw new IllegalStateException("Exception thrown from SimpleApp.");
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds (screenSize.width / 4, screenSize.height / 4, screenSize.width / 2, screenSize.height / 2);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}});

		initializeMenu();
		initializeTextArea(args);
		setVisible(true);
	}

	public static void setLAF() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		System.setProperty("sun.awt.noerasebackground","true");
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.err.println("Failed to set LookAndFeel");
		}
	}

	public static void main(String[] args) {
		setLAF();
		new SimpleApp(args);
	}
   	
	private final void initializeMenu() {
		JMenu menu = new JMenu("Exit with code");
		
		menu.add(new JMenuItem(new AbstractAction("Exit with code 0") {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}));
		
		menu.add(new JMenuItem(new AbstractAction("Exit with code 100 and restart the application") {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(100);
			}
		}));
		
		JMenuBar mb = new JMenuBar();
		mb.setOpaque(true);
		mb.add(menu);
		setJMenuBar(mb);
   	}
   	
	private final void initializeTextArea(String[] args) {
   		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setViewportView(textArea);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    getContentPane().add(scrollPane);

		textArea.setText(getMainProperties(args));
		textArea.append(getAllProperties());
		textArea.append(getEnvironmentVariables());
   	}

	private final String getMainProperties(String[] args) {
		StringBuffer sb = new StringBuffer("Java version: ");
		sb.append(System.getProperty("java.version"));
		sb.append("\nJava home: ");
		sb.append(System.getProperty("java.home"));
		sb.append("\nCurrent dir: ");
		sb.append(System.getProperty("user.dir"));
		sb.append("\nCommand line args: {");

		for (int i = 0; i < args.length; i++) {
			if (i > 0)
			{
				sb.append(' ');
			}
			sb.append(args[i]);
		}

		sb.append("}\n");
		
		final int mb = 1024 * 1024;
		sb.append("Free memory (MB): ");
		sb.append(Runtime.getRuntime().freeMemory() / mb);
		sb.append("\nTotal memory (MB): ");
		sb.append(Runtime.getRuntime().totalMemory() / mb);
		sb.append("\nMax memory (MB): ");
		sb.append(Runtime.getRuntime().maxMemory() / mb);
		sb.append("\n");

		return sb.toString();
	}
   	
	private final String getAllProperties() {
		StringBuffer sb = new StringBuffer("\n========== All properties ==========\n");

		List<String> keys = new ArrayList<String>();
		keys.addAll(System.getProperties().stringPropertyNames());
		Collections.sort(keys);

		for (String key : keys) {
			sb.append(key);
			sb.append(": ");
			sb.append(System.getProperty(key));
			sb.append("\n");
		}

		return sb.toString();
	}
	
	private final String getEnvironmentVariables() {
		StringBuffer sb = new StringBuffer("\n========== Environment variables ==========\n");

		List<String> keys = new ArrayList<String>();
		keys.addAll(System.getenv().keySet());
		Collections.sort(keys);

		for (String key : keys) {
			sb.append(key);
			sb.append(": ");
			sb.append(System.getenv(key));
			sb.append("\n");
		}

		return sb.toString();
	}
}
