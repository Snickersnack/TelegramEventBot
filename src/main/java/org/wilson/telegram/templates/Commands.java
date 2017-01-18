package org.wilson.telegram.templates;

import org.wilson.telegram.config.BotConfig;

/**
 * Static list of available commands
 * 
 */

public final class Commands {
	

    public static final String INIT_CHAR= "/";
    public static final String HELPCOMMAND = INIT_CHAR + "help";
    public static final String MENUCOMMAND = INIT_CHAR + "menu";
    public static final String STARTCOMMAND = INIT_CHAR + "start";
    public static final String DELETEEVENTSCOMMAND = INIT_CHAR + "delete";
    public static final String CLEAREVENTSCOMMAND = INIT_CHAR + "clearevents";
    public static final String TESTCOMMAND = INIT_CHAR + "test";
    public static final String VIEWCOMMAND = INIT_CHAR + "view";
    public static final String EDITCOMMAND = INIT_CHAR + "edit";
    public static final String CANCELCOMMAND = INIT_CHAR + "cancel";
    public static final String RESPONDEESCOMMAND = INIT_CHAR + "respondees";
    public static final String SKIPCOMMAND = INIT_CHAR + "skip";

    
    public static final String HELPTEXT =  "Begin typing @" + BotConfig.BOTUSERNAME + " to start a new event"
    		+ System.getProperty("line.separator")
    		+ System.getProperty("line.separator") + "Once you're done, share the event to a channel by selecting your event from the list (type in @" + BotConfig.BOTUSERNAME +"again). You will be able to see everyone who responded by using /menu in a direct message chat with @" + BotConfig.BOTUSERNAME 
    		+ System.getProperty("line.separator")
    		+ System.getProperty("line.separator") + "Use /view in a channel to see all events that have been shared to that channel. Use /view in a private chat to see all events that you have accepted or declined"
    		+ System.getProperty("line.separator")
			+ System.getProperty("line.separator") + "List of shortcuts in private chat:"
			+ System.getProperty("line.separator")
			+ STARTCOMMAND
			+ System.getProperty("line.separator")
			+ RESPONDEESCOMMAND
			+ System.getProperty("line.separator")
			+ DELETEEVENTSCOMMAND
			+ System.getProperty("line.separator")
			+ CLEAREVENTSCOMMAND
			+ System.getProperty("line.separator")
			+ EDITCOMMAND
			+ System.getProperty("line.separator");


}
