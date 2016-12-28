package org.wilson.telegram.messagehandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.KeyboardBuilder;

public class EditMessageHelper {

	
	public static BotApiMethod<?> parse(Message message){
		
		Integer userId = message.getFrom().getId();
		HashMap<Integer, EditModel> editMap = Cache.getInstance().getInProgressEdit();
		
		EditModel editModel = editMap.get(userId);
		SendMessage sendMessageRequest= new SendMessage();
		sendMessageRequest.setChatId(editModel.getChatId());
//		sendMessageRequest.setMessageId(editModel.getMessageId());
		EventModel event = editModel.getEventModel();
		//possibly do another fine event here?
		StringBuilder sb = new StringBuilder();
		sb.append(EventEdit.EDITTITLE );
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));

		
		if( editModel != null){
			
			String type = editModel.getEditType();
			
			if(type.equals(EventEdit.EDITNAME)){
				event.setEventName(message.getText());
				sb.append("New event name: <i>" + message.getText() + "</i>");
				sb.append(System.getProperty("line.separator"));

			}else if(type.equals(EventEdit.EDITDATE))
				try{
					String dateInput = message.getText().substring(0,10);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate eventDate = LocalDate.parse(dateInput, formatter);
					LocalDateTime currentTime = Cache.getInstance().getCurrentTime();
					LocalDate currentDay = currentTime.toLocalDate();
					if(eventDate.isBefore(currentDay)){
						sendMessageRequest.setText("Date cannot be before today");
						return sendMessageRequest;
					}
					sb.append("New event date: <i>" + message.getText() +  "</i>");
					sb.append(System.getProperty("line.separator"));


				}catch(Exception e){
					System.out.println(e);
					sendMessageRequest.setText("Please specify the date in the correct format (e.g. 11/21/2017 10:00PM)");
					return sendMessageRequest;
				}
			else if(type.equals(EventEdit.EDITLOCATION)){
				event.setEventLocation(message.getText());
				sb.append("New event location: <i>" + message.getText() +  "</i>");
				sb.append(System.getProperty("line.separator"));

				
			}
			String eventText = EventBuilder.build(event);
			event.setEventText(eventText);
			
		}
		
		sb.append(System.getProperty("line.separator"));
		sendMessageRequest.setText(sb.toString());
		
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder(4,1);
		int count = 0;
		for(String editField : EventEdit.EDITFIELDLIST){
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(EventEdit.EDITBUTTONLIST[count]);
			count++;
			StringBuilder buttonString = new StringBuilder();
			buttonString.append(EventEdit.EDITTYPE + " " + event.getEventId());
			buttonString.append(" ");
			buttonString.append(editField);
			button.setCallbackData(buttonString.toString());
			keyboardBuilder.addButton(button);
		}
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setText("<< Back to List");
		button.setCallbackData(EventEdit.EDITTYPE + " " + EventEdit.EVENTLIST);
		keyboardBuilder.addButton(button);
		InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
		sendMessageRequest.setReplyMarkup(markup);
		sendMessageRequest.setParseMode("HTML");
		editMap.put(userId, null);
		return sendMessageRequest;
	}
}
