package org.wilson.telegram.messagehandler;

import java.time.LocalDate;
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
		EditMessageText editRequest = new EditMessageText();
		editRequest.setChatId(editModel.getChatId());
		editRequest.setMessageId(editModel.getMessageId());
		EventModel event = editModel.getEventModel();
		//possibly do another fine event here?
		StringBuilder sb = new StringBuilder();
		sb.append("Edit Events:");
		sb.append(System.getProperty("line.separator"));
		
		if( editModel != null){
			
			String type = editModel.getEditType();
			
			if(type.equals(EventEdit.EDITNAME)){
				event.setEventName(message.getText());
				sb.append("New event name: " + message.getText());
			}else if(type.equals(EventEdit.EDITDATE))
				try{
					String dateInput = message.getText().substring(0,10);
					System.out.println("dateinput: " + dateInput);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate eventDate = LocalDate.parse(dateInput, formatter);
					sb.append("New event name: " + message.getText());

				}catch(Exception e){
					System.out.println(e);
					editRequest.setText("Please specify the date in the correct format (e.g. 11/21/2017 10:00PM)");
					return editRequest;
				}
			else if(type.equals(EventEdit.EDITLOCATION)){
				event.setEventLocation(message.getText());
				sb.append("New event name: " + message.getText());
				
			}
			String eventText = EventBuilder.build(event);
			event.setEventText(eventText);
			
		}
		editRequest.setText(sb.toString());
		
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder(3,1);
		int count = 0;
		for(String editField : EventEdit.EDITFIELDLIST){
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(EventEdit.EDITBUTTONLIST[count]);
			count++;
			StringBuilder buttonString = new StringBuilder();
			buttonString.append(EventEdit.EDITTYPE + " " + event.getEventName());
			System.out.println("eventName: " + event.getEventName());
			buttonString.append(" ");
			buttonString.append(editField);
			button.setCallbackData(buttonString.toString());
			keyboardBuilder.addButton(button);
		}
		InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
		editRequest.setReplyMarkup(markup);
		editRequest.setParseMode("HTML");
		editMap.put(userId, null);
		return editRequest;
	}
}
