package org.wilson.telegram.callbackhandler;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.callbackhandler.groupcallback.InvitationHandler;
import org.wilson.telegram.callbackhandler.usercallback.UserCallback;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.templates.EventResponse;
import org.wilson.telegram.util.EventHelper;

public class CallbackHandler extends UpdateHandler {

	public BotApiMethod<?> parse(Update update) {
		CallbackQuery callback = update.getCallbackQuery();
		String data = callback.getData();
		String inline = callback.getInlineMessageId();
		String[] dataArray = data.split(" ");
		EditMessageText editMessageRequest = new EditMessageText();
		editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		String callbackType = dataArray[0];

		//This is a text callback
		if (inline == null) {
			Message message = callback.getMessage();
			Long chatId = message.getChatId();
			Integer messageId = message.getMessageId();
			Integer userId = callback.getFrom().getId();
			editMessageRequest.setMessageId(messageId);
			editMessageRequest.setChatId(chatId);
			
			//Its a group callback Event
			if (callbackType.equals(EventResponse.ACCEPT) || callbackType.equals(EventResponse.DECLINE)) {
				InvitationHandler invite = new InvitationHandler(update);
				editMessageRequest = invite.handleCallbackQuery();
			
			//It's a user callback Event
			} else {
				if(Cache.getInstance().getInProgressEventCreations().get(userId) != null){
					EventHelper.clearInProgress(userId);
					AnswerCallbackQuery answer = new AnswerCallbackQuery();
					answer.setText("Cancelling current event creation");
					answer.setCallbackQueryId(callback.getId());
					try {
						sendApiMethod(answer);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				UserCallback userCallback = new UserCallback(userId, chatId);
				return userCallback.parse(update, editMessageRequest, callbackType, dataArray);
			}

			
		//This is an inline callback
		} else {
			InvitationHandler invite = new InvitationHandler(update);
			editMessageRequest = invite.handleCallbackQuery();
		}

		return editMessageRequest;
	}
}
