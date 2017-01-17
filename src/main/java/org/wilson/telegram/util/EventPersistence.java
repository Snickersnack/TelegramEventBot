package org.wilson.telegram.util;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.models.RespondeeModel;

import persistence.HibernateUtil;

public class EventPersistence{

	
	public static void delete(EventModel event){
		Session session = null;
		try{
			System.out.println("deleting: " + event.getEventName());
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.delete(event); 
			session.getTransaction().commit();

		}catch(ConstraintViolationException e){
			System.out.println("did not delete: " + event.getEventName());
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
	
	public static void update(EventModel event){
		Session session = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.update(event); 
			session.getTransaction().commit();

		}catch(ConstraintViolationException e){
			System.out.println("did not delete: " + event.getEventName());
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
	
	
	public static void saveOrUpdate(EventModel event){
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
	
	
	public static void saveOrUpdate(RespondeeModel response){
		Session session = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.saveOrUpdate(response); 
			session.getTransaction().commit();

		}catch(ConstraintViolationException e){
			System.out.println("did not consume: " + response.getId());
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
	
//	public static void initialize(EventModel event){
//		Session session = null;
//		try{
//			session = HibernateUtil.getSessionFactory().openSession();
//			session.refresh(event);
//
//
//
//		}catch(Exception e){
//			
//		}finally{
//			if(session != null){
//				session.close();
//			}
//		}
//		
//	}

	
	public static void save(RespondeeModel response){
		Session session = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.save(response); 
			session.getTransaction().commit();

		}catch(ConstraintViolationException e){
			System.out.println("did not consume: " + response.getId());
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
	public static void save(EventModel event){
		Session session = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.save(event); 
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
	
	
	public static void deleteAll(Integer userId){
		Set<EventModel> set = Cache.getInstance().getMasterEventMap().get(userId);
		System.out.println(set.size());
		System.out.println(userId);
		for(EventModel event : set){
			System.out.println(event.getEventName());
			delete(event);
		}
	}

		
	}

