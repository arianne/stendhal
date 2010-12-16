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
package games.stendhal.client.gui.progress;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.InternalManagedWindow;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.common.constants.Actions;

import java.util.List;

import javax.swing.SwingUtilities;

import marauroa.common.game.RPAction;

public class ProgressLogController {
	/** Controller instance */
	private static ProgressLogController instance;
	
	/** Progress window */
	private final ProgressLog progressLog;
	
	/**
	 * Get the book controller instance.
	 * 
	 * @return controller instance
	 */
	public static synchronized ProgressLogController get() {
		if (instance == null) {
			instance = new ProgressLogController();
		}
		return instance;
	}
	
	/**
	 * Create a new ProgressLogController.
	 */
	private ProgressLogController() {
		progressLog = new ProgressLog("travel_log", "Travel log");
	}
	
	/**
	 * Show the travel log. If will be restored in the previous state
	 * if the window has been visible before. Otherwise it will be
	 * opened at the start page.
	 */
	public void show() {
		// unimplemented for now
	}
	
	/**
	 * Show available progress categories.
	 * 
	 * @param categories
	 */
	public void showCategories(final List<String> categories) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressLog.setContentsAvailable(false);
				progressLog.setPageCaption("Contents");
				
				// Prepare appropriate action for content clicks
				RequestAction contentAction = new RequestAction();
				contentAction.setDataKey("progress_type");
				
				progressLog.setPageContent(categories, contentAction);
				showWindow();
			}
		});
	}
	
	/**
	 * Show a list of items in a category.
	 * 
	 * @param category
	 * @param items
	 */
	public void showCategorySummary(final String category, final List<String> items) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressLog.setContentsAvailable(true);
				
				// Send a plain query if the user clicks the "Contents" header
				progressLog.setPageHeader("Contents", new RequestAction());
				progressLog.setPageCaption(category);
				
				// Prepare appropriate action for content clicks
				RequestAction contentAction = new RequestAction();
				contentAction.setDataKey("item");
				contentAction.setProgressType(category);
				
				progressLog.setPageContent(items, contentAction);
				showWindow();
			}
		});
	}
	
	/**
	 * Show the description of an item in a category.
	 * 
	 * @param category
	 * @param item item to be described
	 * @param description description paragraphs
	 */
	public void showDescription(final String category, final String item, final List<String> description) { 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressLog.setContentsAvailable(true);
				
				// Return to category listing when the user clicks the header
				RequestAction categoryAction = new RequestAction();
				categoryAction.setDataKey("progress_type");
				progressLog.setPageHeader(category, categoryAction);
				progressLog.setPageCaption(item);
				progressLog.setPageContent(description, null);
				showWindow();
			}
		});
	}
	
	/**
	 * Show the window, if it's not already visible. Also make it the
	 * topmost window.
	 */
	private void showWindow() {
		InternalManagedWindow window = progressLog.getWindow();
		if (window.getParent() == null) {
			j2DClient.get().addWindow(window);
			window.setVisible(true);
		}
		window.setMinimized(false);
		window.raise();
	}
	
	/**
	 * A class for storing progress query information.
	 */
	private static class RequestAction implements ProgressStatusQuery {
		private String progressType;
		private String item;
		private String dataKey;
		
		/**
		 * Send the progress query to the server.
		 * 
		 * @param data the value of dataKey, if set
		 */
		public void fire(String data) {
			RPAction action = new RPAction();
			action.put("type", Actions.PROGRESS_STATUS);
			if (progressType != null) {
				action.put("progress_type", progressType);
			}
			if (item != null) {
				action.put("item", item);
			}
			if (dataKey != null) {
				action.put(dataKey, data);
			}
			
			StendhalClient.get().send(action);
		}
		
		/**
		 * Set the action key whose value will be passed as a parameter to
		 * fire()
		 * 
		 * @param key
		 */
		void setDataKey(String key) {
			dataKey = key;
		}
		
		/**
		 * Set the "progress_type" value for the query.
		 * 
		 * @param type
		 */
		void setProgressType(String type) {
			progressType = type;
		}
	}
}
