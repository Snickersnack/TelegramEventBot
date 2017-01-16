package org.wilson.telegram.templates;

import org.wilson.telegram.config.BotConfig;

public class EventView {

	public static final String TITLE = "<strong>Events shared to this channel</strong>"
			
			
			+ System.getProperty("line.separator") 
			+ System.getProperty("line.separator") 
			+"<i>Displaying top " + BotConfig.MAX_CHANNEL_VIEW + " events based on # of responses:</i>";
	public static final String SHOW = "Show";
	public static final String HIDE = "Hide";
}
