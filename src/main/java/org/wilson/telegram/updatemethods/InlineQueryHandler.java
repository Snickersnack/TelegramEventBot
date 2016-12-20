package org.wilson.telegram.updatemethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.wilson.telegram.Cache;
import org.wilson.telegram.EventModel;
import org.wilson.telegram.config.BotConfig;

public class InlineQueryHandler {
	
	public InlineQueryHandler(){
		
	}
	
	protected AnswerInlineQuery handleInlineQuery(Update update){
		InlineQuery iQuery = update.getInlineQuery();
		Integer userId = iQuery.getFrom().getId();
		AnswerInlineQuery aQuery = new AnswerInlineQuery();
		List<InlineQueryResult> list = new ArrayList<>();
		HashMap<Integer, HashSet<EventModel>> cachedEvents = Cache.getInstance().getUserEventMap();
		
		//Searches on the userEventMap
		HashSet<EventModel> userSet = cachedEvents.get(userId);
		if(userSet!= null && !userSet.isEmpty()){
			for(EventModel event : cachedEvents.get(userId)){
				
				//Build our list of AnswerInline Results off of each event
				InlineQueryResultArticle qResultArticle = new InlineQueryResultArticle();
				InputTextMessageContent content = new InputTextMessageContent();
				content.setMessageText(event.getEventText());
				content.setParseMode(BotConfig.SENDMESSAGEMARKDOWN);
				qResultArticle.setInputMessageContent(content);
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
				markup.setKeyboard(event.getEventGrid());
				qResultArticle.setReplyMarkup(markup);
				qResultArticle.setId(event.getEventName());
				qResultArticle.setTitle(event.getEventName());
				list.add(qResultArticle);
			
			}	

		}
		
		
		//build our response
		StringBuilder sb = new StringBuilder();
		String setString = "";
		for(Entry<Integer, HashSet<EventModel>> entry : cachedEvents.entrySet()){
			for(EventModel event : entry.getValue()){
				setString += event.getEventName() + " ";
			}
			sb.append(entry.getKey() + ": " + setString);
			setString= "";
		}
		aQuery.setInlineQueryId(iQuery.getId());
		aQuery.setSwitchPmText("Click here to create event");
		aQuery.setSwitchPmParameter("/event");
		aQuery.setResults(list);
		aQuery.setCacheTime(1);
		
		for(InlineQueryResult result : aQuery.getResults()){
			InlineQueryResultArticle art = (InlineQueryResultArticle) result;
//			System.out.println("Article ids: " + art.getId());
			
		}
		
		return aQuery;
		
	}
}
