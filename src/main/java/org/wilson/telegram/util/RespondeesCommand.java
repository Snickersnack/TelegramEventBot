package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventMenu;

public class RespondeesCommand extends UpdateHandler{

	public void build(Integer userId, Long chatId, boolean buttonMenu){
		
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(chatId);
		
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
		EventModel event;
		HashSet<EventModel> userEvents = userMap.get(userId);
		if (userEvents != null && userEvents.size() != 0) {
			for (EventModel channelEvent : userEvents) {
				
				event = channelEvent;
				StringBuilder sb = new StringBuilder();
				sb.append("<strong>" + event.getEventName() + "</strong>");
				sb.append(System.getProperty("line.separator"));
				sb.append("Responded: ");
				int count = 1;
				for(String person : event.getTotalResponses()){
					if(count == event.getTotalResponses().size()){
						sb.append(person);
					}else{
						sb.append(person);
						sb.append(",");
						count++;
					}
				}
				if(count == 1){
					sb.append("<i>No one has responded yet</i>");
				}


				sendMessageRequest.setText(sb.toString());
				sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
				try {
					sendMessage(sendMessageRequest);
					System.out.println("sent");
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(buttonMenu){
				sendMessageRequest.setText("Click to return");
				KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setCallbackData(EventMenu.MENUDATA + " " + EventMenu.MENUSEND );
				button.setText(EventMenu.MENUTEXT);
				keyboardBuilder.addButton(button);
				InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
				sendMessageRequest.setReplyMarkup(markup);
				try {
					sendMessage(sendMessageRequest);
					System.out.println("sent");
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}
}

