package org.wilson.telegram.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;

public class DateEventCleanup implements Runnable{

	private Map<Integer, HashSet<EventModel>> userMap;
	private Map<Long, HashSet<EventModel>> channelMap;
	private LocalDateTime currentTime;
	final Long EXPIRATION_TIME = (24*60*60) * BotConfig.SCHEDULED_REMOVAL_DAYS;
	final ZoneId ZONE_ID = ZoneId.of(BotConfig.TIME_ZONE);
	
	public void run(){
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
						it.remove();
					}
					
				}

			}
		}catch(Exception e){
			System.out.println(e);
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Events cleaned! Current Time: " + dateFormat.format(date));
	}
}
