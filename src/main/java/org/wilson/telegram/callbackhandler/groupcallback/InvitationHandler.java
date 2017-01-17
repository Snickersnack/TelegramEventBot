package org.wilson.telegram.callbackhandler.groupcallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
import org.wilson.telegram.models.RespondeeModel;
import org.wilson.telegram.templates.EventResponse;
import org.wilson.telegram.util.EventBuilder;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.EventPersistence;
import org.wilson.telegram.util.KeyboardBuilder;

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
		boolean inline = false;
		//Set based on existence of inlineMessageId
		if (inLineMessageId != null) {
			inline = true;
			eventModel = EventFinder.findEventByInlineMessageId(inLineMessageId);
			editRequest.setInlineMessageId(inLineMessageId);
		}else{
			
			//we are editing an event sent from /view
			String eventId = callBack.getData().split(" ")[1];
			
			eventModel = EventFinder.findEvent(new EventModel(Long.parseLong(eventId)), Cache.getInstance().getMasterEventMap());
			Long chatId = callBack.getMessage().getChatId();
			editRequest.setMessageId(callBack.getMessage().getMessageId());
			editRequest.setChatId(chatId);
		}
		
		//This isn't a event message or we don't have such an event
		if (eventModel == null) {
			editRequest.setText("This event no longer exists :(");
			return editRequest;
		}
		
		//build the eventtext
		updateAttendees();
		String eventText = EventBuilder.build(eventModel);
		eventModel.setEventText(eventText);
		
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		
		
		if(eventModel.getImgur() == null || inline){
			markup.setKeyboard(eventModel.getEventGrid());

		}else{
			KeyboardBuilder kb = new KeyboardBuilder();
			markup = kb.buildView(eventModel);
			if(eventModel.isShowing()){
				editRequest.enableWebPagePreview();
			}else{
				editRequest.disableWebPagePreview();
			}
				
		}
	
		//set edit request
		editRequest.setParseMode("HTML");
		editRequest.setText(eventText);
		editRequest.setReplyMarkup(markup);

		
		EventPersistence.saveOrUpdate(eventModel);
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
	
	private void updateAttendees(){
		userFirst = callBack.getFrom().getFirstName();
		Integer userId = callBack.getFrom().getId();
		response = callBack.getData();
//		Map<String, Boolean> responses = eventModel.getTotalResponses();

		Set<RespondeeModel> responses = eventModel.getTotalResponses();
		String[] responseArray = response.split(" ");
		response = responseArray[0];
		RespondeeModel responseModel= new RespondeeModel();
		responseModel.setFirstName(userFirst);
		responseModel.setUserId(userId);
		if(!responses.contains(responseModel)){
			responses.add(responseModel);
			responseModel = Cache.getInstance().registerResponse(responseModel);
			EventPersistence.save(responseModel);
		}else{
			for(RespondeeModel item : responses){
				if(item.equals(responseModel)){
					responseModel = item;
				}
			}
		}

		if (response.equals(EventResponse.ACCEPT)) {
			responseModel.setAttending(true);
			
//				responses.put(userFirst, true);
				
			 
		} else {
			responseModel.setAttending(false);
//				responses.put(userFirst, false);

			
		}
		
	}
	

}
