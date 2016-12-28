package org.wilson.telegram.templates;

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
    
    public static final String HELPTEXT =  "Begin typing @EvePlannerBot to start a new event"
    		+ System.getProperty("line.separator")
    		+ System.getProperty("line.separator") + "Use /view to see all events shared to this channel"
    		+ System.getProperty("line.separator")
			+ System.getProperty("line.separator") + "Message /menu to @EvePlannerBot to manage your events"
			+ System.getProperty("line.separator");


    
    

    

    



}
