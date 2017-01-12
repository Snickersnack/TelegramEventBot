package org.wilson.telegram.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.models.EventModel;
import org.wilson.telegram.templates.EventClear;
import org.wilson.telegram.templates.EventDelete;
import org.wilson.telegram.templates.EventEdit;
import org.wilson.telegram.templates.EventMenu;
import org.wilson.telegram.templates.EventRespondees;
import org.wilson.telegram.templates.EventResponse;

public class KeyboardBuilder {

	ArrayList<List<InlineKeyboardButton>> keyboard;
	List<InlineKeyboardButton> buttons;
	Integer rows;
	Integer columns;
	
	public KeyboardBuilder(){
		this.rows = 0;
		this.columns = 0;
		this.buttons = new ArrayList<InlineKeyboardButton>();
	}
	public KeyboardBuilder(Integer row, Integer column){
		this.rows = row;
		this.columns = column;
		this.buttons = new ArrayList<InlineKeyboardButton>();
	}
	
	
	
	public List<List<InlineKeyboardButton>> buildEventButtons(Long eventId){		
		rows = 1;
		columns = 2;
		InlineKeyboardButton button = new InlineKeyboardButton();
		InlineKeyboardButton button2 = new InlineKeyboardButton();
		button.setText(EventResponse.ACCEPT);
		button.setCallbackData(EventResponse.ACCEPT + " " + eventId);
		button2.setText(EventResponse.DECLINE);
		button2.setCallbackData(EventResponse.DECLINE + " " + eventId);
		buttons.add(button);
		buttons.add(button2);
		return buildKeyboard();
	
	}
	 
	public InlineKeyboardMarkup buildClearConfirmation(){
		rows = 1;
		columns = 2;
		InlineKeyboardButton acceptButton = new InlineKeyboardButton();
		InlineKeyboardButton declineButton = new InlineKeyboardButton();
		acceptButton.setText(EventClear.ACCEPT);
		acceptButton.setCallbackData(EventClear.TYPE + " " + EventClear.ACCEPT);
		declineButton.setText(EventClear.DECLINE);
		declineButton.setCallbackData(EventClear.TYPE + " " + EventClear.DECLINE);
		addButton(acceptButton);
		addButton(declineButton);
		return buildMarkup();
	}
	
	public InlineKeyboardMarkup buildEventsList(Integer userId, String type){
		HashMap<Integer, HashSet<EventModel>> userMap = Cache.getInstance().getMasterEventMap();
		HashSet<EventModel> eventSet = userMap.get(userId);
		if(eventSet != null && eventSet.size() > 0){
			Integer eventNumber = eventSet.size();
			System.out.println("total events for user: " + eventNumber);
			rows = eventNumber + 1;
			columns = 1;
			for(EventModel event : eventSet){
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText(event.getEventName());
				button.setCallbackData(type + " " + event.getEventId());					
				addButton(button);
			}
			InlineKeyboardButton menuButton = new InlineKeyboardButton();
			menuButton.setText(EventMenu.MENUTEXT);
			menuButton.setCallbackData(EventMenu.MENUDATA);
			addButton(menuButton);
		}
		return buildMarkup();
			
		
	}
	
	public InlineKeyboardMarkup buildEditMenu(String data){
		rows = 4;
		columns = 1;
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
			addButton(button);
		}
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setText("<< Back to List");
		button.setCallbackData(EventEdit.EDITTYPE + " " + EventEdit.EVENTLIST);
		addButton(button);
		return buildMarkup();
	}
	
	public InlineKeyboardMarkup buildReturnMenu(){
		rows = 1;
		columns = 1;
		InlineKeyboardButton menuButton = new InlineKeyboardButton();
		menuButton.setText(EventMenu.MENUTEXT);
		menuButton.setCallbackData(EventMenu.MENUDATA);
		addButton(menuButton);
		return buildMarkup();
	}
	
	
	public InlineKeyboardMarkup buildMenu(){
		ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> firstRow = new ArrayList<InlineKeyboardButton>();
		InlineKeyboardButton startButton = new InlineKeyboardButton();
		startButton.setText("Create an Event");
		startButton.setCallbackData("Start");
		firstRow.add(startButton);
		
		List<InlineKeyboardButton> secondRow = new ArrayList<InlineKeyboardButton>();
		InlineKeyboardButton editButton = new InlineKeyboardButton();
		editButton.setText("Edit Events");
		editButton.setCallbackData(EventEdit.EDITTYPE + " " + EventEdit.EVENTLIST);
		
		InlineKeyboardButton deleteButton = new InlineKeyboardButton();
		deleteButton.setText("Delete Events");
		deleteButton.setCallbackData(EventDelete.DELETETYPE + " " + EventDelete.DELETELIST);
		secondRow.add(editButton);
		secondRow.add(deleteButton);
		
		List<InlineKeyboardButton> thirdRow = new ArrayList<InlineKeyboardButton>();
		InlineKeyboardButton clearButton = new InlineKeyboardButton();
		clearButton.setText("Clear Events");
		clearButton.setCallbackData("Clear");
		
		InlineKeyboardButton respondeesButton = new InlineKeyboardButton();
		respondeesButton.setText("View Respondees");
		respondeesButton.setCallbackData(EventRespondees.TYPE);
		thirdRow.add(clearButton);
		thirdRow.add(respondeesButton);
		
		keyboard.add(firstRow);
		keyboard.add(secondRow);
		keyboard.add(thirdRow);
		
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		markup.setKeyboard(keyboard);
		
		return markup;

		
	}
	
	public InlineKeyboardMarkup buildMarkup(){
		ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		int counter = 0;
		for(int i =0; i<rows; i++){
			List<InlineKeyboardButton> columnButtons = new ArrayList<InlineKeyboardButton>();
			for(int j =0; j<columns; j++){
				columnButtons.add(buttons.get(counter));
				counter++;
			}
			keyboard.add(columnButtons);
		}
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		markup.setKeyboard(keyboard);
		
		return markup;

	}
	
	public ArrayList<List<InlineKeyboardButton>> buildKeyboard(){
		ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		int counter = 0;
		for(int i =0; i<rows; i++){
			List<InlineKeyboardButton> columnButtons = new ArrayList<InlineKeyboardButton>();
			for(int j =0; j<columns; j++){
				columnButtons.add(buttons.get(counter));
				counter++;
			}
			keyboard.add(columnButtons);
		}

		
		return keyboard;

	}
	
	public void addButton(InlineKeyboardButton button){
		buttons.add(button);
	}
	
	public void addButtons(List<InlineKeyboardButton> buttons){
		this.buttons.addAll(buttons);
	}
	
	public void setRows(Integer rows){
		this.rows = rows;
	}
	public void setColumns(Integer columns){
		this.columns = columns;
	}
	
	
	
}
