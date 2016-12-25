package org.wilson.telegram.messagehandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.commandprocesses.EventStartCommand;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.KeyboardBuilder;

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
		userId = message.getFrom().getId();

		if (command.startsWith(Commands.STARTCOMMAND)) {
			EventModel newEvent = new EventModel();
			newEvent.setEventHostFirst(message.getFrom().getFirstName());
			newEvent.setEventInputStage(1);

			inProgressCache.put(userId, newEvent);

			HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getMasterEventMap();
			if (map.get(userId) == null) {
				map.put(userId, new HashSet<EventModel>());
			}
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
		}

		else if (inProgressCache.get(userId) != null) {

		    if(command.startsWith(Commands.CANCELCOMMAND)){
				inProgressCache.put(userId, null);
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("Cancelled");
				button.setSwitchInlineQuery(" ");
				keyboardBuilder.addButton(button);
				InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
				sendMessageRequest.setReplyMarkup(markup);
				sendMessageRequest.setText("Event Cancelled!");
				return sendMessageRequest;
			}else{
				sendMessageRequest = EventStartCommand.setEventInfo(message);

			}
		}
		
		else if(command.startsWith(Commands.RESPONDEESCOMMAND)){
			HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
			EventModel event;
			HashSet<EventModel> userEvents = userMap.get(userId);
			if (userEvents != null && userEvents.size() != 0) {
				for (EventModel channelEvent : userEvents) {
					event = channelEvent;
					StringBuilder sb = new StringBuilder();
					sb.append("<strong>" + event.getEventName() + "</strong>");
					sb.append(System.getProperty("line.separator"));
					sb.append("Persons responded: ");
					int count = 1;
					for(String person : event.getTotalResponses()){
						if(count == event.getTotalResponses().size()){
							sb.append(person);
						}else{
							sb.append(person);
							sb.append(",");
							count++;
						}

					
					sendMessageRequest.setText(sb.toString());
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
			return null;
		}
		//Provides a bunch of buttons. Actual edit is handled in the callback handler
		else if (command.startsWith(Commands.EDITCOMMAND)) {
			HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
			HashSet<EventModel> eventSet = userMap.get(userId);
			if(eventSet != null && eventSet.size() > 0){
				sendMessageRequest = EventFinder.listAllEvents(userId, sendMessageRequest, EventEdit.EDITTYPE);			
			}else{
				sendMessageRequest.setText("<i>You currently have no events</i>");
			}
			
			return sendMessageRequest;

		}
		else if (command.startsWith(Commands.DELETEEVENTSCOMMAND)) {
				HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
				HashSet<EventModel> eventSet = userMap.get(userId);
				if(eventSet != null && eventSet.size() > 0){
					sendMessageRequest = EventFinder.listAllEvents(userId, sendMessageRequest, EventDelete.DELETETYPE);	
				}else{
					sendMessageRequest.setText("<i>You currently have no events</i>");
				}
				
				return sendMessageRequest;
			

			
			
		} else if (command.startsWith(Commands.CLEAREVENTSCOMMAND)) {
			Cache.getInstance().clearUserEvents(userId);
			sendMessageRequest.setText("<i>Your events have been cleared</i>");

		}
		
		else if(Cache.getInstance().getInProgressEdit().get(message.getFrom().getId())!=null){
			BotApiMethod<?> sendRequest = EditMessageHelper.parse(message);
			SendMessage test = (SendMessage) sendRequest;
			return sendRequest;
			
		}else{
			//return null when there's nothing to update...seems like code smell
			return null;
		}
		return sendMessageRequest;
	}
}
