package org.wilson.telegram.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;

/**
 */

public class Cache {

	private static Cache cache = new Cache();
	private HashMap<Integer, HashSet<EventModel>> masterEventMap; //keyed to users
	private HashMap<Integer, EventModel> inProgressEventCreations;
	private HashMap<Integer, EditModel> inProgressEdit;
	private HashMap<Long, HashSet<EventModel>> channelEventMap;
	private HashSet<Long> persistenceQueue;
	private Long globalEventId;
	private LocalDateTime currentTime;

	


	private Cache() {
		setMasterEventMap(new HashMap<Integer, HashSet<EventModel>>());
		setChannelEventMap(new HashMap<Long, HashSet<EventModel>>() );
		setInProgressEventCreations(new HashMap<Integer, EventModel>());
		setInProgressEdit(new HashMap<Integer, EditModel>());
		setPersistenceQueue(new HashSet<Long>());
		globalEventId = 1L; //resultId CANNOT be 0
		currentTime = null;
	}

	public static Cache getInstance() {
		return cache;
	}

	
	public void init(){
		
	}


	public HashMap<Integer, HashSet<EventModel>> getMasterEventMap() {
		return masterEventMap;
	}

	public HashSet<Long> getPersistenceQueue() {
		return persistenceQueue;
	}

	public void setPersistenceQueue(HashSet<Long> persistenceQueue) {
		this.persistenceQueue = persistenceQueue;
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

	
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}

	public EventModel registerEvent(EventModel event){
		event.setEventId(globalEventId);
		globalEventId++;
		return event;
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
