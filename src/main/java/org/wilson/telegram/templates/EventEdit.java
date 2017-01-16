package org.wilson.telegram.templates;

public final class EventEdit {

	//note that callback data for edits will follow the form:
	//EDIT *Event Name* EDITTYPE
	
	//Changing EDITTYPE will affect Callback handling
	public static final String EDITTYPE = "Edit";
	public static final String EDITNAME = "EditTitle";
	public static final String EDITDATE = "EditDate";
	public static final String EDITLOCATION = "EditLocation";
	public static final String EDITPICTURE = "EditPicture";
	public static final String EDITTITLE = "<strong>Edit Events</strong>";
	public static final String EVENTLIST = "eventList";
	public static final String[] EDITFIELDLIST = {EDITNAME,EDITDATE,EDITLOCATION};	
	public static final String[] EDITFIELDLISTPIC = {EDITNAME,EDITDATE,EDITLOCATION,EDITPICTURE};	
	public static final String[] EDITBUTTONLIST = {"Edit Name" ,"Edit Date" ,"Edit Location", "Add Picture"};	
	public static final String[] EDITBUTTONLISTPIC = {"Edit Name" ,"Edit Date" ,"Edit Location", "Edit Picture"};	


}
