package org.wilson.telegram.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Entity
@Table(name = "events")

public class EventModel {

	private Long eventId;
	private String eventText;
	
	@Transient
	private List<List<InlineKeyboardButton>> eventGrid;
	private Integer eventHost;
	private String eventHostFirst;
	
	private int eventInputStage;
	private String eventName;
	private String eventLocation;
	private String eventDate;
	private String imgur;
	boolean showing;
	private Set<Long> channels;
	
	private Set<RespondeeModel> totalResponses;
//	private Map<String, Boolean> totalResponses;
	
	private Set<String> inLineMessageIds;
	
	public EventModel(){
		showing = false;
		eventText = null;
		eventGrid = new ArrayList<List<InlineKeyboardButton>>();
		eventHost = null;
		eventHostFirst = null;
//		totalResponses = new HashMap<String, Boolean>();
		totalResponses = new HashSet<RespondeeModel>();
		eventInputStage = 0;
		eventName = null;
		eventLocation = null;
		eventDate = null;
		inLineMessageIds = new HashSet<String>();
		this.eventId = null;
		channels = new HashSet<Long>();
	}
	
	public EventModel(Long eventId){
		this();
		this.eventId = eventId;

	}
	
	public EventModel(String eventName, String eventDate, String eventLocation, Integer eventHost){
		this();
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventLocation = eventLocation;
		this.eventHost = eventHost;

	}
	
	@Column(name = "event_text")
	public String getEventText() {
		return eventText;
	}

	
	@Column(name = "event_host")
	public Integer getEventHost() {
		return eventHost;
	}
	
	@Column(name = "event_host_first")
	public String getEventHostFirst() {
		return eventHostFirst;
	}
	
	@Column(name = "event_location")
	public String getEventLocation() {
		return eventLocation;
	}
	
	@Column(name = "event_input_stage")
	public int getEventInputStage() {
		return eventInputStage;
	}
	
	@Column(name = "event_name")
	public String getEventName() {
		return eventName;
	}
	
	@Column(name = "event_date")
	public String getEventDate() {
		return eventDate;
	}
	
	@Id
	@Column(name = "event_id")
	public Long getEventId() {
		return eventId;
	}
	

//	@ElementCollection
//	@CollectionTable(name = "total_responses", joinColumns = @JoinColumn(name = "event_id"))
//	@MapKeyColumn(name = "user_name")
//	@Column(name = "attending")
//	@Cascade({CascadeType.ALL})
//	public Map<String, Boolean> getTotalResponses() {
//		return totalResponses;
//	}

	@ElementCollection
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", referencedColumnName = "event_id")
	@Cascade({ CascadeType.ALL })
	public Set<RespondeeModel> getTotalResponses() {
		return totalResponses;
	}
	
	@ElementCollection
	@CollectionTable(name = "inline_message", joinColumns = @JoinColumn(name = "event_id"))
	@Column(name = "inline_message_id")
	@Cascade({CascadeType.ALL})

	public Set<String> getInLineMessageId() {
		return inLineMessageIds;
	}
	

	
	@Transient
	public List<List<InlineKeyboardButton>> getEventGrid() {
		return eventGrid;
	}
	
	@ElementCollection
	@CollectionTable(name = "channels", joinColumns = @JoinColumn(name = "event_id"))
	@Column(name = "channel_id")
	@Cascade({CascadeType.ALL})

	public Set<Long> getChannels() {
		return channels;
	}

	@Transient
	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean view) {
		this.showing = view;
	}

	@Column(name = "imgur")
	public String getImgur() {
		return imgur;
	}

	public void setImgur(String imgur) {
		this.imgur = imgur;
	}


	public void setChannels(Set<Long> channels) {
		this.channels = channels;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

	public void setEventGrid(List<List<InlineKeyboardButton>> eventGrid) {
		this.eventGrid = eventGrid;
	}

	public void setEventHost(Integer eventHost) {
		this.eventHost = eventHost;
	}

	public void setEventHostFirst(String eventHostFirst) {
		this.eventHostFirst = eventHostFirst;
	}



	public void setEventInputStage(int eventInputStage) {
		this.eventInputStage = eventInputStage;
	}



	public void setEventName(String eventName) {
		this.eventName = eventName;
	}


	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}



	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	

	public void setInLineMessageId(Set<String> inLineMessageId) {
		this.inLineMessageIds = inLineMessageId;
	}



//	public void setTotalResponses(Map<String, Boolean> totalResponses) {
//		this.totalResponses = totalResponses;
//	}
	
	public void setTotalResponses(Set<RespondeeModel> totalResponses){
		this.totalResponses = totalResponses;
	}


	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	//Change hashcode and equals to be on the ID of the event
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(!EventModel.class.isAssignableFrom(o.getClass())){
			return false;
		}
		EventModel temp = (EventModel)o;
		if(!this.eventId.equals(temp.getEventId())){
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = 17;
		hash = 31 * hash + eventId.hashCode();
		return hash;
	}

	@Override
	public String toString() { 
	    return "Name: '" + this.eventName + "', Id: '" + this.eventId ;
	} 

	

	
}
