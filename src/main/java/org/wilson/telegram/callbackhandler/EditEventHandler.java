package org.wilson.telegram.callbackhandler;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EditModel;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.util.EventFinder;
import org.wilson.telegram.util.KeyboardBuilder;

public class EditEventHandler {

	EventModel eventModel;
	CallbackQuery callBack;
	
	public EditEventHandler(Update update) {
		eventModel = null;
		callBack = update.getCallbackQuery();

	}


	public EditMessageText handleCallbackQuery() {
		EditMessageText editRequest = new EditMessageText();
		StringBuilder sb = new StringBuilder();
		sb.append("Edit Events:");
		sb.append(System.getProperty("line.separator"));
		Long chatId = callBack.getMessage().getChatId();
		Integer messageId = callBack.getMessage().getMessageId();
		editRequest.setMessageId(messageId);
		editRequest.setChatId(chatId);		
		String data = callBack.getData();
		String[] dataArray = data.split(" ");
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		
		if(dataArray.length >2){
			
			//Apparently the callback userId is different from the message userid...
			//UPDATE FOR DEBUGGING: Update{updateId=40098800, message=null, inlineQuery=null, chosenInlineQuery=null, callbackQuery=CallbackQuery{id='701781924027710717', from=User{id=163396337, firstName='Wilson', lastName='Tan', userName='snickersnack'}, message=Message{messageId=1917, from=User{id=258320879, firstName='eveplannerbot', lastName='null', userName='EvePlannerBot'}, date=1482523088, chat=Chat{id=163396337, type='private', title='null', firstName='Wilson', lastName='Tan', userName='snickersnack', allMembersAreAdministrators=null}, forwardFrom=null, forwardFromChat=null, forwardDate=null, text='Edit Events:', entities=null, audio=null, document=null, photo=null, sticker=null, video=null, contact=null, location=null, venue=null, pinnedMessage=null, newChatMember=null, leftChatMember=null, newChatTitle='null', newChatPhoto=null, deleteChatPhoto=null, groupchatCreated=null, replyToMessage=null, voice=null, caption='null', superGroupCreated=null, channelChatCreated=null, migrateToChatId=null, migrateFromChatId=null, editDate=1482523090, game=null, forwardFromMessageId=null}, inlineMessageId='null', data='Edit asdf EditTitle', gameShortName='null', chatInstance='564456924938390008'}, editedMessage=null, channelPost=null, editedChannelPost=null}

			Integer userId = callBack.getFrom().getId();
			EventModel event = new EventModel(dataArray[1]);
			System.out.println("dataArray 1: " + dataArray[1]);
			eventModel = EventFinder.findEvent(event, Cache.getInstance().getMasterEventMap());
			System.out.println("when we change name, here's where its null:" + eventModel);
			Cache.getInstance().listAllEvents(dataArray[1]);
			EditModel editModel = new EditModel(messageId, chatId);
			editModel.setEventModel(eventModel);
			String eventType = dataArray[2];
			//This isn't a event message or we don't have such an event
			
			
			switch(eventType){
			
			case EventEdit.EDITNAME:
				sb.append("Current event name: " + dataArray[1]);
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("Enter new event name:");
				editModel.setEditType(EventEdit.EDITNAME);
				break;

			case EventEdit.EDITDATE:
				sb.append("Current event date: " + eventModel.getEventDate());
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("Enter new event date:");
				editModel.setEditType(EventEdit.EDITDATE);
				break;

			case EventEdit.EDITLOCATION:
				sb.append("Current event location: " + eventModel.getEventLocation());
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("Enter new event location:");
				editModel.setEditType(EventEdit.EDITLOCATION);
				break;

			}
			
			Cache.getInstance().getInProgressEdit().put(userId, editModel);
		

			
		}else{
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder(3,1);
			int counter = 0;
			for(String editField : EventEdit.EDITFIELDLIST){
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText(EventEdit.EDITBUTTONLIST[counter]);
				counter++;
				StringBuilder buttonString = new StringBuilder();
				buttonString.append(data);
				buttonString.append(" ");
				buttonString.append(editField);
				button.setCallbackData(buttonString.toString());
				keyboardBuilder.addButton(button);
			}
			markup = keyboardBuilder.buildMarkup();
	    }
		editRequest.setReplyMarkup(markup);
		editRequest.setParseMode("HTML");
		editRequest.setText(sb.toString());
		return editRequest;

	}

}
