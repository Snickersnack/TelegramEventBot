package org.wilson.telegram.messagehandler;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.util.CacheUpdater;

public class GroupMessageHelper extends MessageParser{
	
	
	SendMessage sendMessageRequest;
	
	public GroupMessageHelper(SendMessage sendMessage){
		sendMessageRequest = sendMessage;
	}
	
	public SendMessage parse(Message message){
		
		String command = message.getText();
		
		
		if (command.startsWith(Commands.VIEWCOMMAND)) {
			boolean eventsSent = sendViewCommand(message.getChatId());
			if (!eventsSent) {
				sendMessageRequest.setText("There are no events shared to this channel");
			} else {
				return null;
			}

		} 

		else {
			System.out.println("updating cache");
			CacheUpdater.updateChannelEventMap(message);
			return null;
		}
		return sendMessageRequest;
	}
	
	
	private boolean sendViewCommand(Long channelId) {
		HashMap<Long, HashSet<EventModel>> channelMap = Cache.getInstance().getChannelEventMap();
		boolean shared = false;
		HashSet<EventModel> channelEvents = channelMap.get(channelId);
		
		if (channelEvents != null) {
			if (channelEvents.size() != 0) {
				shared = true;
				for (EventModel channelEvent : channelEvents) {
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					markup.setKeyboard(channelEvent.getEventGrid());
					sendMessageRequest.setReplyMarkup(markup);
					sendMessageRequest.setText(channelEvent.getEventText());
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
