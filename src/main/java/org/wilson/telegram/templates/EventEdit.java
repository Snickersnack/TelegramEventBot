package org.wilson.telegram.templates;

public final class EventEdit {

	//note that callback data for edits will follow the form:
	//EDIT *Event Name* EDITTYPE
	
	//Changing EDITTYPE will affect Callback handling
	public static final String ACCEPT  = "Yes";
	public static final String DECLINE  = "No";
	public static final String EDITTYPE = "Edit";
	public static final String EDITNAME = "EditTitle";
	public static final String EDITDATE = "EditDate";
	public static final String EDITLOCATION = "EditLocation";
	public static final String EDITTITLE = "<strong>Edit Events</strong>";
	public static final String EVENTLIST = "eventList";
	public static final String[] EDITFIELDLIST = {EDITNAME,EDITDATE,EDITLOCATION};	
	public static final String[] EDITBUTTONLIST = {"Event Name" ,"Event Date" ,"Event Location"};	


}
