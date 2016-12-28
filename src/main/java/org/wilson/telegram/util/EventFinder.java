package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;


//Use this to locate eventModels in our cache

public class EventFinder {

	EventModel event;
	HashMap<?, HashSet<EventModel>> map;
	
	public EventFinder(){
		this.event = null;
	}
	
	public EventFinder(EventModel event){
		this.event = event;
	}
	
	//searches through map to find event
	public static EventModel findEvent(EventModel event, HashMap<?, HashSet<EventModel>> map) {
		EventModel newEvent = event;
		for (Entry<?, HashSet<EventModel>> item : map.entrySet()) {
			HashSet<EventModel> set = item.getValue();
			if (set.contains(newEvent)) {
				for (EventModel eventItem : set) {
					if (eventItem.equals(newEvent)) {
						newEvent = eventItem;
						return newEvent;
					}
				}
			}else{
				System.out.println("couldnt find event??");
				System.out.println("event id: " + newEvent.getEventId());
			}
		}
		return null;
	}

	
	
	public static boolean deleteEvent(EventModel event){
		HashMap<Integer, HashSet<EventModel>> newMap = Cache.getInstance().getMasterEventMap();
		boolean found = false;
		try{
			HashSet<EventModel> set = newMap.get(event.getEventHost());
			Iterator<EventModel> it = set.iterator();
			while (it.hasNext()) {
				EventModel eventModel = it.next();
				if (eventModel.equals(event)) {
					it.remove();
					found = true;
					break;
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		}
		return found;
	}
	
	
	public static EventModel findEventbyName(String eventName){
		HashMap<Integer, HashSet<EventModel>> masterEventMap = Cache.getInstance().getMasterEventMap();
		for(Entry<Integer, HashSet<EventModel>> item : masterEventMap.entrySet()){
			for(EventModel userEvent : item.getValue()){
				if(userEvent.getEventName().equals(eventName)){
					return userEvent;
				}
			}
		}
		return null;
	}
	
	public static EventModel findEventbyNameUser(String eventName, Integer userId){
		HashSet<EventModel> userSet = Cache.getInstance().getMasterEventMap().get(userId);
			
			for(EventModel userEvent : userSet){
				if(userEvent.getEventName().equals(eventName)){
					return userEvent;
				}
			}
		
		return null;
	}
	
	
	public static EventModel findEventByInlineMessageId(String inLineMessageId) {
		HashMap<Integer, HashSet<EventModel>>map = Cache.getInstance().getMasterEventMap();
		EventModel eventModel = new EventModel();
		for (Entry<?, HashSet<EventModel>> entry : map.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				for (EventModel event : entry.getValue()) {
					if (event.getInLineMessageId().contains(inLineMessageId)) {
						eventModel = event;
						return eventModel;
					}
				}

			}
		}
		return null;
	}

	public EventModel getEvent() {
		return event;
	}

	public void setEvent(EventModel event) {
		this.event = event;
	}



}
