package org.wilson.telegram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 */

public class Cache {

	private static Cache cache = new Cache();
	private HashMap<Integer, HashSet<EventModel>> userEventMap;
	private HashMap<Integer, EventModel> inProgressEventCreations;
	private HashMap<Long, HashSet<EventModel>> channelEventMap;

	


	private Cache() {
		setUserEventMap(new HashMap<Integer, HashSet<EventModel>>());
		setChannelEventMap(new HashMap<Long, HashSet<EventModel>>() );
		setInProgressEventCreations(new HashMap<Integer, EventModel>());
	}

	public static Cache getInstance() {
		return cache;
	}

	


	public HashMap<Integer, HashSet<EventModel>> getUserEventMap() {
		return userEventMap;
	}

	public void setUserEventMap(HashMap<Integer, HashSet<EventModel>> eventMap) {
		this.userEventMap = eventMap;
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
	
	public Boolean addChannelEvent(Long channelId, EventModel event){
		if(!channelEventMap.containsKey(channelId)){
			HashSet<EventModel> newSet = new HashSet<EventModel>();
			channelEventMap.put(channelId, newSet);
		}
		HashSet<EventModel> channelEventSet = channelEventMap.get(channelId);
		if(channelEventSet == null){
			channelEventSet = new HashSet<EventModel>();
			channelEventMap.put(channelId, channelEventSet);
		}
		Boolean added = channelEventSet.add(event);
		if(added){
			HashMap<Long, HashSet<EventModel>> map = getChannelEventMap();
			map.put(channelId, channelEventSet);
			setChannelEventMap(map);
		}
		return added;
		
	}
	public Boolean addEventMapEvent(Integer userId, EventModel event){
		if(!userEventMap.containsKey(userId)){
			HashSet<EventModel> newSet = new HashSet<EventModel>();
			userEventMap.put(userId, newSet);
		}
		HashSet<EventModel> eventSet = userEventMap.get(userId);
		if(eventSet == null){
			eventSet = new HashSet<EventModel>();
			userEventMap.put(userId, eventSet);
		}
		boolean added = eventSet.add(event);
		if(added){
			HashMap<Integer, HashSet<EventModel>> map = getUserEventMap();
			map.put(userId, eventSet);
			setUserEventMap(map);
		}
		return added;
		
	}
	
	//TODO Add event hosts back to 
	public void clearUserEvents(Integer userId){
		userEventMap.put(userId, new HashSet<EventModel>());
		
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
