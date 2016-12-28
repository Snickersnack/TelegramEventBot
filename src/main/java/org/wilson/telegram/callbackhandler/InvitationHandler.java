package org.wilson.telegram.callbackhandler;

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
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventResponse;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;

public class InvitationHandler extends UpdateHandler {

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

	public InvitationHandler(Update update) {
		answer = new AnswerCallbackQuery();
		cachedUser = null;
		eventModel = null;
		callBack = update.getCallbackQuery();

	}


	public EditMessageText handleCallbackQuery() {
		inLineMessageId = callBack.getInlineMessageId();
		EditMessageText editRequest = new EditMessageText();

		//Set based on existence of inlineMessageId
		if (inLineMessageId != null) {
			eventModel = EventFinder.findEventByInlineMessageId(inLineMessageId);
			editRequest.setInlineMessageId(inLineMessageId);
		}else{
			
			//we are editing an event sent from /view
			String eventName = callBack.getMessage().getText().split("\\r?\\n")[0];
			eventModel = EventFinder.findEventbyName(eventName);
			
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
		HashMap<String, Boolean> responses = eventModel.getTotalResponses();


		String[] responseArray = response.split(" ");
		response = responseArray[0];
		
		if (response.equals(EventResponse.ACCEPT)) {
				attendees.add(userFirst);
				System.out.println(responses.put(userFirst, true));
			 
		} else {
				attendees.remove(userFirst);
				System.out.println(responses.put(userFirst, false));

			
		}
		return attendees;
	}
	

}
