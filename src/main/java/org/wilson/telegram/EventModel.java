package org.wilson.telegram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class EventModel {

	private String eventText;
	private List<List<InlineKeyboardButton>> eventGrid;
	private Integer eventHost;
	private String eventHostFirst;
	private HashSet<String> attendees;
	private int eventInputStage;
	private String eventName;
	private String eventLocation;
	private String eventDate;
	private Long channelId;
	private Integer messageId;


	private HashSet<String> inLineMessageIds;
	
	public EventModel(){
		eventText = null;
		eventGrid = new ArrayList<List<InlineKeyboardButton>>();
		eventHost = null;
		eventHostFirst = null;
		attendees = new HashSet<String>();
		eventInputStage = 0;
		eventName = null;
		eventLocation = null;
		eventDate = null;
		inLineMessageIds = new HashSet<String>();
		channelId = null;
		messageId = null;
	}
	
	public EventModel(String eventName){
		this.eventName = eventName;
		eventText = null;
		eventGrid = new ArrayList<List<InlineKeyboardButton>>();
		eventHost = null;
		eventHostFirst = null;
		attendees = new HashSet<String>();
		eventInputStage = 0;
		eventLocation = null;
		eventDate = null;
		inLineMessageIds = new HashSet<String>();
		channelId = null;
		messageId = null;
	}
	
	public EventModel(String eventName, String eventDate, String eventLocation, Integer eventHost){
		eventText = null;
		eventGrid = new ArrayList<List<InlineKeyboardButton>>();
		eventHostFirst = null;
		attendees = new HashSet<String>();
		eventInputStage = 0;
		inLineMessageIds = new HashSet<String>();
		messageId= null;
		channelId = null;
		
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventLocation = eventLocation;
		this.eventHost = eventHost;
		
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		InlineKeyboardButton button = new InlineKeyboardButton();
		InlineKeyboardButton button2 = new InlineKeyboardButton();
		button.setText("Yes");
		button.setCallbackData("Yes");
		button2.setText("No");
		button2.setCallbackData("No");
		List<InlineKeyboardButton> buttons = new ArrayList();
		buttons.add(button);
		buttons.add(button2);
		List<List<InlineKeyboardButton>> grid = new ArrayList();
		grid.add(buttons);
		this.eventGrid=grid;
			
		//Fix this duplicate here, we don't need to iterate over this when initiating the event

		String attendeeString = "Attendees (" + attendees.size() + "): ";
		String event = "<strong>" + eventName + "</strong>" + System.getProperty("line.separator") 
				+ eventDate + System.getProperty("line.separator") 
				+ "📍" + eventLocation + System.getProperty("line.separator") 

				+ attendeeString;
		
		this.eventText = event;
	}
	
	public String getEventText() {
		return eventText;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

	public Integer getAttendeeNumber() {
		return attendees.size();
	}

	public List<List<InlineKeyboardButton>> getEventGrid() {
		return eventGrid;
	}

	public void setEventGrid(List<List<InlineKeyboardButton>> eventGrid) {
		this.eventGrid = eventGrid;
	}

	public Integer getEventHost() {
		return eventHost;
	}

	public void setEventHost(Integer eventHost) {
		this.eventHost = eventHost;
	}

	public String getEventHostFirst() {
		return eventHostFirst;
	}

	public void setEventHostFirst(String eventHostFirst) {
		this.eventHostFirst = eventHostFirst;
	}

	public HashSet<String> getAttendees() {
		return attendees;
	}

	public void setAttendees(HashSet<String> attendees) {
		this.attendees = attendees;
	}

	public int getEventInputStage() {
		return eventInputStage;
	}

	public void setEventInputStage(int eventInputStage) {
		this.eventInputStage = eventInputStage;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	
	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}


		
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(!EventModel.class.isAssignableFrom(o.getClass())){
			return false;
		}
		EventModel temp = (EventModel)o;
		if(!this.eventName.equals(temp.getEventName())){
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = 17;
		hash = 31 * hash + eventName.hashCode();
		return hash;
	}

	public HashSet<String> getInLineMessageId() {
		return inLineMessageIds;
	}

	public void setInLineMessageId(HashSet<String> inLineMessageId) {
		this.inLineMessageIds = inLineMessageId;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	

	
}
