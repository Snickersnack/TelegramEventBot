package org.wilson.telegram.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;

public class DateEventCleanup implements Runnable{

	HashMap<Integer, HashSet<EventModel>> userMap;
	HashMap<Long, HashSet<EventModel>> channelMap;
	LocalDateTime currentTime;

	
	public void run(){
		currentTime = DateUtil.getCurrentTime();
//		LocalDate currentDay = currentTime.toLocalDate();
		LocalTime currentDay = currentTime.toLocalTime();
		userMap = Cache.getInstance().getUserEventMap();

		for(Entry<Integer, HashSet<EventModel>> userItem : userMap.entrySet()){
			HashSet<EventModel> eventSet = userItem.getValue();
			Iterator<EventModel> it = eventSet.iterator();
			try{
				while(it.hasNext()){
					EventModel event = it.next();
					String stringDate = event.getEventDate();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"); 
					System.out.println("This is the broken date: " + stringDate);
					LocalTime eventDate = LocalTime.parse(stringDate, formatter);
					
					if(currentDay.until(eventDate, ChronoUnit.SECONDS) <= -40){
						System.out.println("removed event: " + event.getEventName());
						it.remove();
					}
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
//					LocalDate eventDate = LocalDate.parse(stringDate, formatter);
//					if(currentDay.compareTo(eventDate) >= BotConfig.SCHEDULEDTIMEEVENTREMOVAL){
//						it.remove();
//					}
					

				}
			}catch(Exception e){
				System.out.println(e);
			}
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Events cleaned! Current Time: " + dateFormat.format(date));
	}
}
