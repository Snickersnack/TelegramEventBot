package org.wilson.telegram.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.EventPersistence;

public class DateEventCleanup implements Runnable{

	private Map<Integer, HashSet<EventModel>> userMap;
	private Map<Long, HashSet<EventModel>> channelMap;
	private LocalDateTime currentTime;
	final Long EXPIRATION_TIME = (24*60*60) * BotConfig.SCHEDULED_REMOVAL_DAYS;
	final ZoneId ZONE_ID = ZoneId.of(BotConfig.TIME_ZONE);
	
	public void run(){
		HashSet<EventModel> deletionQueue = new HashSet();

		try{
		currentTime = DateUtil.getCurrentTime();
		LocalDate currentDay = currentTime.toLocalDate();
		long currentSeconds = currentDay.atStartOfDay(ZONE_ID).toEpochSecond();

		userMap = Cache.getInstance().getMasterEventMap();

		for(Entry<Integer, HashSet<EventModel>> userItem : userMap.entrySet()){
			HashSet<EventModel> eventSet = userItem.getValue();
			Iterator<EventModel> it = eventSet.iterator();
				while(it.hasNext()){
					EventModel event = it.next();
					String stringDate = event.getEventDate();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
					LocalDate eventDate = LocalDate.parse(stringDate, formatter);
					long eventSeconds = eventDate.atStartOfDay(ZONE_ID).toEpochSecond();
					
					System.out.println("currentSeconds: " + currentSeconds + "eventSeconds = " + eventSeconds);
					if(currentSeconds - eventSeconds >= EXPIRATION_TIME){
						System.out.println("removed event: " + event.getEventName());
						deletionQueue.add(event);

					}
					
				}

			}
		}catch(Exception e){
			System.out.println(e);
		}
		for(EventModel event : deletionQueue){
			EventFinder.deleteEvent(event);
		}
		
		HashMap<Integer, EventModel> inProgressMap = Cache.getInstance().getInProgressEventCreations();
		//Consider what happens if we need to delete inprogress as well
		Iterator it = inProgressMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			EventModel event = (EventModel)pair.getKey();
			
			if(event.getEventInputStage()!= 0){
				it.remove();
				EventPersistence.delete(event);
			}
		}


		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Events cleaned! Current Time: " + dateFormat.format(date));
	}
}
