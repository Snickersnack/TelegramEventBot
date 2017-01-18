package org.wilson.telegram.templates;

public class EventCreation {

	public static final String TITLE  = 	
				"<strong>Event Creation</strong>"
				+ System.getProperty("line.separator")
				+ System.getProperty("line.separator");
	
	
	public static final String DUPLICATE = TITLE + "You already have an event with this name. Please try another name";
	public static final String DATE = TITLE + 
			"What is the date of this event?"
			+ System.getProperty("line.separator")
			+ System.getProperty("line.separator")
			+ "<i>Use month/date format (e.g. 01/21/2018 10:00PM)</i>";
	
	
	public static final String DATEBEFOREERROR = TITLE + "<i>Date cannot be before today</i>";
	public static final String LOCATION = TITLE + "Where is the location of this event?";
	public static final String DATEERROR = TITLE + "<i>Please specify the date in the correct format</i> <strong>(e.g. 01/21/2018)</strong>";
	public static final String STICKER = TITLE + "Attach an image or sticker to this event or /skip";
	public static final String UPLOADERROR = TITLE + "<i>There was an issue uploading. Please send again </i>";
	public static final String STICKERERROR = TITLE + "<i>Please upload a </i><strong>photo</strong> <i>or a</i> <strong>sticker</strong>";
}
