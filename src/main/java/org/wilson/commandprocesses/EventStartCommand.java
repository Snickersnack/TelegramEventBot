package org.wilson.commandprocesses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;

public class EventStartCommand {

	Message message;
	
	public EventStartCommand(){
		
	}
	
	public static SendMessage setEventInfo(Message message){
		//maybe add protection against same user creating event at the same tiem?
		HashMap<Integer, EventModel> inProgressItem = Cache.getInstance().getInProgressEventCreations();
		Integer userId = message.getFrom().getId();
		int stage = inProgressItem.get(userId).getEventInputStage();

		EventModel inProgressEventItem = inProgressItem.get(userId);
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId());
		

			if(stage == 1){
				inProgressEventItem.setEventName(message.getText());

				//If event already exists, request another input
				HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getUserEventMap();
				EventModel temp = EventFinder.findEvent(inProgressEventItem, userMap);
				if(temp != null){
					sendMessageRequest.setText("There is already an event with this name. Please try another name");
				}else{
					sendMessageRequest.setText("Where is the location of this event?");
					inProgressEventItem.setEventInputStage(2);
					inProgressItem.put(userId,inProgressEventItem);
					Cache.getInstance().setInProgressEventCreations(inProgressItem);
				}
			}else if(stage == 2){
				sendMessageRequest.setText("What is the date of this event? (e.g. 11/21/2017 10:00PM)");
				inProgressEventItem.setEventLocation(message.getText());
				inProgressEventItem.setEventInputStage(3);
				inProgressItem.put(userId,inProgressEventItem);
				Cache.getInstance().setInProgressEventCreations(inProgressItem);
		
			//date is substring(0,10)
			}else if(stage == 3){
				try{
//					String dateInput = "12/21/2016 06:20:30";
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//					LocalDateTime eventDate = LocalDateTime.parse(dateInput, formatter);
					
					String dateInput = message.getText();
					System.out.println("dateinput: " + dateInput);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
					LocalDateTime eventDate = LocalDateTime.parse(dateInput, formatter);
					inProgressEventItem.setEventDate(dateInput);

				}catch(Exception e){
					System.out.println(e);
					sendMessageRequest.setText("Please specify the date in the correct format (e.g. 11/21/2017 10:00PM)");
					return sendMessageRequest;
				}
				HashMap<Integer, HashSet<EventModel>> completedEvent = generateNewEvent(message);
		
				
				
				//Generate a return keyboard button
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("Search Events");
				button.setSwitchInlineQuery(inProgressEventItem.getEventName());
				List<InlineKeyboardButton> buttons = new ArrayList();
				buttons.add(button);
				List<List<InlineKeyboardButton>> grid = new ArrayList();
				grid.add(buttons);
				markup.setKeyboard(grid);
				sendMessageRequest.setReplyMarkup(markup);
				sendMessageRequest.setText("Event Created!");
				sendMessageRequest.setParseMode(BotConfig.SENDMESSAGEMARKDOWN);	
			
				
				inProgressItem.put(userId, null);
				
				
				//clear item for this user from cache. Add Event to Eventmap

				Cache.getInstance().setUserEventMap(completedEvent);
				Cache.getInstance().setInProgressEventCreations(inProgressItem);
				
			}
		

		return sendMessageRequest;
		
	}

    public static HashMap<Integer, HashSet<EventModel>> generateNewEvent(Message message){
    	Integer userId = message.getFrom().getId();
    	HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getUserEventMap();
    	EventModel newEvent = Cache.getInstance().getInProgressEventCreations().get(userId);
    	HashSet<EventModel> mapSet = map.get(userId);
    	
    	System.out.println("Event start command: " + userId);
		
		InlineKeyboardButton button = new InlineKeyboardButton();
		InlineKeyboardButton button2 = new InlineKeyboardButton();
		button.setText("Yes");
		button.setCallbackData("Yes");
		button2.setText("No");
		button2.setCallbackData("No");
		List<InlineKeyboardButton> buttons = new ArrayList();
		buttons.add(button);
		buttons.add(button2);
		List<List<InlineKeyboardButton>> grid = new ArrayList();
		grid.add(buttons);
		newEvent.setEventGrid(grid);
		String event = EventBuilder.build(newEvent);
		//Add event to the set, then add to map and return map
		newEvent.setEventText(event);
		newEvent.setEventHost(userId);
		newEvent.setEventHostFirst(message.getFrom().getFirstName());
		mapSet.add(newEvent);
		
		return map;
    }
}
