package org.wilson.telegram.callbackhandler;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.wilson.telegram.callbackhandler.usercallback.UserCallback;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.templates.EventResponse;

public class CallbackHandler {

	public static BotApiMethod<?> parse(Update update) {
		CallbackQuery callback = update.getCallbackQuery();
		String data = callback.getData();
		String inline = callback.getInlineMessageId();
		String[] dataArray = data.split(" ");
		EditMessageText editMessageRequest = new EditMessageText();
		editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		String callbackType = dataArray[0];

		// Respond to text invitation

		if (inline == null) {
			Message message = callback.getMessage();
			Long chatId = message.getChatId();
			Integer messageId = message.getMessageId();
			Integer userId = callback.getFrom().getId();
			editMessageRequest.setMessageId(messageId);
			editMessageRequest.setChatId(chatId);

			if (callbackType.equals(EventResponse.ACCEPT)
					|| callbackType.equals(EventResponse.DECLINE)) {
				InvitationHandler invite = new InvitationHandler(update);
				editMessageRequest = invite.handleCallbackQuery();
			} else {
				UserCallback userCallback = new UserCallback(userId, chatId);
				editMessageRequest = (EditMessageText) userCallback.parse(update, editMessageRequest, callbackType, dataArray);
			}

		// Respond to inline invitation
		
		} else {
			InvitationHandler invite = new InvitationHandler(update);
			editMessageRequest = invite.handleCallbackQuery();
		}

		return editMessageRequest;
	}
}
