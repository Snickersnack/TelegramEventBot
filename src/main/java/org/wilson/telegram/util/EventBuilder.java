package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;

public class EventBuilder {
	
	
	
	public static SendMessage listAllEvents(Integer userId, SendMessage sendMessageRequest, String type, StringBuilder sb){
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();

		HashSet<EventModel> eventSet = userMap.get(userId);

		Integer eventNumber = eventSet.size();
		if(eventNumber == 0){
			sb.append("You have no events");
			sendMessageRequest.setText(sb.toString());
			return sendMessageRequest;
		}
		System.out.println("total events for user: " + eventNumber);
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
		InlineKeyboardMarkup markup = keyboardBuilder.buildEventsList(userId, type);
		sendMessageRequest.setReplyMarkup(markup);
		if(type.equals(EventEdit.EDITTYPE)){
			sendMessageRequest.setText(EventEdit.EDITTITLE
					+ System.getProperty("line.separator")
					+ System.getProperty("line.separator"));
		}else if(type.equals(EventDelete.DELETETYPE)){
			sendMessageRequest.setText(EventDelete.DELETETITLE 
					+ System.getProperty("line.separator")
					+ System.getProperty("line.separator"));
		}

		return sendMessageRequest;
			

	}
	
	//duplicate of the keyboardbuilder
	public static EditMessageText listAllEvents(Integer userId, EditMessageText editRequest, String type, StringBuilder sb){
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();

		HashSet<EventModel> eventSet = userMap.get(userId);
		if(eventSet == null){
			eventSet = new HashSet<EventModel>();
			userMap.put(userId, eventSet);
		}
		Integer eventNumber = eventSet.size();
		if(eventNumber == 0){
			sb.append("You have no events");
			editRequest.setText(sb.toString());
			return editRequest;
		}
		System.out.println("total events for user: " + eventNumber);
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
		InlineKeyboardMarkup markup = keyboardBuilder.buildEventsList(userId, type);
		editRequest.setReplyMarkup(markup);
		
		
		if(type.equals(EventEdit.EDITTYPE)){
			editRequest.setText(EventEdit.EDITTITLE 
					+ System.getProperty("line.separator")
					+ System.getProperty("line.separator"));
		}else if(type.equals(EventDelete.DELETETYPE)){
			editRequest.setText(EventDelete.DELETETITLE
					+ System.getProperty("line.separator")
					+ System.getProperty("line.separator"));
		}
		return editRequest;
			

	}
	
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
