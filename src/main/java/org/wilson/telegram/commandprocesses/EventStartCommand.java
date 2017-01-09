package org.wilson.telegram.commandprocesses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.KeyboardBuilder;

import persistence.HibernateUtil;

public class EventStartCommand {

	
	public EventStartCommand(){
		
	}
	
	public static SendMessage start(User user, Long chatId){
		
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(chatId);
		sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		Integer userId = user.getId();
		
		
		HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
		if (map.get(userId) == null) {
			map.put(userId, new HashSet<EventModel>());
		}
		if(map.get(userId).size() >= BotConfig.MAX_EVENTS){
			System.out.println(map.get(userId).size());
			sendMessageRequest.setText("You can only have " + BotConfig.MAX_EVENTS + " events at a time. Please clear or delete your events");
			return sendMessageRequest;
		}
		
		EventModel newEvent = new EventModel();
		newEvent.setEventHostFirst(user.getFirstName());
		newEvent.setEventInputStage(1);

		HashMap<Integer, EventModel> inProgressCache = Cache.getInstance().getInProgressEventCreations();

		inProgressCache.put(userId, newEvent);


		Cache.getInstance().setInProgressEventCreations(inProgressCache);
		sendMessageRequest.setText(
				"<strong>Event Creation</strong>"
				+ System.getProperty("line.separator")
				+ "Use /start to start from the beginning. Use /cancel to exit"
				+ System.getProperty("line.separator")
				+ System.getProperty("line.separator")
				+ System.getProperty("line.separator")
				+ "What is the name of your event? "
				 );

		return sendMessageRequest;
	}
	
	
	
	public static SendMessage setEventInfo(Message message){
		//maybe add protection against same user creating event at the same tiem?
		HashMap<Integer, EventModel> inProgressMap = Cache.getInstance().getInProgressEventCreations();
		Integer userId = message.getFrom().getId();
		int stage = inProgressMap.get(userId).getEventInputStage();

		EventModel inProgressEventItem = inProgressMap.get(userId);
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId());
		

			if(stage == 1){
				inProgressEventItem.setEventName(message.getText());

				//If event already exists, request another input
				EventModel temp = EventFinder.findEventbyNameUser(inProgressEventItem.getEventName(), userId);
				if(temp != null){
					sendMessageRequest.setText("You already have an event with this name. Please try another name");
				}else{
					sendMessageRequest.setText("What is the date of this event? Use month/date format (e.g. 01/21/2018 10:00PM)");
					inProgressEventItem.setEventInputStage(2);
					
				}
			}else if(stage == 2){
				try{
					
					String date = message.getText();
					String dateInput = message.getText().substring(0,10);
					System.out.println("dateinput: " + dateInput);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate eventDate = LocalDate.parse(dateInput, formatter);
					LocalDateTime currentTime = Cache.getInstance().getCurrentTime();
					LocalDate currentDay = currentTime.toLocalDate();
					if(eventDate.isBefore(currentDay)){
						sendMessageRequest.setText("Date cannot be before today");
						return sendMessageRequest;
					}

					
					inProgressEventItem.setEventDate(date);
					sendMessageRequest.setText("Where is the location of this event?");
					inProgressEventItem.setEventInputStage(3);




				}catch(Exception e){
					System.out.println(e);
					sendMessageRequest.setText("Please specify the date in the correct format <strong>(e.g. 01/21/2018)</strong>");
					sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);	
					return sendMessageRequest;
				}

				
		
			//date is substring(0,10)
			}else if(stage == 3){
				inProgressEventItem.setEventLocation(message.getText());
				EventModel newEvent = generateNewEvent(message);
		
				//Generate a return keyboard button
		
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("Share your event");
				button.setSwitchInlineQuery(inProgressEventItem.getEventName());
				keyboardBuilder.addButton(button);
				InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
				
				sendMessageRequest.setReplyMarkup(markup);
				sendMessageRequest.setText("Event Created!");
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);	
			
				
				Session session = null;
				try{
					session =  HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction(); 
					newEvent.setEventInputStage(0);
					session.save(newEvent); 
					session.getTransaction().commit();

				}catch(ConstraintViolationException e){
					System.out.println("did not consume: " + newEvent.getEventId());
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
				//clear item for this user from cache.

				inProgressMap.put(userId, null);
				
			}
		

		return sendMessageRequest;
		
	}

    public static EventModel generateNewEvent(Message message){
    	Integer userId = message.getFrom().getId();
    	HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
    	EventModel newEvent = Cache.getInstance().getInProgressEventCreations().get(userId);
    	System.out.println("new event name: " + newEvent.getEventName());
    	HashSet<EventModel> mapSet = map.get(userId);
    	
    	System.out.println("Event start command: " + userId);
		
		KeyboardBuilder eventKeyBoard = new KeyboardBuilder();
		List<List<InlineKeyboardButton>> newKeyboard = eventKeyBoard.buildEventButtons();
		newEvent.setEventGrid(newKeyboard);
		String event = EventBuilder.build(newEvent);
		//Add event to the set, then add to map and return map
		newEvent.setEventText(event);
		newEvent.setEventHost(userId);
		newEvent.setEventHostFirst(message.getFrom().getFirstName());
		newEvent = Cache.getInstance().registerEvent(newEvent);
		mapSet.add(newEvent);
		Cache.getInstance().getPersistenceQueue().add(newEvent.getEventId());
		return newEvent;
    }
}
