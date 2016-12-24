package org.wilson.telegram.util;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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
