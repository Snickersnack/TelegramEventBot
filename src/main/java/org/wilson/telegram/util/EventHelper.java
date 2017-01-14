package org.wilson.telegram.util;

import org.wilson.telegram.client.Cache;

public class EventHelper {

	public static void clearInprogress(Integer userId){
		if(Cache.getInstance().getInProgressEdit().get(userId) != null){
			Cache.getInstance().getInProgressEdit().put(userId, null);
		}
		if(Cache.getInstance().getInProgressEventCreations().get(userId) != null){
			Cache.getInstance().getInProgressEventCreations().put(userId, null);
		}
	}
}
