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
		
		//remove from master
		try{
			HashSet<EventModel> set = newMap.get(event.getEventHost());
			Iterator<EventModel> it = set.iterator();
			while (it.hasNext()) {
				EventModel eventModel = it.next();
				if (eventModel.equals(event)) {
					EventPersistence.delete(eventModel);
					it.remove();
					found = true;
					break;
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		}
		
		//remove from channelMap
		HashMap<Long, HashSet<EventModel>> channel = Cache.getInstance().getChannelEventMap();
		for(Entry<Long, HashSet<EventModel>> item : channel.entrySet()){
			HashSet<EventModel> set = item.getValue();
			try{
				Iterator<EventModel> it = set.iterator();
				while (it.hasNext()) {
					EventModel eventModel = it.next();
					if (eventModel.equals(event)) {
						it.remove();
					}
				}	
			}catch(Exception e){
				System.out.println(e);
			}
		}

		return found;
	}
	
	//Find event by name only works because the bot only reads what it has forwarded (via "bot name")
	//This means that you can't paste the exact eventname to another channel because the bot won't pick it up
	//However, since we don't have globally unique event names, it's possible that two differenet users can share the
	//exact same name. It's also possible for users to forward "via" messages so we can have a situation where 
	//two different users are forwarding the exact name. Thus, we need to make a check on both userId and eventname
	
	//This also allows us to have a way to optionally make events "public". We can allow different users to forward
	//the event even if they were not the creator and have it added to the /view command. 
	public static EventModel findEventbyName(String eventName, Integer userId){
		HashMap<Integer, HashSet<EventModel>> masterEventMap = Cache.getInstance().getMasterEventMap();
		for(Entry<Integer, HashSet<EventModel>> item : masterEventMap.entrySet()){
			for(EventModel userEvent : item.getValue()){
				if(userEvent.getEventName().equals(eventName)){
					if(userId != null){
						if(userId == userEvent.getEventHost()){
							return userEvent;
						}
					}
					System.out.println("found: " + eventName);
					return userEvent;
				}
			}
		}
		return null;
	}
	
	public static EventModel findEventbyNameUser(String eventName, Integer userId){
		HashSet<EventModel> userSet = Cache.getInstance().getMasterEventMap().get(userId);
			
			for(EventModel userEvent : userSet){
				System.out.println("current in cache: " + userEvent.getEventName());
				if(userEvent.getEventName().equals(eventName)){
					System.out.println("name of input: " + userEvent.getEventName());

					return userEvent;
				}
			}
		
		return null;
	}
	
	public static void printAll(Integer userId){
		HashSet<EventModel> userSet = Cache.getInstance().getMasterEventMap().get(userId);
		for(EventModel userEvent : userSet){
			System.out.println(userEvent.getEventName());
		}
	}
	public static EventModel findEventByInlineMessageId(String inLineMessageId) {
		HashMap<Integer, HashSet<EventModel>>map = Cache.getInstance().getMasterEventMap();
		EventModel eventModel = new EventModel();
		for (Entry<?, HashSet<EventModel>> entry : map.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				for (EventModel event : entry.getValue()) {
//					EventPersistence.initialize(event);
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
