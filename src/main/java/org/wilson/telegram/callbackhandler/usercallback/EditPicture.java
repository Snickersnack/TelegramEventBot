package org.wilson.telegram.callbackhandler.usercallback;

import java.util.HashMap;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.commandprocesses.ImgurHelper;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventPersistence;
import org.wilson.telegram.util.KeyboardBuilder;

public class EditPicture {

	public static SendMessage execute(Message message){
		ImgurHelper imgur = new ImgurHelper(message);
		Integer userId = message.getFrom().getId();
		HashMap<Integer, EditModel> editMap = Cache.getInstance().getInProgressEdit();
		EventModel event = editMap.get(userId).getEventModel();
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId());
		sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		
		String imgurUrl = null;
		
		if(!message.hasPhoto() && message.getSticker() == null){
			sendMessageRequest.setText("<i>Please upload a </i><strong>photo</strong> <i>or a</i> <strong>sticker</strong>");
			return sendMessageRequest;
		}
		
		try{
			imgurUrl = imgur.post();

		}catch(Exception e){
			e.printStackTrace();
			sendMessageRequest.setText("<i>There was an issue uploading. Please send again </i>");
			return sendMessageRequest;
		}
		
		event.setImgur(imgurUrl);
		String eventText = EventBuilder.build(event);
		event.setEventText(eventText);
		EventPersistence.saveOrUpdate(event);
		StringBuilder sb = new StringBuilder();
		sb.append(EventEdit.EDITTITLE );
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append("New picture: " + imgurUrl);
		sendMessageRequest.setText(sb.toString());
		KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
		
		InlineKeyboardMarkup markup = keyboardBuilder.buildEditMenu(event.getEventId().toString());
		sendMessageRequest.setReplyMarkup(markup);
		sendMessageRequest.setParseMode("HTML");
		editMap.put(userId, null);
		return sendMessageRequest;
	}
}
