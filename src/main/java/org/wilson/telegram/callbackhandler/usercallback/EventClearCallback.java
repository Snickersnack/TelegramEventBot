package org.wilson.telegram.callbackhandler.usercallback;

import java.util.HashSet;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventClear;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.util.EventPersistence;
import org.wilson.telegram.util.KeyboardBuilder;

public class EventClearCallback {
	
	private EditMessageText editMessageRequest;
	private Update update;
	
	public EventClearCallback(Update update, EditMessageText editMessageRequest){
		this.update = update;
		this.editMessageRequest = editMessageRequest;
	}

	public BotApiMethod<?> execute(Integer userId, String[] dataArray){
		
		HashSet<EventModel> set = Cache.getInstance().getMasterEventMap().get(userId);
		StringBuilder sb = new StringBuilder();
		sb.append(EventClear.TITLE
						+ System.getProperty("line.separator")
						+ System.getProperty("line.separator"));
		
		if( set == null || set.size() == 0){
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
			markup = keyboardBuilder.buildReturnMenu();
			editMessageRequest.setReplyMarkup(markup);
			sb.append(EventClear.NOEVENTS);
			editMessageRequest.setText(sb.toString());
			editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
			
		}
		
		//we are in confirmation
		else if(dataArray.length>1){
			
			//proceed with event deletion
			if(dataArray[1].equals(EventClear.ACCEPT)){
				EventPersistence.deleteAll(userId);
				Cache.getInstance().clearUserEvents(userId);
				sb.append(EventClear.CLEARED);
				editMessageRequest.setText(sb.toString());
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
				markup = keyboardBuilder.buildReturnMenu();
				editMessageRequest.setReplyMarkup(markup);
				editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
			
			//If not deleting, provide menu
			}else{
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
				InlineKeyboardMarkup markup = keyboardBuilder.buildMenu();
				editMessageRequest.setReplyMarkup(markup);
				editMessageRequest.setText(EventMenu.MENUINTRO);

			}
		
		//Ask for confirmation of deletion
		}else{
			sb.append(EventClear.CONFIRMATION);
			editMessageRequest.setText(sb.toString());
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
			InlineKeyboardMarkup markup = keyboardBuilder.buildClearConfirmation();
			editMessageRequest.setReplyMarkup(markup);
		}
		
		return editMessageRequest;
	}
}
