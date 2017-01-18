package org.wilson.telegram.config;


public class BotConfig {

	public BotConfig(){
		
	}
    
	
    public static final String BOTTOKEN = System.getenv("BOTTOKEN");
    public static final String BOTUSERNAME = System.getenv("BOTUSERNAME");
    public static final String IMGURCLIENTID= System.getenv("IMGURCLIENTID");
    public static final String IMGURSECRET = System.getenv("IMGURSECRET");
    public static final String GETFILEURL = "https://api.telegram.org/file/bot";

    public static final String MESSAGE_MARKDOWN = "HTML";
    public static final String TIME_ZONE = "America/Los_Angeles";
    public static final Long SCHEDULED_REMOVAL_DAYS = 2L;
    public static final Integer MAX_EVENTS = 5;
    public static final Integer MAX_CHANNEL_VIEW = 3;
    

    
}
