package org.wilson.telegram.callbackhandler.usercallback;

import java.util.HashSet;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.commandprocesses.EventStartCommand;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventClear;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.templates.EventRespondees;
import org.wilson.telegram.util.KeyboardBuilder;
import org.wilson.telegram.util.RespondeesCommand;

public class UserCallback {

	Integer userId;
	Long chatId;
	
	public UserCallback(Integer userId, Long chatId){
		this.userId = userId;
		this.chatId = chatId;
	}
	
	public BotApiMethod<?> parse(Update update, EditMessageText editMessageRequest, String callbackType, String[] dataArray){
		
		CallbackQuery callback = update.getCallbackQuery();
		if(callbackType.equals(EventEdit.EDITTYPE)){
			EditEventHandler editor = new EditEventHandler(update);
			editMessageRequest = editor.handleCallbackQuery();
				
		}
		
		else if(callbackType.equals(EventDelete.DELETETYPE)){
			DeleteEventHandler deleteEvent = new DeleteEventHandler(update);
			editMessageRequest = deleteEvent.handleCallbackQuery();
		}
		
		
		
		else if(callbackType.equals(EventMenu.MENUDATA)){
			editMessageRequest.setText(EventMenu.MENUINTRO);
			KeyboardBuilder kb = new KeyboardBuilder();
			InlineKeyboardMarkup markup = kb.buildMenu();
			editMessageRequest.setReplyMarkup(markup);

		}
		else if(callbackType.equals("Start")){
			User user = callback.getFrom();	
			return EventStartCommand.start(user, chatId);
		}
		
		else if(callbackType.equals(EventRespondees.TYPE)){
			HashSet<EventModel> set = Cache.getInstance().getMasterEventMap().get(userId);
			if( set == null || set.size() == 0){
				String text = EventRespondees.TITLE
						+System.getProperty("line.separator")
						+System.getProperty("line.separator")
						+EventRespondees.NOEVENTS;
				editMessageRequest.setText(text);
				editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
				markup = keyboardBuilder.buildReturnMenu();
				editMessageRequest.setReplyMarkup(markup);
			
			}else{
				
			
			RespondeesCommand respond = new RespondeesCommand();
			editMessageRequest = respond.buildEdit(userId, editMessageRequest);
			
			}
			
		}
		else if(callbackType.equals(EventClear.TYPE)){
			EventClearCallback eventCallback = new EventClearCallback(update, editMessageRequest);
			eventCallback.execute(userId, dataArray);
		}
		
		return editMessageRequest;
	}
}
