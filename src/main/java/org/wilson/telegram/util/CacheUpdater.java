package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;

public class CacheUpdater {

	
	public static void updateInlineId(Update update){
		String resultId = update.getChosenInlineQuery().getResultId();
		String inLineMessageId = update.getChosenInlineQuery().getInlineMessageId();

		
		HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getUserEventMap();
		EventModel tempEvent = new EventModel(resultId);
		EventModel foundEvent = EventFinder.findEvent(tempEvent, map);
		
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

		// is it already in channelMap?
		// if not find and populate channelmap
		Cache.getInstance().addChannelEvent(channelId, newEvent);
		System.out.println("event added: " + eventName);
	}
}
