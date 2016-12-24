package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;


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
		System.out.println("starting event search: " + event.getEventName());
		Cache.getInstance().listAllEvents(event.getEventName());
		for (Entry<?, HashSet<EventModel>> item : map.entrySet()) {
			HashSet<EventModel> set = item.getValue();
			if (set.contains(newEvent)) {
				for (EventModel eventItem : set) {
					if (eventItem.equals(newEvent)) {
						newEvent = eventItem;
						return newEvent;
					}
				}
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
