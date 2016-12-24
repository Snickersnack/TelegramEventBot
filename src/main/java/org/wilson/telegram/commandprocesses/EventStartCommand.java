package org.wilson.telegram.commandprocesses;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.KeyboardBuilder;

public class EventStartCommand {

	
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
				HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
				EventModel temp = EventFinder.findEvent(inProgressEventItem, userMap);
				if(temp != null){
					sendMessageRequest.setText("There is already an event with this name. Please try another name");
				}else{
					sendMessageRequest.setText("What is the date of this event? (e.g. 11/21/2017 10:00PM)");
					inProgressEventItem.setEventInputStage(2);
					
				}
			}else if(stage == 2){
				try{
//					String dateInput = "12/21/2016 06:20:30";
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//					LocalDateTime eventDate = LocalDateTime.parse(dateInput, formatter);
					
					String dateInput = message.getText().substring(0,10);
					System.out.println("dateinput: " + dateInput);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate eventDate = LocalDate.parse(dateInput, formatter);
					inProgressEventItem.setEventDate(dateInput);
					sendMessageRequest.setText("Where is the location of this event?");
					inProgressEventItem.setEventInputStage(3);




				}catch(Exception e){
					System.out.println(e);
					sendMessageRequest.setText("Please specify the date in the correct format (e.g. 11/21/2017 10:00PM)");
					return sendMessageRequest;
				}

				
		
			//date is substring(0,10)
			}else if(stage == 3){
				inProgressEventItem.setEventLocation(message.getText());
				HashMap<Integer, HashSet<EventModel>> completedEvent = generateNewEvent(message);
		
				
				
				//Generate a return keyboard button
		
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("Search Events");
				button.setSwitchInlineQuery(inProgressEventItem.getEventName());
				keyboardBuilder.addButton(button);
				InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
				
				sendMessageRequest.setReplyMarkup(markup);
				sendMessageRequest.setText("Event Created!");
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);	
			
				
				inProgressItem.put(userId, null);
				
				
				//clear item for this user from cache. Add Event to Eventmap
				Cache.getInstance().setMasterEventMap(completedEvent);
				Cache.getInstance().setInProgressEventCreations(inProgressItem);
				
			}
		

		return sendMessageRequest;
		
	}

    public static HashMap<Integer, HashSet<EventModel>> generateNewEvent(Message message){
    	Integer userId = message.getFrom().getId();
    	HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
    	EventModel newEvent = Cache.getInstance().getInProgressEventCreations().get(userId);
    	System.out.println("new event name: " + newEvent.getEventName());
    	HashSet<EventModel> mapSet = map.get(userId);
    	
    	System.out.println("Event start command: " + userId);
		
		KeyboardBuilder eventKeyBoard = new KeyboardBuilder(1,2);
		InlineKeyboardButton button = new InlineKeyboardButton();
		InlineKeyboardButton button2 = new InlineKeyboardButton();
		button.setText("Yes");
		button.setCallbackData("Yes");
		button2.setText("No");
		button2.setCallbackData("No");
		eventKeyBoard.addButton(button);
		eventKeyBoard.addButton(button2);
		List<List<InlineKeyboardButton>> newKeyboard = eventKeyBoard.buildKeyboard();

		newEvent.setEventGrid(newKeyboard);
		String event = EventBuilder.build(newEvent);
		//Add event to the set, then add to map and return map
		newEvent.setEventText(event);
		newEvent.setEventHost(userId);
		newEvent.setEventHostFirst(message.getFrom().getFirstName());
		mapSet.add(newEvent);
		
		return map;
    }
}
