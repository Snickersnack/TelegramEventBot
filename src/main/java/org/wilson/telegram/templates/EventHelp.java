package org.wilson.telegram.templates;

import org.wilson.telegram.config.BotConfig;

public class EventHelp {

    public static final String HELPTEXT =  "Begin typing @" + BotConfig.BOTUSERNAME + " to start a new event"
    		+ System.getProperty("line.separator")
    		+ System.getProperty("line.separator") + "Once you're done, share the event to a channel by selecting your event from the list (type in @" + BotConfig.BOTUSERNAME +" again). You will be able to manage your events by using /menu in a direct message chat with @" + BotConfig.BOTUSERNAME 
    		+ System.getProperty("line.separator")
    		+ System.getProperty("line.separator") + "Use /view in a channel to see all events that have been shared to that channel. Use /view in a private chat to see all events that you have accepted or declined"
    		+ System.getProperty("line.separator")
			+ System.getProperty("line.separator") + "List of shortcuts in private chat:"
			+ System.getProperty("line.separator")
			+ Commands.STARTCOMMAND
			+ System.getProperty("line.separator")
			+ Commands.RESPONDEESCOMMAND
			+ System.getProperty("line.separator")
			+ Commands.DELETEEVENTSCOMMAND
			+ System.getProperty("line.separator")
			+ Commands.CLEAREVENTSCOMMAND
			+ System.getProperty("line.separator")
			+ Commands.EDITCOMMAND
			+ System.getProperty("line.separator")
			+ System.getProperty("line.separator")
			+ "*Events will be removed " + BotConfig.SCHEDULED_REMOVAL_DAYS + " days after the event date. You can only have " + BotConfig.MAX_EVENTS + " events at a time."			
			;
}
