package org.wilson.telegram.util;

import java.util.HashSet;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;

import persistence.HibernateUtil;

public class EventPersistence implements Runnable{

	public void run(){
		HashSet<Long> persistenceQueue = Cache.getInstance().getPersistenceQueue();
		if(persistenceQueue.size() > 0){
			for(Long eventId : persistenceQueue){
				EventModel temp = new EventModel(eventId);
				EventModel event = EventFinder.findEvent(temp, Cache.getInstance().getMasterEventMap());
				Session session = null;
				try{
					session =  HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction(); 
					session.saveOrUpdate(event); 
					session.getTransaction().commit();

				}catch(ConstraintViolationException e){
					System.out.println("did not consume: " + event.getEventName());
					session.getTransaction().rollback();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					if (session != null){
					session.close();
					}
				}
			}

		}
	}
}
