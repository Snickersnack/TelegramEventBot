package org.wilson.telegram.callbackhandler;

import java.util.HashSet;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.KeyboardBuilder;

public class DeleteEventHandler extends UpdateHandler{

	EventModel eventModel;
	CallbackQuery callBack;
	
	public DeleteEventHandler(Update update) {
		eventModel = null;
		callBack = update.getCallbackQuery();

	}
	
	public EditMessageText handleCallbackQuery() {
		
		EditMessageText editRequest = new EditMessageText();
		StringBuilder sb = new StringBuilder();
		sb.append(EventDelete.DELETETITLE);
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		Long chatId = callBack.getMessage().getChatId();
		Integer messageId = callBack.getMessage().getMessageId();
		editRequest.setMessageId(messageId);
		editRequest.setChatId(chatId);		
		String data = callBack.getData();
		String[] dataArray = data.split(" ");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		Integer userId = callBack.getFrom().getId();
		
		
		
		//If we've already processed all steps:
		if(dataArray.length >2){
			
			EventModel event = new EventModel(Long.parseLong(dataArray[1]));
			event = EventFinder.findEvent(event, Cache.getInstance().getMasterEventMap());
			System.out.println("dataArray 1: " + dataArray[1]);
			String deleteResponse = dataArray[2];
			
			
			switch(deleteResponse){
			
			case EventDelete.ACCEPT:
				boolean found = EventFinder.deleteEvent(event);
				
				if(found){			
					sb.append("Removed event: <i>" + event.getEventName() + "</i>");
					sb.append(System.getProperty("line.separator"));
					sb.append(System.getProperty("line.separator"));
				}else{
					sb.append("Your event has already been deleted");
					sb.append(System.getProperty("line.separator"));
					sb.append(System.getProperty("line.separator"));
					
				}
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("<< Back to List");
				button.setCallbackData(EventDelete.DELETETYPE + " " + EventDelete.DELETELIST);
				keyboardBuilder.addButton(button);
				markup = keyboardBuilder.buildMarkup();
				
				break;

			case EventDelete.DECLINE:
				editRequest = EventBuilder.listAllEvents(userId, editRequest, EventDelete.DELETETYPE, sb);
				editRequest.setParseMode("HTML");
				return editRequest;
				}	
			

			
		}
		
		//Listing all events
		else if(dataArray[1].equals(EventDelete.DELETELIST)){
			HashSet<EventModel> events = Cache.getInstance().getMasterEventMap().get(userId);
			if(events == null || events.size() == 0){
				sb.append("<i>You have no events</i>");
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
				markup = keyboardBuilder.buildReturnMenu();
			}else{
				editRequest = EventBuilder.listAllEvents(userId, editRequest, EventDelete.DELETETYPE, sb);	
				editRequest.setParseMode("HTML");
				return editRequest;
			}

		}
		
		//Else send a confirmation deletion
		else{
			EventModel event = new EventModel(Long.parseLong(dataArray[1]));
			event = EventFinder.findEvent(event, Cache.getInstance().getMasterEventMap());
			if(event == null){
				sb.append("This event has already been deleted");
				editRequest.setText(sb.toString());
				editRequest.setParseMode("HTML");
				return editRequest;
			}
			sb.append("Confirm deletion of <i>" + event.getEventName() + "</i>");
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,2);
			
			
			//yes or no. No brings us back to list. Yes proceeds with deletion
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText("Yes");
			button.setCallbackData(data + " " + EventDelete.ACCEPT);
			keyboardBuilder.addButton(button);
			
			InlineKeyboardButton button2 = new InlineKeyboardButton();
			button2.setText("No");
			button2.setCallbackData(data + " " + EventDelete.DECLINE);
			keyboardBuilder.addButton(button2);
			
			markup = keyboardBuilder.buildMarkup();
	    }
		sb.append(System.getProperty("line.separator"));
		editRequest.setReplyMarkup(markup);
		editRequest.setParseMode("HTML");
		editRequest.setText(sb.toString());
		return editRequest;	
	}
}
