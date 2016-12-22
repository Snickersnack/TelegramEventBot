package org.wilson.telegram.updatemethods;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;

public class CallBackHandler extends UpdateHandler {

	AnswerCallbackQuery answer;
	String response;
	User user;
	String userFirst;
	String inLineMessageId;
	HashMap<Integer, HashSet<EventModel>> map;
	Integer cachedUser;
	EventModel eventModel;
	Message message;
	EventFinder eventFinder;
	CallbackQuery callBack;

	public CallBackHandler(Update update) {
		answer = new AnswerCallbackQuery();
		cachedUser = null;
		eventModel = null;
		callBack = update.getCallbackQuery();

	}


	protected EditMessageText handleCallbackQuery() {
		inLineMessageId = callBack.getInlineMessageId();
		EditMessageText editRequest = new EditMessageText();

		//Set based on existence of inlineMessageId
		if (inLineMessageId != null) {
			eventModel = EventFinder.findEventByInlineMessageId(inLineMessageId);
			editRequest.setInlineMessageId(inLineMessageId);
		}else{
			String eventName = callBack.getMessage().getText().split("\\r?\\n")[0];
			EventModel event = new EventModel(eventName);
			eventModel = EventFinder.findEvent(event, Cache.getInstance().getChannelEventMap());
			
			editRequest.setMessageId(callBack.getMessage().getMessageId());
			editRequest.setChatId(eventModel.getChannelId());
		}
		
		//This isn't a event message or we don't have such an event
		if (eventModel == null) {
			editRequest.setText("This event no longer exists :(");
			return editRequest;
		}
		
		//build the eventtext
		HashSet<String> updatedAttendees = updateAttendees();
		eventModel.setAttendees(updatedAttendees);
		String eventText = EventBuilder.build(eventModel);
		eventModel.setEventText(eventText);
		
		
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		markup.setKeyboard(eventModel.getEventGrid());
	
		//set edit request
		editRequest.setParseMode("HTML");
		editRequest.setText(eventText);
		editRequest.setReplyMarkup(markup);
		
		sendAnswerFeedBack();
		return editRequest;
	}

	
	private void sendAnswerFeedBack(){
		answer.setText("Response accepted!");
		answer.setCallbackQueryId(callBack.getId());
		try {
			sendApiMethod(answer);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private HashSet<String> updateAttendees(){
		userFirst = callBack.getFrom().getFirstName();
		response = callBack.getData();
		HashSet<String> attendees = eventModel.getAttendees();
		String[] responseArray = response.split(" ");
		response = responseArray[0];
		
		if (response.equals("Yes")) {
			if (!eventModel.getAttendees().contains(userFirst)) {
				attendees.add(userFirst);
			} 
		} else {
			if (eventModel.getAttendees().contains(userFirst)) {
				attendees.remove(userFirst);
			} 
		}
		return attendees;
	}
	

}
