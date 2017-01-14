package org.wilson.telegram.inlinequeryhandler;

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
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.models.EventModel;

public class InlineQueryHandler {
	
	public InlineQueryHandler(){
		
	}
	
	public AnswerInlineQuery handleInlineQuery(Update update){
		InlineQuery iQuery = update.getInlineQuery();
		Integer userId = iQuery.getFrom().getId();
		AnswerInlineQuery aQuery = new AnswerInlineQuery();
		List<InlineQueryResult> list = new ArrayList<>();
		HashMap<Integer, HashSet<EventModel>> cachedEvents = Cache.getInstance().getMasterEventMap();
			
		
		//Searches on the userEventMap
		HashSet<EventModel> userSet = cachedEvents.get(userId);
		if(userSet!= null && !userSet.isEmpty()){
			for(EventModel event : cachedEvents.get(userId)){
			
				InputTextMessageContent content = new InputTextMessageContent();
				content.setMessageText(event.getEventText());
				content.setParseMode(BotConfig.MESSAGE_MARKDOWN);
				content.setDisableWebPagePreview(false);
				
				
				
				//Build our list of AnswerInline Results off of each event
				InlineQueryResultArticle qResultArticle = new InlineQueryResultArticle();
				

				
				
				InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
				markup.setKeyboard(event.getEventGrid());
				qResultArticle.setThumbUrl(event.getImgur());
				System.out.println(event.getImgur());
//				qResultArticle.setUrl("http://i.imgur.com/GK2OaMxs.jpg");
				qResultArticle.setReplyMarkup(markup);
				qResultArticle.setInputMessageContent(content);
				qResultArticle.setId(event.getEventId().toString());
				qResultArticle.setTitle(event.getEventName());
				qResultArticle.setHideUrl(false);
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

			
		aQuery.setSwitchPmText("Create an Event");
		aQuery.setSwitchPmParameter("/start");

		
		aQuery.setResults(list);
		aQuery.setCacheTime(1);
		
//		for(InlineQueryResult result : aQuery.getResults()){
//			InlineQueryResultArticle art = (InlineQueryResultArticle) result;
//			System.out.println("Article ids: " + art.getId());
//			
//		}
		
		return aQuery;
		
	}
}
