package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;


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
	
	public static EventModel findEvent(EventModel event, HashMap<?, HashSet<EventModel>> map) {
		EventModel newEvent = event;
		for (Entry<?, HashSet<EventModel>> item : map.entrySet()) {
			HashSet<EventModel> set = item.getValue();
			if (set.contains(newEvent)) {
				for (EventModel userEvent : set) {
					if (userEvent.equals(newEvent)) {
						newEvent = userEvent;
						return newEvent;
					}
				}
			}
		}
		return null;
	}
	
	public static EventModel findEventByInlineMessageId(String inLineMessageId) {
		HashMap<Integer, HashSet<EventModel>>map = Cache.getInstance().getUserEventMap();
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
