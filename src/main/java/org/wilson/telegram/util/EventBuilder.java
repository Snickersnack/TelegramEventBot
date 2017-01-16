package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;

import persistence.HibernateUtil;

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
		System.out.println("event builder list all events markup: " + markup);
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
//		EventPersistence.initialize(eventModel);
		if(eventModel.getImgur()!=null){
//			"<a href=\"http://i.imgur.com/GK2OaMxm.jpg\">test</a>"
			eventName = "<a href=\"" + eventModel.getImgur()+ "\">" + eventName + "</a>";
		}else{
			eventName = "<strong>" + eventName + "</strong>";
		}

		Map<String, Boolean> attendees = eventModel.getTotalResponses();

		


		StringBuilder ab = new StringBuilder();
		Integer attendeeSize = 0;
		if (!attendees.isEmpty()) {
			for (Entry<String, Boolean> item : attendees.entrySet()) {	
				if(item.getValue()){
						ab.append(" " + item.getKey() + ",");
						attendeeSize++;
				}
			}
		}
		String attendeeList = "👥  (" + attendeeSize + "): ";
		
		if(attendeeSize == 0){
			attendeeList = attendeeList + "<i>No one has responded</i>";
		}else{
			attendeeList = attendeeList + ab.toString().substring(0, ab.length() - 1);
		}
		
		
		eventText = eventName 
				+ System.getProperty("line.separator") + eventDate
				+ System.getProperty("line.separator") + eventLocation
				+ System.getProperty("line.separator") + attendeeList;
		
		return eventText;
	}
	
	
}
