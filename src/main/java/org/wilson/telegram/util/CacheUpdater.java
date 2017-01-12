package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;

import persistence.HibernateUtil;

public final class CacheUpdater {

	private CacheUpdater(){
		
	}
	
	public static void updateInlineId(Update update){
		String resultId = update.getChosenInlineQuery().getResultId();
		String inLineMessageId = update.getChosenInlineQuery().getInlineMessageId();

		HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
		EventModel tempEvent = new EventModel(Long.parseLong(resultId));
		EventModel foundEvent = EventFinder.findEvent(tempEvent, map);
//		EventPersistence.initialize(foundEvent);

		//search based on the ResultId
		Set<String> set = foundEvent.getInLineMessageId();
		
		
		if(set.add(inLineMessageId)){
			EventPersistence.saveOrUpdate(foundEvent);
		}
	}
	

	public static void updateChannelEventMap(Message message) {
		
		String command = message.getText();
		String[] arr = command.split("\\r?\\n");
		String eventName = arr[0];
		String eventDate = arr[1];
		String eventLocation = arr[2];
		Integer user = message.getFrom().getId();
		Long channelId = message.getChatId();
		
		HashMap<Long, HashSet<EventModel>> channelMap = Cache.getInstance().getChannelEventMap();
		if(!channelMap.containsKey(channelId)){
			channelMap.put(channelId, new HashSet<EventModel>());
		}
		HashSet<EventModel> channelSet = channelMap.get(channelId);
		
		
		EventModel newEvent = EventFinder.findEventbyName(eventName, user);
		Set<Long> channels = newEvent.getChannels();
		if(channels.add(channelId)){
			EventPersistence.saveOrUpdate(newEvent);
		};
		

		System.out.println("adding to channels:" + channelSet.add(newEvent));

	}
}
