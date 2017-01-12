package org.wilson.telegram.callbackhandler;

import java.util.HashSet;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
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
import org.wilson.telegram.templates.EventResponse;
import org.wilson.telegram.util.EventPersistence;
import org.wilson.telegram.util.KeyboardBuilder;
import org.wilson.telegram.util.RespondeesCommand;

public class CallbackHandler {

	
	public static BotApiMethod<?> parse(Update update){
		CallbackQuery callback = update.getCallbackQuery();
		String data = callback.getData();
		String inline = callback.getInlineMessageId();
		String[] dataArray = data.split(" ");
		EditMessageText editMessageRequest = new EditMessageText();
		editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		String callbackType = dataArray[0];
		
		if(inline == null){
			
			Message message = callback.getMessage();				
			Long chatId = message.getChatId();
			Integer messageId = message.getMessageId();
		    Integer userId = callback.getFrom().getId();
			editMessageRequest.setMessageId(messageId);
			editMessageRequest.setChatId(chatId);
			
		//if there's a message, that means its not an event response
		
		
		if(callbackType.equals(EventResponse.ACCEPT) || callbackType.equals(EventResponse.DECLINE)){
			InvitationHandler invite = new InvitationHandler(update);
			editMessageRequest = invite.handleCallbackQuery();
		}
		
		else if(callbackType.equals(EventEdit.EDITTYPE)){
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
				
				editMessageRequest.setText(EventRespondees.NOEVENTS);
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
		}
	}else{
		InvitationHandler invite = new InvitationHandler(update);
		editMessageRequest = invite.handleCallbackQuery();
	}

		return editMessageRequest;
	}
}
