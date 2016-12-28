package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;

public final class CacheUpdater {

	private CacheUpdater(){
		
	}
	
	public static void updateInlineId(Update update){
		String resultId = update.getChosenInlineQuery().getResultId();
		String inLineMessageId = update.getChosenInlineQuery().getInlineMessageId();

		HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
		EventModel tempEvent = new EventModel(Long.parseLong(resultId));
		EventModel foundEvent = EventFinder.findEvent(tempEvent, map);
//		Cache.getInstance().listAllEvents(Long.parseLong(resultId));
		
		//search based on the ResultId
		HashSet<String> set = foundEvent.getInLineMessageId();
		set.add(inLineMessageId);

		
	}
	

	public static void updateChannelEventMap(Message message) {
		String command = message.getText();
		String[] arr = command.split("\\r?\\n");
		String eventName = arr[0];
		String eventDate = arr[1];
		String eventLocation = arr[2];
		Integer user = message.getFrom().getId();
		Long channelId = message.getChatId();
		EventModel newEvent = new EventModel(eventName, eventDate,eventLocation, user);
		
		HashMap<Long, HashSet<EventModel>> channelMap = Cache.getInstance().getChannelEventMap();
		if(!channelMap.containsKey(channelId)){
			channelMap.put(channelId, new HashSet<EventModel>());
		}
		HashSet<EventModel> channelSet = channelMap.get(channelId);
		
		
		
		newEvent = EventFinder.findEventbyName(eventName);
		newEvent.setChannelId(channelId);
		channelSet.add(newEvent);
		// is it already in channelMap?
		// if not find and add to channelMap from userMap (linking events)
	}
}
