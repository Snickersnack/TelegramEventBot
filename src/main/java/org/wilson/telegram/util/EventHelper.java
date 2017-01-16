package org.wilson.telegram.util;

import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;

public class EventHelper {

	public static void clearInProgress(Integer userId){
		EventModel inProgressEvent = Cache.getInstance().getInProgressEventCreations().get(userId);
		if( inProgressEvent != null){
			EventPersistence.delete(inProgressEvent);
			inProgressEvent = null;
			Cache.getInstance().getInProgressEventCreations().put(userId, inProgressEvent);
		}
		
		EditModel inProgressEdit = Cache.getInstance().getInProgressEdit().get(userId);
		if( inProgressEdit != null){
			inProgressEdit = null;
			Cache.getInstance().getInProgressEdit().put(userId, inProgressEdit);
		}
	}
}
