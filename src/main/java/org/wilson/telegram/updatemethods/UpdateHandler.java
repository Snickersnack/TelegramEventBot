package org.wilson.telegram.updatemethods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EditTextCallback;
import org.wilson.telegram.util.EventFinder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Re-implemented handler class. Deals with message updates
 * 
 */

public class UpdateHandler extends TelegramLongPollingBot {
	private static final String TOKEN = BotConfig.TOKENNEWBOT;
	private static final String BOTNAME = BotConfig.USERNAMENEWBOT;

	ObjectMapper mapper = new ObjectMapper();

	private static final boolean USEWEBHOOK = false;

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
		
		SendMessage sendMessageRequest = new SendMessage();
		
		if(update.hasChosenInlineQuery()){		
			updateCacheId(update);
		}
		else if (update.hasInlineQuery()) {
			InlineQueryHandler iq = new InlineQueryHandler();
			AnswerInlineQuery aQuery = iq.handleInlineQuery(update);
			return aQuery;
		}
		else if (update.hasCallbackQuery()) {
			CallBackHandler cb = new CallBackHandler(update);
			EditMessageText editMessageRequest = cb.handleCallbackQuery();
			return editMessageRequest;
		}
		else if (update.hasMessage()){
			Message message = update.getMessage();
			//if this is an event, check for exact string match of event title

			MessageHandler messageHandler = new MessageHandler(message);
			try {
				sendMessageRequest = messageHandler.parse();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return sendMessageRequest;		
			}
		return null;

	}
	


	private void executeMessage(BotApiMethod<?> msg)
			throws TelegramApiException {

		if(msg == null){
			return;
		}
//		System.out.println("sending: "  + msg.toString());
		if (msg instanceof SendMessage) {
			SendMessage sMessage = (SendMessage) msg;
			if(sMessage.getChatId()!=null){
				Message botMessage = sendMessage(sMessage);
				System.out.println("outgoing message id: " + botMessage.getMessageId());

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

	private void updateCacheId(Update update){
		String resultId = update.getChosenInlineQuery().getResultId();
		String inLineMessageId = update.getChosenInlineQuery().getInlineMessageId();

		
		HashMap<Integer, HashSet<EventModel>> map = Cache.getInstance().getUserEventMap();
		EventModel tempEvent = new EventModel(resultId);
		EventModel foundEvent = EventFinder.findEvent(tempEvent, map);
		
		//search based on the ResultId
		HashSet<String> set = foundEvent.getInLineMessageId();
		set.add(inLineMessageId);

		
//		for(Entry<Integer, HashSet<EventModel>> entry : map.entrySet()){
//			if(!entry.getValue().isEmpty()){
//				for(EventModel event : entry.getValue()){
//					if(event.getEventName().equals(resultId)){
//						HashSet<String> set = event.getInLineMessageId();
//						set.add(inLineMessageId);
////						event.setInLineMessageId(set);
//						return;
//					}
//				}
//			}
//		}
		
		
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