package org.wilson.telegram.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.templates.EventRespondees;

public class RespondeesCommand extends UpdateHandler {

	public void buildSend(Integer userId, Long chatId) {

		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(chatId);
		sendMessageRequest.setText(EventRespondees.TITLE);
		sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance()
				.getMasterEventMap();
		EventModel event;
		HashSet<EventModel> userEvents = userMap.get(userId);
		if (userEvents != null && userEvents.size() != 0) {

			for (EventModel channelEvent : userEvents) {

				event = channelEvent;
				StringBuilder sb = new StringBuilder();
				StringBuilder accept = new StringBuilder();
				StringBuilder reject = new StringBuilder();

				sb.append("<strong>" + event.getEventName()+"</strong>");
				sb.append(System.getProperty("line.separator"));

				int count = 1;
				
				for (Entry<String, Boolean> entry : event.getTotalResponses()
						.entrySet()) {

					Boolean attend = entry.getValue();
					String person = entry.getKey();
					if (attend) {
						accept.append(" " + person);
						accept.append(",");
						count++;
					} else {
						reject.append(" " + person);
						reject.append(",");
						count++;
					}

				}
				if (count == 1) {
					sb.append("<i>No one has responded yet</i>");
				} else {
					if (accept.length() > 1 && reject.length() == 0) {
						sb.append("✔️"
								+ accept.toString().substring(0,accept.length() - 1) );
					} else if (accept.length() == 0 && reject.length() > 1) {
						sb.append(  "✖️ " + reject.toString().substring(0,
								reject.length() - 1));

					} else {
						sb.append("✔️"
								 +accept.toString().substring(0,accept.length() - 1)
								 + 	System.getProperty("line.separator")
								+ "✖️" + reject.toString().substring(0,
										reject.length() - 1));
					}

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

		}
		
		//We have no events
		else{
			sendMessageRequest.setText("You currently have no events");
		}
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1, 1);
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setCallbackData(EventMenu.MENUDATA + " " + EventMenu.MENUSEND);
		button.setText(EventMenu.MENUTEXT);
		keyboardBuilder.addButton(button);
		InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
//		sendMessageRequest.setReplyMarkup(markup);
//		sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
//		sendMessageRequest.setChatId(chatId);
//		try {
//			sendMessage(sendMessageRequest);
//		} catch (TelegramApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public EditMessageText buildEdit(Integer userId, EditMessageText editRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append(EventRespondees.TITLE);
		sb.append(System.getProperty("line.separator"));
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance()
				.getMasterEventMap();
		EventModel event;
		HashSet<EventModel> userEvents = userMap.get(userId);
		if (userEvents != null && userEvents.size() != 0) {

			for (EventModel channelEvent : userEvents) {

				event = channelEvent;
				StringBuilder accept = new StringBuilder();
				StringBuilder reject = new StringBuilder();

				sb.append("<strong>" + event.getEventName()+"</strong>");
				sb.append(System.getProperty("line.separator"));

				int count = 1;
				for (Entry<String, Boolean> entry : event.getTotalResponses()
						.entrySet()) {

					Boolean attend = entry.getValue();
					String person = entry.getKey();
					if (attend) {
						accept.append(person);
						accept.append(",");
						count++;
					} else {
						reject.append(person);
						reject.append(",");
						count++;
					}

				}
				if (count == 1) {
					sb.append("<i>No one has responded yet</i>");
				} else {
					if (accept.length() > 1 && reject.length() == 0) {
						sb.append("✔️ "
								+ accept.toString().substring(0,accept.length() - 1) );
					} else if (accept.length() == 0 && reject.length() > 1) {
						sb.append(  "✖️  " + reject.toString().substring(0,
								reject.length() - 1));

					} else {
						sb.append("✔️️ "
								 +accept.toString().substring(0,accept.length() - 1)
								 + 	System.getProperty("line.separator")
								+ "✖️  " + reject.toString().substring(0,
										reject.length() - 1));
					}

				}
				
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));

			}
		editRequest.setText(sb.toString());

		}else{
			editRequest.setText("You currently have no events");
		}
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder(1, 1);
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setCallbackData(EventMenu.MENUDATA + " " + EventMenu.MENUSEND);
		button.setText(EventMenu.MENUTEXT);
		keyboardBuilder.addButton(button);
		InlineKeyboardMarkup markup = keyboardBuilder.buildMarkup();
		editRequest.setReplyMarkup(markup);
		editRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);

		return editRequest;
	}

}
