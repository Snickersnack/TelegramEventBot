package org.wilson.telegram.util;

import java.util.HashSet;

import org.wilson.telegram.EventModel;

public class EventBuilder {
	
	public static String build(EventModel eventModel){
		String eventText = "";
		String eventName = eventModel.getEventName();
		String eventLocation = eventModel.getEventLocation();
		String eventDate = eventModel.getEventDate();
		String eventHostFirst = eventModel.getEventHostFirst();
		HashSet<String> attendees = eventModel.getAttendees();
		Integer newAttendee = attendees.size();
		String attendeeList = "Attendees (" + newAttendee + "): ";
		int counter = 1;
		if (!attendees.isEmpty()) {
			for (String item : attendees) {
				if (counter == attendees.size()) {
					attendeeList += " " + item;
				} else {
					attendeeList += " " + item + ", ";

				}
				counter++;
			}
		}
		eventText = "<strong>" + eventName + "</strong>"
				+ System.getProperty("line.separator") + eventDate
				+ System.getProperty("line.separator") + "📍" + eventLocation
				+ System.getProperty("line.separator") + attendeeList;
		
		return eventText;
	}
	
	
}
