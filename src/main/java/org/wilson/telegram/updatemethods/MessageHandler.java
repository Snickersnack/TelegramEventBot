package org.wilson.telegram.updatemethods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.commandprocesses.EventStartCommand;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;
import org.wilson.telegram.config.AvailableCommands;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.util.CacheUpdater;

/**
 * Service to parse commands
 * 
 */

// Takes in the message and parses for command
// Runs service based on command match

public class MessageHandler extends UpdateHandler {

	protected String command;
	protected Message message;
	SendMessage sendMessageRequest;
	protected static final String TOKEN = BotConfig.TOKENNEWBOT;
	Long chatId;

	public MessageHandler(Message message) {
		this.message = message;
		this.chatId = message.getChatId();
		sendMessageRequest = new SendMessage();

	}

	// Push commands to service classes

	public SendMessage parse() throws TelegramApiException {
		command = message.getText().toLowerCase();
		// String user = message.getFrom().getUserName();
		Integer userId = message.getFrom().getId();
		HashMap<Integer, EventModel> inProgressCache = Cache.getInstance()
				.getInProgressEventCreations();

		// Check if we're in the middle of creating an event for user

		if (inProgressCache.get(userId) != null) {
			if (message.isUserMessage()) {
				sendMessageRequest = EventStartCommand.setEventInfo(message);

			} else {
				sendMessageRequest.setText("Events must be created privately");
			}
		}

		else if (command.startsWith(AvailableCommands.DELETEEVENTSCOMMAND)) {
			sendMessageRequest.setChatId(message.getChatId());
			String eventName = command.substring(7);
			if (message.isUserMessage()) {

				HashMap<Integer, HashSet<EventModel>> newMap = Cache.getInstance().getUserEventMap();
				HashSet<EventModel> set = newMap.get(userId);
				Iterator<EventModel> it = set.iterator();
				while (it.hasNext()) {
					EventModel event = it.next();
					if (event.getEventName().toLowerCase().equals(eventName.toLowerCase())) {
						it.remove();
						break;
					}
				}
				newMap.put(userId, set);
				Cache.getInstance().setUserEventMap(newMap);
				sendMessageRequest.setText(eventName
						+ " has been deleted from your events");
			} else {
				sendMessageRequest.setText("Events must be deleted privately");
			}
		} else if (command.startsWith(AvailableCommands.CLEAREVENTSCOMMAND)) {
			sendMessageRequest.setChatId(message.getChatId());
			if (message.isUserMessage()) {
				Cache.getInstance().clearUserEvents(message.getFrom().getId());
				sendMessageRequest.setText("Your events have been cleared");
			} else {
				sendMessageRequest.setText("Events must be cleared privately");
			}

		} else if (command.startsWith(AvailableCommands.HELPCOMMAND)
				&& command.substring(0, 5).equals("/help")) {

		}

		// else if(command.equals(Commands.TESTCOMMAND)){
		//
		// }
		else if (command.equals(AvailableCommands.VIEWCOMMAND)) {
			boolean eventsSent = sendViewCommand(message.getChatId());
			if (!eventsSent) {
				sendMessageRequest
						.setText("There are no events shared to this channel");
				sendMessageRequest.setChatId(chatId);
			} else {
				return null;
			}

		} else if (command.startsWith(AvailableCommands.STARTCOMMAND)) {
			// See if user is already creating an event
			if (inProgressCache.get(userId) != null) {
				sendMessageRequest.setChatId(message.getChatId());
				sendMessageRequest.setText("Event creation already in progress");
			}
			// Check if we are in a group chat
			else if (message.getChat().isUserChat()) {
				EventModel newEvent = new EventModel();
				newEvent.setEventHostFirst(message.getFrom().getFirstName());
				newEvent.setEventInputStage(1);

				inProgressCache.put(userId, newEvent);

				// Check if the cache has an existing user, if not add it to
				// eventMap
				HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance()
						.getUserEventMap();
				if (map.get(userId) == null) {
					map.put(userId, new HashSet<EventModel>());
				}
				Cache.getInstance().setInProgressEventCreations(inProgressCache);
				sendMessageRequest.setChatId(message.getChatId());
				sendMessageRequest.setText("What is the name of your event?");
			} else {
				sendMessageRequest.setChatId(message.getChatId());
				sendMessageRequest.setText("Events must be created privately");
			}

			// update our event list every time we see an event posted
		} else {
			CacheUpdater.updateChannelEventMap(message);
		}

		return sendMessageRequest;

	}



	private boolean sendViewCommand(Long channelId) {
		HashMap<Long, HashSet<EventModel>> channelMap = Cache.getInstance()
				.getChannelEventMap();
		EventModel event;
		boolean shared = false;
		HashSet<EventModel> channelEvents = channelMap.get(channelId);
		if (channelEvents != null) {
			if (channelEvents.size() != 0) {
				shared = true;
				for (EventModel channelEvent : channelEvents) {
					event = channelEvent;
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					markup.setKeyboard(event.getEventGrid());
					sendMessageRequest.setReplyMarkup(markup);
					sendMessageRequest.setText(event.getEventText());
					sendMessageRequest.setChatId(chatId);
					sendMessageRequest.setParseMode("HTML");
					try {
						Message botMessage = sendMessage(sendMessageRequest);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		sendMessageRequest = new SendMessage();
		return shared;
	}

}
