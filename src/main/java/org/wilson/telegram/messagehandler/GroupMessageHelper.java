package org.wilson.telegram.messagehandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.templates.EventView;
import org.wilson.telegram.util.CacheUpdater;
import org.wilson.telegram.util.KeyboardBuilder;

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
		PriorityQueue<EventModel> pQueue = new PriorityQueue<EventModel>(new EventComparator());
		
		if (channelEvents != null) {
			if (channelEvents.size() != 0) {
				shared = true;
				pQueue.addAll(channelEvents);
				int maxEvents = BotConfig.MAX_CHANNEL_VIEW;
				sendMessageRequest.setText(EventView.TITLE);
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
				try {
					sendMessage(sendMessageRequest);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(maxEvents>0){
					EventModel channelEvent = pQueue.poll();
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					channelEvent.setShowing(false);
					if(channelEvent.getImgur() == null){
						markup.setKeyboard(channelEvent.getEventGrid());
					}else{
						KeyboardBuilder kb = new KeyboardBuilder();
						markup = kb.buildView(channelEvent);
					}
					sendMessageRequest.setReplyMarkup(markup);
					sendMessageRequest.setText(channelEvent.getEventText());
					sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
					sendMessageRequest.disableWebPagePreview();
					try {
						sendMessage(sendMessageRequest);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					maxEvents--;
				}
			}
		}
			

			return shared;
	}
	
	public class EventComparator implements Comparator<EventModel>{
		@Override
	    public int compare(EventModel x, EventModel y)
	    {
			if(x.getTotalResponses().size() > y.getTotalResponses().size()){
			return -1;
			}
			if(x.getTotalResponses().size() < y.getTotalResponses().size()){
			return 1;
			}
			
			return 0;
	    	}
	    }
	
}
