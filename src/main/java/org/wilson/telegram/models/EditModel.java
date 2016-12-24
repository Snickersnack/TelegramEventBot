package org.wilson.telegram.models;

public class EditModel {

	//holds information during an edit event
	private long chatId;
	private Integer messageId;
	private String editType;
	private EventModel eventModel;
	
	public EditModel(Integer messageId, Long chatID){
		this.messageId = messageId;
		this.chatId = chatID;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public EventModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(EventModel eventModel) {
		this.eventModel = eventModel;
	}
}
