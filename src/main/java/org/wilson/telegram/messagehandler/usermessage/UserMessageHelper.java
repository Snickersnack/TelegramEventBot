package org.wilson.telegram.messagehandler.usermessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.commandprocesses.EventStartCommand;
import org.wilson.telegram.commandprocesses.ImgurHelper;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.messagehandler.MessageParser;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.templates.EventClear;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.templates.EventRespondees;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventHelper;
import org.wilson.telegram.util.EventPersistence;
import org.wilson.telegram.util.KeyboardBuilder;
import org.wilson.telegram.util.RespondeesCommand;

public class UserMessageHelper extends MessageParser{

	SendMessage sendMessageRequest;
	String command;
	HashMap<Integer, EventModel> inProgressCache = Cache.getInstance().getInProgressEventCreations();
	Integer userId;
	
	public UserMessageHelper(SendMessage sendMessage){
		sendMessageRequest = sendMessage;
	}
	public BotApiMethod<?> parse(Message message) {
		
		command = message.getText();
		User user = message.getFrom();
		userId = user.getId();
		Long chatId = message.getChatId();
		EventModel inProgressEvent = inProgressCache.get(userId);

		if (command.startsWith(Commands.STARTCOMMAND)) {
			if (inProgressEvent != null){
				inProgressCache.put(userId, null);
				EventPersistence.delete(inProgressEvent);
			}
			return EventStartCommand.start(user, chatId);
		}

	    if (inProgressEvent != null) {
    		//We always clear in progress things if it's not view
	    	
	    	if(command.startsWith(Commands.INIT_CHAR) && !command.startsWith(Commands.VIEWCOMMAND) && !command.startsWith(Commands.SKIPCOMMAND)){
	    		EventHelper.clearInProgress(userId);
	    		inProgressEvent = null;
			    if(command.startsWith(Commands.CANCELCOMMAND)){
					KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
					InlineKeyboardButton button = new InlineKeyboardButton();
					button.setText("Return to a channel");
					button.setSwitchInlineQuery(" ");
					keyboardBuilder.addButton(button);
					InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
					sendMessageRequest.setReplyMarkup(markup);
					sendMessageRequest.setText("Return to the /menu");
					return sendMessageRequest;
				}
	    	}
	    }

	    if (inProgressEvent != null) {
				sendMessageRequest = EventStartCommand.setEventInfo(message);
			    
		}
		

		else if(command.startsWith(Commands.MENUCOMMAND)){
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
			InlineKeyboardMarkup markup = keyboardBuilder.buildMenu();
			sendMessageRequest.setReplyMarkup(markup);
			StringBuilder sb = new StringBuilder();
			sb.append(EventMenu.MENUTITLE);
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append(EventMenu.MENUDESCRIPTION);
			sb.append(System.getProperty("line.separator"));
			sendMessageRequest.setText(sb.toString());


		}
		
		else if(command.startsWith(Commands.RESPONDEESCOMMAND)){
			RespondeesCommand respond = new RespondeesCommand();
			respond.buildSend(userId, chatId);
			return null;
		}
		
		else if(command.startsWith(Commands.VIEWCOMMAND)){
			boolean eventsSent = sendViewCommand(userId);
			if (!eventsSent) {
				sendMessageRequest.setText("You haven't responded to any events!");
				KeyboardBuilder kb = new KeyboardBuilder();
				InlineKeyboardMarkup markup = kb.buildReturnMenu();
				sendMessageRequest.setReplyMarkup(markup);
			} else {
				return null;
			}
		}
		
		//Provides a bunch of buttons. Actual edit is handled in the callback handler
		else if (command.startsWith(Commands.EDITCOMMAND)) {
			HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
			HashSet<EventModel> eventSet = userMap.get(userId);
			StringBuilder sb = new StringBuilder();
			sb.append(EventEdit.EDITTITLE);
			sb.append(System.getProperty("line.separator"));

			if(eventSet != null && eventSet.size() > 0){
				sendMessageRequest = EventBuilder.listAllEvents(userId, sendMessageRequest, EventEdit.EDITTYPE, sb);			
			}else{
				sendMessageRequest.setText("<i>You currently have no events</i>");
				KeyboardBuilder kb = new KeyboardBuilder();
				InlineKeyboardMarkup markup = kb.buildReturnMenu();
				sendMessageRequest.setReplyMarkup(markup);
			}
			
			sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);

			return sendMessageRequest;

		}
		else if (command.startsWith(Commands.DELETEEVENTSCOMMAND)) {
				HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
				HashSet<EventModel> eventSet = userMap.get(userId);
				StringBuilder sb = new StringBuilder();
				sb.append(EventDelete.DELETETITLE);
				sb.append(System.getProperty("line.separator"));
				if(eventSet != null && eventSet.size() > 0){
					sendMessageRequest = EventBuilder.listAllEvents(userId, sendMessageRequest, EventDelete.DELETETYPE, sb);	
				}else{
					sendMessageRequest.setText("<i>You currently have no events</i>");
					KeyboardBuilder kb = new KeyboardBuilder();
					InlineKeyboardMarkup markup = kb.buildReturnMenu();
					sendMessageRequest.setReplyMarkup(markup);
				}
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);

				return sendMessageRequest;
			

			
			
		} else if (command.startsWith(Commands.CLEAREVENTSCOMMAND)) {
			
			
			sendMessageRequest.setText("Are you sure you want to remove all your events?");
			sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
			KeyboardBuilder kb = new KeyboardBuilder(1,2);
			InlineKeyboardButton acceptButton = new InlineKeyboardButton();
			InlineKeyboardButton declineButton = new InlineKeyboardButton();
			acceptButton.setText(EventClear.ACCEPT);
			acceptButton.setCallbackData(EventClear.TYPE + " " + EventClear.ACCEPT);
			declineButton.setText(EventClear.DECLINE);
			declineButton.setCallbackData(EventClear.TYPE + " " + EventClear.DECLINE);
			kb.addButton(acceptButton);
			kb.addButton(declineButton);
			InlineKeyboardMarkup  markup = kb.buildMarkup();
			sendMessageRequest.setReplyMarkup(markup);
			
			


		}
		
		else if(Cache.getInstance().getInProgressEdit().get(message.getFrom().getId())!=null){
			BotApiMethod<?> sendRequest = EditMessageHelper.parse(message);
			SendMessage test = (SendMessage) sendRequest;
			System.out.println(test.getText());
			
			return sendRequest;
			
		}else{
			return null;
		}
		
		return sendMessageRequest;
	}
	
	private boolean sendViewCommand(Integer userId) {
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getUserRespondeeMap();
		boolean shared = false;
		HashSet<EventModel> userEvents = userMap.get(userId);
		
		if (userEvents != null) {
			if (userEvents.size() != 0) {
				sendMessageRequest.setText("Here's a list of events you have responded to:");
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
				try {
					sendMessage(sendMessageRequest);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shared = true;
				for (EventModel event : userEvents) {
					KeyboardBuilder kb = new KeyboardBuilder();
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					if(event.getImgur() == null){
						markup.setKeyboard(event.getEventGrid());
						sendMessageRequest.setReplyMarkup(markup);
					}else{
						sendMessageRequest.setReplyMarkup(kb.buildView(event));

					}
					sendMessageRequest.disableWebPagePreview();
					sendMessageRequest.setText(event.getEventText());
					sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
					try {
						sendMessage(sendMessageRequest);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
//		sendMessageRequest = new SendMessage();
		return shared;
	}
}
