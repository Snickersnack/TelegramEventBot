package org.wilson.telegram.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.models.RespondeeModel;
import org.wilson.telegram.util.KeyboardBuilder;

import persistence.HibernateUtil;

/**
 */


public class Cache {

	private static Cache cache = new Cache();
	private HashMap<Integer, HashSet<EventModel>> masterEventMap; //keyed to users
	private HashMap<Integer, EventModel> inProgressEventCreations;
	private HashMap<Integer, EditModel> inProgressEdit;
	private HashMap<Long, HashSet<EventModel>> channelEventMap;
	private HashSet<Long> persistenceQueue; //tabling this until i need it
	private HashMap<Integer, HashSet<EventModel>> userRespondeeMap; //map for users that have responded


	private Long globalEventId;
	private Long globalResponseId; //for persistence
	private LocalDateTime currentTime;

	


	private Cache() {
		setMasterEventMap(new HashMap<Integer, HashSet<EventModel>>());
		setChannelEventMap(new HashMap<Long, HashSet<EventModel>>() );
		setInProgressEventCreations(new HashMap<Integer, EventModel>());
		setInProgressEdit(new HashMap<Integer, EditModel>());
		setUserRespondeeMap(new HashMap<Integer, HashSet<EventModel>>());
		setPersistenceQueue(new HashSet<Long>());
		globalEventId = 1L; //resultId CANNOT be 0
		currentTime = null;
	}

	public static Cache getInstance() {
		return cache;
	}

	
	public void init(){
		
		Session session = null;
		try{
			session = HibernateUtil.getSessionFactory().openSession();
			Criteria crit = session.createCriteria(EventModel.class);
			List<EventModel> events = crit.list();
			
			//populate master event map
			Long maxId = 0L;
			Long maxResponseId = 0L;
			for(EventModel event : events){
				if(event.getEventId()>maxId){
					maxId = event.getEventId();
				}
				KeyboardBuilder kb = new KeyboardBuilder();
				event.setEventGrid(kb.buildEventButtons(event.getEventId()));
				Integer userId = event.getEventHost();
				
				//"Eager" load these 
				Hibernate.initialize(event.getInLineMessageId());
				Hibernate.initialize(event.getChannels());
				
				
				//for each response in event, map the largest responseid
				for(RespondeeModel response : event.getTotalResponses()){
					if(response.getId() > maxResponseId){
						maxResponseId = response.getId();
					}
					HashSet<EventModel> responseSet = userRespondeeMap.get(response.getUserId());
					if(responseSet == null){
						responseSet = new HashSet<EventModel>();						
					}
					responseSet.add(event);
					userRespondeeMap.put(response.getUserId(), responseSet);
					
				}
				
				//always add the user to the event map (even if we have no events for them)
				HashSet<EventModel> eventSet = masterEventMap.get(userId);
				if(eventSet == null){
					eventSet = new HashSet<EventModel>();
					masterEventMap.put(userId, eventSet);
					
				}
				
				//only add to master events if if's already completed
				if(event.getEventInputStage() == 0){
					eventSet.add(event);
				}
				//if event stage isn't 0, put this into our inprogress
				else{
					if(inProgressEventCreations.get(userId) == null){
						EventModel eventCopy = new EventModel(event.getEventId());
						eventCopy.setEventHost(event.getEventHost());
						eventCopy.setEventHostFirst(event.getEventHostFirst());
						eventCopy.setEventName(event.getEventName());
						eventCopy.setEventInputStage(event.getEventInputStage());
						eventCopy.setEventLocation(event.getEventLocation());
						eventCopy.setEventDate(event.getEventDate());
						inProgressEventCreations.put(userId, eventCopy);
					}
				}
				
				
				//add to channel map
				Iterator<Long> it = event.getChannels().iterator();
				while (it.hasNext()) {
					Long channel = it.next();					
					HashSet<EventModel> channelSet = channelEventMap.get(channel);
					if( channelSet == null){
						channelSet = new HashSet<EventModel>();
						channelEventMap.put(channel, channelSet);
					}
					channelSet.add(event);
				}
				
			}
			globalEventId = maxId+1;
			globalResponseId = maxResponseId + 1;
			
			System.out.println("Size of master event map: " + masterEventMap.size());		
			System.out.println("Size of channel event map: " + channelEventMap.size());		
//			System.out.println("Size of inprogress event map: " + inProgressEventCreations.size());		
//			EventFinder.printAll(163396337);
//			System.out.println("size of event for me: " + masterEventMap.get(163396337).size());
		}
		finally{
			if (session != null){
			session.close();
			}
		}


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

	
	public HashMap<Integer, HashSet<EventModel>> getUserRespondeeMap() {
		return userRespondeeMap;
	}

	public void setUserRespondeeMap(
			HashMap<Integer, HashSet<EventModel>> userRespondeeMap) {
		this.userRespondeeMap = userRespondeeMap;
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

	public RespondeeModel registerResponse(RespondeeModel response){
		response.setId(globalResponseId);
		globalResponseId++;
		return response;
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
