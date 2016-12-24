package org.wilson.telegram.callbackhandler;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

//Handles the response message (response isn't able to be serialized so this is to get rid of the longer logs)

public class EditTextCallback implements SentCallback<Message> {

	@Override
	public void onResult(BotApiMethod<Message> method, Message response) {
		// TODO Auto-generated method stub
		System.out.println(response.getText());
		
	}

	public void onError(BotApiMethod<Message> method,
			TelegramApiRequestException apiException) {
		System.out.println("Error: " + apiException);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onException(BotApiMethod<Message> method, Exception exception) {
		// TODO Auto-generated method stub
		
	}

}
