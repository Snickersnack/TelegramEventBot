package org.wilson.telegram.client;

import java.util.HashSet;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.callbackhandler.DeleteEventHandler;
import org.wilson.telegram.callbackhandler.EditEventHandler;
import org.wilson.telegram.callbackhandler.EditTextCallback;
import org.wilson.telegram.callbackhandler.InvitationHandler;
import org.wilson.telegram.commandprocesses.EventStartCommand;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.inlinequeryhandler.InlineQueryHandler;
import org.wilson.telegram.messagehandler.MessageParser;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventClear;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.templates.EventRespondees;
import org.wilson.telegram.templates.EventResponse;
import org.wilson.telegram.util.CacheUpdater;
import org.wilson.telegram.util.KeyboardBuilder;
import org.wilson.telegram.util.RespondeesCommand;

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
		
		
		if(update.hasChosenInlineQuery()){
			System.out.println("WTF");
			CacheUpdater.updateInlineId(update);
		}
		else if (update.hasInlineQuery()) {
			InlineQueryHandler iq = new InlineQueryHandler();
			AnswerInlineQuery aQuery = iq.handleInlineQuery(update);
			return aQuery;
		}
		else if (update.hasCallbackQuery()) {
			CallbackQuery callback = update.getCallbackQuery();
			String data = callback.getData();
			String inline = callback.getInlineMessageId();
			String[] dataArray = data.split(" ");
			EditMessageText editMessageRequest = new EditMessageText();

			editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);

			
			String callbackType = dataArray[0];
			

			
			if(inline == null){
				
				Message message = callback.getMessage();				
				Long chatId = message.getChatId();
				Integer messageId = message.getMessageId();
			    Integer userId = callback.getFrom().getId();
				editMessageRequest.setMessageId(messageId);
				editMessageRequest.setChatId(chatId);
				
			//if there's a message, that means its not an event response
			
			
			if(dataArray[0].equals(EventResponse.ACCEPT) || dataArray[0].equals(EventResponse.DECLINE)){
				System.out.println("invitehandler?");
				InvitationHandler invite = new InvitationHandler(update);
				editMessageRequest = invite.handleCallbackQuery();
			}
			
			else if(dataArray[0].equals(EventEdit.EDITTYPE)){
				EditEventHandler editor = new EditEventHandler(update);
				editMessageRequest = editor.handleCallbackQuery();
					
			}
			
			else if(dataArray[0].equals(EventDelete.DELETETYPE)){
				DeleteEventHandler deleteEvent = new DeleteEventHandler(update);
				editMessageRequest = deleteEvent.handleCallbackQuery();
			}
			
			
			
			else if(dataArray[0].equals(EventMenu.MENUDATA)){
//				if(dataArray.length >1){
//					if(dataArray[1].equals(EventMenu.MENUSEND)){
//						SendMessage sendMessageRequest = new SendMessage();
//						KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
//						InlineKeyboardMarkup markup = keyboardBuilder.buildMenu();
//						sendMessageRequest.setChatId(chatId);
//						sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
//						sendMessageRequest.setReplyMarkup(markup);
//						StringBuilder sb = new StringBuilder();
//						sb.append(EventMenu.MENUTITLE);
//						sb.append(System.getProperty("line.separator"));
//						sb.append(System.getProperty("line.separator"));
//						sb.append("<i>Select an action below</i>");
//						sb.append(System.getProperty("line.separator"));
//						sendMessageRequest.setText(sb.toString());
//						try{
//							
//						}catch(Exception e){
//							
//						}
//						return sendMessageRequest;
//					}
//				}else{
					StringBuilder sb = new StringBuilder();
					sb.append(EventMenu.MENUTITLE);
					sb.append(System.getProperty("line.separator"));
					sb.append(System.getProperty("line.separator"));
					sb.append("<i>Select an action below</i>");
					sb.append(System.getProperty("line.separator"));
					editMessageRequest.setText(sb.toString());
					KeyboardBuilder kb = new KeyboardBuilder();
					InlineKeyboardMarkup markup = kb.buildMenu();
					editMessageRequest.setReplyMarkup(markup);
//						return editMessageRequest;
//				}

			}
			else if(dataArray[0].equals("Start")){
				User user = callback.getFrom();	
				return EventStartCommand.start(user, chatId);
			}
			else if(dataArray[0].equals(EventRespondees.TYPE)){
				HashSet<EventModel> set = Cache.getInstance().getMasterEventMap().get(userId);
				if( set == null || set.size() == 0){
					
					editMessageRequest.setText("<i>You have no events</i>");
					editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
					markup = keyboardBuilder.buildReturnMenu();
					editMessageRequest.setReplyMarkup(markup);
				
				}else{
					
				
				RespondeesCommand respond = new RespondeesCommand();
				respond.build(userId, chatId, true);
				
				}
				
			}
			else if(dataArray[0].equals(EventClear.TYPE)){
				HashSet<EventModel> set = Cache.getInstance().getMasterEventMap().get(userId);
				if( set == null || set.size() == 0){
					InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
					KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
					markup = keyboardBuilder.buildReturnMenu();
					editMessageRequest.setReplyMarkup(markup);
					editMessageRequest.setText("<i>You have no events</i>");
					editMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
					
				}
				
				//we are in confirmation
				else if(dataArray.length>1){
					
					//proceed with event deletion
					if(dataArray[1].equals(EventClear.ACCEPT)){
						Cache.getInstance().clearUserEvents(userId);
						editMessageRequest.setText("<i>Your events have been cleared</i>");

					
					//If not deleting, provide menu
					}else{
						KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
						InlineKeyboardMarkup markup = keyboardBuilder.buildMenu();

						editMessageRequest.setReplyMarkup(markup);
						StringBuilder sb = new StringBuilder();
						sb.append(EventMenu.MENUTITLE);
						sb.append(System.getProperty("line.separator"));
						sb.append(System.getProperty("line.separator"));
						sb.append("<i>Select an action below</i>");
						sb.append(System.getProperty("line.separator"));
						editMessageRequest.setText(sb.toString());

					}
				
				//Ask for confirmation of deletion
				}else{
					
					editMessageRequest.setText("Are you sure you want to remove all your events?");
					KeyboardBuilder kb = new KeyboardBuilder(1,2);
					InlineKeyboardButton acceptButton = new InlineKeyboardButton();
					InlineKeyboardButton declineButton = new InlineKeyboardButton();
					acceptButton.setText(EventClear.ACCEPT);
					acceptButton.setCallbackData(EventClear.TYPE + " " + EventClear.ACCEPT);
					declineButton.setText(EventClear.DECLINE);
					declineButton.setCallbackData(EventClear.TYPE + " " + EventClear.DECLINE);
					kb.addButton(acceptButton);
					kb.addButton(declineButton);
					InlineKeyboardMarkup  markup = kb.buildMarkup();
					editMessageRequest.setReplyMarkup(markup);
				}
			}
		}else{
			InvitationHandler invite = new InvitationHandler(update);
			editMessageRequest = invite.handleCallbackQuery();
		}
			


			return editMessageRequest;


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