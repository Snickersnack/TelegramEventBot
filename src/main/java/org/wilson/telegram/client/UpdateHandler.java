package org.wilson.telegram.client;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.callbackhandler.CallbackHandler;
import org.wilson.telegram.callbackhandler.EditTextCallback;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.inlinequeryhandler.InlineQueryHandler;
import org.wilson.telegram.messagehandler.MessageParser;
import org.wilson.telegram.util.CacheUpdater;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Re-implemented handler class. Deals with message updates
 * 
 */

public class UpdateHandler extends TelegramLongPollingBot {
	private static final String TOKEN = BotConfig.TOKENNEWBOT;
	private static final String BOTNAME = BotConfig.USERNAMENEWBOT;

	ObjectMapper mapper = new ObjectMapper();

	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		System.out.println("UPDATE FOR DEBUGGING: " + update);
		
		try {
			BotApiMethod<?> msg = handleUpdate(update);
			
			if (msg != null) {
				executeMessage(msg);
			}

		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public BotApiMethod onWebhookUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		return null;
	}

	public BotApiMethod<?> handleUpdate(Update update)
			throws TelegramApiException {
		
		
		if(update.hasChosenInlineQuery()){
			CacheUpdater.updateInlineId(update);
		}
		else if (update.hasInlineQuery()) {
			InlineQueryHandler iq = new InlineQueryHandler();
			AnswerInlineQuery aQuery = iq.handleInlineQuery(update);
			return aQuery;
		}
		else if (update.hasCallbackQuery()) {
			BotApiMethod<?> request = CallbackHandler.parse(update);

			return request;


		}
		else if (update.hasMessage()){
			Message message = update.getMessage();
			//if this is an event, check for exact string match of event title

			MessageParser messageHandler = new MessageParser(message);
			try {
				return messageHandler.parse();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;

	}
	


	private void executeMessage(BotApiMethod<?> msg)
			throws TelegramApiException {

		if(msg == null){
			return;
		}
		
		
		if (msg instanceof SendMessage) {
			SendMessage sMessage = (SendMessage) msg;
			if(sMessage.getChatId()!=null){
				Message botMessage = sendMessage(sMessage);

			}


		} else if(msg instanceof EditMessageText){
			EditTextCallback cb = new EditTextCallback();
			EditMessageText test = (EditMessageText)msg;
			System.out.println("Outgoing edit message: " + msg);
			editMessageTextAsync(test, cb);
		}
		

		else{

			sendApiMethod(msg);

		}

	}


	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return BOTNAME;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return TOKEN;
	}
	
	

}