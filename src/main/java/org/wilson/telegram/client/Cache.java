package org.wilson.telegram.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.util.EventFinder;

/**
 */

public class Cache {

	private static Cache cache = new Cache();
	private HashMap<Integer, HashSet<EventModel>> masterEventMap; //keyed to users
	private HashMap<Integer, EventModel> inProgressEventCreations;
	private HashMap<Integer, EditModel> inProgressEdit;
	private HashMap<Long, HashSet<EventModel>> channelEventMap;
	private Long globalEventId;

	


	private Cache() {
		setMasterEventMap(new HashMap<Integer, HashSet<EventModel>>());
		setChannelEventMap(new HashMap<Long, HashSet<EventModel>>() );
		setInProgressEventCreations(new HashMap<Integer, EventModel>());
		setInProgressEdit(new HashMap<Integer, EditModel>());
		globalEventId = 1L; //resultId CANNOT be 0
	}

	public static Cache getInstance() {
		return cache;
	}

	


	public HashMap<Integer, HashSet<EventModel>> getMasterEventMap() {
		return masterEventMap;
	}

	public void setMasterEventMap(HashMap<Integer, HashSet<EventModel>> eventMap) {
		this.masterEventMap = eventMap;
	}

	public HashMap<Integer, EventModel> getInProgressEventCreations() {
		return inProgressEventCreations;
	}

	public void setInProgressEventCreations(HashMap<Integer, EventModel> inProgressEventCreations) {
		this.inProgressEventCreations = inProgressEventCreations;
	}


	public HashMap<Long, HashSet<EventModel>> getChannelEventMap() {
		return channelEventMap;
	}

	public void setChannelEventMap(HashMap<Long, HashSet<EventModel>> channelEventMap) {
		this.channelEventMap = channelEventMap;
	}

	public HashMap<Integer, EditModel> getInProgressEdit() {
		return inProgressEdit;
	}

	public void setInProgressEdit(HashMap<Integer, EditModel> inProgressEdit) {
		this.inProgressEdit = inProgressEdit;
	}

	
	public EventModel registerEvent(EventModel event){
		event.setEventId(globalEventId);
		globalEventId++;
		return event;
	}
	
	public void listAllEvents(Long id){
		EventModel test = new EventModel(id);

		for(Entry<Integer, HashSet<EventModel>> item : masterEventMap.entrySet()){
			if(item.getValue().contains(test)){
				System.out.println("ITS EVEN IN THE SET");
			}else{
				System.out.println("how is it not in the set");
			}
			for(EventModel event : item.getValue()){
				System.out.println("listing all events :" + event.getEventName());
				
				if(event.equals(test)){
					System.out.println("foudn it here");
				}
				
				else{
					System.out.println("nooope");
				}
			}
		}
	}
	public Boolean addEventMapEvent(Integer userId, EventModel event){
		if(!masterEventMap.containsKey(userId)){
			HashSet<EventModel> newSet = new HashSet<EventModel>();
			masterEventMap.put(userId, newSet);
		}
		HashSet<EventModel> eventSet = masterEventMap.get(userId);
		if(eventSet == null){
			eventSet = new HashSet<EventModel>();
			masterEventMap.put(userId, eventSet);
		}
		boolean added = eventSet.add(event);
		if(added){
			HashMap<Integer, HashSet<EventModel>> map = getMasterEventMap();
			map.put(userId, eventSet);
			setMasterEventMap(map);
		}
		return added;
		
	}
	
	public void clearUserEvents(Integer userId){
		masterEventMap.put(userId, new HashSet<EventModel>());
		
		for(Entry<Long, HashSet<EventModel>> item :channelEventMap.entrySet()){
			HashSet<EventModel> set = item.getValue();
			for(Iterator<EventModel> i = set.iterator(); i.hasNext();){
				EventModel event = i.next();
				if(event.getEventHost().equals(userId)){
					i.remove();
				}
			}
		}
	}
}
